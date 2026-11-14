package com.murad.app.matrimony.slider


import java.math.RoundingMode

/**
 * <glass:weightRangeSlider/> — a glassmorphic, touch-enabled range slider
 * for GSP, purpose-built for human body weight. Fractional values (0.5 kg
 * steps by default, or any decimal step) and a kg <-> lb display toggle are
 * first-class, on top of the same drag/keyboard/dual-thumb mechanics every
 * glass slider kind shares.
 *
 * REFACTOR NOTE: the glassmorphic CSS and the drag/keyboard/ARIA/render
 * engine used to live entirely in this file, duplicated (module-for-module,
 * just with different class-name prefixes) in GlassRangeSliderTagLib. Both
 * are now shared via GlassSliderCommonTagLib (`<glass:sliderAssets/>`,
 * `window.GlassSlider`), called here via static methods —
 * `GlassSliderCommonTagLib.injectAssets(...)`, `.toBigDecimal(...)`, etc.
 * — rather than an injected property (Grails doesn't reliably autowire
 * one taglib into another; static calls need no bean lookup at all, so
 * they can't come through null). This
 * file now contains only what is actually specific to *weight*: attribute
 * parsing, markup assembly, the SSR kg->lb display helper, and the
 * "weight" kind's registration (kg/lb formatting + the unit-toggle
 * button wiring — the one piece of behavior no other kind needs).
 *
 * DESIGN DECISION — canonical unit is always kilograms:
 *   `min`, `max`, `step`, `value`, `valueMin`, `valueMax` are ALL in
 *   kilograms, and the hidden form field(s) submitted to the server are
 *   ALWAYS in kilograms, regardless of what unit is showing on screen.
 *   The kg/lb toggle is purely a display convenience — it does not change
 *   what gets POSTed. This keeps weight stored in one unit in the DB
 *   (avoids "some profiles in kg, some in lb" data-quality problems) while
 *   still letting a user drag/read the slider in whichever unit they think
 *   in. If per-user unit *storage* is ever wanted, read the extra hidden
 *   field `${name}Unit` this tag emits (last unit the user had selected)
 *   and persist that separately — it is never used to reinterpret the kg
 *   value.
 *
 * DESIGN DECISION — drag mechanics stay in kg:
 *   The physical min/max/step driving thumb position and keyboard
 *   ArrowLeft/Right stepping are always the kg values you passed in (this
 *   is just how the shared Engine works — it only ever sees the numbers
 *   in `min`/`max`/`step`/hidden-input, never a unit). Only the *readout
 *   text* (header readout + drag bubble), via `formatText`, reformats
 *   into lb when lb is the active display unit (value * 2.2046226218,
 *   rounded to 1 decimal). This avoids float drift from repeatedly
 *   converting the authoritative value back and forth, and keeps
 *   "step=0.5" behavior predictable no matter which unit is showing.
 *
 * Single-thumb usage:
 *   <glass:weightRangeSlider name="weight" min="30" max="200" step="0.5"
 *                             value="68.5" label="Weight" unit="kg"/>
 *
 * Dual-thumb (range) usage — e.g. "preferred partner weight range":
 *   <glass:weightRangeSlider name="partnerWeight" min="35" max="150" step="0.5"
 *                             dual="true" valueMin="50" valueMax="75"
 *                             label="Preferred weight range" unit="lb"/>
 *
 * Submits as (always kilograms):
 *   single mode -> ${name}, ${name}Unit
 *   dual mode   -> ${name}Min, ${name}Max, ${name}Unit
 *
 * Attributes:
 *   name              (required) form field name
 *   id                            element id, auto-generated if omitted
 *   min                           minimum, in kg, default 30
 *   max                           maximum, in kg, default 200
 *   step                          step increment, in kg, default 0.5 (fractional OK)
 *   value                         initial value, in kg (single-thumb mode)
 *   valueMin / valueMax           initial values, in kg (dual-thumb / range mode)
 *   dual        boolean           enable a two-handle range slider, default false
 *   label                         visible label text
 *   unit                          initial DISPLAY unit only: "kg" (default) | "lb".
 *                                 Never reinterprets min/max/step/value, which
 *                                 are always kg — see DESIGN DECISION above.
 *   allowUnitToggle boolean       show the kg/lb toggle control, default true
 *   disabled    boolean
 *   theme                         "auto" (default, follows OS) | "dark" | "light"
 *   locale                        BCP-47 tag for number formatting, e.g. "en-IN" (optional)
 *   haptics     boolean           vibrate on drag/step/unit-toggle on supporting devices, default true
 */
