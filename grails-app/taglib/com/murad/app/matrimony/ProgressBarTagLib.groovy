package com.murad.app.matrimony

import groovy.util.logging.Slf4j

/**
 * Progress Bar — Fill & Glow — GSP TagLib
 *
 * Usage in GSP:
 *   <glass:progressBar id="profileCompletion" value="78" title="Profile Completeness"
 *                       accent="success"/>
 *
 *   <!-- update it later from JS (e.g. after an AJAX save) -->
 *   <script>
 *     glassProgress.set('profileCompletion', 92);
 *   </script>
 *
 *   <!-- render at 0 and animate manually instead of on page load -->
 *   <glass:progressBar id="uploadBar" value="0" trigger="manual" title="Uploading..."/>
 *   <script>
 *     glassProgress.set('uploadBar', 45);   // ...later, as real progress comes in
 *     glassProgress.set('uploadBar', 100);  // ...on completion — triggers the glow pulse
 *   </script>
 *
 * What it renders
 * ─────────────────────────────────────────────────────────────────────────
 * A glassmorphic track with an animated fill bar that:
 *   1. eases from its current width to the target `value`% over `duration`,
 *   2. shows a diagonal shimmer stripe sweeping across the fill while it's
 *      actively animating (skipped when prefers-reduced-motion is set),
 *   3. once the fill finishes moving AND the value has reached
 *      `glowThreshold` (100 by default — i.e. "done"), plays a brief outer
 *      glow pulse on the filled portion, then settles back to a steady glow.
 * An optional label row above the bar shows a title (left) and the live
 * percentage (right), both of which update in sync with the bar.
 *
 * Nothing is required to make it move on page load — by default
 * (trigger="auto") it animates from 0 up to `value` as soon as it renders.
 * For bars driven by real async progress (uploads, multi-step wizards),
 * use trigger="manual" and drive it entirely via the global JS API this
 * tag registers:
 *
 *   glassProgress.set('yourId', 62)                 // animate to 62%
 *   glassProgress.set('yourId', 100, { duration: 400 }) // one-off override
 *   glassProgress.get('yourId')                      // current % (number)
 *
 * Attributes
 * ─────────────────────────────────────────────────────────────────────────
 *   id             (String, optional)  — unique id for this instance; needed
 *                                        to target it with glassProgress.set('id')
 *                                        when more than one exists on the page.
 *                                        Auto-generated when omitted.
 *   value          (int, optional)     — target fill percentage, 0..100
 *                                        (default: 0, clamped 0..100).
 *   trigger        (String, optional)  — "auto" (default) animates from 0 up
 *                                        to `value` as soon as the bar renders;
 *                                        "manual" renders at 0% and waits for
 *                                        glassProgress.set(id, ...) calls.
 *   duration       (ms, optional)      — fill animation duration (default: 1200,
 *                                        clamped >= 100).
 *   glowThreshold  (int, optional)     — value at/above which the completion
 *                                        glow pulse plays once the fill
 *                                        finishes moving (default: 100,
 *                                        clamped 1..100). Set lower if, say,
 *                                        90%+ should already feel "done".
 *   striped        (Boolean, optional) — animated diagonal shimmer while the
 *                                        fill is actively moving (default: true).
 *   height         (px, optional)      — track height (default: 14, clamped 4..60).
 *   accent         (String, optional)  — Bootstrap variant name (primary, secondary,
 *                                        success, danger, warning, info, dark, light)
 *                                        resolving to var(--bs-{name}), or any raw
 *                                        CSS color. Drives the fill and glow color.
 *                                        Default: "primary"
 *   glass          (Boolean, optional) — glassmorphic frosted track (default: true).
 *   title          (String, optional)  — label shown above-left of the bar.
 *   titleHtml      (Boolean, optional, default: false) — render title as raw HTML.
 *   showPercentage (Boolean, optional) — show the live "NN%" label above-right
 *                                        of the bar (default: true).
 *   id             (String, optional)  — HTML id for the wrapper (see `id` above).
 *   class          (String, optional)  — extra CSS classes appended to the wrapper.
 */
@Slf4j
class ProgressBarTagLib {

  static namespace = 'glass'

  private static final Set<String> BS_VARIANTS = [
    'primary', 'secondary', 'success', 'danger', 'warning', 'info', 'dark', 'light'
  ] as Set

  private String resolveColor(String value) {
    return (value in BS_VARIANTS) ? "var(--bs-${value})".toString() : value
  }

