package com.murad.app.matrimony

import groovy.util.logging.Slf4j

/**
 * Count-Up Number — GSP TagLib
 *
 * Usage in GSP:
 *   <glass:countUp id="profileViews" value="58" title="Profile Views" accent="info"/>
 *
 *   <!-- later, e.g. after a websocket push or polling refresh -->
 *   <script>
 *     glassCountUp.set('profileViews', 71);   // animates 58 -> 71, then pulses
 *   </script>
 *
 *   <!-- render at 0 and count up manually instead of on page load -->
 *   <glass:countUp id="matchCount" value="0" trigger="manual" title="New Matches"/>
 *   <script>
 *     glassCountUp.set('matchCount', 4);
 *   </script>
 *
 * What it renders
 * ─────────────────────────────────────────────────────────────────────────
 * A single stat number that eases from its CURRENT displayed value (0 on
 * first render, or whatever it last landed on) up to a new target value
 * over `duration`, formatting the number as it counts (decimals, thousands
 * separator, prefix/suffix — e.g. "$", "%", " views"). Once the count
 * finishes, it briefly pulses (scale + glow) so repeated updates to the
 * same stat still register as "something changed" even for small deltas,
 * not just the first count-up.
 *
 * By default (trigger="auto") it counts up from 0 to `value` as soon as it
 * renders — good for a stat that's already known server-side. For stats
 * that arrive/refresh asynchronously (polling, websockets, after a AJAX
 * action), use trigger="manual" and drive it via the global JS API this
 * tag registers:
 *
 *   glassCountUp.set('yourId', 71)                     // count up/down to 71
 *   glassCountUp.set('yourId', 71, { duration: 400 })  // one-off override
 *   glassCountUp.get('yourId')                          // current displayed value (number)
 *
 * Counting also works downward (e.g. a countdown of remaining free
 * interests) — the same easing and formatting apply either direction.
 *
 * Attributes
 * ─────────────────────────────────────────────────────────────────────────
 *   id            (String, optional)  — unique id for this instance; needed to
 *                                       target it with glassCountUp.set('id')
 *                                       when more than one exists on the page.
 *                                       Auto-generated when omitted.
 *   value         (Number, optional)  — target value to count up (or down) to
 *                                       (default: 0). May be negative or a
 *                                       decimal; see `decimals`.
 *   trigger       (String, optional)  — "auto" (default) counts from 0 to
 *                                       `value` as soon as it renders; "manual"
 *                                       renders at 0 and waits for
 *                                       glassCountUp.set(id, ...) calls.
 *   duration      (ms, optional)      — count animation duration (default: 1200,
 *                                       clamped >= 100).
 *   decimals      (int, optional)     — decimal places to display (default: 0,
 *                                       clamped 0..6).
 *   separator     (String, optional)  — thousands separator (default: ","; pass
 *                                       an empty string to disable grouping).
 *   prefix        (String, optional)  — text before the number, e.g. "$".
 *   suffix        (String, optional)  — text after the number, e.g. "%", " views".
 *   pulseOnChange (Boolean, optional) — brief scale/glow pulse once each count
 *                                       finishes (default: true).
 *   accent        (String, optional)  — Bootstrap variant name (primary, secondary,
 *                                       success, danger, warning, info, dark, light)
 *                                       resolving to var(--bs-{name}), or any raw
 *                                       CSS color. Drives the number's color and
 *                                       pulse glow. Default: "primary"
 *   fontSize      (CSS length, opt.)  — size of the number itself (default: "2.4rem").
 *   title         (String, optional)  — label shown under the number.
 *   titleHtml     (Boolean, optional, default: false) — render title as raw HTML.
 *   id            (String, optional)  — HTML id for the wrapper (see `id` above).
 *   class         (String, optional)  — extra CSS classes appended to the wrapper.
 */
@Slf4j
class CountUpTagLib {

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

  private double toDoubleOrDefault(Object v, double fallback) {
    if (v == null) return fallback
    try {
      return Double.parseDouble(v.toString().trim())
    } catch (NumberFormatException ignored) {
      return fallback
    }
  }

  private int clampInt(int v, int min, int max) {
    return Math.max(min, Math.min(max, v))
  }

