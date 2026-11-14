package com.murad.app.matrimony.slider


import java.math.RoundingMode

/**
 * <glass:heightRangeSlider/> — a glassmorphic, touch-enabled range slider
 * for GSP, purpose-built for human height, with a cm <-> feet+inches
 * display toggle. Built the same way as GlassWeightRangeSliderTagLib: all
 * shared CSS/drag/keyboard/ARIA/resize/render behavior comes from
 * GlassSliderCommonTagLib (`window.GlassSlider`, called here via static
 * methods — `GlassSliderCommonTagLib.injectAssets(...)`,
 * `.toBigDecimal(...)`, etc. — rather than an injected property; Grails
 * doesn't reliably autowire one taglib into another, and static calls
 * need no bean lookup at all, so they can't come through null); this file
 * only holds what is actually specific to *height* — attribute parsing,
 * markup, the SSR display helper, and the
 * "height" kind's registration (cm<->ft/in conversion + the unit-toggle
 * wiring).
 *
 * DESIGN DECISION — canonical unit is always centimeters:
 *   `min`, `max`, `step`, `value`, `valueMin`, `valueMax` are ALL in
 *   centimeters, and the hidden form field(s) submitted to the server are
 *   ALWAYS in centimeters, regardless of what unit is showing on screen.
 *   Same reasoning as GlassWeightRangeSliderTagLib's kg-is-canonical
 *   decision: one unit in the DB avoids "some profiles in cm, some in
 *   feet/inches" data-quality problems, while the kg/lb-style toggle
 *   still lets a user drag/read in whichever unit they think in. Read the
 *   extra hidden field `${name}Unit` (the last unit the user had
 *   selected) if you want to persist a per-user display preference
 *   separately — it's never used to reinterpret the cm value.
 *
 * DESIGN DECISION — feet+inches is a DISPLAY format, not a second model:
 *   Unlike kg/lb (a simple linear conversion), feet+inches is a compound,
 *   base-12 rendering of a single length. The engine still only ever
 *   drags/steps in cm; the "ft" display unit just formats that cm value
 *   as `5'9"` (whole inches — cm/2.54, rounded to the nearest inch, split
 *   into feet and remainder inches) instead of `175 cm`. There is no
 *   "step in inches" mode — dragging always moves in `step` centimeters,
 *   same as weight always drags in kg. If you want inch-granularity
 *   dragging, set `step="2.54"` (i.e. 1 inch in cm); the ft/in readout
 *   will then land on whole inches every step instead of just on render.
 *
 * Single-thumb usage:
 *   <glass:heightRangeSlider name="height" min="140" max="210" step="1"
 *                             value="175" label="Height" unit="cm"/>
 *
 * Dual-thumb (range) usage — e.g. "preferred partner height range":
 *   <glass:heightRangeSlider name="partnerHeight" min="140" max="210" step="1"
 *                             dual="true" valueMin="160" valueMax="180"
 *                             label="Preferred height range" unit="ft"/>
 *
 * Submits as (always centimeters):
 *   single mode -> ${name}, ${name}Unit
 *   dual mode   -> ${name}Min, ${name}Max, ${name}Unit
 *
 * Attributes:
 *   name              (required) form field name
 *   id                            element id, auto-generated if omitted
 *   min                           minimum, in cm, default 120
 *   max                           maximum, in cm, default 220
 *   step                          step increment, in cm, default 1 (fractional OK,
 *                                 e.g. step="2.54" for 1-inch increments)
 *   value                         initial value, in cm (single-thumb mode)
 *   valueMin / valueMax           initial values, in cm (dual-thumb / range mode)
 *   dual        boolean           enable a two-handle range slider, default false
 *   label                         visible label text
 *   unit                          initial DISPLAY unit only: "cm" (default) | "ft"
 *                                 (feet+inches, e.g. 5'9"). Never reinterprets
 *                                 min/max/step/value, which are always cm — see
 *                                 DESIGN DECISION above.
 *   allowUnitToggle boolean       show the cm/ft toggle control, default true
 *   disabled    boolean
 *   theme                         "auto" (default, follows OS) | "dark" | "light"
 *   locale                        BCP-47 tag for number formatting, e.g. "en-IN" (optional)
 *   haptics     boolean           vibrate on drag/step/unit-toggle on supporting devices, default true
 */
class GlassHeightRangeSliderTagLib {

