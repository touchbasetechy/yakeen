package com.murad.app.matrimony.textinput

/**
 * Tag library for rendering a glassmorphism-styled numeric input field
 * that accepts only positive numbers starting from 1.
 *
 * Usage:
 *   <glass:tooltipStyles />          ← emit once in your layout <head>
 *   <glass:positiveNumericField name="qty" value="${qty}" label="Quantity"
 *                               helperText="Enter a positive number" />
 *
 * Features
 * --------
 *  • Accepts only positive integers (≥ 1)
 *  • Floating label via CSS :placeholder-shown trick
 *  • Clear button – NOT rendered when disabled=true
 *  • Disabled state: frosted-glass + hatched overlay on the shell;
 *    muted italic text on the input
 *  • Tooltip on the clear button:
 *    – Rendered as a real <span class="glass-tooltip"> INSIDE the button
 *    – On hover/focus the tooltip is cloned to <body> so it escapes every
 *      backdrop-filter stacking context (fixes blur bleed from sibling fields)
 *    – Placement: top | right | bottom | left | auto (default auto)
 *      "auto" resolves dynamically:
 *        1. Coarse placement from button's viewport quadrant
 *        2. Bubble overflow check — flips to opposite side if bubble clips viewport
 *        3. Re-evaluated on resize, scroll, mouseenter, and focusin
 *
 * CSS responsibility
 * ──────────────────
 *  • Tooltip tokens + portal rules  → GlassTooltipStylesTagLib
 *    (emitted via emitTooltipStylesOnce(), guarded by TOOLTIP_STYLES_EMITTED)
 *  • Input shell, disabled state, clear button shape, floating label,
 *    helper text                    → this taglib
 *    (emitted via emitSharedAssetsOnce(), guarded by STYLES_EMITTED)
 *
 * Note: glass:tooltipStyles is defined in GlassTooltipStylesTagLib.
 *       This taglib no longer re-declares it — use <glass:tooltipStyles />
 *       from GlassTooltipStylesTagLib directly in your layout <head>.
 *
 * Dependencies
 * ─────────────
 *  • Font Awesome 6 (fa-solid fa-xmark)
 *  • A backdrop-filter–capable browser for the glass blur effect
 *
 * Fix log
 * ───────
 *  v2 – Fixed all Groovy string-escaping bugs that produced malformed HTML
 *       attributes and broken inline JS:
 *
 *  1. onkeydown attribute: was built with Groovy single-quoted strings
 *     containing \" sequences.  In Groovy single-quoted strings \" is NOT
 *     an escape — it emits a literal backslash+quote, which terminates the
 *     HTML attribute early and leaves dangling JS tokens → "Unexpected end
 *     of input".  Fixed by using Groovy double-quoted strings and single
 *     quotes inside the JS comparison expressions.
 *
 *  2. oninput handler: contained " inside the attribute value
 *     (replace(/[^0-9]/g,"") / this.value="1" / ==="function") which
 *     prematurely closed the oninput="…" HTML attribute.  Fixed by
 *     switching all inner JS string delimiters to single quotes.
 *
 *  3. clear-btn onclick attribute: 'clearInput(this)' was fine but is now
 *     written consistently as a double-quoted Groovy string for clarity.
 *
 *  4. data-tooltip-text and aria-label attributes: were already using
 *     .encodeAsHTML() correctly — no change needed.
 *
 *  5. Inline IIFE <script> blocks: _portal.textContent assignment used
 *     unescaped single quotes inside a Groovy triple-quoted string that is
 *     itself wrapped in a GString — safe because textContent is a property
 *     assignment, not an HTML attribute, but verified no further breakage.
 */
class PositiveNumericFieldTagLib {

  static namespace = "glass"

  private static final String STYLES_EMITTED = 'glass_stylesEmitted'

