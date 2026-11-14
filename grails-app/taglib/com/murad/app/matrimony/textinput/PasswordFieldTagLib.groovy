package com.murad.app.matrimony.textinput

import com.murad.app.matrimony.GlassTooltipStylesTagLib

/**
 * Renders a glassmorphic password input field with real-time validation.
 * Displays validation messages for password strength requirements in a floating balloon.
 *
 * IMPORTANT: This taglib requires Font Awesome to be available (loaded automatically via CDN).
 *
 * Usage:
 * <glass:passwordField name="password" label="Password" helperText="Create a strong password" />
 *
 * Dependencies
 * ────────────
 *  • Font Awesome 6 (loaded via CDN, emitted once per page)
 *  • GlassTooltipStylesTagLib — owns all tooltip CSS tokens and portal styles.
 *    emitSharedAssetsOnce() calls tooltipStylesTagLib.emitTooltipStylesOnce()
 *    via a lazy Spring context lookup so tooltip CSS is emitted exactly once
 *    per page regardless of field order.
 *
 * CSS / JS emission strategy
 * ──────────────────────────
 *  • The shared <script> block (validation logic, portal tooltip functions) and the
 *    shared <style> block (toggle button, balloon, strength meter, etc.) are each
 *    guarded by 'glass_passwordStylesEmitted' and emitted exactly once per page.
 *  • Tooltip bubble CSS (.pw-tooltip-portal) is NOT emitted here — it is owned
 *    exclusively by GlassTooltipStylesTagLib via emitSharedAssetsOnce().
 *  • The per-field portal IIFE (<script> with field-specific IDs) is always emitted
 *    because each field instance needs its own portal spans bound to its own buttons.
 *
 * @attr name                REQUIRED - The name attribute for the input field
 * @attr value               - The initial value of the field
 * @attr id                  - The id attribute (defaults to name if not specified)
 * @attr label               - Label text for the field (also used as floating label)
 * @attr placeholder         - Placeholder text for the input (not needed with floating label)
 * @attr helperText          - Helper text displayed below the input
 * @attr required            - Whether the field is required (true/false)
 * @attr minlength           - Minimum length (defaults to 8)
 * @attr class               - Additional CSS classes to add
 * @attr disabled            - Whether the field is disabled
 * @attr readonly            - Whether the field is read-only
 * @attr autofocus           - Whether the field should receive focus on page load (true/false)
 * @attr autocomplete        - Autocomplete attribute (e.g., "current-password", "new-password", "off")
 * @attr onchange            - JavaScript onChange event handler
 * @attr oninput             - JavaScript onInput event handler
 * @attr wrapper             - Whether to wrap in .glass-form-group (defaults to true)
 * @attr showToggle          - Whether to show password visibility toggle (defaults to true)
 * @attr showPasswordTooltip - Tooltip text when password is hidden (default: "Show password")
 * @attr hidePasswordTooltip - Tooltip text when password is visible (default: "Hide password")
 * @attr clearTooltip        - Tooltip for the clear button (default: "Clear")
 * @attr closeTooltip        - Tooltip for the balloon close button (default: "Close")
 * @attr showStrength        - Whether to show password strength indicator (defaults to true)
 * @attr showValidation      - Whether to show validation balloon (defaults to true)
 * @attr floatingLabel       - Whether to use floating label (defaults to true)
 */
class PasswordFieldTagLib {

  static namespace = "glass"

  // ── Tooltip-styles delegate ───────────────────────────────────────────────
  // Property injection of cross-namespace taglibs is unreliable in Grails —
  // the bean may not be wired by the time the taglib is first used.
  // grailsApplication is inherited from WebAttributes and is always available;
  // we look up GlassTooltipStylesTagLib lazily via the Spring context.
  private GlassTooltipStylesTagLib getTooltipStylesTagLib() {
    grailsApplication.mainContext.getBean(GlassTooltipStylesTagLib)
  }

  // Guard key for shared script + style blocks (one emission per page)
  private static final String STYLES_EMITTED = 'glass_passwordStylesEmitted'

