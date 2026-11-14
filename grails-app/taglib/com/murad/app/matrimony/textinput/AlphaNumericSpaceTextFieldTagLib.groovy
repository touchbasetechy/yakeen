package com.murad.app.matrimony.textinput

import com.murad.app.matrimony.GlassTooltipStylesTagLib

/**
 * Renders a glassmorphism-styled text input that accepts ONLY alphanumeric
 * characters [a-zA-Z0-9] and SINGLE spaces between words.
 *
 * Usage:
 *   <glass:tooltipStyles />            <- emit once in layout <head>
 *   <glass:textInputStyles />          <- emit once in layout <head>
 *   <glass:alphaNumericSpaceTextField name="fullName" value="${fullName}"
 *       label="Full name"
 *       helperText="Letters, numbers and single spaces only" />
 *
 * Rules enforced
 * --------------
 *  - Allowed: a-z, A-Z, 0-9 and the space character
 *  - Hyphen, underscore, dot and every other symbol are stripped on input
 *  - Consecutive spaces are collapsed to one by oninput
 *  - A leading space is removed by oninput
 *  - A trailing space is allowed while typing (so the next word can be typed),
 *    trimmed on blur, and rejected at submit time by the pattern attribute
 *
 * Features
 * --------
 *  - Floating label: resting when blurred+empty; raised on focus/value
 *  - Clear button (omitted when disabled=true)
 *  - Disabled state via .input-glass.is-disabled shell class
 *  - Tooltip on clear button (portal pattern — GlassTooltipStylesTagLib)
 *
 * CSS responsibility
 * ------------------
 *  - Tooltip tokens + portal rules -> GlassTooltipStylesTagLib
 *  - Input shell, label, clear btn, validation icon, helper text
 *                                  -> TextInputStylesTagLib
 *  This taglib emits NO CSS — only HTML and portal JS.
 *
 * Regex notes (v-flag safe — Chromium 112+ / Safari 17+)
 * ------------------------------------------------------
 *  No hyphen is used anywhere in the patterns, so v-flag character-class
 *  restrictions are not a concern. A literal space inside [...] is valid.
 *  \s is avoided because it also matches tabs and newlines.
 *
 *  pattern attr : [a-zA-Z0-9]+(?: [a-zA-Z0-9]+)*
 *  strip        : /[^a-zA-Z0-9 ]/g
 *  collapse     : / {2,}/g  -> " "
 *  leading trim : /^ /      -> ""
 *
 * Dependencies
 * ------------
 *  - GlassTooltipStylesTagLib   (same package)
 *  - TextInputStylesTagLib      (same package)
 *  - Font Awesome 6 (fa-solid fa-xmark)
 */
class AlphaNumericSpaceTextFieldTagLib {

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
   * @attr label              — floating-label text
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
   */
  def alphaNumericSpaceTextField = { attrs ->
    if (!attrs.name) {
      throwTagError("Tag [alphaNumericSpaceTextField] is missing required attribute [name]")
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
      // Single space triggers :placeholder-shown reliably across all browsers.
      out << 'placeholder=" " '
    } else if (placeholder) {
      out << "placeholder=\"${placeholder.encodeAsHTML()}\" "
    }

    if (minlength) out << "minlength=\"${minlength}\" "
    if (maxlength) out << "maxlength=\"${maxlength}\" "

    String inputClasses = "form-control ${additionalClasses}".trim()
    out << "class=\"${inputClasses.encodeAsHTML()}\" "

    // HTML pattern is implicitly anchored ^(?:...)$
    //   [a-zA-Z0-9]+         leading alphanumeric block (no leading space)
    //   (?: [a-zA-Z0-9]+)*   zero or more: exactly ONE space + alphanumeric block
    // Single spaces are enforced by structure; trailing space is rejected.
    out << 'pattern="[a-zA-Z0-9]+(?: [a-zA-Z0-9]+)*" '
    out << 'title="Only letters, numbers, and single spaces between words are allowed" '

    out << 'inputmode="text" '
    out << "autocomplete=\"${autocomplete.encodeAsHTML()}\" "
    out << 'autocorrect="off" '
    out << 'autocapitalize="off" '
    out << 'spellcheck="false" '

    if (required) out << 'required '
    if (disabled) out << 'disabled '
    if (readonly) out << 'readonly '
    if (autofocus) out << 'autofocus '

    // oninput transforms, in order:
    //   1. Strip everything except a-z, A-Z, 0-9 and space
    //   2. Collapse runs of spaces to a single space
    //   3. Remove a leading space
    // The trailing space is kept so the user can keep typing the next word.
    String onInputHandler =
      'this.value=this.value.replace(/[^a-zA-Z0-9 ]/g,"")' +
        '.replace(/ {2,}/g," ")' +
        '.replace(/^ /,"");' +
        'if(typeof toggleClearButton==="function")toggleClearButton(this);'
    if (oninput) onInputHandler += " ${oninput}"
    out << "oninput=\"${onInputHandler.encodeAsHTML()}\" "

    // onblur: trim the trailing space left over from mid-typing.
    String onBlurHandler =
      'this.value=this.value.replace(/ +$/,"");' +
        'if(typeof toggleClearButton==="function")toggleClearButton(this);'
    out << "onblur=\"${onBlurHandler.encodeAsHTML()}\" "

    if (onchange) out << "onchange=\"${onchange.encodeAsHTML()}\" "

    out << '/>'

    if (!disabled) {
      String escapedId = id.encodeAsHTML()
      String placementAttr = autoPlacement ? '' : " data-tooltip-placement=\"${tooltipPlacement}\""

      out << "<button type=\"button\" class=\"clear-btn\"${placementAttr} "
      out << 'aria-label="Clear" '
      out << "data-tooltip-text=\"${tooltipText.encodeAsHTML()}\" "
      out << 'onclick="typeof clearInput===\'function\'&&clearInput(this)">'
      out << '<i class="fa-solid fa-xmark" aria-hidden="true"></i>'
      out << '<span class="glass-tooltip" aria-hidden="true">' + tooltipText.encodeAsHTML() + '</span>'
      out << '</button>'

      out << """<script>
(function(PLACEMENT, IS_AUTO){

  var _passiveOpt = false;
  try {
    var _o = Object.defineProperty({}, 'passive', { get: function(){ _passiveOpt = { passive: true }; } });
    window.addEventListener('_glassTest', null, _o);
    window.removeEventListener('_glassTest', null, _o);
  } catch(e){}

  var OPPOSITE = { top:'bottom', bottom:'top', left:'right', right:'left' };

  function setTransform(el, val){
    el.style.webkitTransform = val;
    el.style.transform       = val;
  }

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
    var br      = btn.getBoundingClientRect();
    var pr      = portal.getBoundingClientRect();
    var gap     = parseInt(getComputedStyle(document.documentElement)
                    .getPropertyValue('--tt-gap')) || 8;
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

    /* touchstart is non-passive so preventDefault() is honoured. */
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
