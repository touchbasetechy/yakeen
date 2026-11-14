package com.murad.app.matrimony.textinput

/**
 * Emits the shared glassmorphic text-input CSS and JavaScript once per page.
 *
 * Owns every CSS rule that is text-input-specific:
 *  • .input-glass              shell — glass background only, no borders
 *  • .input-glass input        always transparent; no border, no glow
 *  • .input-glass .floating-label   animated floating label (up/down)
 *  • .input-glass .clear-btn   44×44 px mobile touch target, 20×20 px desktop
 *  • .input-glass .validation-icon  valid / invalid indicator
 *  • .glass-helper-text, .glass-error, .glass-form-group
 *  • Autofill override (WebKit / Blink)
 *  • Disabled state
 *  • Shared JS: clearInput(), toggleClearButton(), autofill detection
 *
 * Architecture
 * ─────────────
 *  The shell (.input-glass) owns the glass background only — no borders.
 *  The <input> is always background:transparent with no border at any state.
 *  This is the only approach that survives Bootstrap and browser UA stylesheets:
 *    • Bootstrap .form-control:focus targets <input> — not the shell.
 *    • All Bootstrap and browser focus rings (box-shadow, outline, border)
 *      are killed with !important on the full input selector list.
 *    • -webkit-box-shadow alongside box-shadow covers Safari/iOS.
 *    • :-moz-focusring covers Firefox's separate blue-ring pseudo-class.
 *    • outline-width/color/offset sub-properties are set explicitly.
 *    • Bootstrap 5 --bs-focus-ring-* tokens are zeroed at element level.
 *    • background:transparent !important is on BOTH the multi-selector block
 *      AND the structural rule so no cascade path can restore white.
 *    • background-color 5000s transition suppresses any background flash.
 *    • accent-color:transparent + forced-color-adjust:none kill
 *      OS/browser accent-derived rings (#7EB8F7, #7486CF).
 *    • outline:0 (not none) kills Chrome's outline:auto UA rule for
 *      :focus-visible which produces a coloured platform ring.
 *
 * Floating label
 * ───────────────
 *  Default (blur + empty)  top:50% translateY(-50%), font-size:1rem (100%)
 *  Raised  (focus/value)   translateY(calc(-50% - 1.2rem)) scale(0.70)
 *                           label sits inside the shell at the top-left corner
 *  Triggers: :focus-within on shell, :not(:placeholder-shown), :-webkit-autofill,
 *  .has-value class (JS). placeholder=" " (single space) is emitted by field
 *  taglibs so :placeholder-shown fires reliably on all browsers + type=email.
 *  Autofill detection uses the animationstart trick — Chrome does not fire
 *  the input event on autofill, so a zero-duration @keyframe is applied to
 *  :-webkit-autofill and JS adds has-value when the animation fires.
 *
 * Mobile-first / cross-browser
 * ──────────────────────────────
 *  SIZING
 *  • box-sizing:border-box on the shell and 100% width prevent collapse in
 *    flex/grid parents on Android and Samsung Internet.
 *  • border-radius uses 0.5rem (not fixed px) so the shell corners scale with
 *    the user's browser font-size preference.
 *  • .glass-form-group margin-bottom uses 1.5rem (not 24px) for the same reason.
 *  • .glass-helper-text and .glass-error font-size stay at 0.75rem (relative).
 *
 *  INPUT BEHAVIOUR
 *  • font-size:1rem prevents iOS Safari auto-zoom on focus (requires >=16px).
 *  • -webkit-appearance:none strips iOS/Safari native input chrome.
 *  • -moz-appearance:none added (was missing) — Firefox full chrome reset.
 *  • touch-action:manipulation eliminates the 300ms double-tap delay.
 *  • -webkit-tap-highlight-color:transparent removes tap flash.
 *
 *  LAYOUT
 *  • @supports guards backdrop-filter — Firefox <103 gets solid fallback.
 *  • :hover gated behind @media (hover:hover) and (pointer:fine).
 *  • Clear button 44×44px mobile, 20×20px on pointer screens.
 *  • env(safe-area-inset-right) on clear button for notched devices.
 *  • All -webkit- / -ms- vendor prefixes present.
 *  • forced-color-adjust:none on shell and input for Windows High Contrast.
 *
 *  KEYFRAMES
 *  • Empty @keyframes bodies get a no-op `opacity:1` declaration so aggressive
 *    CSS minifiers (clean-css, PostCSS cssnano) do not strip the rules entirely,
 *    which would break the animationstart autofill-detection trick in Chrome.
 *
 *  JS COMPATIBILITY
 *  • Element.closest() is absent in IE 11. All usages replaced with a
 *    closestPolyfill() helper that walks parentElement up the DOM.
 *  • new Event('input', { bubbles: true }) is absent in IE 11.
 *    Replaced with a createCompatEvent() helper that falls back to
 *    document.createEvent / initEvent for IE 11.
 *  • All other JS is already IE-safe (index-based for loops, no arrow
 *    functions, no const/let, no spread, no optional chaining).
 *
 * Guard key is versioned (_v53). Bump the suffix whenever CSS or JS changes
 * so a running server emits the new block without needing a restart.
 */