class GlassWeightRangeSliderTagLib {

  static namespace = 'glass'
  static encodeAsForTags = [weightRangeSlider: 'raw', weightRangeSliderAssets: 'raw']

  private static int instanceCounter = 0

  /** Only this kind's own small script — the shared CSS/engine script
   *  is injected once by GlassSliderCommonTagLib, tracked under its own
   *  flag there. */
  private static final String KIND_ASSETS_FLAG = 'glassWeightRangeSliderKindAssetsInjected'

  private static final String KIND_JS = '''
(function () {
  'use strict';

  // The "weight" kind is what actually needs something the shared Engine
  // doesn't provide: a second, purely-cosmetic unit (lb) layered on top
  // of a kg-denominated model, plus the toggle control that switches it.
  GlassSlider.registerKind('weight', function (root) {
    var displayUnit = root.dataset.unit === 'lb' ? 'lb' : 'kg';
    var kgDecimals = (String(root.dataset.step).split('.')[1] || '').length;
    var kgFormat = GlassSlider.makeFormatter(root.dataset.locale, kgDecimals);
    var lbFormat = GlassSlider.makeFormatter(root.dataset.locale, 1);
    var unitInput = root.querySelector('input[data-gs-input="unit"]');
    var kgBtn = root.querySelector('[data-gs-unit-btn="kg"]');
    var lbBtn = root.querySelector('[data-gs-unit-btn="lb"]');
    var allowToggle = root.dataset.allowUnitToggle !== 'false';

    function displayValue(kgValue) {
      return displayUnit === 'lb' ? kgValue * GlassSlider.KG_TO_LB : kgValue;
    }

    function formatDisplay(kgValue) {
      var v = displayValue(kgValue);
      var text = displayUnit === 'lb' ? lbFormat(v) : kgFormat(v);
      return text + (displayUnit === 'lb' ? ' lb' : ' kg');
    }

    var engine = new GlassSlider.Engine(root, {
      min: parseFloat(root.dataset.min),
      max: parseFloat(root.dataset.max),
      step: parseFloat(root.dataset.step) || 0.5,
      dual: root.dataset.dual === 'true',
      disabled: root.dataset.disabled === 'true',
      haptics: root.dataset.haptics !== 'false',
      // engine always hands us the canonical kg value here, whatever unit is showing
      formatText: function (which, kgValue) { return formatDisplay(kgValue); },
      eventName: 'gs-wrange:change',
      buildDetail: function (eng) {
        return eng.dual
          ? {
              name: root.dataset.name,
              unit: displayUnit,
              valueMin: displayValue(eng.valueMin),
              valueMax: displayValue(eng.valueMax),
              valueMinKg: eng.valueMin,
              valueMaxKg: eng.valueMax
            }
          : {
              name: root.dataset.name,
              unit: displayUnit,
              value: displayValue(eng.valueMin),
              valueKg: eng.valueMin
            };
      }
    });

    function setUnit(u) {
      if (u !== 'kg' && u !== 'lb') return;
      if (u === displayUnit) return;
      displayUnit = u;
      if (unitInput) unitInput.value = u;
      if (kgBtn) kgBtn.setAttribute('aria-pressed', u === 'kg' ? 'true' : 'false');
      if (lbBtn) lbBtn.setAttribute('aria-pressed', u === 'lb' ? 'true' : 'false');
      engine._haptic('unit:' + u);
      engine.render();
      engine.emitChange();
    }

    if (allowToggle) {
      if (kgBtn) kgBtn.addEventListener('click', function () { setUnit('kg'); });
      if (lbBtn) lbBtn.addEventListener('click', function () { setUnit('lb'); });
    }

    return engine;
  });

  // Kept for anyone still calling the old per-kind init name directly;
  // both now just delegate to the one shared entrypoint.
  window.GsWeightRangeSlider = { init: GlassSlider.init };
})();
'''

