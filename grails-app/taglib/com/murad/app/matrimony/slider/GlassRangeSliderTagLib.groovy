package com.murad.app.matrimony.slider
/**
 * <glass:rangeSlider/> — a glassmorphic, touch-enabled range slider for GSP.
 *
 * REFACTOR NOTE: the glassmorphic CSS and the drag/keyboard/ARIA/render
 * engine used to live entirely in this file, duplicated (module-for-module,
 * just with different class-name prefixes) in GlassWeightRangeSliderTagLib.
 * Both are now shared via GlassSliderCommonTagLib (`<glass:sliderAssets/>`,
 * `window.GlassSlider`), called here via static methods —
 * `GlassSliderCommonTagLib.injectAssets(...)`, `.toBigDecimal(...)`, etc.
 * — rather than an injected property (Grails doesn't reliably autowire
 * one taglib into another; static calls need no bean lookup at all, so
 * they can't come through null). This
 * file now contains only what is actually specific to a *plain numeric*
 * slider: attribute parsing, markup assembly, and a ~25-line "range" kind
 * registration (identity formatting — no unit conversion, unlike weight).
 *
 * NOTE on `theme`: GlassmorphicRadioTagLib defines exactly ONE look — white
 * text on a white-tinted glass border, always. `theme="light"` / "auto"
 * still lighten the *background* fill/shadow for contrast against light
 * page backgrounds, but never invent border/text colors that don't exist
 * anywhere in the radio component (see GlassSliderCommonTagLib's CSS for
 * the shared derivation table).
 *
 * Single-thumb usage:
 *   <glass:rangeSlider name="budget" min="0" max="100000" step="1000"
 *                       value="45000" label="Monthly budget" unit=" USD"/>
 *
 * Dual-thumb (range) usage:
 *   <glass:rangeSlider name="age" min="21" max="60" step="1" dual="true"
 *                       valueMin="24" valueMax="34" label="Age range" unit=" yrs"/>
 *
 * Submits as:
 *   single mode -> one field:  ${name}
 *   dual mode   -> two fields: ${name}Min, ${name}Max
 *
 * Attributes:
 *   name       (required) form field name
 *   id                     element id, auto-generated if omitted
 *   min                    minimum value, default 0
 *   max                    maximum value, default 100
 *   step                   step increment, default 1
 *   value                  initial value (single-thumb mode)
 *   valueMin / valueMax    initial values (dual-thumb / range mode)
 *   dual        boolean    enable a two-handle range slider, default false
 *   label                  visible label text
 *   unit                   suffix shown next to the value, e.g. " km", " yrs", "%"
 *   disabled    boolean
 *   theme                  "auto" (default, follows OS) | "dark" | "light"
 *   locale                 BCP-47 tag for number formatting, e.g. "en-IN" (optional)
 *   haptics     boolean    vibrate on drag/step on supporting devices, default true
 */
class GlassRangeSliderTagLib {

  static namespace = 'glass'
  static encodeAsForTags = [rangeSlider: 'raw', rangeSliderAssets: 'raw']

  private static int instanceCounter = 0

  /** Only this kind's own small script — the shared CSS/engine script
   *  is injected once by GlassSliderCommonTagLib, tracked under its own
   *  flag there. */
  private static final String KIND_ASSETS_FLAG = 'glassRangeSliderKindAssetsInjected'

  private static final String KIND_JS = '''
(function () {
  'use strict';

  // The "range" kind is the simplest possible use of the shared Engine:
  // no unit conversion, no toggle — just append the configured suffix
  // (e.g. " yrs", " USD") to the formatted number.
  GlassSlider.registerKind('range', function (root) {
    var unit = root.dataset.unit || '';
    var decimals = (String(root.dataset.step).split('.')[1] || '').length;
    var format = GlassSlider.makeFormatter(root.dataset.locale, decimals);

    return new GlassSlider.Engine(root, {
      min: parseFloat(root.dataset.min),
      max: parseFloat(root.dataset.max),
      step: parseFloat(root.dataset.step) || 1,
      dual: root.dataset.dual === 'true',
      disabled: root.dataset.disabled === 'true',
      haptics: root.dataset.haptics !== 'false',
      formatText: function (which, value) { return format(value) + unit; },
      eventName: 'gs-range:change',
      buildDetail: function (engine) {
        return engine.dual
          ? { name: root.dataset.name, valueMin: engine.valueMin, valueMax: engine.valueMax }
          : { name: root.dataset.name, value: engine.valueMin };
      }
    });
  });

  // Kept for anyone still calling the old per-kind init name directly;
  // both now just delegate to the one shared entrypoint.
  window.GsRangeSlider = { init: GlassSlider.init };
})();
'''