  // ─────────────────────────────────────────────────────────────────────────
  // glass:positiveNumericField
  // ─────────────────────────────────────────────────────────────────────────
  /**
   * @attr name               REQUIRED – name attribute for the input
   * @attr value              – initial value
   * @attr id                 – id attribute (defaults to name)
   * @attr label              – label / floating-label text
   * @attr placeholder        – placeholder (ignored when floatingLabel=true)
   * @attr helperText         – small hint shown below the field
   * @attr required           – true/false (default false)
   * @attr min                – minimum value (default 1)
   * @attr max                – maximum value
   * @attr step               – step increment (default 1)
   * @attr class              – extra CSS classes on <input>
   * @attr disabled           – true/false (default false)
   *                            When true: clear button NOT rendered,
   *                            shell gets frosted disabled treatment.
   * @attr readonly           – true/false (default false)
   * @attr autofocus          – true/false (default false)
   * @attr onchange           – JS onChange handler
   * @attr oninput            – JS onInput handler (appended after built-ins)
   * @attr wrapper            – wrap in .glass-form-group (default true)
   * @attr floatingLabel      – use floating label (default true)
   * @attr tooltipPlacement   – top | right | bottom | left | auto (default auto)
   * @attr tooltip            – text shown inside the tooltip bubble (default "Clear")
   */
  def positiveNumericField = { attrs ->
    if (!attrs.name) {
      throwTagError("Tag [positiveNumericField] is missing required attribute [name]")
    }

    // ── Attribute extraction ────────────────────────────────────────────────
    String name = attrs.name
    String id = attrs.id ?: name
    String value = attrs.value ?: ''
    String label = attrs.label ?: ''
    String placeholder = attrs.placeholder ?: ''
    String helperText = attrs.helperText ?: ''
    String min = attrs.min ?: '1'
    String max = attrs.max ?: ''
    String step = attrs.step ?: '1'
    String additionalClasses = attrs.class ?: ''
    boolean required = attrs.required?.toString()?.toBoolean() ?: false
    boolean disabled = attrs.disabled?.toString()?.toBoolean() ?: false
    boolean readonly = attrs.readonly?.toString()?.toBoolean() ?: false
    boolean autofocus = attrs.autofocus?.toString()?.toBoolean() ?: false
    boolean wrapper = attrs.wrapper?.toString()?.toBoolean() != false
    boolean floatingLabel = attrs.floatingLabel?.toString()?.toBoolean() != false
    String onchange = attrs.onchange ?: ''
    String oninput = attrs.oninput ?: ''

    // ── Tooltip placement ───────────────────────────────────────────────────
    List validPlacements = ['top', 'right', 'bottom', 'left']
    String rawPlacement = attrs.tooltipPlacement?.toString()?.toLowerCase() ?: 'auto'
    boolean autoPlacement = !validPlacements.contains(rawPlacement)
    String tooltipPlacement = autoPlacement ? 'auto' : rawPlacement
    String tooltipText = attrs.tooltip ?: 'Clear'

    emitTooltipStylesOnce()
    emitSharedAssetsOnce()

    // ── Outer wrapper ───────────────────────────────────────────────────────
    if (wrapper) {
      String wrapperClass = 'glass-form-group'
      if (floatingLabel && label) wrapperClass += ' has-floating-label'
      out << "<div class=\"${wrapperClass}\">"
      if (label && !floatingLabel) {
        out << "<label for=\"${id.encodeAsHTML()}\">${label.encodeAsHTML()}</label>"
      }
    }

    // ── .input-glass shell ──────────────────────────────────────────────────
    String shellClass = disabled ? 'input-glass is-disabled' : 'input-glass'
    out << "<div class=\"${shellClass}\">"

    // ── <input> ─────────────────────────────────────────────────────────────
    out << '<input '
    out << 'type="number" '
    out << "name=\"${name.encodeAsHTML()}\" "
    out << "id=\"${id.encodeAsHTML()}\" "
    if (value) out << "value=\"${value.encodeAsHTML()}\" "
    if (floatingLabel && label) {
      out << 'placeholder=" " '   // single space so :placeholder-shown fires
    } else if (placeholder) {
      out << "placeholder=\"${placeholder.encodeAsHTML()}\" "
    }
    out << "min=\"${min}\" "
    if (max) out << "max=\"${max}\" "
    out << "step=\"${step}\" "

    String inputClasses = disabled
      ? "form-control glass-disabled ${additionalClasses}".trim()
      : "form-control ${additionalClasses}".trim()
    out << "class=\"${inputClasses.encodeAsHTML()}\" "

    if (required) out << 'required '
    if (disabled) out << 'disabled '
    if (readonly) out << 'readonly '
    if (autofocus) out << 'autofocus '

    // ── oninput handler ─────────────────────────────────────────────────────
    // FIX #1 & #2: Use single quotes for all JS string literals so they are
    // safe inside the HTML attribute value (which is delimited by double quotes).
    // Previously used double quotes inside the JS which broke the HTML parser.
    String onInputHandler =
      "this.value=this.value.replace(/[^0-9]/g,'');" +
        "if(this.value&&parseInt(this.value)<1)this.value='1';" +
        "if(typeof toggleClearButton==='function')toggleClearButton(this);"
    if (oninput) onInputHandler += " ${oninput}"
    out << "oninput=\"${onInputHandler.encodeAsHTML()}\" "

    if (onchange) out << "onchange=\"${onchange.encodeAsHTML()}\" "

    // ── onkeydown handler ───────────────────────────────────────────────────
    // FIX #3: Was built with Groovy single-quoted strings containing \"
    // which is NOT an escape sequence in Groovy — it emits a literal backslash
    // before the quote, corrupting the HTML attribute and breaking the JS parser.
    // Now uses Groovy double-quoted strings with single quotes inside the JS.
    out << "onkeydown=\"return (event.key&gt;='0'&amp;&amp;event.key&lt;='9')||" +
      "['Backspace','Delete','Tab','ArrowLeft','ArrowRight'," +
      "'Home','End'].includes(event.key)\" "

    out << '/>'

    // ── Clear button ──────────────────────────────────────────────────────────
    // Not rendered when disabled=true.
    if (!disabled) {
      String escapedId = id.encodeAsHTML()
      String placementAttr = autoPlacement ? '' : " data-tooltip-placement=\"${tooltipPlacement}\""

      // FIX #4: onclick value uses single quotes inside the attribute — safe.
      out << "<button type=\"button\" class=\"clear-btn\"${placementAttr} "
      out << 'aria-label="Clear" '
      out << "data-tooltip-text=\"${tooltipText.encodeAsHTML()}\" "
      out << "onclick=\"clearInput(this)\">"
      out << '<i class="fa-solid fa-xmark" aria-hidden="true"></i>'
      // Source span — always hidden; portal clone on <body> is what the user sees.
      out << "<span class=\"glass-tooltip\" aria-hidden=\"true\">${tooltipText.encodeAsHTML()}</span>"
      out << '</button>'

      // ── Portal tooltip IIFE ─────────────────────────────────────────────
      if (autoPlacement) {
        // FIX #5: All JS string delimiters inside the IIFE that previously
        // used double quotes are now single quotes so there is no conflict
        // with the surrounding Groovy GString interpolation or HTML context.
        out << """<script>
(function(){
  var OPPOSITE = { top:'bottom', bottom:'top', left:'right', right:'left' };

  function getBtn(){
    var inp = document.getElementById('${escapedId}');
    return inp ? inp.parentElement.querySelector('.clear-btn') : null;
  }

  function coarsePlacement(btn){
    var r  = btn.getBoundingClientRect();
    var vw = window.innerWidth  || document.documentElement.clientWidth;
    var vh = window.innerHeight || document.documentElement.clientHeight;
    var cx = r.left + r.width  / 2;
    var cy = r.top  + r.height / 2;
    if      (cy < vh * 0.25) return 'bottom';
    else if (cy > vh * 0.75) return 'top';
    else if (cx < vw * 0.5)  return 'right';
    else                     return 'left';
  }

  function portalCoords(btn, placement, portal){
    var br  = btn.getBoundingClientRect();
    var pr  = portal.getBoundingClientRect();
    var gap = parseInt(getComputedStyle(document.documentElement)
                .getPropertyValue('--tt-gap')) || 8;
    var scrollX = window.scrollX || window.pageXOffset || 0;
    var scrollY = window.scrollY || window.pageYOffset || 0;
    var top, left;
    if (placement === 'top') {
      top  = br.top  + scrollY - pr.height - gap;
      left = br.left + scrollX + (br.width  - pr.width)  / 2;
    } else if (placement === 'bottom') {
      top  = br.bottom + scrollY + gap;
      left = br.left   + scrollX + (br.width  - pr.width) / 2;
    } else if (placement === 'right') {
      top  = br.top  + scrollY + (br.height - pr.height) / 2;
      left = br.right + scrollX + gap;
    } else {
      top  = br.top   + scrollY + (br.height - pr.height) / 2;
      left = br.left  + scrollX - pr.width - gap;
    }
    return { top: top, left: left };
  }

  function resolvedPlacement(btn, portal){
    var candidate = coarsePlacement(btn);
    portal.setAttribute('data-tooltip-placement', candidate);
    portal.style.visibility = 'hidden';
    portal.style.opacity    = '0';
    portal.style.display    = 'block';
    var coords = portalCoords(btn, candidate, portal);
    portal.style.top  = coords.top  + 'px';
    portal.style.left = coords.left + 'px';
    var br = portal.getBoundingClientRect();
    var vw = window.innerWidth  || document.documentElement.clientWidth;
    var vh = window.innerHeight || document.documentElement.clientHeight;
    var clips = (candidate === 'top'    && br.top    < 0)
             || (candidate === 'bottom' && br.bottom > vh)
             || (candidate === 'left'   && br.left   < 0)
             || (candidate === 'right'  && br.right  > vw);
    portal.style.visibility = '';
    portal.style.display    = '';
    return clips ? OPPOSITE[candidate] : candidate;
  }

  var _portal = null;

  function getOrCreatePortal(btn){
    if (!_portal) {
      _portal = document.createElement('span');
      _portal.className    = 'glass-tooltip glass-tooltip-portal';
      _portal.setAttribute('aria-hidden', 'true');
      _portal.style.position = 'absolute';
      _portal.style.margin   = '0';
      document.body.appendChild(_portal);
    }
    _portal.textContent = btn.dataset.tooltipText || 'Clear';
    return _portal;
  }

  function showPortal(btn){
    var portal    = getOrCreatePortal(btn);
    var placement = resolvedPlacement(btn, portal);
    var coords    = portalCoords(btn, placement, portal);
    portal.setAttribute('data-tooltip-placement', placement);
    portal.style.top       = coords.top  + 'px';
    portal.style.left      = coords.left + 'px';
    portal.style.opacity   = '1';
    portal.style.transform = 'scale(1)';
    btn._glassPortal = portal;
  }

  function hidePortal(btn){
    if (btn._glassPortal) {
      btn._glassPortal.style.opacity   = '0';
      btn._glassPortal.style.transform = 'scale(0.88)';
    }
  }

  function updatePortalPosition(){
    var btn = getBtn();
    if (!btn || !btn._glassPortal) return;
    var placement = btn._glassPortal.getAttribute('data-tooltip-placement') || coarsePlacement(btn);
    var coords    = portalCoords(btn, placement, btn._glassPortal);
    btn._glassPortal.style.top  = coords.top  + 'px';
    btn._glassPortal.style.left = coords.left + 'px';
  }

  function bindBtn(){
    var btn = getBtn();
    if (!btn) return;
    btn.addEventListener('mouseenter', function(){ showPortal(btn); }, { passive: true });
    btn.addEventListener('focusin',    function(){ showPortal(btn); }, { passive: true });
    btn.addEventListener('mouseleave', function(){ hidePortal(btn); }, { passive: true });
    btn.addEventListener('focusout',   function(){ hidePortal(btn); }, { passive: true });
  }

  window.addEventListener('resize', updatePortalPosition, { passive: true });
  window.addEventListener('scroll', updatePortalPosition, { passive: true, capture: true });

  if (document.readyState === 'loading'){
    document.addEventListener('DOMContentLoaded', bindBtn);
  } else {
    bindBtn();
  }
})();
</script>"""
      } else {
        // Fixed placement — still portal to escape backdrop-filter; skip overflow check.
        String fixedPlacement = tooltipPlacement
        out << """<script>
(function(){
  var PLACEMENT = '${fixedPlacement}';

  function getBtn(){
    var inp = document.getElementById('${escapedId}');
    return inp ? inp.parentElement.querySelector('.clear-btn') : null;
  }

  function portalCoords(btn, placement, portal){
    var br  = btn.getBoundingClientRect();
    var pr  = portal.getBoundingClientRect();
    var gap = parseInt(getComputedStyle(document.documentElement)
                .getPropertyValue('--tt-gap')) || 8;
    var scrollX = window.scrollX || window.pageXOffset || 0;
    var scrollY = window.scrollY || window.pageYOffset || 0;
    var top, left;
    if (placement === 'top') {
      top  = br.top  + scrollY - pr.height - gap;
      left = br.left + scrollX + (br.width  - pr.width)  / 2;
    } else if (placement === 'bottom') {
      top  = br.bottom + scrollY + gap;
      left = br.left   + scrollX + (br.width  - pr.width) / 2;
    } else if (placement === 'right') {
      top  = br.top  + scrollY + (br.height - pr.height) / 2;
      left = br.right + scrollX + gap;
    } else {
      top  = br.top   + scrollY + (br.height - pr.height) / 2;
      left = br.left  + scrollX - pr.width - gap;
    }
    return { top: top, left: left };
  }

  var _portal = null;

  function getOrCreatePortal(btn){
    if (!_portal) {
      _portal = document.createElement('span');
      _portal.className    = 'glass-tooltip glass-tooltip-portal';
      _portal.setAttribute('aria-hidden', 'true');
      _portal.setAttribute('data-tooltip-placement', PLACEMENT);
      _portal.style.position = 'absolute';
      _portal.style.margin   = '0';
      document.body.appendChild(_portal);
    }
    _portal.textContent = btn.dataset.tooltipText || 'Clear';
    return _portal;
  }

  function showPortal(btn){
    var portal = getOrCreatePortal(btn);
    portal.style.opacity   = '0';
    portal.style.transform = 'scale(0.88)';
    portal.style.display   = 'block';
    var coords = portalCoords(btn, PLACEMENT, portal);
    portal.style.top       = coords.top  + 'px';
    portal.style.left      = coords.left + 'px';
    portal.style.opacity   = '1';
    portal.style.transform = 'scale(1)';
    btn._glassPortal = portal;
  }

  function hidePortal(btn){
    if (btn._glassPortal) {
      btn._glassPortal.style.opacity   = '0';
      btn._glassPortal.style.transform = 'scale(0.88)';
    }
  }

  function updatePortalPosition(){
    var btn = getBtn();
    if (!btn || !btn._glassPortal) return;
    var coords = portalCoords(btn, PLACEMENT, btn._glassPortal);
    btn._glassPortal.style.top  = coords.top  + 'px';
    btn._glassPortal.style.left = coords.left + 'px';
  }

  function bindBtn(){
    var btn = getBtn();
    if (!btn) return;
    btn.addEventListener('mouseenter', function(){ showPortal(btn); }, { passive: true });
    btn.addEventListener('focusin',    function(){ showPortal(btn); }, { passive: true });
    btn.addEventListener('mouseleave', function(){ hidePortal(btn); }, { passive: true });
    btn.addEventListener('focusout',   function(){ hidePortal(btn); }, { passive: true });
  }

  window.addEventListener('resize', updatePortalPosition, { passive: true });
  window.addEventListener('scroll', updatePortalPosition, { passive: true, capture: true });

  if (document.readyState === 'loading'){
    document.addEventListener('DOMContentLoaded', bindBtn);
  } else {
    bindBtn();
  }
})();
</script>"""
      }
    } // end !disabled

    // ── Floating label ────────────────────────────────────────────────────────
    if (floatingLabel && label) {
      out << "<label for=\"${id.encodeAsHTML()}\" class=\"floating-label\">${label.encodeAsHTML()}</label>"
    }

    out << '</div>' // .input-glass

    if (helperText) {
      out << "<div class=\"glass-helper-text\">${helperText.encodeAsHTML()}</div>"
    }

    if (wrapper) {
      out << '</div>' // .glass-form-group
    }
  }

