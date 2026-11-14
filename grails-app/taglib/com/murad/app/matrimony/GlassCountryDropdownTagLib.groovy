package com.murad.app.matrimony

/**
 * GlassCountryDropdownTagLib.groovy
 *
 * Opaque glassmorphic country-code dropdown GSP tag library.
 * Cross-browser compatible · Mobile-first · Keyboard / mouse / touch navigable.
 *
 * Namespace : glass
 * Tag       : countryDropdown
 *
 * Usage:
 *   <glass:countryDropdown
 *       id="phone1"
 *       countries="${countries}"
 *       isdCodeName="isdCode"
 *       countryName="country"
 *       flagName="flag"
 *       selectedIsdCode="${contactDetail?.isdCode}"
 *       selectedCountry="${contactDetail?.country}"
 *       selectedFlag="${contactDetail?.flag}"
 *       placeholder="Select Country"
 *       label="Country Code"
 *       disabled="false"
 *       cssClass=""
 *       style="max-width: 320px"
 *   />
 *
 * Each item in `countries` must expose:
 *   .dial_code  (e.g. "+91")
 *   .name       (e.g. "India")
 *   .emoji      (e.g. "🇮🇳")
 *
 * ── Keyboard (closed / trigger focused) ──────────────────────
 *   Enter / Space / ArrowDown  — open dropdown
 *
 * ── Keyboard (open) ──────────────────────────────────────────
 *   ArrowDown / ArrowUp  — move highlight (wraps around)
 *   Home / End           — first / last visible item
 *   Enter                — select highlighted item
 *   Escape               — close, return focus to trigger
 *   Tab                  — close
 *   Any printable char   — handled by search input naturally
 *
 * ── Mouse ────────────────────────────────────────────────────
 *   Hover                — highlights item (no auto-scroll)
 *   Click                — selects item
 *   Scroll wheel         — scrolls list via overflow-y: auto
 *
 * ── Touch ────────────────────────────────────────────────────
 *   Tap                  — selects item
 *   Swipe / drag         — scrolls list (Y-delta guard)
 *   Ghost-click guard    — prevents duplicate selection on iOS
 *
 * ── Validation (external JS) ─────────────────────────────────
 *   wrapperEl.classList.add('gcd-invalid')
 *   wrapperEl.classList.add('gcd-valid')
 *   wrapperEl.classList.remove('gcd-invalid', 'gcd-valid')
 *
 * ── Smart positioning ────────────────────────────────────────
 *   Opens UPWARD when near viewport bottom, DOWNWARD otherwise.
 *   Recalculated on every open() and on scroll/resize.
 *   Menu uses position:fixed so it escapes every stacking context
 *   and always renders above all other elements on the page.
 *
 * ── ARIA ─────────────────────────────────────────────────────
 *   role=combobox on wrapper, role=listbox on items, role=option
 *   with unique id on every item, aria-activedescendant on search
 *   tracks the highlighted option for screen readers.
 *
 * ── Cross-browser / mobile notes ─────────────────────────────
 *   - All backgrounds are 100% opaque — no backdrop-filter needed
 *   - Menu uses position:fixed + z-index:2147483647 (INT_MAX) so it
 *     floats above every stacking context, z-index, transform, or
 *     opacity on any ancestor or sibling taglib / component
 *   - getBoundingClientRect() drives menu placement on open, scroll
 *     and resize — correct even inside overflow:hidden containers
 *   - touch-action:manipulation removes 300 ms tap delay
 *   - font-size:1rem on search prevents iOS Safari auto-zoom
 *   - -webkit-overflow-scrolling:touch gives iOS momentum scrolling
 *   - overscroll-behavior:contain stops scroll bleeding to page
 *   - Element.closest + CustomEvent polyfills for IE11 / old WebViews
 *   - Array.prototype.forEach.call instead of NodeList.forEach
 *   - indexOf instead of String.includes for IE11 compat
 *   - e.key with e.keyCode fallback covers IE11 keyboard events
 *   - All vendor-prefixed CSS properties included
 */
class GlassCountryDropdownTagLib {

  static namespace = "glass"