  private int toIntOrDefault(Object v, int fallback) {
    if (v == null) return fallback
    try {
      return Integer.parseInt(v.toString().trim())
    } catch (NumberFormatException ignored) {
      return fallback
    }
  }

  private int clampInt(int v, int min, int max) {
    return Math.max(min, Math.min(max, v))
  }

  /**
   * Renders a fill-and-glow progress bar and registers it with the
   * page-level window.glassProgress.set(id, value) / .get(id) API.
   *
   * @attr id              Unique id for this instance (auto-generated if omitted).
   * @attr value           Target fill percentage, 0..100 (default: 0).
   * @attr trigger         "auto" (default) animates on render, or "manual" to
   *                       drive entirely via glassProgress.set().
   * @attr duration        Fill animation duration in ms (default: 1200).
   * @attr glowThreshold   Value at/above which the completion glow plays (default: 100).
   * @attr striped         "true"/"false" — animated shimmer while filling (default: true).
   * @attr height          Track height in px (default: 14, clamped 4..60).
   * @attr accent          Bootstrap variant or raw CSS color for fill/glow (default: "primary").
   * @attr glass           "true"/"false" glassmorphic track look (default: true).
   * @attr title            Label shown above-left of the bar.
   * @attr titleHtml           "true" to render title as raw HTML (default: false).
   * @attr showPercentage        "true"/"false" — show the live percentage label (default: true).
   * @attr class                    Extra CSS classes for the wrapper.
   */
  def progressBar = { attrs, body ->
    String id = (attrs.remove('id') ?: "glassProgress_${System.currentTimeMillis()}_${(int) (Math.random() * 9999)}").toString()
    String extra = (attrs.remove('class') ?: '').toString()

    int value = clampInt(toIntOrDefault(attrs.remove('value'), 0), 0, 100)

    String trigger = (attrs.remove('trigger') ?: 'auto').toString().toLowerCase()
    if (!(trigger in ['auto', 'manual'])) trigger = 'auto'

    int durationMs = Math.max(100, toIntOrDefault(attrs.remove('duration'), 1200))
    int glowThreshold = clampInt(toIntOrDefault(attrs.remove('glowThreshold'), 100), 1, 100)
    boolean striped = attrs.containsKey('striped') ? Boolean.parseBoolean(attrs.remove('striped').toString()) : true
    int heightPx = clampInt(toIntOrDefault(attrs.remove('height'), 14), 4, 60)
    String accent = resolveColor((attrs.remove('accent') ?: 'primary').toString())
    boolean glass = attrs.containsKey('glass') ? Boolean.parseBoolean(attrs.remove('glass').toString()) : true

    String title = (attrs.remove('title') ?: '').toString()
    boolean titleHtml = attrs.remove('titleHtml') in [true, 'true', '1', 'yes']
    boolean showPercentage = attrs.containsKey('showPercentage') ? Boolean.parseBoolean(attrs.remove('showPercentage').toString()) : true

    String renderedTitle = title ? (titleHtml ? title : title.encodeAsHTML()) : ''

    int startValue = trigger == 'auto' ? 0 : 0 // both start visually at 0; auto immediately animates to `value`

    log.debug("progressBar: id=${id}, value=${value}, trigger=${trigger}, duration=${durationMs}, glowThreshold=${glowThreshold}, striped=${striped}, accent=${accent}")

    out << """
<style>
  #${id}.glass-progress-wrap {
    width: 100%;
  }

  #${id} .glass-progress-labels {
    display                : -webkit-box;
    display                 : -webkit-flex;
    display                 : -ms-flexbox;
    display                 : flex;
    -webkit-box-pack        : justify;
    -webkit-justify-content : space-between;
        -ms-flex-pack       : justify;
            justify-content : space-between;
    -webkit-box-align       : baseline;
    -webkit-align-items     : baseline;
        -ms-flex-align      : baseline;
            align-items     : baseline;
    margin-bottom            : 8px;
    font-family              : 'Outfit', sans-serif;
  }
  #${id} .glass-progress-title {
    font-size   : 0.85rem;
    font-weight : 600;
    color       : rgba(255,255,255,0.85);
  }
  #${id} .glass-progress-pct {
    font-size   : 0.8rem;
    font-weight : 700;
    color       : ${accent};
    font-variant-numeric: tabular-nums;
  }

  #${id} .glass-progress-track {
    --progress-accent: ${accent};

    position               : relative;
    width                  : 100%;
    height                 : ${heightPx}px;
    -webkit-border-radius  : 999px;
            border-radius  : 999px;
    overflow               : hidden;
    isolation              : isolate;

    ${glass ? """
    background              : rgba(255, 255, 255, 0.10);
    -webkit-backdrop-filter : blur(10px);
            backdrop-filter : blur(10px);
    border                   : 1px solid rgba(255, 255, 255, 0.18);
    -webkit-box-shadow       : inset 0 1px 3px rgba(0,0,0,0.25);
            box-shadow       : inset 0 1px 3px rgba(0,0,0,0.25);
    """ : """
    background : rgba(0, 0, 0, 0.08);
    border      : 1px solid rgba(0, 0, 0, 0.10);
    """}
  }

  #${id} .glass-progress-fill {
    position                : relative;
    height                  : 100%;
    width                   : ${startValue}%;
    -webkit-border-radius   : 999px;
            border-radius   : 999px;
    background              : var(--progress-accent);
    -webkit-box-shadow      : 0 0 0 0 color-mix(in srgb, var(--progress-accent) 0%, transparent);
            box-shadow      : 0 0 0 0 color-mix(in srgb, var(--progress-accent) 0%, transparent);

    -webkit-transition:
        width 0ms linear,
        -webkit-box-shadow 0.4s ease;
            transition:
        width 0ms linear,
        box-shadow 0.4s ease;
  }
  @supports not (background: color-mix(in srgb, red 50%, transparent)) {
    #${id} .glass-progress-fill { box-shadow: none; }
  }

  /* Diagonal shimmer stripe, only running while .is-filling is present */
  #${id} .glass-progress-fill::after {
    content    : "";
    position   : absolute;
    inset      : 0;
    background : -webkit-linear-gradient(
                    135deg,
                    rgba(255,255,255,0) 0%,
                    rgba(255,255,255,0.35) 45%,
                    rgba(255,255,255,0) 60%
                 );
    background : linear-gradient(
                    135deg,
                    rgba(255,255,255,0) 0%,
                    rgba(255,255,255,0.35) 45%,
                    rgba(255,255,255,0) 60%
                 );
    background-size : 60px 100%;
    background-repeat: repeat-x;
    opacity          : 0;
  }
  #${id} .glass-progress-fill.is-filling::after {
    opacity : ${striped ? 1 : 0};
    -webkit-animation : glassProgressShimmer_${id} 0.9s linear infinite;
            animation : glassProgressShimmer_${id} 0.9s linear infinite;
  }
  @-webkit-keyframes glassProgressShimmer_${id} {
    from { background-position: 0 0; }
    to   { background-position: 120px 0; }
  }
  @keyframes glassProgressShimmer_${id} {
    from { background-position: 0 0; }
    to   { background-position: 120px 0; }
  }

  /* Completion glow: a brief bright pulse, then it settles to a steady
     soft glow so a finished bar still reads as "done" at rest. */
  #${id} .glass-progress-fill.is-glow-pulse {
    -webkit-animation : glassProgressGlowPulse_${id} 0.7s ease-out;
            animation : glassProgressGlowPulse_${id} 0.7s ease-out;
  }
  @-webkit-keyframes glassProgressGlowPulse_${id} {
    0%   { -webkit-box-shadow: 0 0 0 0 color-mix(in srgb, var(--progress-accent) 0%, transparent); }
    40%  { -webkit-box-shadow: 0 0 18px 4px color-mix(in srgb, var(--progress-accent) 85%, transparent); }
    100% { -webkit-box-shadow: 0 0 8px 1px color-mix(in srgb, var(--progress-accent) 45%, transparent); }
  }
  @keyframes glassProgressGlowPulse_${id} {
    0%   { box-shadow: 0 0 0 0 color-mix(in srgb, var(--progress-accent) 0%, transparent); }
    40%  { box-shadow: 0 0 18px 4px color-mix(in srgb, var(--progress-accent) 85%, transparent); }
    100% { box-shadow: 0 0 8px 1px color-mix(in srgb, var(--progress-accent) 45%, transparent); }
  }
  #${id} .glass-progress-fill.is-glow-settled {
    -webkit-box-shadow: 0 0 8px 1px color-mix(in srgb, var(--progress-accent) 45%, transparent);
            box-shadow: 0 0 8px 1px color-mix(in srgb, var(--progress-accent) 45%, transparent);
  }

  @media (prefers-reduced-motion: reduce) {
    #${id} .glass-progress-fill { transition: width 0ms linear; }
    #${id} .glass-progress-fill::after { display: none; }
    #${id} .glass-progress-fill.is-glow-pulse { -webkit-animation: none; animation: none; }
  }
</style>

<div id="${id}" class="glass-progress-wrap${extra ? ' ' + extra : ''}">
  ${(renderedTitle || showPercentage) ? """
  <div class="glass-progress-labels">
    ${renderedTitle ? "<span class=\"glass-progress-title\">${renderedTitle}</span>" : '<span></span>'}
    ${showPercentage ? "<span class=\"glass-progress-pct\" data-glass-progress-pct=\"${id}\">${startValue}%</span>" : ''}
  </div>
  """ : ''}
  <div class="glass-progress-track" role="progressbar" aria-valuemin="0" aria-valuemax="100"
       aria-valuenow="${startValue}" aria-label="${renderedTitle ? renderedTitle.replaceAll(/<[^>]*>/, '') : 'Progress'}">
    <div class="glass-progress-fill" data-glass-progress-fill="${id}"></div>
  </div>
</div>

<script>
(function () {
  // ── Shared global API: window.glassProgress.set(id, value, overrides) ────
  // Reused across every glass:progressBar instance on the page — defined
  // once (first instance wins), every subsequent instance just registers.
  window.glassProgress = window.glassProgress || (function () {
    var registry = {}; // id -> { duration, glowThreshold, currentValue }

    function animateTo(id, targetValue, overrides) {
      var entry = registry[id];
      if (!entry) {
        if (window.console) console.warn('glassProgress.set: no progress bar found for id "' + id + '"');
        return;
      }
      var duration = (overrides && overrides.duration != null) ? overrides.duration : entry.duration;
      var glowThreshold = (overrides && overrides.glowThreshold != null) ? overrides.glowThreshold : entry.glowThreshold;

      targetValue = Math.max(0, Math.min(100, targetValue));

      var track = document.querySelector('[data-glass-progress-fill="' + id + '"]');
      var pctLabel = document.querySelector('[data-glass-progress-pct="' + id + '"]');
      var trackWrap = track ? track.parentElement : null;
      if (!track) return;

      track.classList.remove('is-glow-pulse', 'is-glow-settled');
      track.classList.add('is-filling');
      track.style.transition = 'width ' + duration + 'ms cubic-bezier(0.22, 0.61, 0.36, 1)';

      // Force reflow so the transition duration change takes effect before
      // the width change that should animate with it.
      void track.offsetWidth;

      track.style.width = targetValue + '%';
      if (trackWrap) trackWrap.setAttribute('aria-valuenow', String(targetValue));

      var startValue = entry.currentValue;
      var stepStart = null;

      function animateLabel(ts) {
        if (pctLabel) {
          if (!stepStart) stepStart = ts;
          var t = Math.min(1, (ts - stepStart) / duration);
          var current = Math.round(startValue + (targetValue - startValue) * t);
          pctLabel.textContent = current + '%';
          if (t < 1) requestAnimationFrame(animateLabel);
        }
      }
      if (pctLabel) requestAnimationFrame(animateLabel);

      entry.currentValue = targetValue;

      setTimeout(function () {
        track.classList.remove('is-filling');
        if (targetValue >= glowThreshold) {
          track.classList.add('is-glow-pulse');
          setTimeout(function () {
            track.classList.remove('is-glow-pulse');
            track.classList.add('is-glow-settled');
          }, 700);
        }
      }, duration + 30);
    }

    return {
      _register: function (id, cfg) {
        registry[id] = { duration: cfg.duration, glowThreshold: cfg.glowThreshold, currentValue: 0 };
      },
      /** Animates a registered bar to `value` (0..100). `overrides` may
       *  contain { duration, glowThreshold } for a one-off change. */
      set: function (id, value, overrides) {
        animateTo(id, value, overrides);
      },
      /** Returns the bar's current (target) value, or null if unknown. */
      get: function (id) {
        return registry[id] ? registry[id].currentValue : null;
      }
    };
  })();

  window.glassProgress._register('${id}', {
    duration: ${durationMs},
    glowThreshold: ${glowThreshold}
  });

  ${trigger == 'auto' ? """
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', function () { window.glassProgress.set('${id}', ${value}); });
  } else {
    window.glassProgress.set('${id}', ${value});
  }
  """ : ''}
})();
</script>
"""
  }
}