  /**
   * Renders a count-up stat number and registers it with the page-level
   * window.glassCountUp.set(id, value) / .get(id) API.
   *
   * @attr id             Unique id for this instance (auto-generated if omitted).
   * @attr value          Target value to count to (default: 0). May be negative/decimal.
   * @attr trigger        "auto" (default) counts on render, or "manual" to
   *                      drive entirely via glassCountUp.set().
   * @attr duration       Count animation duration in ms (default: 1200).
   * @attr decimals       Decimal places to display (default: 0, clamped 0..6).
   * @attr separator      Thousands separator (default: ","; "" disables grouping).
   * @attr prefix         Text before the number, e.g. "$".
   * @attr suffix         Text after the number, e.g. "%", " views".
   * @attr pulseOnChange  "true"/"false" — pulse once each count finishes (default: true).
   * @attr accent         Bootstrap variant or raw CSS color for the number/pulse (default: "primary").
   * @attr fontSize       CSS length for the number's size (default: "2.4rem").
   * @attr title           Label shown under the number.
   * @attr titleHtml           "true" to render title as raw HTML (default: false).
   * @attr class                    Extra CSS classes for the wrapper.
   */
  def countUp = { attrs, body ->
    String id = (attrs.remove('id') ?: "glassCountUp_${System.currentTimeMillis()}_${(int) (Math.random() * 9999)}").toString()
    String extra = (attrs.remove('class') ?: '').toString()

    double value = toDoubleOrDefault(attrs.remove('value'), 0d)

    String trigger = (attrs.remove('trigger') ?: 'auto').toString().toLowerCase()
    if (!(trigger in ['auto', 'manual'])) trigger = 'auto'

    int durationMs = Math.max(100, toIntOrDefault(attrs.remove('duration'), 1200))
    int decimals = clampInt(toIntOrDefault(attrs.remove('decimals'), 0), 0, 6)
    String separator = attrs.containsKey('separator') ? attrs.remove('separator').toString() : ','
    String prefix = (attrs.remove('prefix') ?: '').toString()
    String suffix = (attrs.remove('suffix') ?: '').toString()
    boolean pulseOnChange = attrs.containsKey('pulseOnChange') ? Boolean.parseBoolean(attrs.remove('pulseOnChange').toString()) : true
    String accent = resolveColor((attrs.remove('accent') ?: 'primary').toString())
    String fontSize = (attrs.remove('fontSize') ?: '2.4rem').toString()

    String title = (attrs.remove('title') ?: '').toString()
    boolean titleHtml = attrs.remove('titleHtml') in [true, 'true', '1', 'yes']
    String renderedTitle = title ? (titleHtml ? title : title.encodeAsHTML()) : ''

    // Escape for safe embedding inside a single-quoted JS string literal
    String prefixJs = prefix.replace('\\', '\\\\').replace("'", "\\'")
    String suffixJs = suffix.replace('\\', '\\\\').replace("'", "\\'")
    String separatorJs = separator.replace('\\', '\\\\').replace("'", "\\'")

    log.debug("countUp: id=${id}, value=${value}, trigger=${trigger}, duration=${durationMs}, decimals=${decimals}, separator='${separator}', prefix='${prefix}', suffix='${suffix}', pulseOnChange=${pulseOnChange}, accent=${accent}")

    out << """
<style>
  #${id}.glass-countup-wrap {
    --countup-accent: ${accent};
    display          : inline-block;
    text-align        : center;
    font-family        : 'Outfit', sans-serif;
  }

  #${id} .glass-countup-value {
    display                : inline-block;
    font-size               : ${fontSize};
    font-weight             : 800;
    letter-spacing          : -0.02em;
    color                    : var(--countup-accent);
    font-variant-numeric     : tabular-nums;
    -webkit-transition       : -webkit-transform 0.25s ease, text-shadow 0.35s ease;
            transition       : transform 0.25s ease, text-shadow 0.35s ease;
    -webkit-transform         : scale(1);
            transform         : scale(1);
    text-shadow               : 0 0 0 color-mix(in srgb, var(--countup-accent) 0%, transparent);
  }
  @supports not (background: color-mix(in srgb, red 50%, transparent)) {
    #${id} .glass-countup-value { text-shadow: none; }
  }

  #${id} .glass-countup-value.is-pulsing {
    -webkit-animation : glassCountUpPulse_${id} 0.5s ease-out;
            animation : glassCountUpPulse_${id} 0.5s ease-out;
  }
  @-webkit-keyframes glassCountUpPulse_${id} {
    0%   { -webkit-transform: scale(1); text-shadow: 0 0 0 color-mix(in srgb, var(--countup-accent) 0%, transparent); }
    35%  { -webkit-transform: scale(1.12); text-shadow: 0 0 18px color-mix(in srgb, var(--countup-accent) 70%, transparent); }
    100% { -webkit-transform: scale(1); text-shadow: 0 0 0 color-mix(in srgb, var(--countup-accent) 0%, transparent); }
  }
  @keyframes glassCountUpPulse_${id} {
    0%   { transform: scale(1); text-shadow: 0 0 0 color-mix(in srgb, var(--countup-accent) 0%, transparent); }
    35%  { transform: scale(1.12); text-shadow: 0 0 18px color-mix(in srgb, var(--countup-accent) 70%, transparent); }
    100% { transform: scale(1); text-shadow: 0 0 0 color-mix(in srgb, var(--countup-accent) 0%, transparent); }
  }

  #${id} .glass-countup-title {
    display     : block;
    margin-top  : 4px;
    font-size   : 0.8rem;
    font-weight : 300;
    color       : rgba(255, 255, 255, 0.6);
    letter-spacing: 0.02em;
  }

  @media (prefers-reduced-motion: reduce) {
    #${id} .glass-countup-value { transition: none; }
    #${id} .glass-countup-value.is-pulsing { -webkit-animation: none; animation: none; }
  }
</style>

<div id="${id}" class="glass-countup-wrap${extra ? ' ' + extra : ''}">
  <span class="glass-countup-value" data-glass-countup="${id}" aria-live="polite">${prefix.encodeAsHTML()}0${suffix.encodeAsHTML()}</span>
  ${renderedTitle ? "<span class=\"glass-countup-title\">${renderedTitle}</span>" : ''}
</div>

<script>
(function () {
  // ── Shared global API: window.glassCountUp.set(id, value, overrides) ─────
  // Reused across every glass:countUp instance on the page — defined once
  // (first instance wins), every subsequent instance just registers.
  window.glassCountUp = window.glassCountUp || (function () {
    var registry = {}; // id -> { duration, decimals, separator, prefix, suffix, pulseOnChange, currentValue }

    function formatNumber(n, decimals, separator) {
      var fixed = n.toFixed(decimals);
      var parts = fixed.split('.');
      if (separator) {
        parts[0] = parts[0].replace(/\\B(?=(\\d{3})+(?!\\d))/g, separator);
      }
      return parts.join('.');
    }

    function animateTo(id, targetValue, overrides) {
      var entry = registry[id];
      if (!entry) {
        if (window.console) console.warn('glassCountUp.set: no count-up found for id "' + id + '"');
        return;
      }
      var duration = (overrides && overrides.duration != null) ? overrides.duration : entry.duration;
      var decimals = (overrides && overrides.decimals != null) ? overrides.decimals : entry.decimals;
      var separator = (overrides && overrides.separator != null) ? overrides.separator : entry.separator;
      var prefix = (overrides && overrides.prefix != null) ? overrides.prefix : entry.prefix;
      var suffix = (overrides && overrides.suffix != null) ? overrides.suffix : entry.suffix;
      var pulseOnChange = (overrides && overrides.pulseOnChange != null) ? overrides.pulseOnChange : entry.pulseOnChange;

      var el = document.querySelector('[data-glass-countup="' + id + '"]');
      if (!el) return;

      var startValue = entry.currentValue;
      var delta = targetValue - startValue;
      var stepStart = null;

      el.classList.remove('is-pulsing');

      function easeOutCubic(t) { return 1 - Math.pow(1 - t, 3); }

      function frame(ts) {
        if (!stepStart) stepStart = ts;
        var t = Math.min(1, (ts - stepStart) / duration);
        var eased = easeOutCubic(t);
        var current = startValue + delta * eased;
        el.textContent = prefix + formatNumber(current, decimals, separator) + suffix;

        if (t < 1) {
          requestAnimationFrame(frame);
        } else {
          el.textContent = prefix + formatNumber(targetValue, decimals, separator) + suffix;
          entry.currentValue = targetValue;
          if (pulseOnChange && delta !== 0) {
            el.classList.add('is-pulsing');
            setTimeout(function () { el.classList.remove('is-pulsing'); }, 520);
          }
        }
      }
      requestAnimationFrame(frame);
    }

    return {
      _register: function (id, cfg) {
        registry[id] = {
          duration: cfg.duration,
          decimals: cfg.decimals,
          separator: cfg.separator,
          prefix: cfg.prefix,
          suffix: cfg.suffix,
          pulseOnChange: cfg.pulseOnChange,
          currentValue: 0
        };
      },
      /** Animates a registered count-up to `value` (up or down). `overrides`
       *  may contain { duration, decimals, separator, prefix, suffix,
       *  pulseOnChange } for a one-off change on just this call. */
      set: function (id, value, overrides) {
        animateTo(id, value, overrides);
      },
      /** Returns the count-up's current (target) value, or null if unknown. */
      get: function (id) {
        return registry[id] ? registry[id].currentValue : null;
      }
    };
  })();

  window.glassCountUp._register('${id}', {
    duration: ${durationMs},
    decimals: ${decimals},
    separator: '${separatorJs}',
    prefix: '${prefixJs}',
    suffix: '${suffixJs}',
    pulseOnChange: ${pulseOnChange}
  });

  ${trigger == 'auto' ? """
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', function () { window.glassCountUp.set('${id}', ${value}); });
  } else {
    window.glassCountUp.set('${id}', ${value});
  }
  """ : ''}
})();
</script>
"""
  }
}