  /** Explicit/manual include for <head>. Injects the shared assets (if
   *  not already present this request) plus this kind's own script. */
  def weightRangeSliderAssets = { attrs ->
    GlassSliderCommonTagLib.injectAssets(request, out)
    injectKindAssets()
  }

  def weightRangeSlider = { attrs ->
    String name = attrs.remove('name')
    if (!name) {
      throwTagError('Tag [weightRangeSlider] is missing required attribute [name]')
    }

    String id = (attrs.remove('id') ?: "gs-wrange-${name}-${instanceCounter++}").toString()
    BigDecimal min = GlassSliderCommonTagLib.toBigDecimal(attrs.remove('min'), 30)
    BigDecimal max = GlassSliderCommonTagLib.toBigDecimal(attrs.remove('max'), 200)
    BigDecimal step = GlassSliderCommonTagLib.toBigDecimal(attrs.remove('step'), new BigDecimal('0.5'))
    boolean dual = GlassSliderCommonTagLib.toBoolean(attrs.remove('dual'))
    boolean disabled = GlassSliderCommonTagLib.toBoolean(attrs.remove('disabled'))
    String label = attrs.remove('label')
    String theme = (attrs.remove('theme') ?: 'auto').toString() // "auto" | "dark" | "light"
    String locale = attrs.remove('locale')
    boolean haptics = attrs.containsKey('haptics') ? GlassSliderCommonTagLib.toBoolean(attrs.remove('haptics')) : true
    boolean allowUnitToggle = attrs.containsKey('allowUnitToggle') ? GlassSliderCommonTagLib.toBoolean(attrs.remove('allowUnitToggle')) : true

    String unitAttr = (attrs.remove('unit') ?: 'kg').toString()
    String displayUnit = (unitAttr == 'lb') ? 'lb' : 'kg'

    // All values below are kilograms — the canonical unit. See class Groovydoc.
    BigDecimal valueMin
    BigDecimal valueMax = null
    if (dual) {
      valueMin = GlassSliderCommonTagLib.toBigDecimal(attrs.remove('valueMin'), min)
      valueMax = GlassSliderCommonTagLib.toBigDecimal(attrs.remove('valueMax'), max)
    } else {
      valueMin = GlassSliderCommonTagLib.toBigDecimal(attrs.remove('value'), min)
    }

    GlassSliderCommonTagLib.injectAssets(request, out)
    injectKindAssets()

    String resolvedTheme = (theme == 'light' || theme == 'dark') ? theme : 'auto'
    int kgDecimals = step.stripTrailingZeros().scale() < 0 ? 0 : step.stripTrailingZeros().scale()

    StringBuilder sb = new StringBuilder(768)

    sb << '<div class="gs-slider" data-gs-slider data-gs-kind="weight" '
    sb << 'data-name="' << name.encodeAsHTML() << '" '
    sb << 'data-min="' << min << '" data-max="' << max << '" data-step="' << step << '" '
    sb << 'data-dual="' << dual << '" data-theme="' << resolvedTheme << '" '
    sb << 'data-unit="' << displayUnit << '" '
    sb << 'data-allow-unit-toggle="' << allowUnitToggle << '" '
    sb << 'data-haptics="' << haptics << '" '
    if (locale) sb << 'data-locale="' << locale.encodeAsHTML() << '" '
    if (disabled) sb << 'data-disabled="true" '
    sb << 'id="' << id << '">'

    sb << '<div class="gs-slider-header">'
    sb << '<span class="gs-slider-label-group">'
    if (label) {
      sb << '<span class="gs-slider-label">' << label.encodeAsHTML() << '</span>'
    }
    sb << '<span class="gs-slider-readout" data-gs-readout>'
    sb << (dual
      ? "${display(valueMin, displayUnit, kgDecimals)} &ndash; ${display(valueMax, displayUnit, kgDecimals)}"
      : display(valueMin, displayUnit, kgDecimals))
    sb << '</span>'
    sb << '</span>' // .gs-slider-label-group

    if (allowUnitToggle) {
      sb << '<div class="gs-slider-unit-toggle" role="group" aria-label="Weight unit">'
      sb << '<button type="button" class="gs-slider-unit-btn" data-gs-unit-btn="kg" '
      sb << 'aria-pressed="' << (displayUnit == 'kg') << '"' << (disabled ? ' disabled' : '') << '>kg</button>'
      sb << '<button type="button" class="gs-slider-unit-btn" data-gs-unit-btn="lb" '
      sb << 'aria-pressed="' << (displayUnit == 'lb') << '"' << (disabled ? ' disabled' : '') << '>lb</button>'
      sb << '</div>'
    }
    sb << '</div>' // .gs-slider-header

    sb << '<div class="gs-slider-body">'
    sb << '<div class="gs-slider-track" data-gs-track>'
    sb << '<div class="gs-slider-fill" data-gs-fill></div>'

    sb << thumbMarkup('min', min, max, valueMin, label, displayUnit, kgDecimals, disabled)
    if (dual) {
      sb << thumbMarkup('max', min, max, valueMax, label, displayUnit, kgDecimals, disabled)
    }

    sb << '</div>' // .gs-slider-track
    sb << '</div>' // .gs-slider-body

    if (dual) {
      sb << '<input type="hidden" name="' << name.encodeAsHTML() << 'Min" data-gs-input="min" value="' << valueMin << '"/>'
      sb << '<input type="hidden" name="' << name.encodeAsHTML() << 'Max" data-gs-input="max" value="' << valueMax << '"/>'
    } else {
      sb << '<input type="hidden" name="' << name.encodeAsHTML() << '" data-gs-input="min" value="' << valueMin << '"/>'
    }
    sb << '<input type="hidden" name="' << name.encodeAsHTML() << 'Unit" data-gs-input="unit" value="' << displayUnit << '"/>'

    sb << '</div>' // .gs-slider

    out << sb.toString()
  }

