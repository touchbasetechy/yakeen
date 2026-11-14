package com.murad.app.matrimony.textinput


import com.murad.app.matrimony.GlassTooltipStylesTagLib

/**
 * Renders a glassmorphism-styled text input that accepts alphabetic
 * characters plus single spaces only (no digits, hyphens, underscores, or dots).
 * Consecutive spaces are collapsed to one by the oninput handler.
 *
 * Usage:
 *   <glass:tooltipStyles />            <- emit once in layout <head>
 *   <glass:textInputStyles />          <- emit once in layout <head>
 *   <glass:alphaTextField name="fullName" value="${fullName}"
 *       label="Full Name"
 *       helperText="Letters and single spaces only" />
 *
 * Features
 * --------
 *  - Accepts [a-zA-Z] plus single spaces only
 *  - Consecutive spaces collapsed to one by oninput
 *  - Floating label: centred/resting at blur+empty; raised at focus/value
 *  - Clear button (omitted when disabled=true)
 *  - Disabled state via .input-glass.is-disabled shell class
 *  - Tooltip on clear button (portal pattern — GlassTooltipStylesTagLib)
 *
 * CSS responsibility
 * ------------------
 *  - Tooltip tokens + portal rules  -> GlassTooltipStylesTagLib
 *  - Input shell, label, clear btn,
 *    validation icon, helper text   -> TextInputStylesTagLib
 *  This taglib emits NO CSS — only HTML and portal JS.
 *
 * Bootstrap compatibility
 * -----------------------
 *  <input> carries class="form-control" for Grails binding and hasErrors().
 *  TextInputStylesTagLib uses .input-glass input selectors (higher
 *  specificity) so glass styles always win over Bootstrap's .form-control.
 *
 * Portal JS
 * ---------
 *  Single unified IIFE: (function(PLACEMENT, IS_AUTO){...})('auto', true)
 *  Auto vs fixed placement handled via the two arguments.
 *  (Identical portal implementation to AlphaNumericTextFieldTagLib — the only
 *  differences in this taglib are the character set and its regexes.)
 *
 * Mobile-first & cross-browser notes
 * ------------------------------------
 *  HTML INPUT ATTRIBUTES
 *  - inputmode="text"     : standard text keyboard on Samsung Internet / old Android
 *  - autocomplete         : attr-controlled (default "off"); pass "name" etc. to enable
 *  - autocorrect="off"    : disables iOS Safari word correction for name-like fields
 *  - autocapitalize="words": capitalises each word's first letter — sensible default
 *                            for name fields; override via the autocapitalize attr
 *  - spellcheck="false"   : suppresses red-squiggle underlining in Chrome/Firefox/Safari
 *
 *  ONINPUT REGEX / PATTERN ATTRIBUTE — v-flag character class rules
 *  The HTML pattern attribute and inline JS event handlers are both validated
 *  by the browser using v-flag (Unicode sets) in Chromium 112+ / Safari 17+.
 *  Unlike the alphanumeric field, this field has no hyphen in its alphabet, so
 *  the hyphen-outside-the-class workaround is not needed here. The only
 *  separator is a literal space, and a literal space inside [...] IS valid
 *  in v-flag (only escape sequences like \u0020 or \x20 are rejected).
 *
 *  Pattern breakdown (HTML pattern is implicitly anchored ^(?:...)$):
 *    [a-zA-Z]+          one or more letters (required leading word)
 *    (?: [a-zA-Z]+)*    zero or more additional words, each preceded by
 *                       exactly one space — single spaces enforced by structure
 *
 *  oninput applies two transforms (see inline comments for detail):
 *    1. Strip:    /[^a-zA-Z ]/g
 *    2. Collapse: / (?= )/g
 *  No leading/trailing trim in oninput — trimming on every keystroke prevents
 *  the user from typing a trailing space mid-word-entry.
 *  Leading/trailing spaces are rejected at submit time by the pattern attr.
 *
 *  TOUCHSTART PASSIVE LISTENER
 *  - touchstart uses false (non-passive) so e.preventDefault() is honoured.
 *    All other listeners use _passiveOpt ({passive:true} when supported).
 *
 *  CLEAR BUTTON JS GUARD
 *  - onclick guards: typeof clearInput==="function"&&clearInput(this)
 *    so the button fails silently if the style taglib JS has not yet run.
 *
 * Dependencies
 * ------------
 *  - GlassTooltipStylesTagLib    (same package)
 *  - TextInputStylesTagLib  (same package)
 *  - Font Awesome 6 (fa-solid fa-xmark)
 */