  def passwordField = { attrs ->
    if (!attrs.name) {
      throwTagError("Tag [passwordField] is missing required attribute [name]")
    }

    String name = attrs.name
    String id = attrs.id ?: name
    String value = attrs.value ?: ''
    String label = attrs.label ?: ''
    String placeholder = attrs.placeholder ?: ''
    String helperText = attrs.helperText ?: ''
    String minlength = attrs.minlength ?: '8'
    String autocomplete = attrs.autocomplete ?: 'current-password'
    String additionalClasses = attrs.class ?: ''
    String showPasswordTooltip = attrs.showPasswordTooltip ?: 'Show password'
    String hidePasswordTooltip = attrs.hidePasswordTooltip ?: 'Hide password'
    String clearTooltip = attrs.clearTooltip ?: 'Clear'
    String closeTooltip = attrs.closeTooltip ?: 'Close'
    boolean required = attrs.required?.toString()?.toBoolean() ?: false
    boolean disabled = attrs.disabled?.toString()?.toBoolean() ?: false
    boolean readonly = attrs.readonly?.toString()?.toBoolean() ?: false
    boolean autofocus = attrs.autofocus?.toString()?.toBoolean() ?: false
    boolean wrapper = attrs.wrapper?.toString()?.toBoolean() != false
    boolean showToggle = attrs.showToggle?.toString()?.toBoolean() != false
    boolean showStrength = attrs.showStrength?.toString()?.toBoolean() != false
    boolean showValidation = attrs.showValidation?.toString()?.toBoolean() != false
    boolean floatingLabel = attrs.floatingLabel?.toString()?.toBoolean() != false
    String onchange = attrs.onchange ?: ''
    String oninput = attrs.oninput ?: ''

    // ── Shared assets (once per page) ────────────────────────────────────────
    emitSharedAssetsOnce()

    // ── Wrapper ──────────────────────────────────────────────────────────────
    if (wrapper) {
      String wrapperClass = 'glass-form-group'
      if (floatingLabel && label) wrapperClass += ' has-floating-label'
      if (value) wrapperClass += ' has-content'
      out << "<div class=\"${wrapperClass}\">"
      if (label && !floatingLabel) {
        out << "<label for=\"${id.encodeAsHTML()}\">${label.encodeAsHTML()}</label>"
      }
    }

    // ── Input container ───────────────────────────────────────────────────────
    out << '<div class="input-glass password-field-container">'
    out << '<input type="password" '
    out << "name=\"${name.encodeAsHTML()}\" "
    out << "id=\"${id.encodeAsHTML()}\" "
    if (value) out << "value=\"${value.encodeAsHTML()}\" "
    if (floatingLabel && label) {
      out << 'placeholder="" '
    } else if (placeholder) {
      out << "placeholder=\"${placeholder.encodeAsHTML()}\" "
    }
    out << "minlength=\"${minlength}\" "
    out << "autocomplete=\"${autocomplete.encodeAsHTML()}\" "
    out << "class=\"form-control ${additionalClasses.trim()}\" "
    if (required) out << 'required '
    if (disabled) out << 'disabled '
    if (readonly) out << 'readonly '
    if (autofocus) out << 'autofocus '
    String onInputHandler = "updatePasswordValidation(this, ${minlength});"
    if (oninput) onInputHandler += " ${oninput}"
    out << "oninput=\"${onInputHandler.encodeAsHTML()}\" "
    out << "onfocus=\"handlePasswordFocus(this, ${minlength});\" "
    out << "onblur=\"handlePasswordBlur(this);\" "
    if (onchange) out << "onchange=\"${onchange.encodeAsHTML()}\" "
    out << '/>'

    // ── Toggle button ─────────────────────────────────────────────────────────
    if (showToggle) {
      String toggleId = id.encodeAsHTML() + '_toggle_btn'
      out << '<button type="button" '
      out << "id=\"${toggleId}\" "
      out << 'class="password-toggle-btn" '
      out << "data-tooltip-text=\"${showPasswordTooltip.encodeAsHTML()}\" "
      out << "onmousedown=\"savePasswordCursor('${id.encodeAsHTML()}', event)\" "
      out << "onclick=\"togglePasswordFieldVisibility('${id.encodeAsHTML()}', '${toggleId}', '${showPasswordTooltip.encodeAsHTML()}', '${hidePasswordTooltip.encodeAsHTML()}')\" "
      out << "aria-label=\"${showPasswordTooltip.encodeAsHTML()}\">"
      out << '<i class="fa-solid fa-eye"></i>'
      out << '<span class="pw-tooltip-source" aria-hidden="true"></span>'
      out << '</button>'
    }

    // ── Clear button ──────────────────────────────────────────────────────────
    out << '<button type="button" class="clear-btn" '
    out << "data-tooltip-text=\"${clearTooltip.encodeAsHTML()}\" "
    out << 'onclick="clearInput(this)" '
    out << "aria-label=\"${clearTooltip.encodeAsHTML()}\">"
    out << '<i class="fa-solid fa-xmark"></i>'
    out << '<span class="pw-tooltip-source" aria-hidden="true"></span>'
    out << '</button>'

    // ── Validation balloon ────────────────────────────────────────────────────
    if (showValidation) {
      String balloonId = id.encodeAsHTML() + '_validation'
      String closeBtnId = id.encodeAsHTML() + '_close_btn'
      out << "<div id=\"${balloonId}\" class=\"password-validation-balloon\">"
      out << '<button type="button" class="balloon-close-btn" '
      out << "id=\"${closeBtnId}\" "
      out << "onclick=\"closePasswordValidationBalloon('${balloonId}')\" "
      out << "data-tooltip-text=\"${closeTooltip.encodeAsHTML()}\" "
      out << "aria-label=\"${closeTooltip.encodeAsHTML()}\">"
      out << '<i class="fa-solid fa-xmark"></i>'
      out << '<span class="pw-tooltip-source" aria-hidden="true"></span>'
      out << '</button>'
      out << '<div class="validation-content"></div>'
      if (showStrength) {
        out << '<div class="password-strength-container-balloon">'
        out << '<div class="password-strength-track"><div class="password-strength-fill"></div></div>'
        out << '<div class="password-strength-text"></div>'
        out << '</div>'
      }
      out << '</div>'
    }

    // ── Floating label ────────────────────────────────────────────────────────
    if (floatingLabel && label) {
      out << "<label for=\"${id.encodeAsHTML()}\" class=\"floating-label\">${label.encodeAsHTML()}</label>"
    }

    out << '</div>' // .input-glass / .password-field-container

    if (helperText) {
      out << "<div class=\"glass-helper-text\">${helperText.encodeAsHTML()}</div>"
    } else {
      out << "<div class=\"glass-helper-text\">&nbsp;</div>"
    }

    if (wrapper) {
      out << '</div>'
    }

    // ── Per-field tooltip portal IIFE ─────────────────────────────────────────
    // Each button gets its own dedicated portal span on <body>.
    // Always emitted — each field instance needs portals bound to its own buttons.
    String escapedId = id.encodeAsHTML()
    String toggleBtnId = id.encodeAsHTML() + '_toggle_btn'
    String closeBtnId2 = id.encodeAsHTML() + '_close_btn'

    out << """
<script>
(function() {
    var inp       = document.getElementById('${escapedId}');
    var container = inp ? inp.closest('.input-glass') : null;

    function getToggleBtn()  { return document.getElementById('${toggleBtnId}'); }
    function getClearBtn()   { return container ? container.querySelector('.clear-btn') : null; }
    function getCloseBtn()   { return document.getElementById('${closeBtnId2}'); }

    function makePortal() {
        var p = document.createElement('span');
        p.className = 'pw-tooltip-portal';
        p.setAttribute('aria-hidden', 'true');
        p.style.position  = 'absolute';
        p.style.margin    = '0';
        p.style.opacity   = '0';
        p.style.transform = 'scale(0.88)';
        document.body.appendChild(p);
        return p;
    }

    function bindBtn(btn, portal) {
        if (!btn || !portal) return;
        btn._pwTooltipPortal = portal;
        btn.addEventListener('mouseenter', function() { window.pwTooltipShow(btn, portal); }, { passive: true });
        btn.addEventListener('focusin',    function() { window.pwTooltipShow(btn, portal); }, { passive: true });
        btn.addEventListener('mouseleave', function() { window.pwTooltipHide(btn); },         { passive: true });
        btn.addEventListener('focusout',   function() { window.pwTooltipHide(btn); },         { passive: true });
    }

    function init() {
        var toggleBtn = getToggleBtn();
        var clearBtn  = getClearBtn();
        var closeBtn  = getCloseBtn();
        if (toggleBtn) bindBtn(toggleBtn, makePortal());
        if (clearBtn)  bindBtn(clearBtn,  makePortal());
        if (closeBtn)  bindBtn(closeBtn,  makePortal());
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }
})();
</script>
"""
  }

