package com.murad.app.matrimony.textinput

import com.murad.app.matrimony.GlassTooltipStylesTagLib
import grails.gsp.TagLib

/**
 * Renders a glassmorphic password input field with visibility toggle.
 *
 * @attr name                   REQUIRED
 * @attr value / id / label / placeholder / helperText / required
 * @attr minlength (default 8) / maxlength / autocomplete (default "current-password")
 * @attr class / disabled / readonly / autofocus / onchange / oninput
 * @attr wrapper / showToggle / floatingLabel
 * @attr tooltipPlacement / tooltip
 * @attr toggleTooltipPlacement / showPasswordTooltip / hidePasswordTooltip
 */
@TagLib

class SimplePasswordFieldTagLib {

  static namespace = "glass"

  // ── Tooltip-styles delegate ───────────────────────────────────────────────
  // Property injection of cross-namespace taglibs is unreliable in Grails —
  // the bean may not be wired by the time the taglib is first used.
  // grailsApplication is always injected reliably; we look up the bean lazily
  // on first use via a private accessor.
  private GlassTooltipStylesTagLib getTooltipStylesTagLib() {
    grailsApplication.mainContext.getBean(GlassTooltipStylesTagLib)
  }

  // ── Page-scope guards ─────────────────────────────────────────────────────
  private static final String STYLES_EMITTED = 'glass_stylesEmitted'
  private static final String SPW_SCRIPT_EMITTED = 'glass_spwScriptEmitted'