  static namespace = 'glass'
  static encodeAsForTags = [heightRangeSlider: 'raw', heightRangeSliderAssets: 'raw']

  private static int instanceCounter = 0

  /** Only this kind's own small script — the shared CSS/engine script
   *  is injected once by GlassSliderCommonTagLib, tracked under its own
   *  flag there. */
  private static final String KIND_ASSETS_FLAG = 'glassHeightRangeSliderKindAssetsInjected'

  private static final String KIND_JS = '''
(function () {
  'use strict';

  // The "height" kind's one bit of behavior no other kind needs: cm's
  // compound, base-12 feet+inches rendering, plus the toggle that
  // switches to it. Dragging/stepping always happens in cm — see the
  // class Groovydoc's DESIGN DECISION notes.
  GlassSlider.registerKind('height', function (root) {
    var displayUnit = root.dataset.unit === 'ft' ? 'ft' : 'cm';
    var cmDecimals = (String(root.dataset.step).split('.')[1] || '').length;
    var cmFormat = GlassSlider.makeFormatter(root.dataset.locale, cmDecimals);
    var unitInput = root.querySelector('input[data-gs-input="unit"]');
    var cmBtn = root.querySelector('[data-gs-unit-btn="cm"]');
    var ftBtn = root.querySelector('[data-gs-unit-btn="ft"]');
    var allowToggle = root.dataset.allowUnitToggle !== 'false';

    function cmToFeetInches(cmValue) {
      var totalInches = Math.round(cmValue / GlassSlider.CM_PER_INCH);
      var feet = Math.floor(totalInches / 12);
      var inches = totalInches - feet * 12;
      return feet + "'" + inches + '"';
    }

    function formatDisplay(cmValue) {
      return displayUnit === 'ft' ? cmToFeetInches(cmValue) : (cmFormat(cmValue) + ' cm');
    }

    var engine = new GlassSlider.Engine(root, {
      min: parseFloat(root.dataset.min),
      max: parseFloat(root.dataset.max),
      step: parseFloat(root.dataset.step) || 1,
      dual: root.dataset.dual === 'true',
      disabled: root.dataset.disabled === 'true',
      haptics: root.dataset.haptics !== 'false',
      // engine always hands us the canonical cm value here, whatever unit is showing
      formatText: function (which, cmValue) { return formatDisplay(cmValue); },
      eventName: 'gs-hrange:change',
      buildDetail: function (eng) {
        return eng.dual
          ? {
              name: root.dataset.name,
              unit: displayUnit,
              valueMinCm: eng.valueMin,
              valueMaxCm: eng.valueMax,
              valueMinDisplay: formatDisplay(eng.valueMin),
              valueMaxDisplay: formatDisplay(eng.valueMax)
            }
          : {
              name: root.dataset.name,
              unit: displayUnit,
              valueCm: eng.valueMin,
              valueDisplay: formatDisplay(eng.valueMin)
            };
      }
    });

    function setUnit(u) {
      if (u !== 'cm' && u !== 'ft') return;
      if (u === displayUnit) return;
      displayUnit = u;
      if (unitInput) unitInput.value = u;
      if (cmBtn) cmBtn.setAttribute('aria-pressed', u === 'cm' ? 'true' : 'false');
      if (ftBtn) ftBtn.setAttribute('aria-pressed', u === 'ft' ? 'true' : 'false');
      engine._haptic('unit:' + u);
      engine.render();
      engine.emitChange();
    }

    if (allowToggle) {
      if (cmBtn) cmBtn.addEventListener('click', function () { setUnit('cm'); });
      if (ftBtn) ftBtn.addEventListener('click', function () { setUnit('ft'); });
    }

    return engine;
  });

  window.GsHeightRangeSlider = { init: GlassSlider.init };
})();
'''

  /** Explicit/manual include for <head>. Injects the shared assets (if
   *  not already present this request) plus this kind's own script. */
  def heightRangeSliderAssets = { attrs ->
    GlassSliderCommonTagLib.injectAssets(request, out)
    injectKindAssets()
  }

