package com.murad.app.matrimony.textinput


import com.murad.app.matrimony.GlassTooltipStylesTagLib

/**
 * Tag library for rendering a glassmorphism-styled email input field
 * with built-in email validation and floating label.
 *
 * Usage:
 *   <glass:tooltipStyles />
 *   <glass:textInputStyles />
 *   <glass:emailField name="email" value="${email}" label="Email Address"
 *                     helperText="Enter your email address" />
 *
 * Floating label behaviour
 * ─────────────────────────
 *  Default (blur + empty) → centred inside shell, font-size 1rem
 *  Raised  (focus / value) → top: 6px, font-size: 0.5rem (50 % of base)
 *  Restores on blur when value is empty via :placeholder-shown reversal.
 *  placeholder=" " (single space) emitted so :placeholder-shown fires
 *  reliably on all browsers including type="email".
 *
 * CSS responsibility
 * ──────────────────
 *  • Tooltip tokens + portal rules  → GlassTooltipStylesTagLib
 *  • Input shell, label, clear btn,
 *    validation icon, helper text   → TextInputStylesTagLib
 *  • Email-specific overrides       → this taglib
 *      – .glass-helper-text.helper-text-hidden  (collapse animation)
 *      – .validation-icon mobile-first positioning + colour overrides
 *
 * Bootstrap compatibility
 * ────────────────────────
 *  The <input> carries class="form-control" for Grails form binding and
 *  validation error hooks. TextInputStylesTagLib uses .input-glass input
 *  selectors which have higher specificity than Bootstrap's .form-control,
 *  so glass background, border, and box-shadow always win at every state.
 *
 * Disabled state
 * ───────────────
 *  Handled entirely by .input-glass.is-disabled on the shell (CSS in
 *  TextInputStylesTagLib). No extra class is needed on the <input>.
 *
 * Dependencies
 * ────────────
 *  • GlassTooltipStylesTagLib    (same package, looked up via grailsApplication)
 *  • TextInputStylesTagLib  (same package, looked up via grailsApplication)
 *  • Font Awesome 6 (fa-solid fa-xmark)
 */
class EmailFieldTagLib {

  static namespace = "glass"

  // ── Lazy Spring-context lookups ───────────────────────────────────────────
  private GlassTooltipStylesTagLib getTooltipStylesTagLib() {
    grailsApplication.mainContext.getBean(GlassTooltipStylesTagLib)
  }

  private TextInputStylesTagLib getTextInputStylesTagLib() {
    grailsApplication.mainContext.getBean(TextInputStylesTagLib)
  }

  // Guard key — email validation JS emitted once per page
  private static final String EMAIL_JS_EMITTED = 'glass_emailJsEmitted'