  def countryDropdown = { attrs, body ->

    /* ── Require countries ───────────────────────────────────── */
    if (attrs.countries == null) {
      throwTagError("Tag [glass:countryDropdown] is missing required attribute [countries]")
    }

    /* ── Read attributes ─────────────────────────────────────── */
    String id = attrs.id?.toString() ?: "gcd_${System.nanoTime()}"
    def countries = attrs.countries ?: []
    String isdCodeName = attrs.isdCodeName?.toString() ?: "isdCode"
    String countryName = attrs.countryName?.toString() ?: "country"
    String flagName = attrs.flagName?.toString() ?: "flag"
    String selectedIsd = attrs.selectedIsdCode?.toString() ?: ""
    String selectedCountry = attrs.selectedCountry?.toString() ?: ""
    String selectedFlag = attrs.selectedFlag?.toString() ?: ""
    String placeholder = attrs.placeholder?.toString() ?: "Select Country"
    String label = attrs.label?.toString() ?: ""
    String cssClass = attrs.cssClass?.toString() ?: ""
    String wrapStyle = attrs.style?.toString() ?: ""

    def disabledRaw = attrs.disabled
    boolean disabled = (disabledRaw instanceof Boolean) ? disabledRaw
      : disabledRaw?.toString()?.equalsIgnoreCase("true") ?: false

    /* ── HTML encoder ────────────────────────────────────────── */
    def h = { String s ->
      s?.replace("&", "&amp;")
        ?.replace("<", "&lt;")
        ?.replace(">", "&gt;")
        ?.replace("\"", "&quot;") ?: ""
    }

    /* ── Determine initial display ───────────────────────────── */
    String initDisplay = ""
    String initFlag = ""
    if (selectedIsd) {
      def match = countries.find { it?.dial_code?.toString() == selectedIsd }
      if (match) {
        initFlag = match.emoji?.toString() ?: ""
        initDisplay = "${match.dial_code} ${match.name}"
      } else {
        initDisplay = selectedIsd
      }
    }

    /* ── Unique listbox id for ARIA wiring ───────────────────── */
    String listboxId = "${id}-listbox"

    /* ══════════════════════════════════════════════════════════
       Inject CSS + JS exactly once per page
       ══════════════════════════════════════════════════════════ */
    if (!pageScope["__gcdCssWritten__"]) {
      pageScope["__gcdCssWritten__"] = true
      out << '''<style id="glass-country-dropdown-styles">
@import url('https://fonts.googleapis.com/css2?family=Outfit:wght@300;400;600&family=DM+Sans:opsz,wght@9..40,300;9..40,400;9..40,500&display=swap');

/* ═══════════════════════════════════════════════════════════════
   DESIGN TOKENS — fully opaque dark theme
   ═══════════════════════════════════════════════════════════════ */
:root {
  /* Wrapper */
  --gcd-background             : #1c2350;
  /* Wrapper — opaque base (IE/old WebView fallback) */
  --gcd-background             : #1a2156;
  --gcd-background-hover       : #1f2870;
  --gcd-background-open        : #1f2870;
  /* Glassmorphic overrides applied via @supports below */
  --gcd-background-glass       : rgba(255,255,255,0.07);
  --gcd-background-glass-hover : rgba(255,255,255,0.11);
  --gcd-background-glass-open  : rgba(255,255,255,0.14);
  /* Borders */
  --gcd-border         : rgba(255,255,255,0.13);
  --gcd-border-hover   : rgba(255,255,255,0.26);
  --gcd-border-focus   : rgba(100,186,255,0.70);
  /* Shadows — layered depth */
  --gcd-shadow         : 0 2px 6px rgba(0,0,0,0.30),
                         0 8px 28px rgba(0,0,0,0.42),
                         inset 0 1px 0 rgba(255,255,255,0.10);
  --gcd-shadow-hover   : 0 4px 10px rgba(0,0,0,0.36),
                         0 16px 40px rgba(0,0,0,0.52),
                         inset 0 1px 0 rgba(255,255,255,0.14);
  --gcd-shadow-focus   : 0 0 0 3px rgba(100,186,255,0.28),
                         0 0 0 1px rgba(100,186,255,0.55),
                         0 8px 32px rgba(0,0,0,0.50),
                         inset 0 1px 0 rgba(255,255,255,0.12);
  --gcd-shadow-active  : 0 0 0 4px rgba(100,186,255,0.35),
                         0 4px 16px rgba(0,0,0,0.50),
                         inset 0 1px 0 rgba(255,255,255,0.08);
  --gcd-radius         : 16px;
  --gcd-text           : #eaf0ff;
  --gcd-text-muted     : #8899cc;
  /* Menu */
  --gcd-menu-background        : rgba(10,14,52,0.94);
  --gcd-menu-border    : rgba(255,255,255,0.12);
  --gcd-menu-shadow-dn : 0 4px 6px rgba(0,0,0,0.22),
                         0 20px 60px rgba(0,0,0,0.72),
                         inset 0 1px 0 rgba(255,255,255,0.07);
  --gcd-menu-shadow-up : 0 -4px 6px rgba(0,0,0,0.22),
                         0 -20px 60px rgba(0,0,0,0.72),
                         inset 0 -1px 0 rgba(255,255,255,0.07);
  /* Items */
  --gcd-item-text      : #ccd8ff;
  --gcd-item-hover-background  : rgba(255,255,255,0.07);
  --gcd-item-sel-background    : rgba(80,150,255,0.18);
  --gcd-item-sel-text  : #c0dcff;
  --gcd-item-hi-background     : rgba(255,255,255,0.10);
  /* Search */
  --gcd-search-background      : rgba(255,255,255,0.05);
  --gcd-search-border  : rgba(255,255,255,0.12);
  --gcd-search-text    : #dce8ff;
  /* Label */
  --gcd-label-background       : rgba(20,26,80,0.72);
  --gcd-label-text     : #a8bcee;
  /* Validation */
  --gcd-valid-border   : rgba(61,214,140,0.80);
  --gcd-valid-shadow   : 0 0 0 3px rgba(61,214,140,0.22),
                         0 0 0 1px rgba(61,214,140,0.60),
                         0 8px 28px rgba(0,0,0,0.42);
  --gcd-invalid-border : rgba(255,90,90,0.80);
  --gcd-invalid-shadow : 0 0 0 3px rgba(255,90,90,0.22),
                         0 0 0 1px rgba(255,90,90,0.60),
                         0 8px 28px rgba(0,0,0,0.42);
  /* Misc */
  --gcd-transition     : 200ms cubic-bezier(0.4,0,0.2,1);
  --gcd-font-label     : 'Outfit',  system-ui,-apple-system,BlinkMacSystemFont,sans-serif;
  --gcd-font-ctrl      : 'DM Sans', system-ui,-apple-system,BlinkMacSystemFont,sans-serif;
}

/* ═══════════════════════════════════════════════════════════════
   SCOPED BOX-SIZING RESET
   ═══════════════════════════════════════════════════════════════ */
.gcd-wrapper,
.gcd-wrapper *,
.gcd-wrapper *::before,
.gcd-wrapper *::after {
  -webkit-box-sizing : border-box;
          box-sizing : border-box;
}

/* ═══════════════════════════════════════════════════════════════
   WRAPPER — opaque base for IE11 / old WebViews
   Glassmorphic enhancement layered on top via @supports.
   ═══════════════════════════════════════════════════════════════ */
.gcd-wrapper {
  position       : relative;
  display        : inline-block;
  width          : 100%;
  border-radius  : var(--gcd-radius);
  z-index        : 1;
  /* Solid fallback */
  background     : var(--gcd-background);
  border         : 1px solid var(--gcd-border);
  box-shadow     : var(--gcd-shadow);
  -webkit-transition : border-color  var(--gcd-transition),
                       box-shadow    var(--gcd-transition),
                       background    var(--gcd-transition),
                       -webkit-transform var(--gcd-transition);
          transition : border-color  var(--gcd-transition),
                       box-shadow    var(--gcd-transition),
                       background    var(--gcd-transition),
                       transform     var(--gcd-transition);
  font-family    : var(--gcd-font-ctrl);
  touch-action   : manipulation;
  -webkit-tap-highlight-color : transparent;
  -webkit-user-select : none;
     -moz-user-select : none;
      -ms-user-select : none;
          user-select : none;
  -webkit-font-smoothing  : antialiased;
  -moz-osx-font-smoothing : grayscale;
}

/* ── Gradient top-rim shimmer ───────────────────────────────────
   A single-pixel gradient line fades from the accent colour
   through white to transparent — gives the glass a lit-from-above
   feeling without any background bleed artefacts.
   ─────────────────────────────────────────────────────────────── */
.gcd-wrapper::after {
  content       : '';
  position      : absolute;
  inset         : 0;
  border-radius : inherit;
  border        : 1px solid transparent;
  background    : linear-gradient(
                    135deg,
                    rgba(255,255,255,0.22) 0%,
                    rgba(160,200,255,0.14) 40%,
                    rgba(255,255,255,0.04) 100%
                  ) border-box;
  /* Masks the gradient so it only shows through the border area */
  -webkit-mask  : linear-gradient(#fff 0 0) padding-box,
                  linear-gradient(#fff 0 0);
  -webkit-mask-composite : destination-out;
          mask-composite : exclude;
  pointer-events : none;
  z-index        : 0;
  -webkit-transition : opacity var(--gcd-transition);
          transition : opacity var(--gcd-transition);
}
/* Brighten rim on hover / open */
@media (hover:hover) and (pointer:fine) {
  .gcd-wrapper:hover:not(.gcd-disabled)::after { opacity: 1.5; }
}
.gcd-wrapper.gcd-open::after {
  background : linear-gradient(
                 135deg,
                 rgba(100,186,255,0.50) 0%,
                 rgba(180,220,255,0.20) 50%,
                 rgba(100,186,255,0.10) 100%
               ) border-box;
}

/* ── Glassmorphic progressive enhancement ───────────────────────
   Applied only when the browser supports backdrop-filter.
   Falls back gracefully to the solid tokens above.
   ─────────────────────────────────────────────────────────────── */
@supports ((-webkit-backdrop-filter: blur(1px)) or (backdrop-filter: blur(1px))) {
  .gcd-wrapper {
    background             : var(--gcd-background-glass);
    -webkit-backdrop-filter: blur(20px) saturate(160%);
            backdrop-filter: blur(20px) saturate(160%);
  }
}

/* HOVER */
@media (hover:hover) and (pointer:fine) {
  .gcd-wrapper:hover:not(.gcd-disabled) {
    background   : var(--gcd-background-hover);
    border-color : var(--gcd-border-hover);
    box-shadow   : var(--gcd-shadow-hover);
  }
  @supports ((-webkit-backdrop-filter: blur(1px)) or (backdrop-filter: blur(1px))) {
    .gcd-wrapper:hover:not(.gcd-disabled) {
      background : var(--gcd-background-glass-hover);
    }
  }
}

/* ─────────────────────────────────────────────────────────────
   OPEN STATES
   ───────────────────────────────────────────────────────────── */
.gcd-wrapper.gcd-open:not(.gcd-disabled) {
  background    : var(--gcd-background-open);
  border-color  : var(--gcd-border-focus);
  box-shadow    : var(--gcd-shadow-focus);
  border-radius : var(--gcd-radius);
}
@supports ((-webkit-backdrop-filter: blur(1px)) or (backdrop-filter: blur(1px))) {
  .gcd-wrapper.gcd-open:not(.gcd-disabled) {
    background : var(--gcd-background-glass-open);
  }
}

/* ACTIVE press */
.gcd-wrapper.gcd-active:not(.gcd-disabled) {
  border-color         : var(--gcd-border-focus);
  box-shadow           : var(--gcd-shadow-active);
  -webkit-transform    : scale(0.997);
          transform    : scale(0.997);
}

/* DISABLED */
.gcd-wrapper.gcd-disabled {
  opacity        : 0.42;
  cursor         : not-allowed;
  pointer-events : none;
  -webkit-filter : grayscale(30%);
          filter : grayscale(30%);
}

/* VALID */
.gcd-wrapper.gcd-valid {
  border-color : var(--gcd-valid-border);
  box-shadow   : var(--gcd-valid-shadow);
}
.gcd-wrapper.gcd-valid .gcd-label,
.gcd-wrapper.gcd-valid .gcd-chevron { color: #5ef0a8; }

/* INVALID */
.gcd-wrapper.gcd-invalid {
  border-color : var(--gcd-invalid-border);
  box-shadow   : var(--gcd-invalid-shadow);
}
.gcd-wrapper.gcd-invalid .gcd-label,
.gcd-wrapper.gcd-invalid .gcd-chevron { color: #ff8080; }

/* ═══════════════════════════════════════════════════════════════
   FLOATING LABEL
   ═══════════════════════════════════════════════════════════════ */
.gcd-label {
  position       : absolute;
  top            : 0;
  left           : 17px;
  -webkit-transform : translateY(-50%);
          transform : translateY(-50%);
  font-family    : var(--gcd-font-label);
  font-size      : 0.67rem;
  font-weight    : 700;
  letter-spacing : 0.10em;
  text-transform : uppercase;
  color          : var(--gcd-label-text);
  background     : var(--gcd-label-background);
  padding        : 1px 6px;
  pointer-events : none;
  white-space    : nowrap;
  border-radius  : 20px;
  z-index        : 3;
  opacity        : 0.55;
  -webkit-transition : color var(--gcd-transition), opacity var(--gcd-transition);
          transition : color var(--gcd-transition), opacity var(--gcd-transition);
}
.gcd-wrapper:hover:not(.gcd-disabled) .gcd-label { opacity: 0.75; }
.gcd-wrapper.gcd-open .gcd-label { color: #8dcaff; opacity: 1; }

/* ═══════════════════════════════════════════════════════════════
   TRIGGER BUTTON — min 52 px on mobile (WCAG 2.5.5)
   ═══════════════════════════════════════════════════════════════ */
.gcd-trigger {
  display            : -webkit-box;
  display            : -webkit-flex;
  display            :     -ms-flexbox;
  display            :         flex;
  -webkit-box-align  : center;
  -webkit-align-items: center;
      -ms-flex-align : center;
          align-items: center;
  gap                : 10px;
  width              : 100%;
  padding            : 14px 52px 14px 17px;
  min-height         : 52px;
  background         : transparent;
  border             : none;
  outline            : none;
  cursor             : pointer;
  position           : relative;
  z-index            : 1;
  border-radius      : var(--gcd-radius);
  -webkit-tap-highlight-color : transparent;
  touch-action       : manipulation;
  -webkit-user-select : none;
     -moz-user-select : none;
      -ms-user-select : none;
          user-select : none;
}
@media (min-width:640px) {
  .gcd-trigger { padding: 13px 46px 13px 17px; min-height: 48px; }
}

/* Keyboard focus ring (non-mouse users) */
.gcd-trigger:focus-visible {
  outline        : 2px solid var(--gcd-border-focus);
  outline-offset : -2px;
  border-radius  : var(--gcd-radius);
}
.gcd-trigger:focus:not(:focus-visible) { outline: none; }

.gcd-trigger-flag {
  font-size           : 1.5em;
  line-height         : 1;
  min-width           : 1.7em;
  text-align          : center;
  -webkit-flex-shrink : 0;
       -ms-flex-negative: 0;
          flex-shrink : 0;
}

.gcd-trigger-text {
  font-family   : var(--gcd-font-ctrl);
  font-size     : 0.9375rem;
  font-weight   : 400;
  color         : var(--gcd-text-muted);
  white-space   : nowrap;
  overflow      : hidden;
  text-overflow : ellipsis;
  -webkit-transition : color var(--gcd-transition);
          transition : color var(--gcd-transition);
}
.gcd-trigger-text.gcd-has-value { color: var(--gcd-text); }

/* ═══════════════════════════════════════════════════════════════
   CHEVRON
   ═══════════════════════════════════════════════════════════════ */
.gcd-chevron {
  position       : absolute;
  right: 14px; top: 0; bottom: 0;
  margin         : auto 0;
  height         : 20px;
  pointer-events : none;
  display        : -webkit-box;
  display        : -webkit-flex;
  display        :     -ms-flexbox;
  display        :         flex;
  -webkit-box-align  : center;
  -webkit-align-items: center;
      -ms-flex-align : center;
          align-items: center;
  color          : #7a8ab8;
  -webkit-transition : color var(--gcd-transition), -webkit-transform var(--gcd-transition);
          transition : color var(--gcd-transition),         transform var(--gcd-transition);
  z-index        : 2;
}
.gcd-chevron svg { width:20px; height:20px; display:block; }

/* Open (either direction): rotate up ∧ — closed rests at 0° pointing down ∨ */
.gcd-wrapper.gcd-open .gcd-chevron {
  color                : #6aadff;
  -webkit-transform    : rotate(180deg);
          transform    : rotate(180deg);
}

/* ═══════════════════════════════════════════════════════════════
   DROPDOWN MENU PANEL
   ─────────────────────────────────────────────────────────────
   KEY CHANGE: position:fixed + z-index:2147483647
   ─────────────────────────────────────────────────────────────
   position:fixed removes the menu from every ancestor stacking
   context (transforms, opacity, overflow:hidden, z-index) so it
   always renders on top of all other page elements and taglibs.

   width / top / left / bottom are set dynamically by positionMenu()
   in JS using getBoundingClientRect(), so the menu tracks its
   trigger correctly even inside scrollable or transformed parents.

   z-index 2147483647 is INT_MAX — the highest value browsers
   honour, ensuring no other component can ever paint over the menu.
   ═══════════════════════════════════════════════════════════════ */
.gcd-menu {
  position   : fixed;
  /* Coordinates injected by positionMenu() on every open/scroll/resize */
  z-index    : 2147483647;
  /* Solid fallback for browsers without backdrop-filter */
  background : var(--gcd-menu-background);
  border     : 1px solid var(--gcd-menu-border);
  overflow   : hidden;
  display    : none;
  -webkit-box-orient     : vertical;
  -webkit-box-direction  : normal;
  -webkit-flex-direction : column;
      -ms-flex-direction : column;
          flex-direction : column;
}

/* Glassmorphic menu on capable browsers */
@supports ((-webkit-backdrop-filter: blur(1px)) or (backdrop-filter: blur(1px))) {
  .gcd-menu {
    background             : rgba(8,12,48,0.80);
    -webkit-backdrop-filter: blur(28px) saturate(160%);
            backdrop-filter: blur(28px) saturate(160%);
  }
}

/* DROP-DOWN direction — JS adds this class */
.gcd-menu.gcd-menu-dir-dn {
  border-top    : none;
  border-radius : 0 0 var(--gcd-radius) var(--gcd-radius);
  box-shadow    : var(--gcd-menu-shadow-dn);
  /* Accent glow on the top opening edge */
  border-top    : 1px solid rgba(100,186,255,0.35);
}

/* DROP-UP direction — JS adds this class */
.gcd-menu.gcd-menu-dir-up {
  border-bottom : none;
  border-top    : 1px solid var(--gcd-border-focus);
  border-radius : var(--gcd-radius) var(--gcd-radius) 0 0;
  box-shadow    : var(--gcd-menu-shadow-up);
  /* Accent glow on the bottom opening edge */
  border-bottom : 1px solid rgba(100,186,255,0.35);
}

/* Show the menu — class applied directly by JS after portal to <body>.
   The old ".gcd-wrapper.gcd-open .gcd-menu" descendant selector cannot
   be used once the menu is moved out of the wrapper DOM subtree.       */
.gcd-menu.gcd-menu-open {
  display        : -webkit-box;
  display        : -webkit-flex;
  display        :     -ms-flexbox;
  display        :         flex;
}

/* In drop-up: items first, search bar at bottom */
.gcd-menu.gcd-menu-dir-up .gcd-search-wrap {
  -webkit-box-ordinal-group : 2;
  -webkit-order : 1;
      -ms-flex-order : 1;
          order : 1;
  border-top    : 1px solid rgba(255,255,255,0.08);
  border-bottom : none;
  padding       : 8px 12px 10px;
}
.gcd-menu.gcd-menu-dir-up .gcd-items {
  -webkit-box-ordinal-group : 1;
  -webkit-order : 0;
      -ms-flex-order : 0;
          order : 0;
}

/* ── Search bar ─────────────────────────────────────────────── */
.gcd-search-wrap {
  padding             : 12px 12px 10px;
  -webkit-flex-shrink : 0;
       -ms-flex-negative: 0;
          flex-shrink : 0;
  border-bottom       : 1px solid rgba(255,255,255,0.07);
  background          : rgba(255,255,255,0.03);
}

.gcd-search {
  width          : 100%;
  padding        : 10px 14px 10px 38px;
  background     : var(--gcd-search-background);
  border         : 1px solid var(--gcd-search-border);
  border-radius  : 10px;
  color          : var(--gcd-search-text);
  font-family    : var(--gcd-font-ctrl);
  /* 16 px critical: prevents iOS Safari viewport zoom on focus */
  font-size      : 1rem;
  line-height    : 1.4;
  outline        : none;
  -webkit-appearance : none;
     -moz-appearance : none;
          appearance : none;
  touch-action   : manipulation;
  -webkit-transition : border-color var(--gcd-transition), box-shadow var(--gcd-transition);
          transition : border-color var(--gcd-transition), box-shadow var(--gcd-transition);
  background-image    : url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='16' height='16' viewBox='0 0 24 24' fill='none' stroke='%236677aa' stroke-width='2.2' stroke-linecap='round' stroke-linejoin='round'%3E%3Ccircle cx='11' cy='11' r='8'/%3E%3Cline x1='21' y1='21' x2='16.65' y2='16.65'/%3E%3C/svg%3E");
  background-repeat   : no-repeat;
  background-position : 12px center;
  background-size     : 16px 16px;
}
.gcd-search::-webkit-input-placeholder { color: rgba(150,170,220,0.45); }
.gcd-search::-moz-placeholder          { color: rgba(150,170,220,0.45); opacity:1; }
.gcd-search:-ms-input-placeholder      { color: rgba(150,170,220,0.45); }
.gcd-search::placeholder               { color: rgba(150,170,220,0.45); }
.gcd-search:focus {
  border-color : var(--gcd-border-focus);
  box-shadow   : 0 0 0 3px rgba(100,186,255,0.15),
                 inset 0 1px 0 rgba(255,255,255,0.06);
}

/* ── Items list (scrollable container) ──────────────────────── */
.gcd-items {
  max-height                 : 264px;
  overflow-y                 : auto;
  /* iOS momentum scrolling */
  -webkit-overflow-scrolling : touch;
  /* Contain scroll — don't bleed to page */
  overscroll-behavior        : contain;
  /* Firefox thin scrollbar */
  scrollbar-width            : thin;
  scrollbar-color            : rgba(255,255,255,0.15) transparent;
}
@media (min-width:640px) { .gcd-items { max-height: 224px; } }
.gcd-items::-webkit-scrollbar       { width: 4px; }
.gcd-items::-webkit-scrollbar-track { background: rgba(255,255,255,0.03); }
.gcd-items::-webkit-scrollbar-thumb { background-color: rgba(255,255,255,0.15); border-radius:10px; }

/* ── Single item ────────────────────────────────────────────── */
.gcd-item {
  display            : -webkit-box;
  display            : -webkit-flex;
  display            :     -ms-flexbox;
  display            :         flex;
  -webkit-box-align  : center;
  -webkit-align-items: center;
      -ms-flex-align : center;
          align-items: center;
  gap                : 10px;
  padding            : 12px 16px;
  min-height         : 48px;   /* comfortable mobile tap target */
  cursor             : pointer;
  color              : var(--gcd-item-text);
  font-family        : var(--gcd-font-ctrl);
  font-size          : 0.9rem;
  border-bottom      : 1px solid rgba(255,255,255,0.05);
  -webkit-tap-highlight-color : transparent;
  touch-action       : manipulation;
  -webkit-transition : background var(--gcd-transition);
          transition : background var(--gcd-transition);
}
.gcd-item:last-child { border-bottom: none; }

/* Tighter rows on desktop mouse users */
@media (hover:hover) and (pointer:fine) {
  .gcd-item        { padding: 9px 16px; min-height: 40px; }
  .gcd-item:hover  { background: var(--gcd-item-hover-background); }
}

/* HIGHLIGHTED — keyboard arrow or mouse hover (set via JS) */
.gcd-item.gcd-highlighted {
  background : var(--gcd-item-hi-background);
  outline    : none;
}

/* SELECTED — currently chosen value */
.gcd-item.gcd-selected {
  background : var(--gcd-item-sel-background);
  color      : var(--gcd-item-sel-text);
}

.gcd-item-flag {
  font-size           : 1.4em;
  line-height         : 1;
  min-width           : 1.7em;
  text-align          : center;
  -webkit-flex-shrink : 0;
       -ms-flex-negative: 0;
          flex-shrink : 0;
}

.gcd-item-name {
  -webkit-box-flex : 1;
  -webkit-flex     : 1;
       -ms-flex    : 1 1 auto;
          flex     : 1;
  white-space      : nowrap;
  overflow         : hidden;
  text-overflow    : ellipsis;
}

.gcd-item-code {
  font-size           : 0.82rem;
  color               : rgba(120,160,240,0.80);
  -webkit-flex-shrink : 0;
       -ms-flex-negative: 0;
          flex-shrink : 0;
  min-width           : 52px;
}
.gcd-item.gcd-selected .gcd-item-code { color: rgba(160,210,255,0.90); }

/* No-results message */
.gcd-no-results {
  padding    : 18px 16px;
  color      : #4a5878;
  font-family: var(--gcd-font-ctrl);
  font-size  : 0.875rem;
  text-align : center;
  font-style : italic;
}

/* ═══════════════════════════════════════════════════════════════
   LIGHT-MODE OVERRIDES — glassmorphic light theme
   ═══════════════════════════════════════════════════════════════ */
@media (prefers-color-scheme:light) {
  :root {
    /* Wrapper — opaque fallback */
    --gcd-background             : rgba(240,245,255,0.85);
    --gcd-background-hover       : rgba(230,238,255,0.90);
    --gcd-background-open        : rgba(225,235,255,0.92);
    --gcd-background-glass       : rgba(255,255,255,0.55);
    --gcd-background-glass-hover : rgba(255,255,255,0.68);
    --gcd-background-glass-open  : rgba(255,255,255,0.74);
    --gcd-border         : rgba(100,130,200,0.28);
    --gcd-border-hover   : rgba(80,110,200,0.50);
    --gcd-border-focus   : rgba(40,110,220,0.75);
    --gcd-shadow         : 0 2px 6px rgba(60,90,180,0.14),
                           0 8px 28px rgba(60,90,180,0.20),
                           inset 0 1px 0 rgba(255,255,255,0.80);
    --gcd-shadow-hover   : 0 4px 10px rgba(60,90,180,0.18),
                           0 16px 40px rgba(60,90,180,0.26),
                           inset 0 1px 0 rgba(255,255,255,0.90);
    --gcd-shadow-focus   : 0 0 0 3px rgba(40,110,220,0.20),
                           0 0 0 1px rgba(40,110,220,0.50),
                           0 8px 32px rgba(60,90,180,0.28),
                           inset 0 1px 0 rgba(255,255,255,0.80);
    --gcd-shadow-active  : 0 0 0 4px rgba(40,110,220,0.25),
                           0 4px 16px rgba(60,90,180,0.28),
                           inset 0 1px 0 rgba(255,255,255,0.70);
    --gcd-text           : #0e1e60;
    --gcd-text-muted     : #6070a8;
    --gcd-menu-background        : rgba(240,245,255,0.94);
    --gcd-menu-border    : rgba(100,140,220,0.22);
    --gcd-menu-shadow-dn : 0 4px 6px rgba(60,90,180,0.10),
                           0 20px 60px rgba(60,90,180,0.30),
                           inset 0 1px 0 rgba(255,255,255,0.80);
    --gcd-menu-shadow-up : 0 -4px 6px rgba(60,90,180,0.10),
                           0 -20px 60px rgba(60,90,180,0.30),
                           inset 0 -1px 0 rgba(255,255,255,0.80);
    --gcd-item-text      : #1a2870;
    --gcd-item-hover-background  : rgba(60,100,220,0.07);
    --gcd-item-sel-background    : rgba(40,100,220,0.12);
    --gcd-item-sel-text  : #0a1850;
    --gcd-item-hi-background     : rgba(60,100,220,0.10);
    --gcd-search-background      : rgba(255,255,255,0.60);
    --gcd-search-border  : rgba(100,130,210,0.28);
    --gcd-search-text    : #0e1e60;
    --gcd-label-background       : rgba(235,242,255,0.82);
    --gcd-label-text     : #4050a0;
    --gcd-valid-border   : rgba(22,160,90,0.80);
    --gcd-valid-shadow   : 0 0 0 3px rgba(22,160,90,0.18),
                           0 0 0 1px rgba(22,160,90,0.55),
                           0 8px 28px rgba(60,90,180,0.18);
    --gcd-invalid-border : rgba(200,40,40,0.80);
    --gcd-invalid-shadow : 0 0 0 3px rgba(200,40,40,0.18),
                           0 0 0 1px rgba(200,40,40,0.55),
                           0 8px 28px rgba(60,90,180,0.18);
  }

  /* Glassmorphic light wrapper */
  @supports ((-webkit-backdrop-filter: blur(1px)) or (backdrop-filter: blur(1px))) {
    .gcd-wrapper {
      background             : var(--gcd-background-glass);
      -webkit-backdrop-filter: blur(20px) saturate(200%);
              backdrop-filter: blur(20px) saturate(200%);
    }
    @media (hover:hover) and (pointer:fine) {
      .gcd-wrapper:hover:not(.gcd-disabled) { background: var(--gcd-background-glass-hover); }
    }
    .gcd-wrapper.gcd-open:not(.gcd-disabled) { background: var(--gcd-background-glass-open); }
    .gcd-menu {
      background             : rgba(248,251,255,0.82);
      -webkit-backdrop-filter: blur(28px) saturate(200%);
              backdrop-filter: blur(28px) saturate(200%);
    }
  }

  /* Gradient rim — light mode */
  .gcd-wrapper::after {
    background : linear-gradient(
                   135deg,
                   rgba(255,255,255,0.90) 0%,
                   rgba(200,220,255,0.50) 40%,
                   rgba(255,255,255,0.20) 100%
                 ) border-box;
  }
  .gcd-wrapper.gcd-open::after {
    background : linear-gradient(
                   135deg,
                   rgba(40,110,220,0.40) 0%,
                   rgba(120,170,255,0.20) 50%,
                   rgba(40,110,220,0.08) 100%
                 ) border-box;
  }

  .gcd-wrapper.gcd-open .gcd-label   { color: #1a60c8; opacity: 1; }
  .gcd-wrapper.gcd-open .gcd-chevron { color: #1a60c8; }
  .gcd-chevron                        { color: #6070a8; }
  .gcd-search::-webkit-input-placeholder { color: rgba(90,110,170,0.50); }
  .gcd-search::-moz-placeholder          { color: rgba(90,110,170,0.50); opacity:1; }
  .gcd-search:-ms-input-placeholder      { color: rgba(90,110,170,0.50); }
  .gcd-search::placeholder               { color: rgba(90,110,170,0.50); }
  .gcd-search {
    background-image : url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='16' height='16' viewBox='0 0 24 24' fill='none' stroke='%23809ad0' stroke-width='2.2' stroke-linecap='round' stroke-linejoin='round'%3E%3Ccircle cx='11' cy='11' r='8'/%3E%3Cline x1='21' y1='21' x2='16.65' y2='16.65'/%3E%3C/svg%3E");
  }
  .gcd-search:focus { box-shadow: 0 0 0 3px rgba(40,110,220,0.14), inset 0 1px 0 rgba(255,255,255,0.60); }
  .gcd-search-wrap  { background: rgba(60,100,200,0.04); border-bottom-color: rgba(60,100,200,0.08); }
  .gcd-item-code                         { color: rgba(60,100,200,0.70); }
  .gcd-item.gcd-selected .gcd-item-code  { color: rgba(20,60,180,0.90); }
  .gcd-no-results                        { color: #8090b8; }
  .gcd-items::-webkit-scrollbar-track    { background: rgba(60,100,200,0.04); }
  .gcd-items::-webkit-scrollbar-thumb    { background-color: rgba(60,100,200,0.22); }
  .gcd-wrapper.gcd-valid .gcd-label,
  .gcd-wrapper.gcd-valid .gcd-chevron    { color: #148050; }
  .gcd-wrapper.gcd-invalid .gcd-label,
  .gcd-wrapper.gcd-invalid .gcd-chevron  { color: #b01818; }
}
</style>

<script id="glass-country-dropdown-script">
(function () {
  'use strict';

  /* ══════════════════════════════════════════════════════════════
     POLYFILLS
     ══════════════════════════════════════════════════════════════ */

  /* Element.closest — IE 11, old Android WebViews, Samsung < 5 */
  if (typeof Element !== 'undefined' && !Element.prototype.closest) {
    Element.prototype.closest = function (sel) {
      var el = this;
      while (el && el.nodeType === 1) {
        var m = el.matches || el.msMatchesSelector || el.webkitMatchesSelector;
        if (m && m.call(el, sel)) return el;
        el = el.parentElement || el.parentNode;
      }
      return null;
    };
  }

  /* CustomEvent constructor — IE 11 */
  if (typeof window !== 'undefined' && typeof window.CustomEvent !== 'function') {
    function CustomEvent(evt, p) {
      p = p || { bubbles: false, cancelable: false, detail: null };
      var e = document.createEvent('CustomEvent');
      e.initCustomEvent(evt, p.bubbles, p.cancelable, p.detail);
      return e;
    }
    CustomEvent.prototype = window.Event.prototype;
    window.CustomEvent = CustomEvent;
  }

  /* ══════════════════════════════════════════════════════════════
     UTILITIES
     ══════════════════════════════════════════════════════════════ */

  function each(list, fn) { Array.prototype.forEach.call(list, fn); }

  /* Detect passive event-listener support once */
  var supportsPassive = false;
  try {
    var _o = Object.defineProperty({}, 'passive', { get: function () { supportsPassive = true; return true; } });
    window.addEventListener('__gcd_p', null, _o);
    window.removeEventListener('__gcd_p', null, _o);
  } catch (e) {}

  function on(el, type, fn, passive) {
    el.addEventListener(type, fn, (passive && supportsPassive) ? { passive: true } : false);
  }

  /* Cross-browser dataset read — IE 10 getAttribute fallback */
  function data(el, key) {
    if (el.dataset && el.dataset[key] !== undefined) return el.dataset[key];
    return el.getAttribute('data-' + key.replace(/([A-Z])/g, '-$1').toLowerCase());
  }

  /* Normalise key name: e.key with e.keyCode fallback for IE 11 */
  var KC = { 38:'ArrowUp', 40:'ArrowDown', 13:'Enter', 27:'Escape',
             32:' ',       9:'Tab',        36:'Home',  35:'End' };
  function getKey(e) { return e.key || KC[e.keyCode] || ''; }

  /* ══════════════════════════════════════════════════════════════
     SMART FLIP + FIXED POSITIONING
     ─────────────────────────────────────────────────────────────
     positionMenu() is the single source of truth for where the
     menu panel sits.  It runs on:
       • every open() call
       • every scroll event while the menu is open
       • every resize event while the menu is open

     Using getBoundingClientRect() + position:fixed means the menu
     tracks its trigger correctly even when:
       - the wrapper is inside overflow:hidden / overflow:scroll
       - a parent has transform, will-change, or opacity < 1
       - the page has a sticky/fixed header that shifts layout
       - the user scrolls or resizes after opening

     Width is set to wrapper.offsetWidth + 2 to account for the
     -1 px left offset that aligns the menu border with the wrapper
     border without a visible gap.
     ══════════════════════════════════════════════════════════════ */
  function positionMenu(wrapper, menu) {
    var r    = wrapper.getBoundingClientRect();
    var vH   = window.innerHeight || document.documentElement.clientHeight;
    /* Approximate max panel height — search bar + item list */
    var panelH = 340;
    var goUp = (vH - r.bottom) < panelH && r.top > (vH - r.bottom);

    /* ── Width + horizontal position ──────────────────────────
       width  : match the wrapper exactly (border included)
       left   : viewport-relative left edge of wrapper
       right  : MUST be forced to 'auto' — if any prior value
                lingers the browser resolves left+width+right and
                the menu drifts to the far right of the screen.   */
    menu.style.width = r.width + 'px';
    menu.style.left  = r.left  + 'px';
    menu.style.right = 'auto';            /* ← fixes rightmost drift */

    if (goUp) {
      /* ── DROP-UP ── menu grows upward from the trigger top edge */
      wrapper.classList.add('gcd-drop-up');
      menu.classList.add('gcd-menu-dir-up');
      menu.classList.remove('gcd-menu-dir-dn');
      menu.style.top    = 'auto';
      /* fixed bottom = viewport height − trigger top (1 px seam) */
      menu.style.bottom = (vH - r.top + 1) + 'px';
    } else {
      /* ── DROP-DOWN ── menu grows downward from the trigger bottom */
      wrapper.classList.remove('gcd-drop-up');
      menu.classList.add('gcd-menu-dir-dn');
      menu.classList.remove('gcd-menu-dir-up');
      menu.style.bottom = 'auto';
      /* fixed top = trigger bottom + 1 px seam */
      menu.style.top    = (r.bottom + 1) + 'px';
    }
  }

  /* ══════════════════════════════════════════════════════════════
     INIT — attach behaviour to every un-initialised .gcd-wrapper
     ══════════════════════════════════════════════════════════════ */
  function initGcd() {
    each(document.querySelectorAll('.gcd-wrapper:not([data-gcd-init])'), function (wrapper) {
      wrapper.setAttribute('data-gcd-init', '1');

      var trigger  = wrapper.querySelector('.gcd-trigger');
      var menu     = wrapper.querySelector('.gcd-menu');
      var search   = wrapper.querySelector('.gcd-search');
      var itemsEl  = wrapper.querySelector('.gcd-items');
      var trigText = wrapper.querySelector('.gcd-trigger-text');
      var trigFlag = wrapper.querySelector('.gcd-trigger-flag');

      if (!trigger || !menu || !itemsEl) return;

      /* ── PORTAL ────────────────────────────────────────────────
         Move the menu node to <body> immediately on init.
         This is the only reliable way to guarantee it renders
         above every other element on the page regardless of:
           • ancestor transform / filter / will-change / perspective
             (any of these would trap position:fixed inside that
              ancestor's stacking context, clipping z-index)
           • sibling tabbed panels, modals, sticky headers, etc.
         The menu is hidden (display:none) by default so moving it
         to body causes zero visual change until open() is called.
         We keep JS references to both wrapper and menu throughout.
         ────────────────────────────────────────────────────────── */
      document.body.appendChild(menu);
      /* Back-reference lets outside-click guard find the owner */
      menu._gcdWrapper = wrapper;

      /* ── Local state ───────────────────────────────────────── */
      var isOpen       = false;
      var hiIdx        = -1;    /* highlighted index within visible items */
      var lastTouchEnd = 0;     /* ghost-click guard timestamp */
      var tStartY      = 0;     /* touch-start Y for scroll vs tap */
      var tMoved       = false; /* true when finger dragged > 8 px */

      /* ── Get currently visible items ─────────────────────── */
      function visible() {
        var all = itemsEl.querySelectorAll('.gcd-item');
        var out = [];
        each(all, function (it) { if (it.style.display !== 'none') out.push(it); });
        return out;
      }

      /* ── Scroll highlighted item into view ────────────────── */
      function ensureVisible(item) {
        var cTop = itemsEl.scrollTop;
        var cBot = cTop + itemsEl.clientHeight;
        var iTop = item.offsetTop;
        var iBot = iTop + item.offsetHeight;
        if (iTop < cTop)       itemsEl.scrollTop = iTop - 4;
        else if (iBot > cBot)  itemsEl.scrollTop = iBot - itemsEl.clientHeight + 4;
      }

      /* ── Clear highlight ──────────────────────────────────── */
      function clearHi() {
        each(itemsEl.querySelectorAll('.gcd-item.gcd-highlighted'), function (it) {
          it.classList.remove('gcd-highlighted');
        });
        hiIdx = -1;
        if (search) search.removeAttribute('aria-activedescendant');
      }

      /* ── Set highlight by index in visible list ───────────── */
      function setHi(idx, scroll) {
        var vis = visible();
        if (!vis.length) { clearHi(); return; }
        /* Clamp to valid range */
        if (idx < 0)           idx = 0;
        if (idx >= vis.length) idx = vis.length - 1;
        each(vis, function (it, i) { it.classList.toggle('gcd-highlighted', i === idx); });
        hiIdx = idx;
        /* Wire ARIA active-descendant */
        if (search && vis[idx].id) search.setAttribute('aria-activedescendant', vis[idx].id);
        if (scroll !== false) ensureVisible(vis[idx]);
      }

      /* ── OPEN ─────────────────────────────────────────────── */
      function open() {
        if (wrapper.classList.contains('gcd-disabled')) return;
        /* Position the portal menu before making it visible */
        positionMenu(wrapper, menu);
        isOpen = true;
        wrapper.classList.add('gcd-open');
        wrapper.setAttribute('aria-expanded', 'true');
        /* Show the menu — class on menu itself since it is now
           a <body> child, not a descendant of .gcd-wrapper      */
        menu.classList.add('gcd-menu-open');
        /* Reset search */
        if (search) { search.value = ''; filterItems(''); }
        /* Highlight selected item (if any), else nothing */
        var vis = visible();
        var sel = -1;
        each(vis, function (it, i) { if (it.classList.contains('gcd-selected')) sel = i; });
        if (sel >= 0) setHi(sel); else clearHi();
        /* Focus search with a brief delay so iOS layout settles */
        if (search) setTimeout(function () { search.focus(); }, 50);
      }

      /* ── CLOSE ────────────────────────────────────────────── */
      function close(retFocus) {
        isOpen = false;
        clearHi();
        wrapper.classList.remove('gcd-open', 'gcd-active');
        wrapper.setAttribute('aria-expanded', 'false');
        /* Hide the portalled menu */
        menu.classList.remove('gcd-menu-open');
        if (retFocus && trigger) trigger.focus();
        /* Defer drop-up class removal so CSS transition finishes */
        var _w = wrapper;
        setTimeout(function () {
          if (!_w.classList.contains('gcd-open')) _w.classList.remove('gcd-drop-up');
        }, 240);
      }

      /* ── FILTER ───────────────────────────────────────────── */
      function filterItems(q) {
        var norm = q.toLowerCase().trim();
        var rows = itemsEl.querySelectorAll('.gcd-item');
        var any  = false;
        each(rows, function (row) {
          var cn = (data(row, 'country')  || '').toLowerCase();
          var dc = (data(row, 'dialCode') || '').toLowerCase();
          /* indexOf instead of .includes — IE 11 compat */
          var match = !norm || cn.indexOf(norm) !== -1 || dc.indexOf(norm) !== -1;
          row.style.display = match ? '' : 'none';
          if (match) any = true;
        });
        var noRes = itemsEl.querySelector('.gcd-no-results');
        if (noRes) noRes.style.display = any ? 'none' : '';
        /* After filter: highlight first result when typing */
        clearHi();
        if (any && norm) setTimeout(function () { setHi(0, false); }, 0);
      }

      /* ── SELECT ───────────────────────────────────────────── */
      function selectItem(item) {
        if (!item) return;
        var dc = data(item, 'dialCode') || '';
        var cn = data(item, 'country')  || '';
        var fl = data(item, 'flag')     || '';
        /* Write hidden inputs */
        var elIsd = wrapper.querySelector('.gcd-hidden-isd');
        var elCn  = wrapper.querySelector('.gcd-hidden-country');
        var elFl  = wrapper.querySelector('.gcd-hidden-flag');
        if (elIsd) elIsd.value = dc;
        if (elCn)  elCn.value  = cn;
        if (elFl)  elFl.value  = fl;
        /* Update visible trigger */
        if (trigFlag) trigFlag.textContent = fl;
        if (trigText) { trigText.textContent = dc + ' ' + cn; trigText.classList.add('gcd-has-value'); }
        /* Mark selected row */
        each(itemsEl.querySelectorAll('.gcd-item'), function (r) {
          r.classList.toggle('gcd-selected', r === item);
          r.setAttribute('aria-selected', r === item ? 'true' : 'false');
        });
        close(true); /* return focus to trigger button */
        wrapper.dispatchEvent(new CustomEvent('gcd:change', {
          bubbles: true,
          detail : { dialCode: dc, country: cn, flag: fl }
        }));
      }

      /* ══════════════════════════════════════════════════════
         KEYBOARD — trigger (closed)
         ══════════════════════════════════════════════════════ */
      trigger.addEventListener('keydown', function (e) {
        if (isOpen) return;
        var k = getKey(e);
        if (k === 'ArrowDown' || k === 'Enter' || k === ' ') {
          e.preventDefault();
          open();
        }
      });

      /* ══════════════════════════════════════════════════════
         KEYBOARD — search input (open)
         ══════════════════════════════════════════════════════ */
      if (search) {
        search.addEventListener('keydown', function (e) {
          var k   = getKey(e);
          var vis = visible();
          var len = vis.length;
          switch (k) {
            case 'ArrowDown':
              e.preventDefault();
              setHi(hiIdx < len - 1 ? hiIdx + 1 : 0); /* wraps around */
              break;
            case 'ArrowUp':
              e.preventDefault();
              setHi(hiIdx > 0 ? hiIdx - 1 : len - 1); /* wraps around */
              break;
            case 'Home':
              e.preventDefault(); setHi(0); break;
            case 'End':
              e.preventDefault(); setHi(len - 1); break;
            case 'Enter':
              e.preventDefault();
              if (hiIdx >= 0 && vis[hiIdx]) selectItem(vis[hiIdx]);
              break;
            case 'Escape':
              e.preventDefault(); close(true); break;
            case 'Tab':
              close(false); break;
          }
        });

        search.addEventListener('input',      function ()  { filterItems(this.value); });
        search.addEventListener('click',      function (e) { e.stopPropagation(); });
        on(search, 'touchstart', function (e) { e.stopPropagation(); }, true);
      }

      /* ══════════════════════════════════════════════════════
         MOUSE — hover highlights, click selects
         ══════════════════════════════════════════════════════ */
      itemsEl.addEventListener('mouseover', function (e) {
        var item = e.target.closest ? e.target.closest('.gcd-item') : null;
        if (!item) return;
        var vis = visible();
        for (var i = 0; i < vis.length; i++) {
          if (vis[i] === item) { setHi(i, false); break; } /* hover: no auto-scroll */
        }
      });

      itemsEl.addEventListener('click', function (e) {
        selectItem(e.target.closest ? e.target.closest('.gcd-item') : null);
      });

      /* ══════════════════════════════════════════════════════
         TOUCH — distinguish tap (select) from swipe (scroll)
         ══════════════════════════════════════════════════════ */
      on(itemsEl, 'touchstart', function (e) {
        var t = e.changedTouches && e.changedTouches[0];
        if (t) { tStartY = t.clientY; tMoved = false; }
      }, true);

      on(itemsEl, 'touchmove', function (e) {
        var t = e.changedTouches && e.changedTouches[0];
        /* Mark as scroll when finger moves more than 8 px vertically */
        if (t && Math.abs(t.clientY - tStartY) > 8) tMoved = true;
        e.stopPropagation(); /* keep scroll contained to the list */
      }, false);

      on(itemsEl, 'touchend', function (e) {
        if (tMoved) { tMoved = false; return; } /* was a scroll — ignore */
        /* Ghost-click guard: iOS fires a synthetic click ~300 ms later */
        var now = Date.now();
        if (now - lastTouchEnd < 350) { e.preventDefault(); return; }
        lastTouchEnd = now;
        var t      = e.changedTouches && e.changedTouches[0];
        var target = (t && document.elementFromPoint)
          ? document.elementFromPoint(t.clientX, t.clientY)
          : e.target;
        if (!target) return;
        var item = target.closest ? target.closest('.gcd-item') : null;
        if (item && menu.contains(item)) {
          e.preventDefault(); /* suppress the synthetic mouse click */
          selectItem(item);
        }
      }, false);

      /* ══════════════════════════════════════════════════════
         PRESS VISUAL FEEDBACK (mouse + touch)
         ══════════════════════════════════════════════════════ */
      trigger.addEventListener('mousedown', function () {
        if (!wrapper.classList.contains('gcd-disabled')) wrapper.classList.add('gcd-active');
      });
      document.addEventListener('mouseup', function () { wrapper.classList.remove('gcd-active'); });
      on(trigger, 'touchstart', function () {
        if (!wrapper.classList.contains('gcd-disabled')) wrapper.classList.add('gcd-active');
      }, true);
      on(document, 'touchend', function () { wrapper.classList.remove('gcd-active'); }, true);

      /* ══════════════════════════════════════════════════════
         TRIGGER CLICK (covers mouse + touch via touch-action)
         ══════════════════════════════════════════════════════ */
      trigger.addEventListener('click', function (e) {
        e.stopPropagation();
        isOpen ? close(false) : open();
      });

      /* ══════════════════════════════════════════════════════
         CLOSE ON OUTSIDE INTERACTION
         Because the menu is position:fixed we must check both
         the wrapper and the menu itself — the menu is no longer
         a DOM child of the wrapper at paint time.
         ══════════════════════════════════════════════════════ */
      document.addEventListener('click', function (e) {
        if (!wrapper.contains(e.target) && !menu.contains(e.target)) close(false);
      });
      on(document, 'touchstart', function (e) {
        if (!wrapper.contains(e.target) && !menu.contains(e.target)) close(false);
      }, true);

      /* ══════════════════════════════════════════════════════
         RE-POSITION ON SCROLL / RESIZE
         ─────────────────────────────────────────────────────
         Runs on every scroll (capture phase, all ancestors) and
         resize while the menu is open.  positionMenu() recalculates
         getBoundingClientRect() so the fixed panel tracks the
         trigger even as the page layout shifts.
         ══════════════════════════════════════════════════════ */
      function onSR() { if (isOpen) positionMenu(wrapper, menu); }
      on(window, 'scroll', onSR, true);
      on(window, 'resize', onSR, true);
    });
  }

  /* ── Boot ──────────────────────────────────────────────────── */
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initGcd);
  } else {
    initGcd();
  }

  /* Re-run after AJAX / dynamic content insertion */
  window.gcdInit = initGcd;

})();
</script>'''
    }

    /* ══════════════════════════════════════════════════════════
       HTML OUTPUT
       ══════════════════════════════════════════════════════════ */

    String styleAttr = wrapStyle ? " style=\"${h(wrapStyle)}\"" : ""
    String disabledClass = disabled ? " gcd-disabled" : ""
    String extraClass = cssClass ? " ${h(cssClass)}" : ""
    String ariaLabel = label ?: placeholder

    /* Wrapper */
    out << "<div id=\"${h(id)}\" class=\"gcd-wrapper${disabledClass}${extraClass}\"${styleAttr}"
    out << " role=\"combobox\""
    out << " aria-haspopup=\"listbox\""
    out << " aria-expanded=\"false\""
    out << " aria-owns=\"${h(listboxId)}\""
    out << " aria-label=\"${h(ariaLabel)}\">"

    /* Floating label */
    if (label) {
      out << "<span class=\"gcd-label\" aria-hidden=\"true\">${h(label)}</span>"
    }

    /* Trigger button */
    String hasValueClass = initDisplay ? " gcd-has-value" : ""
    out << "<button type=\"button\" class=\"gcd-trigger\""
    out << " aria-label=\"${h(ariaLabel)}\""
    out << " aria-controls=\"${h(listboxId)}\""
    if (disabled) out << " disabled"
    out << ">"
    out << "<span class=\"gcd-trigger-flag\" aria-hidden=\"true\">${initFlag ?: ''}</span>"
    out << "<span class=\"gcd-trigger-text${hasValueClass}\">${initDisplay ? h(initDisplay) : h(placeholder)}</span>"
    out << "</button>"

    /* Chevron */
    out << '<span class="gcd-chevron" aria-hidden="true">'
    out << '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none"'
    out << ' stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">'
    out << '<polyline points="6 9 12 15 18 9"/></svg></span>'

    /* Menu shell — rendered inside wrapper in the DOM but painted
       via position:fixed above everything else on the page         */
    out << "<div class=\"gcd-menu\" role=\"presentation\">"

    /* Search bar */
    out << "<div class=\"gcd-search-wrap\">"
    out << "<input type=\"search\" class=\"gcd-search\""
    out << " placeholder=\"Search country or code\u2026\""
    out << " autocomplete=\"off\" autocorrect=\"off\""
    out << " autocapitalize=\"off\" spellcheck=\"false\""
    out << " inputmode=\"search\""
    out << " role=\"combobox\""
    out << " aria-label=\"Search countries\""
    out << " aria-controls=\"${h(listboxId)}\""
    out << " aria-autocomplete=\"list\""
    out << " aria-expanded=\"true\">"
    out << "</div>"

    /* Items — each gets a unique id for aria-activedescendant */
    out << "<div class=\"gcd-items\" role=\"listbox\" id=\"${h(listboxId)}\" aria-label=\"${h(ariaLabel)}\">"
    int itemIdx = 0
    countries.each { country ->
      String dialCode = country?.dial_code?.toString() ?: ""
      String cName = country?.name?.toString() ?: ""
      String emoji = country?.emoji?.toString() ?: ""
      boolean isSel = (dialCode == selectedIsd)
      String selClass = isSel ? " gcd-selected" : ""
      String ariaSel = isSel ? "true" : "false"
      String itemId = "${id}-opt-${itemIdx++}"

      out << "<div id=\"${h(itemId)}\" class=\"gcd-item${selClass}\" role=\"option\""
      out << " aria-selected=\"${ariaSel}\""
      out << " data-dial-code=\"${h(dialCode)}\""
      out << " data-country=\"${h(cName)}\""
      out << " data-flag=\"${h(emoji)}\">"
      out << "<span class=\"gcd-item-flag\" aria-hidden=\"true\">${emoji}</span>"
      out << "<span class=\"gcd-item-code\" aria-hidden=\"true\">${h(dialCode)}</span>"
      out << "<span class=\"gcd-item-name\">${h(cName)}</span>"
      out << "</div>"
    }
    out << "<div class=\"gcd-no-results\" role=\"status\" style=\"display:none\">No countries found</div>"
    out << "</div>" // .gcd-items  role=listbox

    out << "</div>" // .gcd-menu

    /* Hidden form fields */
    out << "<input type=\"hidden\" class=\"gcd-hidden-isd\"     name=\"${h(isdCodeName)}\" value=\"${h(selectedIsd)}\">"
    out << "<input type=\"hidden\" class=\"gcd-hidden-country\" name=\"${h(countryName)}\" value=\"${h(selectedCountry)}\">"
    out << "<input type=\"hidden\" class=\"gcd-hidden-flag\"    name=\"${h(flagName)}\"    value=\"${h(selectedFlag)}\">"

    out << "</div>" // .gcd-wrapper
  }
}