  // ─────────────────────────────────────────────────────────────────────────
  // Private: emit shared assets once per page
  // ─────────────────────────────────────────────────────────────────────────
  /**
   * Emits the Font Awesome CDN link, shared validation/tooltip JS, and shared
   * CSS (toggle button, balloon, strength meter — but NOT .pw-tooltip-portal)
   * exactly once per page using the guard key 'glass_passwordStylesEmitted'.
   *
   * Tooltip bubble CSS (.pw-tooltip-portal and related tokens) is delegated to
   * GlassTooltipStylesTagLib.emitTooltipStylesOnce() via a lazy Spring context
   * lookup, which uses its own separate guard key 'glass_tooltipStylesEmitted'.
   * This ensures tooltip CSS is emitted exactly once even when multiple glass
   * field taglibs are used together on the same page.
   */
  private void emitSharedAssetsOnce() {
    // Tooltip tokens + portal rules: owned by GlassTooltipStylesTagLib.
    // Looked up via grailsApplication to avoid unreliable cross-namespace
    // property injection. Uses its own guard key so safe to call from any taglib.
    tooltipStylesTagLib.emitTooltipStylesOnce()

    if (pageScope[STYLES_EMITTED]) return
    pageScope[STYLES_EMITTED] = true

    // ── Font Awesome CDN ──────────────────────────────────────────────────────
    out << '<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css" integrity="sha512-Avb2QiuDEEvB4bZJYdft2mNjVShBftLdPG8FJ0V7irTLQ8Uu0qAmTt9NRDxyFVnmgJH1EBMGzDa7ZBen84pg==" crossorigin="anonymous" referrerpolicy="no-referrer" />'

    out << """
<script>
if (typeof window.glassPasswordInitialized === 'undefined') {
    window.glassPasswordInitialized = true;

    window.validatePasswordStrength = function(password, minLength) {
        var rules = [
            { id: 'minlength', test: password.length >= minLength,                                   message: 'At least ' + minLength + ' characters' },
            { id: 'uppercase', test: /[A-Z]/.test(password),                                        message: 'One uppercase letter' },
            { id: 'lowercase', test: /[a-z]/.test(password),                                        message: 'One lowercase letter' },
            { id: 'number',    test: /[0-9]/.test(password),                                        message: 'One number' },
            { id: 'special',   test: /[!@#\$%^&*()_+\\-=\\[\\]{};':"\\\\|,.<>\\/?]/.test(password), message: 'One special character' }
        ];
        var validCount = 0;
        rules.forEach(function(r) { if (r.test) validCount++; });
        var totalRules = rules.length;
        var strengthClass, strengthLabel;
        if      (validCount === 0) { strengthClass = '';            strengthLabel = ''; }
        else if (validCount === 1) { strengthClass = 'weak';        strengthLabel = 'Weak'; }
        else if (validCount === 2) { strengthClass = 'fair';        strengthLabel = 'Fair'; }
        else if (validCount === 3) { strengthClass = 'good';        strengthLabel = 'Good'; }
        else if (validCount === 4) { strengthClass = 'strong';      strengthLabel = 'Strong'; }
        else                       { strengthClass = 'very-strong'; strengthLabel = 'Very Strong'; }
        return { rules: rules, validCount: validCount, totalRules: totalRules, strengthClass: strengthClass, strengthLabel: strengthLabel };
    };

    var STRENGTH_GRADIENT =
        'linear-gradient(90deg,' +
        ' #f56565  0%,' +
        ' #f56565 15%, #f59e42 25%,' +
        ' #f59e42 35%, #ecc94b 45%,' +
        ' #ecc94b 55%, #48bb78 65%,' +
        ' #48bb78 75%, #38a169 85%,' +
        ' #38a169 100%)';
    var STRENGTH_CLIPS         = ['100%', '85%', '65%', '45%', '25%', '0%'];
    var STRENGTH_LABEL_COLOURS = ['#718096', '#f56565', '#f59e42', '#ecc94b', '#48bb78', '#38a169'];

    window.ensureStrengthBar = function(strengthContainer) {
        var fill = strengthContainer.querySelector('.password-strength-fill');
        if (!fill) {
            var track = document.createElement('div');
            track.className = 'password-strength-track';
            fill = document.createElement('div');
            fill.className = 'password-strength-fill';
            track.appendChild(fill);
            var textEl = strengthContainer.querySelector('.password-strength-text');
            strengthContainer.insertBefore(track, textEl || null);
        }
        return fill;
    };

    window.updateStrengthBar = function(strengthContainer, validCount, strengthLabel) {
        var fill   = window.ensureStrengthBar(strengthContainer);
        var textEl = strengthContainer.querySelector('.password-strength-text');
        fill.style.background = STRENGTH_GRADIENT;
        fill.style.clipPath   = 'inset(0 ' + STRENGTH_CLIPS[validCount] + ' 0 0)';
        if (textEl) {
            textEl.textContent = strengthLabel ? ('Password strength: ' + strengthLabel) : '';
            textEl.style.color = STRENGTH_LABEL_COLOURS[validCount];
        }
    };

    window.resetStrengthBar = function(strengthContainer) {
        var fill   = strengthContainer ? strengthContainer.querySelector('.password-strength-fill') : null;
        var textEl = strengthContainer ? strengthContainer.querySelector('.password-strength-text') : null;
        if (fill)   { fill.style.clipPath = 'inset(0 100% 0 0)'; }
        if (textEl) { textEl.textContent = ''; textEl.style.color = '#718096'; }
    };

    var PW_TOOLTIP_OPPOSITE = { top: 'bottom', bottom: 'top', left: 'right', right: 'left' };

    window.pwTooltipCoarsePlacement = function(btn) {
        var r  = btn.getBoundingClientRect();
        var vw = window.innerWidth  || document.documentElement.clientWidth;
        var vh = window.innerHeight || document.documentElement.clientHeight;
        var cx = r.left + r.width  / 2;
        var cy = r.top  + r.height / 2;
        if      (cy < vh * 0.25) return 'bottom';
        else if (cy > vh * 0.75) return 'top';
        else if (cx < vw * 0.5)  return 'right';
        else                     return 'left';
    };

    window.pwTooltipPortalCoords = function(btn, placement, portal) {
        var br  = btn.getBoundingClientRect();
        var pr  = portal.getBoundingClientRect();
        var gap = 10;
        var sx  = window.scrollX || window.pageXOffset || 0;
        var sy  = window.scrollY || window.pageYOffset || 0;
        var top, left;
        if (placement === 'top') {
            top  = br.top  + sy - pr.height - gap;
            left = br.left + sx + (br.width  - pr.width)  / 2;
        } else if (placement === 'bottom') {
            top  = br.bottom + sy + gap;
            left = br.left   + sx + (br.width  - pr.width) / 2;
        } else if (placement === 'right') {
            top  = br.top   + sy + (br.height - pr.height) / 2;
            left = br.right + sx + gap;
        } else {
            top  = br.top  + sy + (br.height - pr.height) / 2;
            left = br.left + sx - pr.width - gap;
        }
        return { top: top, left: left };
    };

    window.pwTooltipResolvedPlacement = function(btn, portal) {
        var candidate = window.pwTooltipCoarsePlacement(btn);
        portal.setAttribute('data-pw-tooltip-placement', candidate);
        portal.style.visibility = 'hidden';
        portal.style.opacity    = '0';
        portal.style.display    = 'block';
        var coords = window.pwTooltipPortalCoords(btn, candidate, portal);
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
        return clips ? PW_TOOLTIP_OPPOSITE[candidate] : candidate;
    };

    window.pwTooltipShow = function(btn, portal) {
        portal.textContent = btn.dataset.tooltipText || '';
        var placement = window.pwTooltipResolvedPlacement(btn, portal);
        var coords    = window.pwTooltipPortalCoords(btn, placement, portal);
        portal.setAttribute('data-pw-tooltip-placement', placement);
        portal.style.top       = coords.top  + 'px';
        portal.style.left      = coords.left + 'px';
        portal.style.opacity   = '1';
        portal.style.transform = 'scale(1)';
        btn._pwTooltipPortal = portal;
    };

    window.pwTooltipHide = function(btn) {
        if (btn._pwTooltipPortal) {
            btn._pwTooltipPortal.style.opacity   = '0';
            btn._pwTooltipPortal.style.transform = 'scale(0.88)';
        }
    };

    window.pwTooltipUpdatePosition = function(btn) {
        if (!btn || !btn._pwTooltipPortal) return;
        var placement = btn._pwTooltipPortal.getAttribute('data-pw-tooltip-placement')
                        || window.pwTooltipCoarsePlacement(btn);
        var coords = window.pwTooltipPortalCoords(btn, placement, btn._pwTooltipPortal);
        btn._pwTooltipPortal.style.top  = coords.top  + 'px';
        btn._pwTooltipPortal.style.left = coords.left + 'px';
    };

    window.clearHelperTextTimer = function(input) {
        if (input.helperTextTimer) { clearTimeout(input.helperTextTimer); input.helperTextTimer = null; }
    };
    window.clearBalloonTimer = function(input) {
        if (input.balloonTimer) { clearTimeout(input.balloonTimer); input.balloonTimer = null; }
    };
    window.scheduleBalloonHide = function(input) {
        clearBalloonTimer(input);
        var container = input.closest('.input-glass');
        var balloon   = container ? container.querySelector('.password-validation-balloon') : null;
        if (balloon && input.value) {
            input.balloonTimer = setTimeout(function() { balloon.classList.add('auto-hidden'); }, 4000);
        }
    };
    window.scheduleHelperTextHide = function(input) {
        clearHelperTextTimer(input);
        var formGroup  = input.closest('.glass-form-group');
        var helperText = formGroup ? formGroup.querySelector('.glass-helper-text') : null;
        if (helperText && input.value) {
            input.helperTextTimer = setTimeout(function() {
                if (helperText.dataset.originalText) {
                    helperText.innerHTML = '<span class="helper-text-child" style="color:rgba(255,255,255,0.9);">' + helperText.dataset.originalText + '</span>';
                    if (formGroup) formGroup.classList.remove('has-content');
                }
            }, 8000);
        }
    };

    window.updatePasswordValidation = function(input, minLength) {
        var container         = input.closest('.input-glass');
        var formGroup         = container.closest('.glass-form-group');
        var balloon           = container.querySelector('.password-validation-balloon');
        var validationContent = balloon ? balloon.querySelector('.validation-content') : null;
        var strengthContainer = balloon ? balloon.querySelector('.password-strength-container-balloon') : null;
        var helperText        = formGroup ? formGroup.querySelector('.glass-helper-text') : null;
        var value             = input.value;

        if (helperText && !helperText.dataset.originalText) {
            helperText.dataset.originalText = helperText.textContent;
        }
        if (typeof window.toggleClearButton === 'function') { toggleClearButton(input); }

        if (value) { if (formGroup) formGroup.classList.add('has-content'); }
        else        { if (formGroup) formGroup.classList.remove('has-content'); }

        clearHelperTextTimer(input);
        clearBalloonTimer(input);
        if (balloon && value) { balloon.classList.remove('auto-hidden'); }

        if (!value) {
            if (validationContent) validationContent.innerHTML = '';
            if (strengthContainer) window.resetStrengthBar(strengthContainer);
            if (helperText && helperText.dataset.originalText) {
                helperText.innerHTML = '<span class="helper-text-child" style="color:rgba(255,255,255,0.9);">' + helperText.dataset.originalText + '</span>';
                if (formGroup) formGroup.classList.remove('has-content');
            }
            return;
        }

        var v = validatePasswordStrength(value, minLength);

        var html = '';
        v.rules.forEach(function(rule) {
            var icon = rule.test
                ? '<i class="fa-solid fa-circle-check validation-fa-icon"></i>'
                : '<i class="fa-solid fa-circle-xmark validation-fa-icon"></i>';
            html += '<div class="validation-item ' + (rule.test ? 'valid' : 'invalid') + '">' + icon + '<span>' + rule.message + '</span></div>';
        });
        if (validationContent) validationContent.innerHTML = html;

        if (strengthContainer) {
            window.updateStrengthBar(strengthContainer, v.validCount, v.strengthLabel);
        }

        if (helperText) {
            var helperMap = {
                'weak':        ['fa-triangle-exclamation', 'Weak password \\u2014 keep going'],
                'fair':        ['fa-triangle-exclamation', 'Fair password \\u2014 can be stronger'],
                'good':        ['fa-circle-check',         'Good password'],
                'strong':      ['fa-shield-halved',        'Strong password'],
                'very-strong': ['fa-shield-halved',        'Very strong password!']
            };
            var mapping = helperMap[v.strengthClass];
            if (mapping) {
                helperText.innerHTML =
                    '<i class="fa-solid ' + mapping[0] + ' helper-fa-icon"></i> ' +
                    '<span class="helper-text-child" style="color:rgba(255,255,255,0.9);">' + mapping[1] + '</span>';
            }
            scheduleHelperTextHide(input);
        }

        scheduleBalloonHide(input);
    };

    window.handlePasswordFocus = function(input, minLength) {
        clearHelperTextTimer(input);
        clearBalloonTimer(input);
        var balloon = input.closest('.input-glass').querySelector('.password-validation-balloon');
        if (balloon) {
            balloon.classList.remove('manually-closed');
            balloon.classList.remove('auto-hidden');
        }
        updatePasswordValidation(input, minLength);
    };
    window.handlePasswordBlur = function(input) {
        scheduleHelperTextHide(input);
    };

    window.closePasswordValidationBalloon = function(balloonId) {
        var balloon = document.getElementById(balloonId);
        if (balloon) balloon.classList.add('manually-closed');
    };

    window.savePasswordCursor = function(inputId, event) {
        var inp = document.getElementById(inputId);
        if (inp) {
            inp._savedSelStart = inp.selectionStart;
            inp._savedSelEnd   = inp.selectionEnd;
        }
        event.preventDefault();
    };

    window.togglePasswordFieldVisibility = function(inputId, buttonId, showTip, hideTip) {
        var inp = document.getElementById(inputId);
        var btn = document.getElementById(buttonId);
        if (!inp || !btn) return;

        var savedStart = (inp._savedSelStart !== undefined) ? inp._savedSelStart : inp.value.length;
        var savedEnd   = (inp._savedSelEnd   !== undefined) ? inp._savedSelEnd   : inp.value.length;

        if (inp.type === 'password') {
            inp.type = 'text';
            btn.querySelector('i').className = 'fa-solid fa-eye-slash';
            btn.setAttribute('aria-label', hideTip);
            btn.dataset.tooltipText = hideTip;
        } else {
            inp.type = 'password';
            btn.querySelector('i').className = 'fa-solid fa-eye';
            btn.setAttribute('aria-label', showTip);
            btn.dataset.tooltipText = showTip;
        }

        inp.focus();
        try { inp.setSelectionRange(savedStart, savedEnd); } catch (e) {}
    };

    document.addEventListener('DOMContentLoaded', function() {
        document.querySelectorAll('.input-glass input[type="password"]').forEach(function(input) {
            var formGroup = input.closest('.glass-form-group');
            if (input.value.trim() && formGroup) formGroup.classList.add('has-content');
            var minLength = parseInt(input.getAttribute('minlength') || '8', 10);
            updatePasswordValidation(input, minLength);
        });
    });

    window.addEventListener('resize', function() {
        document.querySelectorAll('.password-toggle-btn, .clear-btn, .balloon-close-btn').forEach(function(btn) {
            window.pwTooltipUpdatePosition(btn);
        });
    }, { passive: true });
    window.addEventListener('scroll', function() {
        document.querySelectorAll('.password-toggle-btn, .clear-btn, .balloon-close-btn').forEach(function(btn) {
            window.pwTooltipUpdatePosition(btn);
        });
    }, { passive: true, capture: true });
}
</script>
"""

    // ── Shared styles ─────────────────────────────────────────────────────────
    // .pw-tooltip-portal is NOT here — it is owned by GlassTooltipStylesTagLib.
    out << '''
<style>
    .password-field-container input {
        padding-right: 50px !important;
    }

    /* ── Toggle button ──────────────────────────────────────────────────────── */
    .password-toggle-btn {
        position: absolute !important;
        right: 36px !important;
        top: 50% !important;
        transform: translateY(-50%) !important;
        background: rgba(255, 255, 255, 0.3) !important;
        border: none !important;
        width: 20px !important;
        height: 20px !important;
        border-radius: 50% !important;
        cursor: pointer !important;
        display: none !important;
        align-items: center !important;
        justify-content: center !important;
        color: #2d3748 !important;
        transition: all 0.2s ease !important;
        padding: 0 !important;
        font-size: 11px !important;
        z-index: 2 !important;
        overflow: visible !important;
    }
    .password-field-container.has-value .password-toggle-btn {
        display: flex !important;
    }
    .password-toggle-btn:hover {
        background: rgba(255, 255, 255, 0.5) !important;
        transform: translateY(-50%) scale(1.1) !important;
    }
    .password-toggle-btn:active {
        transform: translateY(-50%) scale(0.95) !important;
    }
    .password-toggle-btn i { pointer-events: none !important; }

    /* ── Balloon close button ──────────────────────────────────────────────── */
    .balloon-close-btn {
        position: absolute !important;
        top: 8px !important;
        right: 8px !important;
        background: rgba(45, 55, 72, 0.1) !important;
        border: none !important;
        width: 20px !important;
        height: 20px !important;
        border-radius: 50% !important;
        cursor: pointer !important;
        display: flex !important;
        align-items: center !important;
        justify-content: center !important;
        color: #2d3748 !important;
        transition: all 0.2s ease !important;
        padding: 0 !important;
        font-size: 10px !important;
        z-index: 11 !important;
        overflow: visible !important;
    }
    .balloon-close-btn:hover  { background: rgba(45, 55, 72, 0.2) !important; transform: scale(1.1) !important; }
    .balloon-close-btn:active { transform: scale(0.95) !important; }
    .balloon-close-btn i      { pointer-events: none !important; }

    /* Source tooltip span inside each button — always hidden (portal on body is shown) */
    .pw-tooltip-source {
        display: none !important;
        pointer-events: none !important;
    }

    /* ── Balloon — positioned ABOVE the input ──────────────────────────────── */
    .password-validation-balloon {
        position: absolute !important;
        bottom: calc(100% + 10px) !important;
        top: auto !important;
        left: 0 !important;
        right: 0 !important;
        background: #ffffff !important;
        border: 1px solid rgba(200, 200, 200, 0.8) !important;
        border-radius: 12px !important;
        padding: 36px 16px 14px 16px !important;
        box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.37) !important;
        font-size: 12px !important;
        opacity: 0 !important;
        visibility: hidden !important;
        transform: translateY(10px) !important;
        transition: opacity 0.3s ease, visibility 0.3s ease, transform 0.3s cubic-bezier(0.68, -0.55, 0.265, 1.55) !important;
        z-index: 10 !important;
    }
    .password-validation-balloon::after {
        content: "" !important;
        position: absolute !important;
        bottom: -6px !important;
        top: auto !important;
        left: 20px !important;
        width: 12px !important;
        height: 12px !important;
        background: #ffffff !important;
        border-right: 1px solid rgba(200, 200, 200, 0.8) !important;
        border-bottom: 1px solid rgba(200, 200, 200, 0.8) !important;
        transform: rotate(45deg) !important;
    }
    .password-field-container.has-value input:focus ~ .password-validation-balloon:not(.manually-closed):not(.auto-hidden) {
        opacity: 1 !important;
        visibility: visible !important;
        transform: translateY(0) !important;
    }

    /* ── Validation rule checklist ──────────────────────────────────────────── */
    .validation-content .validation-item {
        display: flex !important;
        align-items: center !important;
        margin-bottom: 6px !important;
        color: #2d3748 !important;
        transition: color 0.3s ease !important;
    }
    .validation-content .validation-item:last-child { margin-bottom: 0 !important; }
    .validation-content .validation-item.valid   { color: #48bb78 !important; }
    .validation-content .validation-item.invalid { color: #f56565 !important; }
    .validation-fa-icon {
        margin-right: 8px !important;
        font-size: 13px !important;
        min-width: 16px !important;
        text-align: center !important;
        pointer-events: none !important;
    }

    /* ── Gradient strength meter ────────────────────────────────────────────── */
    .password-strength-container-balloon {
        margin-top: 12px !important;
        padding-top: 10px !important;
        border-top: 1px solid rgba(45, 55, 72, 0.1) !important;
    }
    .password-strength-track {
        height: 6px !important;
        border-radius: 3px !important;
        background: rgba(45, 55, 72, 0.1) !important;
        overflow: visible !important;
        margin-bottom: 5px !important;
        position: relative !important;
    }
    .password-strength-fill {
        position: absolute !important;
        top: 0 !important;
        left: 0 !important;
        width: 100% !important;
        height: 100% !important;
        border-radius: 3px !important;
        background: transparent;
        clip-path: inset(0 100% 0 0);
        transition: clip-path 0.35s ease;
    }
    .password-strength-text {
        font-size: 11px !important;
        font-weight: 600 !important;
        text-align: right !important;
        transition: color 0.35s ease !important;
    }

    /* ── Helper text FA icons ───────────────────────────────────────────────── */
    .helper-fa-icon {
        color: rgba(255, 255, 255, 0.9) !important;
        font-size: 1.1em !important;
        margin-right: 4px !important;
        pointer-events: none !important;
    }
</style>
'''
  }
}