  def heightRangeSlider = { attrs ->
    String name = attrs.remove('name')
    if (!name) {
      throwTagError('Tag [heightRangeSlider] is missing required attribute [name]')
    }

    String id = (attrs.remove('id') ?: "gs-hrange-${name}-${instanceCounter++}").toString()
    BigDecimal min = GlassSliderCommonTagLib.toBigDecimal(attrs.remove('min'), 120)
    BigDecimal max = GlassSliderCommonTagLib.toBigDecimal(attrs.remove('max'), 220)
    BigDecimal step = GlassSliderCommonTagLib.toBigDecimal(attrs.remove('step'), 1)
    boolean dual = GlassSliderCommonTagLib.toBoolean(attrs.remove('dual'))
    boolean disabled = GlassSliderCommonTagLib.toBoolean(attrs.remove('disabled'))
    String label = attrs.remove('label')
    String theme = (attrs.remove('theme') ?: 'auto').toString() // "auto" | "dark" | "light"
    String locale = attrs.remove('locale')
    boolean haptics = attrs.containsKey('haptics') ? GlassSliderCommonTagLib.toBoolean(attrs.remove('haptics')) : true
    boolean allowUnitToggle = attrs.containsKey('allowUnitToggle') ? GlassSliderCommonTagLib.toBoolean(attrs.remove('allowUnitToggle')) : true

    String unitAttr = (attrs.remove('unit') ?: 'cm').toString()
    String displayUnit = (unitAttr == 'ft') ? 'ft' : 'cm'

    // All values below are centimeters — the canonical unit. See class Groovydoc.
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
    int cmDecimals = step.stripTrailingZeros().scale() < 0 ? 0 : step.stripTrailingZeros().scale()

    StringBuilder sb = new StringBuilder(768)

    sb << '<div class="gs-slider" data-gs-slider data-gs-kind="height" '
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
      ? "${display(valueMin, displayUnit, cmDecimals)} &ndash; ${display(valueMax, displayUnit, cmDecimals)}"
      : display(valueMin, displayUnit, cmDecimals))
    sb << '</span>'
    sb << '</span>' // .gs-slider-label-group

    if (allowUnitToggle) {
      sb << '<div class="gs-slider-unit-toggle" role="group" aria-label="Height unit">'
      sb << '<button type="button" class="gs-slider-unit-btn" data-gs-unit-btn="cm" '
      sb << 'aria-pressed="' << (displayUnit == 'cm') << '"' << (disabled ? ' disabled' : '') << '>cm</button>'
      sb << '<button type="button" class="gs-slider-unit-btn" data-gs-unit-btn="ft" '
      sb << 'aria-pressed="' << (displayUnit == 'ft') << '"' << (disabled ? ' disabled' : '') << '>ft/in</button>'
      sb << '</div>'
    }
    sb << '</div>' // .gs-slider-header

    sb << '<div class="gs-slider-body">'
    sb << '<div class="gs-slider-track" data-gs-track>'
    sb << '<div class="gs-slider-fill" data-gs-fill></div>'

    sb << thumbMarkup('min', min, max, valueMin, label, displayUnit, cmDecimals, disabled)
    if (dual) {
      sb << thumbMarkup('max', min, max, valueMax, label, displayUnit, cmDecimals, disabled)
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
                             String label, String displayUnit, int cmDecimals, boolean disabled) {
    String suffix = which == 'min' ? 'minimum' : 'maximum'
    String ariaLabel = (label ? "${label} ${suffix}" : "${suffix} height").toString()
    String displayText = display(value, displayUnit, cmDecimals)
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

  /** Formats a canonical cm value for initial (pre-hydration) display in
   *  whichever unit was requested. JS takes over on DOMContentLoaded and
   *  re-renders using the same conversion, so this only affects the very
   *  first paint before the script runs. Uses the same CM_PER_INCH
   *  constant as the shared JS engine (GlassSliderCommonTagLib.CM_PER_INCH),
   *  so both sides of the hydration boundary agree. */
  private String display(BigDecimal cmValue, String unit, int cmDecimals) {
    if (cmValue == null) return ''
    if (unit == 'ft') {
      long totalInches = cmValue.divide(GlassSliderCommonTagLib.CM_PER_INCH, 10, RoundingMode.HALF_UP)
        .setScale(0, RoundingMode.HALF_UP).longValueExact()
      long feet = Math.floorDiv(totalInches, 12L)
      long inches = totalInches - (feet * 12L)
      return "${feet}'${inches}\""
    }
    BigDecimal cm = cmValue.setScale(cmDecimals, RoundingMode.HALF_UP)
    return "${GlassSliderCommonTagLib.fmt(cm)} cm"
  }
}