class TextInputStylesTagLib {

  static namespace = "glass"

  static final String TEXT_INPUT_STYLES_EMITTED = 'glass_textInputStylesEmitted_v53'

  def textInputStyles = { attrs ->
    emitTextInputStylesOnce()
  }

  void emitTextInputStylesOnce() {
    if (pageScope[TEXT_INPUT_STYLES_EMITTED]) return
    pageScope[TEXT_INPUT_STYLES_EMITTED] = true

    out << '''
<style>
/* ═══════════════════════════════════════════════════════════════════════════
   Shell  —  glass background only; no borders at any state
   ═══════════════════════════════════════════════════════════════════════════ */
.input-glass {
    position               : relative;
    background             : transparent;
    /* rem unit — corners scale with the user's browser font-size preference  */
    border-radius          : 0.5rem;
    /* 100% width prevents flex/grid collapse on Android / Samsung Internet   */
    width                  : 100%;
    /* border-box so padding never bleeds outside the shell                   */
    -webkit-box-sizing     : border-box;
            box-sizing     : border-box;
    /* overflow:visible so the raised floating-label badge is never clipped   */
    overflow               : visible;
    /* accent-color kills OS/browser focus-ring colour derivation             */
    accent-color           : transparent;
    /* Default border-bottom — 2px solid white at 30% opacity                */
    border-bottom          : 2px solid rgba(255, 255, 255, 0.30);
    /* No box-shadow at any state                                             */
    -webkit-box-shadow     : none !important;
            box-shadow     : none !important;
}

/* Hover and active — no shadow */
.input-glass:hover,
.input-glass:focus,
.input-glass:active {
    -webkit-box-shadow : none !important;
            box-shadow : none !important;
}

@supports (backdrop-filter: blur(1px)) or (-webkit-backdrop-filter: blur(1px)) {
    .input-glass {
        -webkit-backdrop-filter: blur(10px);
                backdrop-filter: blur(10px);
    }
}

/* Focus / active / value-change — background brightens; borders + outer glow */
.input-glass:focus-within {
    background         : rgba(255, 255, 255, 0.25);
    border-top         : 1px solid rgba(255, 255, 255, 0.30) !important;
    border-left        : 1px solid rgba(255, 255, 255, 0.30) !important;
    border-right       : 1px solid rgba(255, 255, 255, 0.30) !important;
    border-bottom      : 2px solid rgba(255, 255, 255, 1.00) !important;
    /* Outer glow on all four sides — slightly transparent                    */
    -webkit-box-shadow : 0 -4px 10px 0px rgba(255, 255, 255, 0.15),
                         -4px 0  10px 0px rgba(255, 255, 255, 0.15),
                          4px 0  10px 0px rgba(255, 255, 255, 0.15),
                          0  4px 10px 0px rgba(255, 255, 255, 0.15) !important;
            box-shadow : 0 -4px 10px 0px rgba(255, 255, 255, 0.15),
                         -4px 0  10px 0px rgba(255, 255, 255, 0.15),
                          4px 0  10px 0px rgba(255, 255, 255, 0.15),
                          0  4px 10px 0px rgba(255, 255, 255, 0.15) !important;
}

@supports (backdrop-filter: blur(1px)) or (-webkit-backdrop-filter: blur(1px)) {
    .input-glass:focus-within {
        background: rgba(255, 255, 255, 0.25);
    }
}

/* Hover — pointer devices only; all four sides appear */
@media (hover: hover) and (pointer: fine) {
    .input-glass:hover {
        border-top    : 1px solid rgba(255, 255, 255, 0.30) !important;
        border-left   : 1px solid rgba(255, 255, 255, 0.30) !important;
        border-right  : 1px solid rgba(255, 255, 255, 0.30) !important;
        border-bottom : 2px solid rgba(255, 255, 255, 1.00) !important;
    }
}

/* Windows High Contrast mode — opt out of forced colour overrides */
@media (forced-colors: active) {
    .input-glass       { forced-color-adjust: none; }
    .input-glass input { forced-color-adjust: none; }
}

/* ═══════════════════════════════════════════════════════════════════════════
   Input  —  transparent, no border, no glow, every state and every class
   ───────────────────────────────────────────────────────────────────────────
   Overrides: Bootstrap 4/5 focus ring, Bootstrap 5 CSS custom properties,
   browser :focus/:focus-visible outline, Firefox :-moz-focusring,
   Bootstrap white background, UA stylesheet backgrounds and borders.
   ═══════════════════════════════════════════════════════════════════════════ */
.input-glass input,
.input-glass input:link,
.input-glass input:visited,
.input-glass input:hover,
.input-glass input:focus,
.input-glass input:active,
.input-glass input:focus-visible,
.input-glass input:focus-within,
.input-glass input:-moz-focusring,
.input-glass input.form-control,
.input-glass input.form-control:hover,
.input-glass input.form-control:focus,
.input-glass input.form-control:active,
.input-glass input.form-control:focus-visible,
.input-glass input.form-control:-moz-focusring {
    background                         : transparent !important;
    background-color                   : transparent !important;
    background-image                   : none        !important;
    border                             : none        !important;
    border-width                       : 0           !important;
    border-style                       : none        !important;
    border-color                       : transparent !important;
    border-bottom                      : none        !important;
    -webkit-box-shadow                 : none        !important;
            box-shadow                 : none        !important;
    outline                            : none        !important;
    outline-width                      : 0           !important;
    outline-color                      : transparent !important;
    outline-offset                     : 0           !important;
    accent-color                       : transparent;
    --bs-focus-ring-width              : 0;
    --bs-focus-ring-color              : transparent;
    --bs-form-control-focus-box-shadow : none;
    /* background-color 5000s suppresses any browser background flash        */
    -webkit-transition                 : color 0.25s ease,
                                         background-color 5000s ease-in-out 0s !important;
            transition                 : color 0.25s ease,
                                         background-color 5000s ease-in-out 0s !important;
}

/* Chrome's UA sets outline:auto for :focus-visible which triggers its native
   coloured platform ring. outline:0 (zero width) is the only reliable kill —
   outline:none does not override outline:auto in Chrome.                    */
.input-glass input:focus,
.input-glass input:active,
.input-glass input:focus-visible,
.input-glass input.form-control:focus,
.input-glass input.form-control:active,
.input-glass input.form-control:focus-visible {
    outline            : 0           !important;
    outline-width      : 0           !important;
    outline-style      : none        !important;
    outline-color      : transparent !important;
    outline-offset     : 0           !important;
    -webkit-box-shadow : none        !important;
            box-shadow : none        !important;
    accent-color       : transparent;
}

/* Structural rule — all reset properties repeated with !important here so
   there is no cascade path through which Bootstrap or a browser can restore
   a white background or glow on any state transition.                       */
.input-glass input {
    background                         : transparent !important;
    background-color                   : transparent !important;
    background-image                   : none        !important;
    -webkit-box-shadow                 : none        !important;
            box-shadow                 : none        !important;
    border                             : none        !important;
    border-width                       : 0           !important;
    border-style                       : none        !important;
    border-color                       : transparent !important;
    border-bottom                      : none        !important;
    outline                            : none        !important;
    outline-width                      : 0           !important;
    outline-color                      : transparent !important;
    outline-offset                     : 0           !important;
    accent-color                       : transparent;
    --bs-focus-ring-width              : 0;
    --bs-focus-ring-color              : transparent;
    --bs-form-control-focus-box-shadow : none;
    /* Structural properties                                                  */
    color              : #ffffff;
    /* Vertical padding creates floating-label lift space. Fixed px is
       intentional here — the floating label geometry is calibrated to these
       exact values (top:50% + translateY(-50% - 1.2rem)).                   */
    padding            : 20px 52px 8px 16px;
    /* 1rem (>=16px at browser default) prevents iOS Safari auto-zoom on focus */
    font-size          : 1rem;
    width              : 100%;
    -webkit-box-sizing : border-box;
            box-sizing : border-box;
    border-radius      : 0;
    /* -webkit-appearance strips native input chrome on iOS/Safari            */
    -webkit-appearance : none;
    /* -moz-appearance was missing — Firefox full chrome reset                */
       -moz-appearance : none;
            appearance : none;
    /* manipulation eliminates the 300ms double-tap delay on touch devices    */
    touch-action                       : manipulation;
    /* Suppress the blue tap flash on iOS Safari and Android Chrome           */
    -webkit-tap-highlight-color        : transparent;
    /* 5000s trick suppresses browser background-color flash (autofill, etc.) */
    -webkit-transition                 : color 0.25s ease,
                                         background-color 5000s ease-in-out 0s !important;
            transition                 : color 0.25s ease,
                                         background-color 5000s ease-in-out 0s !important;
}

/* Tighter right padding on pointer screens — clear button is 20px           */
@media (hover: hover) and (pointer: fine) {
    .input-glass input { padding-right: 40px; }
}

/* Placeholder invisible — floating label is the visual hint                 */
.input-glass input::placeholder {
    color  : transparent;
    opacity: 0;
}

/* ═══════════════════════════════════════════════════════════════════════════
   Number input — suppress spinners
   ═══════════════════════════════════════════════════════════════════════════ */
.input-glass input[type="number"]::-webkit-inner-spin-button,
.input-glass input[type="number"]::-webkit-outer-spin-button {
    -webkit-appearance: none;
    margin: 0;
}
.input-glass input[type="number"] {
    /* -moz-appearance suppresses Firefox number spinners                     */
    -moz-appearance : textfield;
         appearance : textfield;
}

/* ═══════════════════════════════════════════════════════════════════════════
   Autofill detection keyframes
   ───────────────────────────────────────────────────────────────────────────
   Chrome applies :-webkit-autofill but does NOT fire the input event.
   A zero-duration @keyframe on :-webkit-autofill fires animationstart which
   the JS block catches to add has-value — raising the floating label.

   The no-op `opacity:1` declarations inside each keyframe are intentional.
   Empty from{}/to{} bodies are silently stripped by CSS minifiers such as
   clean-css and PostCSS cssnano, which would break the animationstart trick
   entirely. A harmless but non-empty declaration prevents this stripping.
   ═══════════════════════════════════════════════════════════════════════════ */
@-webkit-keyframes glassAutofillStart  { from { opacity: 1; } to { opacity: 1; } }
@-webkit-keyframes glassAutofillCancel { from { opacity: 1; } to { opacity: 1; } }
        @keyframes glassAutofillStart  { from { opacity: 1; } to { opacity: 1; } }
        @keyframes glassAutofillCancel { from { opacity: 1; } to { opacity: 1; } }

.input-glass input:-webkit-autofill {
    -webkit-animation-name    : glassAutofillStart;
            animation-name    : glassAutofillStart;
    -webkit-animation-duration: 0.001s;
            animation-duration: 0.001s;
}
.input-glass input:not(:-webkit-autofill) {
    -webkit-animation-name    : glassAutofillCancel;
            animation-name    : glassAutofillCancel;
    -webkit-animation-duration: 0.001s;
            animation-duration: 0.001s;
}

/* ═══════════════════════════════════════════════════════════════════════════
   Autofill colour override (WebKit / Blink)
   ───────────────────────────────────────────────────────────────────────────
   Light inset mask rgba(255,255,255,0.12) covers the browser yellow/white
   autofill background without making the field appear dark.
   5000s transition delay suppresses the background-color flash.
   ═══════════════════════════════════════════════════════════════════════════ */
.input-glass input:-webkit-autofill,
.input-glass input:-webkit-autofill:link,
.input-glass input:-webkit-autofill:visited,
.input-glass input:-webkit-autofill:hover,
.input-glass input:-webkit-autofill:focus,
.input-glass input:-webkit-autofill:active {
    -webkit-text-fill-color : #ffffff                                        !important;
    -webkit-box-shadow      : 0 0 0 1000px rgba(255, 255, 255, 0.12) inset  !important;
            box-shadow      : 0 0 0 1000px rgba(255, 255, 255, 0.12) inset  !important;
    -webkit-transition      : background-color 5000s ease-in-out 0s         !important;
            transition      : background-color 5000s ease-in-out 0s         !important;
}

/* ═══════════════════════════════════════════════════════════════════════════
   Floating label — transform-only smooth animation
   ───────────────────────────────────────────────────────────────────────────
   Strategy: `top` is FIXED at 50% always. Only `transform` animates.
   This gives pure GPU-composited animation (no layout reflow, no jank).

   Default (blur + empty):
     translateY(-50%) scale(1)  — label's centre sits at shell midpoint.

   Raised (focus / value / autofill):
     translateY(calc(-50% - 1.2rem)) scale(0.70)
     — label's centre sits on the shell's top border line.

   transform-origin: left center — scale shrinks from the left edge.
   All raised-state properties use !important to defeat Bootstrap label resets.
   ═══════════════════════════════════════════════════════════════════════════ */

/* Prevent clipping of raised label */
.glass-form-group,
.input-glass {
    overflow: visible !important;
}

/* ── DEFAULT / BLUR state ──────────────────────────────────────────────────
   top:50% + translateY(-50%) centres label vertically inside the shell.
   Background transparent in default state so the instantaneous top reset
   during raise is never visible.                                             */
.input-glass .floating-label {
    position                  : absolute;
    left                      : 16px !important;
    top                       : 50% !important;
    font-size                 : 1rem !important;
    -webkit-transform         : translateY(-50%) scale(1) !important;
            transform         : translateY(-50%) scale(1) !important;
    -webkit-transform-origin  : left center;
            transform-origin  : left center;
    opacity                   : 1 !important;
    color                     : rgba(255, 255, 255, 0.90) !important;
    pointer-events            : none;
    background                : transparent !important;
    -webkit-box-shadow        : none !important;
            box-shadow        : none !important;
    padding                   : 0 4px !important;
    z-index                   : 3;
    white-space               : nowrap;
    overflow                  : hidden;
    text-overflow             : ellipsis;
    max-width                 : calc(100% - 60px);
    will-change               : transform;
    /* Animate transform, color, background, padding, glow — not top/font-size */
    -webkit-transition        : -webkit-transform   0.22s cubic-bezier(0.25, 0.46, 0.45, 0.94),
                                color              0.18s ease,
                                background         0.18s ease,
                                padding            0.18s ease,
                                -webkit-box-shadow 0.18s ease !important;
            transition        : transform           0.22s cubic-bezier(0.25, 0.46, 0.45, 0.94),
                                color              0.18s ease,
                                background         0.18s ease,
                                padding            0.18s ease,
                                box-shadow         0.18s ease !important;
}

/* ── RAISED state — focus / active / autofill / has-value ─────────────────  */
.input-glass:focus-within .floating-label,
.input-glass input:not(:placeholder-shown) ~ .floating-label,
.input-glass input:-webkit-autofill ~ .floating-label,
.input-glass.has-value .floating-label {
    -webkit-transform         : translateY(calc(-50% - 1.2rem)) scale(0.70) !important;
            transform         : translateY(calc(-50% - 1.2rem)) scale(0.70) !important;
    color                     : #ffffff !important;
    background                : rgba(40, 44, 70, 0.85) !important;
    padding                   : 2px 6px !important;
    border-radius             : 12px !important;
    font-weight               : 500 !important;
    letter-spacing            : 0.12em !important;
    -webkit-box-shadow        : 0 0 12px 3px rgba(255, 255, 255, 0.35),
                                0 0  4px 1px rgba(255, 255, 255, 0.55) !important;
            box-shadow        : 0 0 12px 3px rgba(255, 255, 255, 0.35),
                                0 0  4px 1px rgba(255, 255, 255, 0.55) !important;
}

@supports (backdrop-filter: blur(1px)) or (-webkit-backdrop-filter: blur(1px)) {
    .input-glass:focus-within .floating-label,
    .input-glass input:not(:placeholder-shown) ~ .floating-label,
    .input-glass input:-webkit-autofill ~ .floating-label,
    .input-glass.has-value .floating-label {
        background             : rgba(255, 255, 255, 0.15) !important;
        -webkit-backdrop-filter: blur(10px);
                backdrop-filter: blur(10px);
    }
}

/* ═══════════════════════════════════════════════════════════════════════════
   Clear button  —  44×44 px touch target on mobile (WCAG 2.5.5 / Apple HIG)
                    20×20 px on pointer (mouse) screens
   ═══════════════════════════════════════════════════════════════════════════ */
.input-glass .clear-btn {
    position                    : absolute;
    /* Cascade two `right` declarations: first as a safe fallback for browsers
       that do not support env(), then with the safe-area inset for notched
       devices (iPhone X+, modern Android). The second declaration wins in
       supporting browsers and is ignored in non-supporting ones.            */
    right                       : 8px;
    right                       : calc(8px + env(safe-area-inset-right, 0px));
    top                         : 50%;
    -webkit-transform           : translateY(-50%);
            transform           : translateY(-50%);
    background                  : rgba(255, 255, 255, 0.30);
    border                      : none;
    /* 44×44 px — WCAG 2.5.5 / Apple HIG minimum touch target               */
    width                       : 44px;
    height                      : 44px;
    border-radius               : 50%;
    cursor                      : pointer;
    display                     : none;
    /* Cross-browser flexbox for icon centering                              */
    -webkit-box-align           : center;
        -ms-flex-align          : center;
            align-items         : center;
    -webkit-box-pack            : center;
        -ms-flex-pack           : center;
            justify-content     : center;
    font-size                   : 18px;
    color                       : #2d3748;
    -webkit-transition          : background 0.2s ease, -webkit-transform 0.2s ease;
            transition          : background 0.2s ease, transform 0.2s ease;
    padding                     : 0;
    line-height                 : 1;
    z-index                     : 2;
    /* Eliminate 300ms double-tap delay and suppress tap flash               */
    touch-action                : manipulation;
    -webkit-tap-highlight-color : transparent;
}

.input-glass .clear-btn:active {
    -webkit-transform : translateY(-50%) scale(0.92);
            transform : translateY(-50%) scale(0.92);
}

/* Show the button when the field has a value                                */
.input-glass.has-value .clear-btn {
    display: -webkit-box;
    display: -ms-flexbox;
    display: flex;
}

/* Pointer screens — smaller visual button, same accessible region           */
@media (hover: hover) and (pointer: fine) {
    .input-glass .clear-btn {
        width     : 20px;
        height    : 20px;
        right     : 12px;
        font-size : 14px;
    }
    .input-glass .clear-btn:hover {
        background        : rgba(255, 255, 255, 0.50);
        -webkit-transform : translateY(-50%) scale(1.10);
                transform : translateY(-50%) scale(1.10);
    }
}

/* ═══════════════════════════════════════════════════════════════════════════
   Validation icon
   Mobile : 44px button at right:8px  → icon at right:60px  (8+44+8 gap)
   Desktop: 20px button at right:12px → icon at right:40px  (12+20+8 gap)
   ═══════════════════════════════════════════════════════════════════════════ */
.input-glass .validation-icon {
    position          : absolute;
    right             : 60px;
    top               : 50%;
    -webkit-transform : translateY(-50%);
            transform : translateY(-50%);
    font-size         : 16px;
    display           : none;
    z-index           : 1;
    pointer-events    : none;
}

@media (hover: hover) and (pointer: fine) {
    .input-glass .validation-icon { right: 40px; }
}

.input-glass.has-value .validation-icon { display: block; }
.input-glass .validation-icon.valid     { color: #48bb78; }
.input-glass .validation-icon.invalid   { color: #f56565; }

/* ═══════════════════════════════════════════════════════════════════════════
   Supporting elements
   ═══════════════════════════════════════════════════════════════════════════ */
.glass-helper-text {
    color      : rgba(255, 255, 255, 0.90);
    font-size  : 0.75rem;
    margin-top : 6px;
}

.glass-error {
    color      : #fca5a5;
    font-size  : 0.75rem;
    margin-top : 6px;
    display    : none;
}

.glass-error.show { display: block; }

/* rem unit so spacing scales with the user's browser font-size preference   */
.glass-form-group { margin-bottom: 1.5rem; }

.glass-form-group.has-floating-label > label { display: none; }

/* ═══════════════════════════════════════════════════════════════════════════
   Disabled state
   ═══════════════════════════════════════════════════════════════════════════ */
.input-glass.is-disabled {
    pointer-events : none;
    cursor         : not-allowed;
    opacity        : 0.60;
}

.input-glass.is-disabled input,
.input-glass.is-disabled input.form-control {
    cursor     : not-allowed;
    font-style : italic;
}

.input-glass.is-disabled .floating-label { opacity: 0.60; }

.input-glass.is-disabled .clear-btn { display: none !important; }
</style>
<script>
    if (typeof window.glassInputInitialized === 'undefined') {
        window.glassInputInitialized = true;

        /*
         * closestPolyfill(el, selector)
         * ─────────────────────────────
         * Element.closest() is not available in IE 11.
         * This helper walks parentElement up the DOM and tests each node
         * with matches() (with the -ms-matchesSelector prefix for IE 11).
         * All other JS in this block avoids arrow functions, const, let,
         * spread, and optional chaining for the same IE 11 reason.
         */
        function closestPolyfill(el, selector) {
            var matchFn = el.matches
                       || el.webkitMatchesSelector
                       || el.msMatchesSelector;
            var node = el;
            while (node && node.nodeType === 1) {
                if (matchFn.call(node, selector)) return node;
                node = node.parentElement;
            }
            return null;
        }

        /*
         * createCompatEvent(type, bubbles)
         * ─────────────────────────────────
         * new Event(type, { bubbles: true }) is not available in IE 11.
         * Falls back to the IE-compatible document.createEvent / initEvent API.
         */
        function createCompatEvent(type, bubbles) {
            var evt;
            try {
                evt = new Event(type, { bubbles: !!bubbles });
            } catch (e) {
                /* IE 11 path */
                evt = document.createEvent('Event');
                evt.initEvent(type, !!bubbles, true);
            }
            return evt;
        }

        window.clearInput = function(button) {
            var container = closestPolyfill(button, '.input-glass');
            if (!container) return;
            var input = container.querySelector('input');
            if (!input) return;
            input.value = '';
            input.focus();
            toggleClearButton(input);
            input.dispatchEvent(createCompatEvent('input', true));
        };

        window.toggleClearButton = function(input) {
            var container = closestPolyfill(input, '.input-glass');
            if (!container) return;
            if (input.value) {
                container.classList.add('has-value');
            } else {
                container.classList.remove('has-value');
            }
        };

        /*
         * Autofill detection via animationstart trick.
         * Chrome does not fire the input event on autofill, so
         * toggleClearButton never runs. A zero-duration @keyframe on
         * :-webkit-autofill fires animationstart which we catch here
         * to add/remove has-value — raising or lowering the floating label.
         *
         * The listener is registered in capture phase (true) because
         * animationstart does not bubble reliably in all browsers.
         */
        window.glassHandleAutofill = function(e) {
            if (e.animationName === 'glassAutofillStart') {
                var container = closestPolyfill(e.target, '.input-glass');
                if (container) container.classList.add('has-value');
            }
            if (e.animationName === 'glassAutofillCancel') {
                var container = closestPolyfill(e.target, '.input-glass');
                if (container && !e.target.value) {
                    container.classList.remove('has-value');
                }
            }
        };

        document.addEventListener('animationstart', window.glassHandleAutofill, true);

        document.addEventListener('DOMContentLoaded', function() {
            var inputs = document.querySelectorAll('.input-glass input');
            for (var i = 0; i < inputs.length; i++) {
                toggleClearButton(inputs[i]);
            }
        });
    }
</script>
'''
  }
}