class AlphaTextFieldTagLib {

  static namespace = "glass"

  private GlassTooltipStylesTagLib getTooltipStylesTagLib() {
    grailsApplication.mainContext.getBean(GlassTooltipStylesTagLib)
  }

  private TextInputStylesTagLib getTextInputStylesTagLib() {
    grailsApplication.mainContext.getBean(TextInputStylesTagLib)
  }

  /**
   * @attr name               REQUIRED — name attribute for the input
   * @attr value              — initial value
   * @attr id                 — id attribute (defaults to name)
   * @attr label               — floating-label text
   * @attr placeholder        — placeholder (ignored when floatingLabel=true)
   * @attr helperText         — small hint shown below the field
   * @attr required           — true/false (default false)
   * @attr minlength          — minimum character length
   * @attr maxlength          — maximum character length
   * @attr class              — extra CSS classes on <input>
   * @attr disabled           — true/false (default false)
   * @attr readonly           — true/false (default false)
   * @attr autofocus          — true/false (default false)
   * @attr onchange           — JS onChange handler
   * @attr oninput            — JS onInput handler (appended after built-ins)
   * @attr wrapper            — wrap in .glass-form-group (default true)
   * @attr floatingLabel      — use floating label (default true)
   * @attr tooltipPlacement   — top | right | bottom | left | auto (default auto)
   * @attr tooltip            — tooltip text (default "Clear")
   * @attr autocomplete       — HTML autocomplete value (default "off")
   * @attr autocapitalize     — HTML autocapitalize value (default "words")
   */
  def alphaTextField = { attrs ->
    if (!attrs.name) {
      throwTagError("Tag [alphaTextField] is missing required attribute [name]")
    }

    String name = attrs.name
    String id = attrs.id ?: name
    String value = attrs.value ?: ''
    String label = attrs.label ?: ''
    String placeholder = attrs.placeholder ?: ''
    String helperText = attrs.helperText ?: ''
    String minlength = attrs.minlength ?: ''
    String maxlength = attrs.maxlength ?: ''
    String additionalClasses = attrs.class ?: ''
    boolean required = attrs.required?.toString()?.toBoolean() ?: false
    boolean disabled = attrs.disabled?.toString()?.toBoolean() ?: false
    boolean readonly = attrs.readonly?.toString()?.toBoolean() ?: false
    boolean autofocus = attrs.autofocus?.toString()?.toBoolean() ?: false
    boolean wrapper = attrs.wrapper?.toString()?.toBoolean() != false
    boolean floatingLabel = attrs.floatingLabel?.toString()?.toBoolean() != false
    String onchange = attrs.onchange ?: ''
    String oninput = attrs.oninput ?: ''
    String autocomplete = attrs.autocomplete ?: 'off'
    String autocapitalize = attrs.autocapitalize ?: 'words'

    List validPlacements = ['top', 'right', 'bottom', 'left']
    String rawPlacement = attrs.tooltipPlacement?.toString()?.toLowerCase() ?: 'auto'
    boolean autoPlacement = !validPlacements.contains(rawPlacement)
    String tooltipPlacement = autoPlacement ? 'auto' : rawPlacement
    String tooltipText = attrs.tooltip ?: 'Clear'

    tooltipStylesTagLib.emitTooltipStylesOnce()
    textInputStylesTagLib.emitTextInputStylesOnce()

    if (wrapper) {
      String wrapperClass = 'glass-form-group'
      if (floatingLabel && label) wrapperClass += ' has-floating-label'
      out << "<div class=\"${wrapperClass}\">"
      if (label && !floatingLabel) {
        out << "<label for=\"${id.encodeAsHTML()}\">${label.encodeAsHTML()}</label>"
      }
    }

    String shellClass = disabled ? 'input-glass is-disabled' : 'input-glass'
    if (value) shellClass += ' has-value'
    out << "<div class=\"${shellClass}\">"

    out << '<input '
    out << 'type="text" '
    out << "name=\"${name.encodeAsHTML()}\" "
    out << "id=\"${id.encodeAsHTML()}\" "
    if (value) out << "value=\"${value.encodeAsHTML()}\" "

    if (floatingLabel && label) {
      // Single space triggers :placeholder-shown reliably across all browsers,
      // driving the CSS blur-down / raise animation for the floating label.
      out << 'placeholder=" " '
    } else if (placeholder) {
      out << "placeholder=\"${placeholder.encodeAsHTML()}\" "
    }

    if (minlength) out << "minlength=\"${minlength}\" "
    if (maxlength) out << "maxlength=\"${maxlength}\" "

    String inputClasses = "form-control ${additionalClasses}".trim()
    out << "class=\"${inputClasses.encodeAsHTML()}\" "

    // v-flag (Unicode sets, Chromium 112+ / Safari 17+) character-class rules:
    //   - A literal space inside [...] IS valid in v-flag (only escape sequences
    //     like \u0020 or \x20 are rejected as "Invalid character in character class").
    //   - This alphabet has no hyphen, so none of the hyphen workarounds needed
    //     by the alphanumeric field apply here.
    //
    // Pattern breakdown (HTML pattern is implicitly anchored ^(?:...)$):
    //   [a-zA-Z]+        one or more letters (required leading word)
    //   (?: [a-zA-Z]+)*  zero or more additional words, each preceded by exactly
    //                    one space — single spaces enforced by structure
    //
    // \s is intentionally avoided: it also matches tabs and newlines.
    out << 'pattern="[a-zA-Z]+(?: [a-zA-Z]+)*" '
    out << 'title="Only letters and single spaces are allowed" '

    out << 'inputmode="text" '                                    // standard keyboard on Samsung Internet / old Android
    out << "autocomplete=\"${autocomplete.encodeAsHTML()}\" "     // default "off"; caller passes "name" etc. to enable
    out << 'autocorrect="off" '                                   // iOS Safari: disable word correction for name-like fields
    out << "autocapitalize=\"${autocapitalize.encodeAsHTML()}\" " // default "words": capitalise each word's first letter
    out << 'spellcheck="false" '                                  // Chrome/Firefox/Safari: suppress red-squiggle underlining

    if (required) out << 'required '
    if (disabled) out << 'disabled '
    if (readonly) out << 'readonly '
    if (autofocus) out << 'autofocus '

    // oninput applies two transforms in order:
    //   1. Strip disallowed chars: /[^a-zA-Z ]/g
    //        Literal space is valid inside [...] in v-flag; no digits/hyphen/
    //        underscore/dot are permitted in this field's alphabet.
    //   2. Collapse consecutive spaces: / (?= )/g
    //        Lookahead removes the first of any two adjacent spaces, leaving
    //        at most one space between words regardless of how many were typed.
    //
    // NO leading/trailing trim here — trimming on every keystroke prevents the
    // user from ever typing a space at the end of a word mid-input.
    // Leading/trailing spaces are blocked at submit time by the pattern attr.
    String onInputHandler =
      'this.value=this.value.replace(/[^a-zA-Z ]/g,"")' +
        '.replace(/ (?= )/g,"");' +
        'if(typeof toggleClearButton==="function")toggleClearButton(this);'
    if (oninput) onInputHandler += " ${oninput}"
    out << "oninput=\"${onInputHandler.encodeAsHTML()}\" "
    if (onchange) out << "onchange=\"${onchange.encodeAsHTML()}\" "

    out << '/>'

    if (!disabled) {
      String escapedId = id.encodeAsHTML()
      String placementAttr = autoPlacement ? '' : " data-tooltip-placement=\"${tooltipPlacement}\""

      out << "<button type=\"button\" class=\"clear-btn\"${placementAttr} "
      out << 'aria-label="Clear" '
      out << "data-tooltip-text=\"${tooltipText.encodeAsHTML()}\" "
      // Guard clearInput so the button fails silently if the style taglib JS has not yet run.
      out << 'onclick="typeof clearInput===\'function\'&&clearInput(this)">'
      out << '<i class="fa-solid fa-xmark" aria-hidden="true"></i>'
      out << '<span class="glass-tooltip" aria-hidden="true">' + tooltipText.encodeAsHTML() + '</span>'
      out << '</button>'

      out << """<script>
(function(PLACEMENT, IS_AUTO){

  /* Passive listener support detection (IE11 / old Android fallback) */
  var _passiveOpt = false;
  try {
    var _o = Object.defineProperty({}, 'passive', { get: function(){ _passiveOpt = { passive: true }; } });
    window.addEventListener('_glassTest', null, _o);
    window.removeEventListener('_glassTest', null, _o);
  } catch(e){}

  var OPPOSITE = { top:'bottom', bottom:'top', left:'right', right:'left' };

  /* -webkit-transform helper — Safari < 9, older Android WebKit */
  function setTransform(el, val){
    el.style.webkitTransform = val;
    el.style.transform       = val;
  }

  function getBtn(){
    var inp = document.getElementById('${escapedId}');
    return inp ? inp.parentElement.querySelector('.clear-btn') : null;
  }

  /* Coarse placement from button's viewport quadrant (auto mode only) */
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
    var br      = btn.getBoundingClientRect();
    var pr      = portal.getBoundingClientRect();
    /* --tt-gap: IE11 returns "" from getPropertyValue so parseInt falls back to 8 */
    var gap     = parseInt(getComputedStyle(document.documentElement)
                    .getPropertyValue('--tt-gap')) || 8;
    /* scrollX/scrollY: undefined in IE11 — pageXOffset/pageYOffset are the fallbacks */
    var scrollX = window.scrollX !== undefined ? window.scrollX : (window.pageXOffset || 0);
    var scrollY = window.scrollY !== undefined ? window.scrollY : (window.pageYOffset || 0);
    var top, left;
    if (placement === 'top') {
      top  = br.top    + scrollY - pr.height - gap;
      left = br.left   + scrollX + (br.width - pr.width) / 2;
    } else if (placement === 'bottom') {
      top  = br.bottom + scrollY + gap;
      left = br.left   + scrollX + (br.width - pr.width) / 2;
    } else if (placement === 'right') {
      top  = br.top    + scrollY + (br.height - pr.height) / 2;
      left = br.right  + scrollX + gap;
    } else {
      top  = br.top    + scrollY + (br.height - pr.height) / 2;
      left = br.left   + scrollX - pr.width - gap;
    }
    return { top: top, left: left };
  }

  /* Overflow check + flip (auto mode only) */
  function resolvedPlacement(btn, portal){
    var candidate = coarsePlacement(btn);
    portal.setAttribute('data-tooltip-placement', candidate);
    portal.style.visibility = 'hidden';
    portal.style.opacity    = '0';
    portal.style.display    = 'block';
    var coords = portalCoords(btn, candidate, portal);
    portal.style.top  = coords.top  + 'px';
    portal.style.left = coords.left + 'px';
    var br   = portal.getBoundingClientRect();
    var vw   = window.innerWidth  || document.documentElement.clientWidth;
    var vh   = window.innerHeight || document.documentElement.clientHeight;
    var clips = (candidate === 'top'    && br.top    < 0)
             || (candidate === 'bottom' && br.bottom > vh)
             || (candidate === 'left'   && br.left   < 0)
             || (candidate === 'right'  && br.right  > vw);
    portal.style.visibility = '';
    portal.style.display    = '';
    return clips ? OPPOSITE[candidate] : candidate;
  }

  var _portal    = null;
  var _hideTimer = null;

  function getOrCreatePortal(btn){
    if (!_portal) {
      _portal = document.createElement('span');
      _portal.className = 'glass-tooltip glass-tooltip-portal';
      _portal.setAttribute('aria-hidden', 'true');
      if (!IS_AUTO) _portal.setAttribute('data-tooltip-placement', PLACEMENT);
      _portal.style.position = 'absolute';
      _portal.style.margin   = '0';
      document.body.appendChild(_portal);
    }
    /* btn.dataset: IE10 fallback to getAttribute */
    _portal.textContent = (btn.dataset
      ? btn.dataset.tooltipText
      : btn.getAttribute('data-tooltip-text')) || 'Clear';
    return _portal;
  }

  function showPortal(btn){
    if (_hideTimer) { clearTimeout(_hideTimer); _hideTimer = null; }
    var portal    = getOrCreatePortal(btn);
    var placement = IS_AUTO ? resolvedPlacement(btn, portal) : PLACEMENT;
    portal.setAttribute('data-tooltip-placement', placement);
    var coords    = portalCoords(btn, placement, portal);
    portal.style.top     = coords.top  + 'px';
    portal.style.left    = coords.left + 'px';
    portal.style.opacity = '1';
    setTransform(portal, 'scale(1)');
    btn._glassPortal = portal;
  }

  function hidePortal(btn, delay){
    var doHide = function(){
      if (btn._glassPortal) {
        btn._glassPortal.style.opacity = '0';
        setTransform(btn._glassPortal, 'scale(0.88)');
      }
    };
    if (delay) { _hideTimer = setTimeout(doHide, delay); } else { doHide(); }
  }

  function updatePortalPosition(){
    var btn = getBtn();
    if (!btn || !btn._glassPortal) return;
    var placement = IS_AUTO
      ? (btn._glassPortal.getAttribute('data-tooltip-placement') || coarsePlacement(btn))
      : PLACEMENT;
    var coords = portalCoords(btn, placement, btn._glassPortal);
    btn._glassPortal.style.top  = coords.top  + 'px';
    btn._glassPortal.style.left = coords.left + 'px';
  }

  function bindBtn(){
    var btn = getBtn();
    if (!btn) return;

    btn.addEventListener('mouseenter',  function(){ showPortal(btn); },      _passiveOpt);
    btn.addEventListener('focusin',     function(){ showPortal(btn); },      _passiveOpt);
    btn.addEventListener('mouseleave',  function(){ hidePortal(btn, 0); },   _passiveOpt);
    btn.addEventListener('focusout',    function(){ hidePortal(btn, 0); },   _passiveOpt);

    /* touchstart uses false (non-passive) so e.preventDefault() is honoured.
     * Chrome 56+ silently drops preventDefault() in passive listeners.
     * preventDefault() only fires when the button is the direct touch target —
     * this stops iOS Safari from hiding the tooltip when a parent container
     * intercepts a scroll, while not blocking scroll on non-button swipes.    */
    btn.addEventListener('touchstart', function(e){
      if (e.target === btn || btn.contains(e.target)) {
        e.preventDefault();
      }
      showPortal(btn);
    }, false);

    btn.addEventListener('touchend',    function(){ hidePortal(btn, 320); }, _passiveOpt);
    btn.addEventListener('touchcancel', function(){ hidePortal(btn, 0); },   _passiveOpt);
  }

  window.addEventListener('resize', updatePortalPosition, _passiveOpt);
  window.addEventListener('scroll', updatePortalPosition, _passiveOpt);

  if (document.readyState === 'loading'){
    document.addEventListener('DOMContentLoaded', bindBtn);
  } else {
    bindBtn();
  }

})('${tooltipPlacement}', ${autoPlacement});
</script>"""
    } // end !disabled

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
}