  // ─────────────────────────────────────────────────────────────────────────
  // glass:emailField
  // ─────────────────────────────────────────────────────────────────────────
  /**
   * @attr name               REQUIRED
   * @attr value              – initial value
   * @attr id                 – id attribute (defaults to name)
   * @attr label              – floating-label text
   * @attr placeholder        – placeholder (ignored when floatingLabel=true)
   * @attr helperText         – hint shown below the field
   * @attr required           – true/false (default false)
   * @attr maxlength          – default 254 (RFC 5321)
   * @attr class              – extra CSS classes on <input>
   * @attr disabled           – true/false (default false)
   * @attr readonly           – true/false (default false)
   * @attr autofocus          – true/false (default false)
   * @attr autocomplete       – default "email"
   * @attr onchange           – JS onChange handler
   * @attr oninput            – JS onInput handler (appended)
   * @attr wrapper            – wrap in .glass-form-group (default true)
   * @attr showValidation     – show ✓/✕ icons (default true)
   * @attr floatingLabel      – use floating label (default true)
   * @attr tooltipPlacement   – top|right|bottom|left|auto (default auto)
   * @attr tooltip            – tooltip bubble text (default "Clear")
   */
  def emailField = { attrs ->
    if (!attrs.name) {
      throwTagError("Tag [emailField] is missing required attribute [name]")
    }

    // ── Attribute extraction ────────────────────────────────────────────────
    String name = attrs.name
    String id = attrs.id ?: name
    String value = attrs.value ?: ''
    String label = attrs.label ?: ''
    String placeholder = attrs.placeholder ?: ''
    String helperText = attrs.helperText ?: ''
    String additionalClasses = attrs.class ?: ''
    String autocomplete = attrs.autocomplete ?: 'email'
    String maxlength = attrs.maxlength ?: '254'
    boolean required = attrs.required?.toString()?.toBoolean() ?: false
    boolean disabled = attrs.disabled?.toString()?.toBoolean() ?: false
    boolean readonly = attrs.readonly?.toString()?.toBoolean() ?: false
    boolean autofocus = attrs.autofocus?.toString()?.toBoolean() ?: false
    boolean wrapper = attrs.wrapper?.toString()?.toBoolean() != false
    boolean showValidation = attrs.showValidation?.toString()?.toBoolean() != false
    boolean floatingLabel = attrs.floatingLabel?.toString()?.toBoolean() != false
    String onchange = attrs.onchange ?: ''
    String oninput = attrs.oninput ?: ''

    // ── Tooltip placement ───────────────────────────────────────────────────
    List validPlacements = ['top', 'right', 'bottom', 'left']
    String rawPlacement = attrs.tooltipPlacement?.toString()?.toLowerCase() ?: 'auto'
    boolean autoPlacement = !validPlacements.contains(rawPlacement)
    String tooltipPlacement = autoPlacement ? 'auto' : rawPlacement
    String tooltipText = attrs.tooltip ?: 'Clear'

    // ── Emit CSS — fully delegated, this taglib owns no base CSS ───────────
    tooltipStylesTagLib.emitTooltipStylesOnce()
    textInputStylesTagLib.emitTextInputStylesOnce()

    // ── Email-specific CSS ─────────────────────────────────────────────────
    // Only rules not covered by either reference taglib.
    out << '''
<style>
/* Helper-text collapse animation (email-specific) */
.glass-helper-text.helper-text-hidden {
    height             : 0         !important;
    overflow           : hidden    !important;
    margin             : 0         !important;
    padding            : 0         !important;
    opacity            : 0         !important;
    -webkit-transition : all 0.3s ease !important;
            transition : all 0.3s ease !important;
}
/* Validation icons — mobile-first offsets match TextInputStylesTagLib.
   Mobile : button 44px at right: 8px  -> icon right: 60px  (8 + 44 + 8 gap)
   Desktop: button 20px at right: 12px -> icon right: 40px  (12 + 20 + 8 gap)
   Scoped to .input-glass so specificity (0,2,0) overrides the base taglib's
   same-specificity rule, keeping email-specific colours intact. */
.input-glass .validation-icon {
    right    : 60px;
    font-size: 0.85rem;
}
@media (hover: hover) and (pointer: fine) {
    .input-glass .validation-icon {
        right: 40px;
    }
}
.input-glass .validation-icon.valid   { color: #6ee7b7; }
.input-glass .validation-icon.invalid { color: #fca5a5; }
</style>
'''

    // ── Email validation JS — emitted once per page ─────────────────────────
    if (!pageScope[EMAIL_JS_EMITTED]) {
      pageScope[EMAIL_JS_EMITTED] = true
      out << '''
<script>
if (typeof window.glassEmailInitialized === 'undefined') {
  window.glassEmailInitialized = true;

  window.emailRegex = /^[^@]+@[^@.]+\\.[^@.]+$/;

  window.filterEmailInput = function(input) {
    var value = input.value;
    var atCount = (value.match(/@/g) || []).length;
    if (atCount > 1) {
      var firstAtIndex = value.indexOf('@');
      value = value.substring(0, firstAtIndex + 1) +
              value.substring(firstAtIndex + 1).replace(/@/g, '');
    }
    var atIndex = value.indexOf('@');
    if (atIndex !== -1) {
      var beforeAt = value.substring(0, atIndex + 1);
      var afterAt  = value.substring(atIndex + 1);
      var dotCount = (afterAt.match(/\\./g) || []).length;
      if (dotCount > 1) {
        var firstDotIndex  = afterAt.indexOf('.');
        var cleanedAfterAt = afterAt.substring(0, firstDotIndex + 1) +
                             afterAt.substring(firstDotIndex + 1).replace(/\\./g, '');
        value = beforeAt + cleanedAfterAt;
      }
    }
    if (input.value !== value) {
      var cursorPos = input.selectionStart;
      input.value = value;
      input.setSelectionRange(cursorPos - 1, cursorPos - 1);
    }
  };

  window.validateEmail = function(email) {
    if (!email) return { valid: null, message: '' };
    if (email.indexOf('@') === -1)
      return { valid: false, message: 'Email must contain an @ symbol' };
    var parts = email.split('@');
    if (parts.length !== 2 || !parts[0] || !parts[1])
      return { valid: false, message: 'There should be text before and after the @ symbol' };
    var domain = parts[1];
    if (domain && domain.indexOf('.') === -1)
      return { valid: false, message: 'There should be a . (dot) after the domain' };
    if (domain.indexOf('.') !== -1) {
      var domainParts  = domain.split('.');
      var textAfterDot = domainParts[domainParts.length - 1];
      if (!textAfterDot || textAfterDot.length === 0)
        return { valid: false, message: 'There should be text after the . (dot)' };
      if (textAfterDot.length > 0)
        return { valid: true, message: 'Valid email' };
    }
    if (!window.emailRegex.test(email))
      return { valid: false, message: 'There should be text before and after the @ symbol' };
    return { valid: true, message: 'Valid email' };
  };

  window.clearEmailHelperTextTimer = function(input) {
    if (input.validationTimeout) {
      clearTimeout(input.validationTimeout);
      input.validationTimeout = null;
    }
  };

  window.scheduleEmailHelperTextHide = function(input) {
    clearEmailHelperTextTimer(input);
    var formGroup  = input.closest('.glass-form-group');
    var helperText = formGroup ? formGroup.querySelector('.glass-helper-text') : null;
    if (helperText && input.value) {
      input.validationTimeout = setTimeout(function() {
        var container   = input.closest('.input-glass');
        var validIcon   = container.querySelector('.validation-icon.valid');
        var invalidIcon = container.querySelector('.validation-icon.invalid');
        if (validIcon)   validIcon.style.display   = 'none';
        if (invalidIcon) invalidIcon.style.display = 'none';
        if (helperText.dataset.originalText) {
          helperText.innerHTML = '<span style="color:rgba(255,255,255,0.9);">' +
                                  helperText.dataset.originalText + '</span>';
          helperText.classList.add('helper-text-hidden');
        }
      }, 8000);
    }
  };

  window.updateEmailValidation = function(input) {
    filterEmailInput(input);
    var container   = input.closest('.input-glass');
    var formGroup   = container.closest('.glass-form-group');
    var validIcon   = container.querySelector('.validation-icon.valid');
    var invalidIcon = container.querySelector('.validation-icon.invalid');
    var helperText  = formGroup ? formGroup.querySelector('.glass-helper-text') : null;
    var value       = input.value.trim();
    if (helperText && !helperText.dataset.originalText) {
      helperText.dataset.originalText = helperText.textContent;
    }
    if (typeof window.toggleClearButton === 'function') toggleClearButton(input);
    clearEmailHelperTextTimer(input);
    if (!value) {
      if (formGroup)   formGroup.classList.remove('has-content');
      if (validIcon)   validIcon.style.display   = 'none';
      if (invalidIcon) invalidIcon.style.display = 'none';
      if (helperText && helperText.dataset.originalText) {
        helperText.innerHTML = '<span style="color:rgba(255,255,255,0.9);">' +
                                helperText.dataset.originalText + '</span>';
        helperText.classList.add('helper-text-hidden');
      }
      return;
    }
    if (formGroup) formGroup.classList.add('has-content');
    if (helperText) helperText.classList.remove('helper-text-hidden');
    var validation = validateEmail(value);
    if (validation.valid) {
      if (validIcon)   validIcon.style.display   = 'block';
      if (invalidIcon) invalidIcon.style.display = 'none';
      if (helperText) {
        helperText.innerHTML =
          '<span style="color:rgba(255,255,255,0.9);font-size:1.2em;">\u2713</span> ' +
          '<span style="color:rgba(255,255,255,0.9);">Valid email</span>';
      }
    } else {
      if (validIcon)   validIcon.style.display   = 'none';
      if (invalidIcon) invalidIcon.style.display = 'block';
      if (helperText) {
        helperText.innerHTML =
          '<span style="color:rgba(255,255,255,0.9);font-size:1.2em;">\u2715</span> ' +
          '<span style="color:rgba(255,255,255,0.9);">' + validation.message + '</span>';
      }
    }
    scheduleEmailHelperTextHide(input);
  };

  window.handleEmailFocus = function(input) {
    clearEmailHelperTextTimer(input);
    updateEmailValidation(input);
  };

  window.handleEmailBlur = function(input) {
    scheduleEmailHelperTextHide(input);
  };

  document.addEventListener('DOMContentLoaded', function() {
    var inputs = document.querySelectorAll('.input-glass input[type="email"]');
    for (var i = 0; i < inputs.length; i++) {
      var inp = inputs[i];
      var fg  = inp.closest('.glass-form-group');
      if (inp.value.trim() && fg) fg.classList.add('has-content');
      updateEmailValidation(inp);
    }
  });
}
</script>
'''
    }

    // ── Outer wrapper ───────────────────────────────────────────────────────
    if (wrapper) {
      String wrapperClass = 'glass-form-group'
      if (floatingLabel && label) wrapperClass += ' has-floating-label'
      if (value) wrapperClass += ' has-content'
      out << "<div class=\"${wrapperClass}\">"
      if (label && !floatingLabel) {
        out << "<label for=\"${id.encodeAsHTML()}\">${label.encodeAsHTML()}</label>"
      }
    }

    // ── .input-glass shell ──────────────────────────────────────────────────
    // is-disabled on the shell drives all disabled styling via CSS.
    // has-value stamps the JS-fallback class for pre-filled server-side values.
    String shellClass = disabled ? 'input-glass is-disabled' : 'input-glass'
    if (value) shellClass += ' has-value'
    out << "<div class=\"${shellClass}\">"

    // ── <input> ─────────────────────────────────────────────────────────────
    out << '<input '
    out << 'type="email" '
    out << "name=\"${name.encodeAsHTML()}\" "
    out << "id=\"${id.encodeAsHTML()}\" "
    if (value) out << "value=\"${value.encodeAsHTML()}\" "

    if (floatingLabel && label) {
      // Single space — non-empty placeholder makes :placeholder-shown fire
      // reliably on type="email" inputs across all browsers.
      out << 'placeholder=" " '
    } else if (placeholder) {
      out << "placeholder=\"${placeholder.encodeAsHTML()}\" "
    }

    out << "maxlength=\"${maxlength}\" "
    out << "autocomplete=\"${autocomplete.encodeAsHTML()}\" "

    // form-control kept for Grails form binding and hasErrors() integration.
    // TextInputStylesTagLib selectors have higher specificity and win.
    String inputClasses = "form-control ${additionalClasses}".trim()
    out << "class=\"${inputClasses.encodeAsHTML()}\" "

    if (required) out << 'required '
    if (disabled) out << 'disabled '
    if (readonly) out << 'readonly '
    if (autofocus) out << 'autofocus '

    // updateEmailValidation calls filterEmailInput + toggleClearButton internally.
    // toggleClearButton defined by TextInputStylesTagLib's emitted JS block.
    String onInputHandler = 'updateEmailValidation(this);'
    if (oninput) onInputHandler += " ${oninput}"
    out << "oninput=\"${onInputHandler.encodeAsHTML()}\" "
    out << 'onfocus="handleEmailFocus(this);" '
    out << 'onblur="handleEmailBlur(this);" '
    if (onchange) out << "onchange=\"${onchange.encodeAsHTML()}\" "

    out << '/>'

    // ── Validation icons ────────────────────────────────────────────────────
    if (showValidation) {
      out << '<span class="validation-icon valid">\u2713</span>'
      out << '<span class="validation-icon invalid">\u2715</span>'
    }

    // ── Clear button ────────────────────────────────────────────────────────
    // Not rendered when disabled=true.
    if (!disabled) {
      String escapedId = id.encodeAsHTML()
      String placementAttr = autoPlacement ? '' : " data-tooltip-placement=\"${tooltipPlacement}\""

      out << "<button type=\"button\" class=\"clear-btn\"${placementAttr} "
      out << 'aria-label="Clear" '
      out << "data-tooltip-text=\"${tooltipText.encodeAsHTML()}\" "
      out << 'onclick="clearInput(this)">'
      out << '<i class="fa-solid fa-xmark" aria-hidden="true"></i>'
      out << '<span class="glass-tooltip" aria-hidden="true">' + tooltipText.encodeAsHTML() + '</span>'
      out << '</button>'

      // ── Portal tooltip — single unified IIFE ────────────────────────────
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
    var scrollX = window.scrollX || window.pageXOffset || 0;
    var scrollY = window.scrollY || window.pageYOffset || 0;
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
    var br  = portal.getBoundingClientRect();
    var vw  = window.innerWidth  || document.documentElement.clientWidth;
    var vh  = window.innerHeight || document.documentElement.clientHeight;
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
    btn.addEventListener('touchstart',  function(){ showPortal(btn); },      _passiveOpt);
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

    // ── Floating label ──────────────────────────────────────────────────────
    if (floatingLabel && label) {
      out << "<label for=\"${id.encodeAsHTML()}\" class=\"floating-label\">${label.encodeAsHTML()}</label>"
    }

    out << '</div>' // .input-glass

    // Always rendered to maintain consistent spacing; hidden via CSS until user types.
    if (helperText) {
      out << "<div class=\"glass-helper-text\">${helperText.encodeAsHTML()}</div>"
    } else {
      out << '<div class="glass-helper-text">&nbsp;</div>'
    }

    if (wrapper) {
      out << '</div>' // .glass-form-group
    }
  }
}