  // ---- helpers ----------------------------------------------------

  private void injectKindAssets() {
    if (request.getAttribute(KIND_ASSETS_FLAG)) return
    request.setAttribute(KIND_ASSETS_FLAG, true)
    out << "<script>${KIND_JS}</script>"
  }

  private String thumbMarkup(String which, BigDecimal min, BigDecimal max, BigDecimal value,
                             String label, String displayUnit, int kgDecimals, boolean disabled) {
    String suffix = which == 'min' ? 'minimum' : 'maximum'
    String ariaLabel = (label ? "${label} ${suffix}" : "${suffix} weight").toString()
    String displayText = display(value, displayUnit, kgDecimals)
    StringBuilder t = new StringBuilder()
    t << "<button type=\"button\" class=\"gs-slider-thumb\" data-gs-thumb=\"${which}\" "
    t << 'role="slider" aria-orientation="horizontal" '
    t << "aria-valuemin=\"${min}\" aria-valuemax=\"${max}\" aria-valuenow=\"${value}\" "
    t << "aria-valuetext=\"${displayText.encodeAsHTML()}\" "
    t << "aria-label=\"${ariaLabel.encodeAsHTML()}\" "
    t << (disabled ? 'aria-disabled="true" ' : 'tabindex="0" ')
    t << (disabled ? 'disabled ' : '')
    t << '>'
    t << "<span class=\"gs-bubble\" data-gs-bubble>${displayText.encodeAsHTML()}</span>"
    t << '</button>'
    t.toString()
  }

  /** Formats a canonical kg value for initial (pre-hydration) display in
   *  whichever unit was requested. JS takes over on DOMContentLoaded and
   *  re-renders using the same conversion, so this only affects the very
   *  first paint before the script runs. Uses the same KG_TO_LB constant
   *  as the shared JS engine (GlassSliderCommonTagLib.KG_TO_LB), so both
   *  sides of the hydration boundary agree. */
  private String display(BigDecimal kgValue, String unit, int kgDecimals) {
    if (kgValue == null) return ''
    if (unit == 'lb') {
      BigDecimal lb = kgValue.multiply(GlassSliderCommonTagLib.KG_TO_LB).setScale(1, RoundingMode.HALF_UP)
      return "${GlassSliderCommonTagLib.fmt(lb)} lb"
    }
    BigDecimal kg = kgValue.setScale(kgDecimals, RoundingMode.HALF_UP)
    return "${GlassSliderCommonTagLib.fmt(kg)} kg"
  }
}