  /** Explicit/manual include for <head>. Injects the shared assets (if
   *  not already present this request) plus this kind's own script. */
  def rangeSliderAssets = { attrs ->
    GlassSliderCommonTagLib.injectAssets(request, out)
    injectKindAssets()
  }

  def rangeSlider = { attrs ->
    String name = attrs.remove('name')
    if (!name) {
      throwTagError('Tag [rangeSlider] is missing required attribute [name]')
    }

    String id = (attrs.remove('id') ?: "gs-range-${name}-${instanceCounter++}").toString()
    BigDecimal min = GlassSliderCommonTagLib.toBigDecimal(attrs.remove('min'), 0)
    BigDecimal max = GlassSliderCommonTagLib.toBigDecimal(attrs.remove('max'), 100)
    BigDecimal step = GlassSliderCommonTagLib.toBigDecimal(attrs.remove('step'), 1)
    boolean dual = GlassSliderCommonTagLib.toBoolean(attrs.remove('dual'))
    boolean disabled = GlassSliderCommonTagLib.toBoolean(attrs.remove('disabled'))
    String label = attrs.remove('label')
    String unit = (attrs.remove('unit') ?: '').toString()
    String theme = (attrs.remove('theme') ?: 'auto').toString() // "auto" | "dark" | "light"
    String locale = attrs.remove('locale')
    boolean haptics = attrs.containsKey('haptics') ? GlassSliderCommonTagLib.toBoolean(attrs.remove('haptics')) : true

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

    StringBuilder sb = new StringBuilder(512)

    sb << '<div class="gs-slider" data-gs-slider data-gs-kind="range" '
    sb << 'data-name="' << name.encodeAsHTML() << '" '
    sb << 'data-min="' << min << '" data-max="' << max << '" data-step="' << step << '" '
    sb << 'data-unit="' << unit.encodeAsHTML() << '" '
    sb << 'data-dual="' << dual << '" data-theme="' << resolvedTheme << '" '
    sb << 'data-haptics="' << haptics << '" '
    if (locale) sb << 'data-locale="' << locale.encodeAsHTML() << '" '
    if (disabled) sb << 'data-disabled="true" '
    sb << 'id="' << id << '">'

    if (label) {
      sb << '<div class="gs-slider-header">'
      sb << '<span class="gs-slider-label-group">'
      sb << '<span class="gs-slider-label">' << label.encodeAsHTML() << '</span>'
      sb << '<span class="gs-slider-readout" data-gs-readout>'
      sb << (dual
        ? "${GlassSliderCommonTagLib.fmt(valueMin)}${unit.encodeAsHTML()} &ndash; ${GlassSliderCommonTagLib.fmt(valueMax)}${unit.encodeAsHTML()}"
        : "${GlassSliderCommonTagLib.fmt(valueMin)}${unit.encodeAsHTML()}")
      sb << '</span>'
      sb << '</span>'
      sb << '</div>'
    }

    sb << '<div class="gs-slider-body">'
    sb << '<div class="gs-slider-track" data-gs-track>'
    sb << '<div class="gs-slider-fill" data-gs-fill></div>'

    sb << thumbMarkup('min', min, max, valueMin, label, unit, disabled)
    if (dual) {
      sb << thumbMarkup('max', min, max, valueMax, label, unit, disabled)
    }

    sb << '</div>' // .gs-slider-track
    sb << '</div>' // .gs-slider-body

    if (dual) {
      sb << '<input type="hidden" name="' << name.encodeAsHTML() << 'Min" data-gs-input="min" value="' << valueMin << '"/>'
      sb << '<input type="hidden" name="' << name.encodeAsHTML() << 'Max" data-gs-input="max" value="' << valueMax << '"/>'
    } else {
      sb << '<input type="hidden" name="' << name.encodeAsHTML() << '" data-gs-input="min" value="' << valueMin << '"/>'
    }

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
                             String label, String unit, boolean disabled) {
    String suffix = which == 'min' ? 'minimum' : 'maximum'
    String ariaLabel = (label ? "${label} ${suffix}" : "${suffix} value").toString()
    String displayText = "${GlassSliderCommonTagLib.fmt(value)}${unit}"
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
}