  // ─────────────────────────────────────────────────────────────────────────
  // glass:simplePasswordField
  // ─────────────────────────────────────────────────────────────────────────
  def simplePasswordField = { attrs ->
    if (!attrs.name) {
      throwTagError("Tag [simplePasswordField] is missing required attribute [name]")
    }

    // ── Attribute extraction ────────────────────────────────────────────────
    String name = attrs.name
    String id = attrs.id ?: name
    String value = attrs.value ?: ''
    String label = attrs.label ?: ''
    String placeholder = attrs.placeholder ?: ''
    String helperText = attrs.helperText ?: ''
    String minlength = attrs.minlength ?: '8'
    String maxlength = attrs.maxlength ?: ''
    String autocomplete = attrs.autocomplete ?: 'current-password'
    String additionalClasses = attrs.class ?: ''
    boolean required = attrs.required?.toString()?.toBoolean() ?: false
    boolean disabled = attrs.disabled?.toString()?.toBoolean() ?: false
    boolean readonly = attrs.readonly?.toString()?.toBoolean() ?: false
    boolean autofocus = attrs.autofocus?.toString()?.toBoolean() ?: false
    boolean wrapper = attrs.wrapper?.toString()?.toBoolean() != false
    boolean showToggle = attrs.showToggle?.toString()?.toBoolean() != false
    boolean floatingLabel = attrs.floatingLabel?.toString()?.toBoolean() != false
    String onchange = attrs.onchange ?: ''
    String oninput = attrs.oninput ?: ''

    // ── Tooltip: clear button ───────────────────────────────────────────────
    List validPlacements = ['top', 'right', 'bottom', 'left']

    String rawClearPlacement = attrs.tooltipPlacement?.toString()?.toLowerCase() ?: 'auto'
    boolean autoClearPlacement = !validPlacements.contains(rawClearPlacement)
    String clearPlacement = autoClearPlacement ? 'auto' : rawClearPlacement
    String clearTooltipText = attrs.tooltip ?: 'Clear'

    // ── Tooltip: toggle button ──────────────────────────────────────────────
    String rawTogglePlacement = attrs.toggleTooltipPlacement?.toString()?.toLowerCase() ?: 'auto'
    boolean autoTogglePlacement = !validPlacements.contains(rawTogglePlacement)
    String togglePlacement = autoTogglePlacement ? 'auto' : rawTogglePlacement
    String showPasswordTooltipText = attrs.showPasswordTooltip ?: 'Show Password'
    String hidePasswordTooltipText = attrs.hidePasswordTooltip ?: 'Hide Password'

    // ── Emit CSS + shared JS (delegated to their respective owners) ─────────
    // Tooltip tokens + portal rules: owned by GlassTooltipStylesTagLib.
    // Input-shell rules + shared SPW portal JS: owned by this taglib.
    tooltipStylesTagLib.emitTooltipStylesOnce()
    emitSharedAssetsOnce()
    emitSimplePasswordScriptOnce()

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
    String shellClass = 'input-glass simple-password-field-container'
    if (disabled) shellClass += ' is-disabled'
    if (value) shellClass += ' has-value'
    out << "<div class=\"${shellClass}\">"

    // ── <input> ─────────────────────────────────────────────────────────────
    out << '<input '
    out << 'type="password" '
    out << "name=\"${name.encodeAsHTML()}\" "
    out << "id=\"${id.encodeAsHTML()}\" "
    if (value) out << "value=\"${value.encodeAsHTML()}\" "
    if (floatingLabel && label) {
      out << 'placeholder="" '
    } else if (placeholder) {
      out << "placeholder=\"${placeholder.encodeAsHTML()}\" "
    }
    out << "minlength=\"${minlength}\" "
    if (maxlength) out << "maxlength=\"${maxlength}\" "
    out << "autocomplete=\"${autocomplete.encodeAsHTML()}\" "

    String inputClasses = disabled
      ? "form-control glass-disabled ${additionalClasses}".trim()
      : "form-control ${additionalClasses}".trim()
    out << "class=\"${inputClasses.encodeAsHTML()}\" "

    if (required) out << 'required '
    if (disabled) out << 'disabled '
    if (readonly) out << 'readonly '
    if (autofocus) out << 'autofocus '

    String onInputHandler = 'glassSpwInput(this);'
    if (oninput) onInputHandler += " ${oninput}"
    out << "oninput=\"${onInputHandler.encodeAsHTML()}\" "
    if (onchange) out << "onchange=\"${onchange.encodeAsHTML()}\" "

    out << '/>'

    // ── Buttons — not rendered when disabled=true ───────────────────────────
    if (!disabled) {
      String escapedId = id.encodeAsHTML()

      // ── Show/hide toggle button ───────────────────────────────────────────
      if (showToggle) {
        String toggleBtnId = "${escapedId}_toggle_btn"
        String togglePlacementAttr = autoTogglePlacement ? '' : " data-spw-tooltip-placement=\"${togglePlacement}\""
        out << "<button type=\"button\" id=\"${toggleBtnId}\" "
        out << "class=\"simple-password-toggle-btn\"${togglePlacementAttr} "
        out << "aria-label=\"Toggle password visibility\" "
        out << "data-tooltip-text=\"${showPasswordTooltipText.encodeAsHTML()}\" "
        out << "data-tooltip-show-text=\"${showPasswordTooltipText.encodeAsHTML()}\" "
        out << "data-tooltip-hide-text=\"${hidePasswordTooltipText.encodeAsHTML()}\" "
        out << "onmousedown=\"glassSpwSaveCursor('${escapedId}', event)\" "
        out << "onclick=\"glassSpwToggle('${escapedId}','${toggleBtnId}')\">"
        out << '<i class="fa-solid fa-eye" aria-hidden="true"></i>'
        out << '<span class="glass-tooltip" aria-hidden="true">' + showPasswordTooltipText.encodeAsHTML() + '</span>'
        out << '</button>'

        // Per-field IIFE: bind portal hover events for the toggle button.
        // Shared portal JS (glassSpwTooltipShow/Hide/UpdatePosition) was already
        // emitted once by emitSimplePasswordScriptOnce(); only field-specific
        // IDs are inlined here.
        out << """
<script>
(function () {
  var AUTO = ${autoTogglePlacement};
  var FIXED_PLACEMENT = '${autoTogglePlacement ? '' : togglePlacement}';

  function getBtn() { return document.getElementById('${toggleBtnId}'); }

  function bindBtn() {
    var btn = getBtn();
    if (!btn) return;
    btn.addEventListener('mouseenter', function () { glassSpwTooltipShow(btn, AUTO, FIXED_PLACEMENT); }, { passive: true });
    btn.addEventListener('focusin',    function () { glassSpwTooltipShow(btn, AUTO, FIXED_PLACEMENT); }, { passive: true });
    btn.addEventListener('mouseleave', function () { glassSpwTooltipHide(btn); }, { passive: true });
    btn.addEventListener('focusout',   function () { glassSpwTooltipHide(btn); }, { passive: true });
  }

  window.addEventListener('resize', function () { glassSpwTooltipUpdatePosition(getBtn()); }, { passive: true });
  window.addEventListener('scroll', function () { glassSpwTooltipUpdatePosition(getBtn()); }, { passive: true, capture: true });

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', bindBtn);
  } else {
    bindBtn();
  }
})();
</script>"""
      } // end showToggle

      // ── Clear button ──────────────────────────────────────────────────────
      String clearPlacementAttr = autoClearPlacement ? '' : " data-spw-tooltip-placement=\"${clearPlacement}\""
      out << "<button type=\"button\" class=\"clear-btn\"${clearPlacementAttr} "
      out << 'aria-label="Clear" '
      out << "data-tooltip-text=\"${clearTooltipText.encodeAsHTML()}\" "
      out << "onclick=\"glassSpwClear(this,'${escapedId}')\">"
      out << '<i class="fa-solid fa-xmark" aria-hidden="true"></i>'
      out << '<span class="glass-tooltip" aria-hidden="true">' + clearTooltipText.encodeAsHTML() + '</span>'
      out << '</button>'

      // Per-field IIFE: bind portal hover events for the clear button.
      out << """
<script>
(function () {
  var AUTO = ${autoClearPlacement};
  var FIXED_PLACEMENT = '${autoClearPlacement ? '' : clearPlacement}';

  function getBtn() {
    var inp = document.getElementById('${escapedId}');
    return inp ? inp.parentElement.querySelector('.clear-btn') : null;
  }

  function bindBtn() {
    var btn = getBtn();
    if (!btn) return;
    btn.addEventListener('mouseenter', function () { glassSpwTooltipShow(btn, AUTO, FIXED_PLACEMENT); }, { passive: true });
    btn.addEventListener('focusin',    function () { glassSpwTooltipShow(btn, AUTO, FIXED_PLACEMENT); }, { passive: true });
    btn.addEventListener('mouseleave', function () { glassSpwTooltipHide(btn); }, { passive: true });
    btn.addEventListener('focusout',   function () { glassSpwTooltipHide(btn); }, { passive: true });
  }

  window.addEventListener('resize', function () { glassSpwTooltipUpdatePosition(getBtn()); }, { passive: true });
  window.addEventListener('scroll', function () { glassSpwTooltipUpdatePosition(getBtn()); }, { passive: true, capture: true });

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', bindBtn);
  } else {
    bindBtn();
  }
})();
</script>"""
    } // end !disabled

    // ── Floating label ────────────────────────────────────────────────────────
    if (floatingLabel && label) {
      out << "<label for=\"${id.encodeAsHTML()}\" class=\"floating-label\">${label.encodeAsHTML()}</label>"
    }

    out << '</div>' // .input-glass

    if (helperText) {
      out << "<div class=\"glass-helper-text\">${helperText.encodeAsHTML()}</div>"
    } else {
      out << '<div class="glass-helper-text">&nbsp;</div>'
    }

    if (wrapper) {
      out << '</div>' // .glass-form-group
    }
  }