  // ─────────────────────────────────────────────────────────────────────────
  // Private: emit tooltip CSS once per page
  // ─────────────────────────────────────────────────────────────────────────
  private void emitTooltipStylesOnce() {
    tooltipStyles([:])
  }

  // ─────────────────────────────────────────────────────────────────────────
  // Private: emit input-shell CSS once per page
  // ─────────────────────────────────────────────────────────────────────────
  private void emitSharedAssetsOnce() {
    if (pageScope[STYLES_EMITTED]) return
    pageScope[STYLES_EMITTED] = true

    out << '''
<style>
/* ═══════════════════════════════════════════════════════════════════════════
   Glass input design tokens
   ═══════════════════════════════════════════════════════════════════════════ */
:root {
  --glass-background          : rgba(255, 255, 255, 0.10);
  --glass-border      : rgba(255, 255, 255, 0.25);
  --glass-shadow      : 0 4px 24px rgba(0, 0, 0, 0.18);
  --glass-blur        : blur(14px);
  --glass-radius      : 12px;
  --glass-text        : #f0f4ff;
  --glass-placeholder : rgba(200, 215, 255, 0.45);
  --glass-accent      : #7eb8f7;
  --glass-focus-ring  : rgba(126, 184, 247, 0.40);
  --glass-helper      : rgba(200, 215, 255, 0.55);

  --glass-dis-background      : rgba(120, 130, 160, 0.07);
  --glass-dis-border  : rgba(180, 190, 220, 0.12);
  --glass-dis-text    : rgba(180, 190, 220, 0.38);
  --glass-dis-hatch   : repeating-linear-gradient(
                          135deg,
                          transparent 0px, transparent 4px,
                          rgba(255,255,255,0.025) 4px, rgba(255,255,255,0.025) 5px
                        );
}

/* ═══════════════════════════════════════════════════════════════════════════
   Layout
   ═══════════════════════════════════════════════════════════════════════════ */
.glass-form-group {
  position       : relative;
  display        : flex;
  flex-direction : column;
  gap            : 4px;
}

/* ═══════════════════════════════════════════════════════════════════════════
   Input shell
   ═══════════════════════════════════════════════════════════════════════════ */
.input-glass {
  position               : relative;
  display                : flex;
  align-items            : center;
  background             : var(--glass-background);
  border                 : 1px solid var(--glass-border);
  border-radius          : var(--glass-radius);
  backdrop-filter        : var(--glass-blur);
  -webkit-backdrop-filter: var(--glass-blur);
  box-shadow             : var(--glass-shadow);
  transition             : border-color 200ms ease, box-shadow 200ms ease;
}
.input-glass:focus-within {
  border-color : var(--glass-accent);
  box-shadow   : var(--glass-shadow), 0 0 0 3px var(--glass-focus-ring);
}

.input-glass input.form-control {
  flex            : 1;
  background      : transparent;
  border          : none;
  outline         : none;
  color           : var(--glass-text);
  font-size       : 0.9375rem;
  padding         : 0.85rem 2.6rem 0.85rem 1rem;
  border-radius   : var(--glass-radius);
  -moz-appearance : textfield;
  appearance      : textfield;
  transition      : color 200ms ease, opacity 200ms ease;
}
.input-glass input.form-control::-webkit-outer-spin-button,
.input-glass input.form-control::-webkit-inner-spin-button {
  -webkit-appearance: none;
  margin: 0;
}
.input-glass input.form-control::placeholder {
  color: var(--glass-placeholder);
}

/* ═══════════════════════════════════════════════════════════════════════════
   Disabled state
   ═══════════════════════════════════════════════════════════════════════════ */
.input-glass.is-disabled {
  background           : var(--glass-dis-background);
  background-image     : var(--glass-dis-hatch);
  border-color         : var(--glass-dis-border);
  box-shadow           : none;
  pointer-events       : none;
  cursor               : not-allowed;
}
.input-glass.is-disabled input.form-control,
.input-glass.is-disabled input.form-control.glass-disabled {
  color      : var(--glass-dis-text);
  opacity    : 1;
  font-style : italic;
  cursor     : not-allowed;
}
.input-glass.is-disabled .floating-label {
  color: var(--glass-dis-text) !important;
}
.input-glass.is-disabled .clear-btn {
  display: none !important;
}

/* ═══════════════════════════════════════════════════════════════════════════
   Clear button
   ═══════════════════════════════════════════════════════════════════════════ */
.clear-btn {
  position        : absolute;
  right           : 0.55rem;
  top             : 50%;
  transform       : translateY(-50%);
  display         : flex;
  align-items     : center;
  justify-content : center;
  width           : 1.5rem;
  height          : 1.5rem;
  padding         : 0;
  background      : rgba(255, 255, 255, 0.07);
  border          : 1px solid rgba(255, 255, 255, 0.13);
  border-radius   : 50%;
  color           : rgba(200, 215, 255, 0.60);
  cursor          : pointer;
  backdrop-filter : blur(4px);
  z-index         : 2;
  overflow        : visible;
  transition      : background 140ms ease, color 140ms ease,
                    border-color 140ms ease, transform 140ms ease;
}
.clear-btn .fa-xmark {
  font-size   : 0.65rem;
  line-height : 1;
  display     : block;
  position    : relative;
  z-index     : 1;
}
.clear-btn:hover {
  background   : rgba(126, 184, 247, 0.20);
  border-color : rgba(126, 184, 247, 0.50);
  color        : var(--glass-text);
  transform    : translateY(-50%) scale(1.12);
}
.clear-btn:active {
  transform: translateY(-50%) scale(0.90);
}

/* ═══════════════════════════════════════════════════════════════════════════
   Floating label
   ═══════════════════════════════════════════════════════════════════════════ */
.floating-label {
  position       : absolute;
  left           : 1rem;
  top            : 50%;
  transform      : translateY(-50%);
  color          : var(--glass-placeholder);
  font-size      : 0.9375rem;
  pointer-events : none;
  transition     : top 180ms var(--tt-ease),
                   font-size 180ms var(--tt-ease),
                   color 180ms var(--tt-ease);
  z-index        : 1;
}
.input-glass input.form-control:not(:placeholder-shown) ~ .floating-label,
.input-glass input.form-control:focus                   ~ .floating-label {
  top       : 0.3rem;
  font-size : 0.7rem;
  color     : var(--glass-accent);
  transform : none;
}

/* ═══════════════════════════════════════════════════════════════════════════
   Helper text
   ═══════════════════════════════════════════════════════════════════════════ */
.glass-helper-text {
  font-size : 0.75rem;
  color     : var(--glass-helper);
  padding   : 0 0.25rem;
}
</style>
'''
  }
}