  // ─────────────────────────────────────────────────────────────────────────
  // Private: emit input-shell CSS once per page
  // ─────────────────────────────────────────────────────────────────────────
  private void emitSharedAssetsOnce() {
    if (pageScope[STYLES_EMITTED]) return
    pageScope[STYLES_EMITTED] = true

    out << '''
<style>
:root {
  --glass-background           : rgba(255, 255, 255, 0.10);
  --glass-border       : rgba(255, 255, 255, 0.25);
  --glass-shadow       : 0 4px 24px rgba(0, 0, 0, 0.18);
  --glass-blur         : blur(14px);
  --glass-radius       : 12px;
  --glass-text         : #f0f4ff;
  --glass-placeholder  : rgba(200, 215, 255, 0.45);
  --glass-accent       : #7eb8f7;
  --glass-focus-ring   : rgba(126, 184, 247, 0.40);
  --glass-helper       : rgba(200, 215, 255, 0.55);

  --glass-dis-background       : rgba(120, 130, 160, 0.07);
  --glass-dis-border   : rgba(180, 190, 220, 0.12);
  --glass-dis-text     : rgba(180, 190, 220, 0.38);
  --glass-dis-hatch    : repeating-linear-gradient(
                           135deg,
                           transparent 0px, transparent 4px,
                           rgba(255,255,255,0.025) 4px, rgba(255,255,255,0.025) 5px
                         );
}

.glass-form-group {
  position       : relative;
  display        : flex;
  flex-direction : column;
  gap            : 4px;
}

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
.input-glass.is-disabled .clear-btn,
.input-glass.is-disabled .simple-password-toggle-btn {
  display: none !important;
}

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
  transform    : translateY(-50%) scale(0.90);
}

.clear-btn .glass-tooltip {
  display        : none !important;
  pointer-events : none !important;
}

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

.glass-helper-text {
  font-size  : 0.75rem;
  color      : var(--glass-helper);
  padding    : 0 0.25rem;
  min-height : 1.2em;
}
</style>
'''
  }

  // ─────────────────────────────────────────────────────────────────────────
  // Private: emit simple-password JS + CSS once per page
  // ─────────────────────────────────────────────────────────────────────────
  /**
   * Emits:
   *  • Shared portal tooltip functions (glassSpwTooltipShow/Hide/UpdatePosition)
   *    — analogous to pwTooltipShow/Hide/UpdatePosition in PasswordFieldTagLib,
   *    guarded by window.glassSpwInitialized so they run exactly once per page.
   *  • glassSpwInput / glassSpwClear / glassSpwToggle / glassSpwSaveCursor
   *  • CSS for .simple-password-toggle-btn
   *
   * Per-field IIFEs (emitted inline in the tag closure above) are kept intentionally
   * minimal: they only call these shared functions, passing the button element and
   * the placement parameters resolved at render time.
   */
  private void emitSimplePasswordScriptOnce() {
    if (pageScope[SPW_SCRIPT_EMITTED]) return
    pageScope[SPW_SCRIPT_EMITTED] = true

    out << '''
<script>
if (typeof window.glassSpwInitialized === 'undefined') {
    window.glassSpwInitialized = true;

    // ── Shared portal tooltip helpers ─────────────────────────────────────
    // Mirrors the pw* tooltip helpers in PasswordFieldTagLib.
    // Each button gets its own portal span (created lazily on first hover) so
    // that two buttons on the same page never share a portal element.

    var SPW_OPPOSITE = { top: 'bottom', bottom: 'top', left: 'right', right: 'left' };

    window.glassSpwTooltipCoarsePlacement = function (btn) {
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

    window.glassSpwTooltipPortalCoords = function (btn, placement, portal) {
        var br  = btn.getBoundingClientRect();
        var pr  = portal.getBoundingClientRect();
        var gap = parseInt(getComputedStyle(document.documentElement)
                      .getPropertyValue('--tt-gap'), 10) || 8;
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

    // Determines the best placement, flipping to the opposite side if the
    // candidate would be clipped by the viewport.
    window.glassSpwTooltipResolvedPlacement = function (btn, portal) {
        var candidate = window.glassSpwTooltipCoarsePlacement(btn);
        portal.setAttribute('data-spw-tooltip-placement', candidate);
        portal.style.visibility = 'hidden';
        portal.style.opacity    = '0';
        portal.style.display    = 'block';
        var coords = window.glassSpwTooltipPortalCoords(btn, candidate, portal);
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
        return clips ? SPW_OPPOSITE[candidate] : candidate;
    };

    // Creates a portal span on <body> (lazily, once per button).
    window.glassSpwTooltipMakePortal = function () {
        var p = document.createElement('span');
        p.className = 'glass-tooltip glass-tooltip-portal';
        p.setAttribute('aria-hidden', 'true');
        p.style.position  = 'absolute';
        p.style.margin    = '0';
        p.style.opacity   = '0';
        p.style.transform = 'scale(0.88)';
        document.body.appendChild(p);
        return p;
    };

    // Shows the tooltip portal for btn.
    // autoPlacement: boolean — true = resolve at show time; false = use fixedPlacement.
    window.glassSpwTooltipShow = function (btn, autoPlacement, fixedPlacement) {
        if (!btn) return;
        if (!btn._spwTooltipPortal) {
            btn._spwTooltipPortal = window.glassSpwTooltipMakePortal();
        }
        var portal = btn._spwTooltipPortal;
        portal.textContent = btn.dataset.tooltipText || '';
        var placement;
        if (autoPlacement) {
            placement = window.glassSpwTooltipResolvedPlacement(btn, portal);
        } else {
            placement = fixedPlacement;
            portal.setAttribute('data-spw-tooltip-placement', placement);
        }
        var coords = window.glassSpwTooltipPortalCoords(btn, placement, portal);
        portal.style.top       = coords.top  + 'px';
        portal.style.left      = coords.left + 'px';
        portal.style.opacity   = '1';
        portal.style.transform = 'scale(1)';
    };

    window.glassSpwTooltipHide = function (btn) {
        if (btn && btn._spwTooltipPortal) {
            btn._spwTooltipPortal.style.opacity   = '0';
            btn._spwTooltipPortal.style.transform = 'scale(0.88)';
        }
    };

    window.glassSpwTooltipUpdatePosition = function (btn) {
        if (!btn || !btn._spwTooltipPortal) return;
        var placement = btn._spwTooltipPortal.getAttribute('data-spw-tooltip-placement')
                        || window.glassSpwTooltipCoarsePlacement(btn);
        var coords = window.glassSpwTooltipPortalCoords(btn, placement, btn._spwTooltipPortal);
        btn._spwTooltipPortal.style.top  = coords.top  + 'px';
        btn._spwTooltipPortal.style.left = coords.left + 'px';
    };

    // ── Field interaction helpers ─────────────────────────────────────────

    // Called oninput: syncs the has-value class on the container.
    window.glassSpwInput = function (input) {
        var container = input.closest('.input-glass');
        if (container) container.classList.toggle('has-value', !!input.value);
        if (typeof toggleClearButton === 'function') toggleClearButton(input);
    };

    // Saves the cursor position before the toggle button steals focus,
    // then prevents the default mousedown so focus stays on the input.
    // Mirrors savePasswordCursor in PasswordFieldTagLib.
    window.glassSpwSaveCursor = function (inputId, event) {
        var inp = document.getElementById(inputId);
        if (inp) {
            inp._savedSelStart = inp.selectionStart;
            inp._savedSelEnd   = inp.selectionEnd;
        }
        event.preventDefault();
    };

    // Clears the input, resets toggle icon to eye (password mode), hides portals.
    window.glassSpwClear = function (clearBtn, inputId) {
        var inp = document.getElementById(inputId);
        if (!inp) return;

        inp.value = '';
        inp.type  = 'password';

        var container = inp.closest('.input-glass');
        if (container) {
            var toggleBtn = container.querySelector('.simple-password-toggle-btn');
            if (toggleBtn) {
                // Reuse glassSpwToggle's reset path rather than reconstructing
                // icon HTML here: only call it when still in text-reveal mode.
                if (inp.type !== 'password') {
                    glassSpwToggle(inputId, toggleBtn.id);
                }
                // Sync tooltip text back to "show" label.
                var showText = toggleBtn.dataset.tooltipShowText || toggleBtn.dataset.tooltipText || 'Show Password';
                toggleBtn.dataset.tooltipText = showText;
                if (toggleBtn._spwTooltipPortal) {
                    glassSpwTooltipHide(toggleBtn);
                }
                // Ensure icon reflects password (hidden) state.
                var icon = toggleBtn.querySelector('i');
                if (icon) {
                    icon.className = 'fa-solid fa-eye';
                }
                // Sync the inline (display:none) tooltip span text.
                var inlineTip = toggleBtn.querySelector('.glass-tooltip');
                if (inlineTip) inlineTip.textContent = showText;
            }
            container.classList.remove('has-value');
        }

        if (clearBtn._spwTooltipPortal) {
            glassSpwTooltipHide(clearBtn);
        }

        inp.focus();
        if (typeof toggleClearButton === 'function') toggleClearButton(inp);
        inp.dispatchEvent(new Event('input', { bubbles: true }));
    };

    // Toggles between password/text, updates icon + tooltip text, restores cursor.
    window.glassSpwToggle = function (inputId, btnId) {
        var inp = document.getElementById(inputId);
        var btn = document.getElementById(btnId);
        if (!inp || !btn) return;

        // Restore cursor position saved by glassSpwSaveCursor.
        var savedStart = (inp._savedSelStart !== undefined) ? inp._savedSelStart : inp.value.length;
        var savedEnd   = (inp._savedSelEnd   !== undefined) ? inp._savedSelEnd   : inp.value.length;

        var showText = btn.dataset.tooltipShowText || btn.dataset.tooltipText || 'Show Password';
        var hideText = btn.dataset.tooltipHideText || 'Hide Password';

        if (btn._spwTooltipPortal) glassSpwTooltipHide(btn);

        if (inp.type === 'password') {
            inp.type = 'text';
            btn.dataset.tooltipText = hideText;
            if (btn._spwTooltipPortal) btn._spwTooltipPortal.textContent = hideText;
            var icon = btn.querySelector('i');
            if (icon) icon.className = 'fa-solid fa-eye-slash';
            var inlineTip = btn.querySelector('.glass-tooltip');
            if (inlineTip) inlineTip.textContent = hideText;
        } else {
            inp.type = 'password';
            btn.dataset.tooltipText = showText;
            if (btn._spwTooltipPortal) btn._spwTooltipPortal.textContent = showText;
            var icon = btn.querySelector('i');
            if (icon) icon.className = 'fa-solid fa-eye';
            var inlineTip = btn.querySelector('.glass-tooltip');
            if (inlineTip) inlineTip.textContent = showText;
        }

        inp.focus();
        try { inp.setSelectionRange(savedStart, savedEnd); } catch (e) {}
    };

    document.addEventListener('DOMContentLoaded', function () {
        document.querySelectorAll(
            '.simple-password-field-container input[type="password"],' +
            '.simple-password-field-container input[type="text"]'
        ).forEach(function (input) {
            if (!input.value) return;
            var container = input.closest('.input-glass');
            if (container) container.classList.add('has-value');
            if (typeof toggleClearButton === 'function') toggleClearButton(input);
        });
    });
}
</script>

<style>
.simple-password-field-container input.form-control {
  padding-right: 4.2rem;
}

.simple-password-toggle-btn .glass-tooltip {
  display        : none !important;
  pointer-events : none !important;
}

.simple-password-toggle-btn {
  position        : absolute;
  right           : 2.5rem;
  top             : 50%;
  transform       : translateY(-50%);
  display         : none;
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
.simple-password-toggle-btn .fa-eye,
.simple-password-toggle-btn .fa-eye-slash {
  font-size     : 0.7rem;
  line-height   : 1;
  display       : block;
  position      : relative;
  z-index       : 1;
  pointer-events: none;
}
.input-glass.has-value .simple-password-toggle-btn { display: flex; }
.simple-password-toggle-btn:hover {
  background   : rgba(126, 184, 247, 0.20);
  border-color : rgba(126, 184, 247, 0.50);
  color        : var(--glass-text);
  transform    : translateY(-50%) scale(1.12);
}
.simple-password-toggle-btn:active { transform: translateY(-50%) scale(0.90); }
</style>
'''
  }
}
