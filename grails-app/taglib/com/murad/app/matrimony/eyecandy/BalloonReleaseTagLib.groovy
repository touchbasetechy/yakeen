package com.murad.app.matrimony

/**
 * Balloon Release — GSP TagLib
 *
 * Companion to glass:starBurst / glass:heartBurst — same fixed-overlay,
 * inline-SVG, no-canvas/no-rAF architecture, adapted for balloons that
 * rise and drift rather than stars/hearts that burst and fall.
 *
 * Usage in GSP:
 *   <!-- one-shot release of balloons from a point, e.g. a "match found"
 *        celebration or a proposal-accepted moment -->
 *   <glass:balloonRelease id="matchBurst" mode="burst" attachTo=".match-badge"/>
 *
 *   <!-- continuous stream of balloons that spawns while hovering/dragging
 *        over a matching element, like a party popper you can hold down -->
 *   <glass:balloonRelease id="partyTrail" mode="trail" attachTo=".celebrate-card"/>
 *
 *   <!-- fire either mode manually from your own JS -->
 *   <script>
 *     glassBalloonRelease.release('matchBurst', clickEvent);  // burst: many balloons at once
 *     glassBalloonRelease.drift('partyTrail', moveEvent);      // trail: a single balloon
 *   </script>
 *
 * What it renders
 * ─────────────────────────────────────────────────────────────────────────
 * A fixed, full-viewport, pointer-events:none overlay <div>, populated with
 * small inline-SVG balloon elements on demand — same DOM+CSS approach as
 * glass:starBurst (no canvas, no rAF loop). Each balloon removes itself
 * from the DOM on animationend.
 *
 * Two independent things this tag can do, chosen via `mode`:
 *
 *   mode="burst" (default) — `balloonCount` balloons launch from a single
 *     origin point and rise/fade together, like a bouquet being let go at
 *     once. Good for a one-off celebratory moment (match found, proposal
 *     accepted, milestone reached).
 *
 *   mode="trail" — a continuous, throttled stream of single balloons
 *     spawns at the pointer's current position as it moves over a matching
 *     element (mouse AND touch) — a "hold to celebrate" release rather
 *     than a one-off burst.
 *
 * `attachTo` auto-wires the appropriate listener for whichever mode is
 * active (click → release() for burst, pointer movement → drift() for
 * trail) so most usages need zero custom JS.
 *
 * Shape via `shape`: "round" (default) is a classic round party balloon
 * with a knot and string. "heart" is a heart-shaped balloon — a nice fit
 * for a matrimony app (proposal, anniversary, "they said yes" moments).
 * Inline SVG rather than a 🎈 emoji glyph, for the same cross-platform-
 * consistency reason glass:starBurst/glass:heartBurst use SVG.
 *
 * Balloons always drift upward (that's the "release" in the name); the
 * `rise` attribute only controls how tightly the burst is biased upward
 * vs. spread out to the sides — it does not make them fall.
 *
 * Global API this tag registers (shared across every glass:balloonRelease
 * instance on the page, same pattern as glass:starBurst):
 *
 *   glassBalloonRelease.release('yourId')                    // burst: from viewport center
 *   glassBalloonRelease.release('yourId', clickEvent)         // burst: from event.clientX/Y
 *   glassBalloonRelease.release('yourId', domElement)         // burst: from element's center
 *   glassBalloonRelease.release('yourId', {x: 200, y: 400})   // burst: from explicit coords
 *   glassBalloonRelease.release('yourId', clickEvent, {       // burst: one-off overrides
 *     balloonCount: 24, colors: ['#ff5e5e', '#ffffff']
 *   })
 *   glassBalloonRelease.drift('yourId', moveEvent)            // trail: a single balloon
 *   glassBalloonRelease.drift('yourId', {x: 100, y: 50})      // trail: at explicit coords
 *
 * Attributes
 * ─────────────────────────────────────────────────────────────────────────
 *   id            (String, optional)  — unique id; needed to target with
 *                                       glassBalloonRelease.release/drift('id')
 *                                       when more than one instance exists
 *                                       on the page. Auto-generated when
 *                                       omitted.
 *   mode          (String, optional)  — "burst" (default) or "trail".
 *   attachTo      (String, optional)  — a CSS selector. Matching elements
 *                                       get the mode-appropriate listener
 *                                       auto-wired (click for burst,
 *                                       pointer move for trail). Uses
 *                                       event delegation on document, so
 *                                       it also covers elements added to
 *                                       the DOM later.
 *   trigger       (String, optional)  — "manual" (default) or "auto" —
 *                                       burst mode only; "auto" fires
 *                                       once, from the viewport center,
 *                                       on DOMContentLoaded.
 *   shape         (String, optional)  — "round" (default, classic party
 *                                       balloon) or "heart" (heart-shaped
 *                                       balloon).
 *   balloonCount  (int, optional)     — balloons per burst — burst mode
 *                                       only (default: 12, clamped 1..40).
 *   colors        (String, optional)  — comma-separated CSS colors, chosen
 *                                       randomly per balloon (default: a
 *                                       festive multi-color palette).
 *   minSize       (int, optional)     — smallest balloon size in px
 *                                       (default: 28, clamped >= 10).
 *   maxSize       (int, optional)     — largest balloon size in px — burst
 *                                       mode (default: 46, clamped >=
 *                                       minSize). Trail mode uses minSize/
 *                                       maxSize too, but typically smaller.
 *   spread        (int, optional)     — px horizontal/vertical travel
 *                                       distance from origin — burst mode
 *                                       only (default: 160, clamped >= 20).
 *   duration      (ms, optional)      — animation length per balloon —
 *                                       burst mode (default: 2200, clamped
 *                                       >= 400) — or per drifting balloon
 *                                       — trail mode (default overridden
 *                                       to 1600 when mode="trail" and
 *                                       duration isn't explicitly set).
 *   rise          (Boolean, optional) — burst mode only. "true" (default)
 *                                       for a tight, mostly-upward release
 *                                       (like letting go of a bouquet),
 *                                       "false" for a wider fan-out burst
 *                                       that still drifts up overall.
 *   trailInterval (ms, optional)      — trail mode only — minimum gap
 *                                       between spawned balloons while the
 *                                       pointer keeps moving, so a fast
 *                                       swipe doesn't spawn hundreds of
 *                                       elements (default: 90, clamped
 *                                       >= 20).
 *   trailJitter   (int, optional)     — trail mode only — px of random
 *                                       offset applied to each balloon's
 *                                       spawn position, so the trail isn't
 *                                       a perfectly straight line
 *                                       (default: 10, clamped >= 0).
 *   zIndex        (int, optional)     — stacking order of the overlay
 *                                       (default: 9999).
 *   class         (String, optional)  — extra CSS classes on the overlay.
 */
class BalloonReleaseTagLib {

  static namespace = 'glass'

  private static final List<String> DEFAULT_COLORS = [
    '#ff5e5e', '#ffb84d', '#ffe14d', '#7ed957', '#4dd2ff', '#7a7dff', '#ff7de9'
  ]

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
   * Renders a balloon-release overlay and registers it with the
   * page-level window.glassBalloonRelease.release(id, origin, overrides)
   * and .drift(id, origin, overrides) API.
   *
   * @attr id            Unique id for this instance (auto-generated if omitted).
   * @attr mode            "burst" (default) or "trail".
   * @attr attachTo           CSS selector — matching elements auto-wire the
   *                          mode-appropriate listener.
   * @attr trigger                "manual" (default) or "auto" — burst mode only.
   * @attr shape                     "round" (default) or "heart".
   * @attr balloonCount                 Balloons per burst — burst mode only (default: 12).
   * @attr colors                          Comma-separated CSS colors for the balloons.
   * @attr minSize                            Smallest balloon size in px (default: 28).
   * @attr maxSize                               Largest balloon size in px (default: 46).
   * @attr spread                                   Px travel distance — burst mode only (default: 160).
   * @attr duration                                    Animation length in ms.
   * @attr rise                                           "true"/"false" — burst mode only (default: true).
   * @attr trailInterval                                     Ms between balloons — trail mode only (default: 90).
   * @attr trailJitter                                          Px random offset — trail mode only (default: 10).
   * @attr zIndex                                                  Overlay stacking order (default: 9999).
   * @attr class                                                      Extra CSS classes for the overlay.
   */
  def balloonRelease = { attrs, body ->
    String id = (attrs.remove('id') ?: "glassBalloonRelease_${System.currentTimeMillis()}_${(int) (Math.random() * 9999)}").toString()
    String extra = (attrs.remove('class') ?: '').toString()
    String attachTo = (attrs.remove('attachTo') ?: '').toString()

    String mode = (attrs.remove('mode') ?: 'burst').toString().toLowerCase()
    if (!(mode in ['burst', 'trail'])) mode = 'burst'

    String trigger = (attrs.remove('trigger') ?: 'manual').toString().toLowerCase()
    if (!(trigger in ['manual', 'auto'])) trigger = 'manual'

    String shape = (attrs.remove('shape') ?: 'round').toString().toLowerCase()
    if (!(shape in ['round', 'heart'])) shape = 'round'

    int balloonCount = clampInt(toIntOrDefault(attrs.remove('balloonCount'), 12), 1, 40)
    int minSize = Math.max(10, toIntOrDefault(attrs.remove('minSize'), 28))
    int maxSize = Math.max(minSize, toIntOrDefault(attrs.remove('maxSize'), 46))
    int spread = Math.max(20, toIntOrDefault(attrs.remove('spread'), 160))

    int defaultDuration = (mode == 'trail') ? 1600 : 2900
    int durationMs = Math.max(400, toIntOrDefault(attrs.remove('duration'), defaultDuration))

    boolean rise = attrs.containsKey('rise') ? Boolean.parseBoolean(attrs.remove('rise').toString()) : true
    int trailInterval = Math.max(20, toIntOrDefault(attrs.remove('trailInterval'), 90))
    int trailJitter = Math.max(0, toIntOrDefault(attrs.remove('trailJitter'), 10))
    int zIndex = toIntOrDefault(attrs.remove('zIndex'), 9999)

    String colorsAttr = (attrs.remove('colors') ?: '').toString()
    List<String> colors = colorsAttr ? colorsAttr.split(',').collect { it.trim() }.findAll { it } : DEFAULT_COLORS
    String colorsJs = colors.collect { "\"${it.replace('"', '\\"')}\"" }.join(',')

    out << """
<style>
  #${id}.glass-balloonrelease-overlay {
    position       : fixed;
    inset          : 0;
    z-index        : ${zIndex};
    pointer-events : none;
    overflow       : hidden;
  }

  #${id} .gbr-balloon {
    position    : absolute;
    top         : 0;
    left        : 0;
    width       : var(--gbr-size);
    height      : calc(var(--gbr-size) * 1.35);
    color       : var(--gbr-color);
    opacity     : 0;
    will-change : transform, opacity;
    filter      : drop-shadow(0 3px 4px rgba(0,0,0,0.2));
  }
  #${id} .gbr-balloon svg { width: 100%; height: 100%; display: block; }

  #${id} .gbr-balloon.gbr-burst {
    transform : translate(-50%, -50%) translate(0, 0) rotate(var(--gbr-rot-start)) scale(0.4);
    animation : glassBalloonBurstRise var(--gbr-dur) cubic-bezier(0.16, 1, 0.3, 1) var(--gbr-delay) forwards;
  }

  #${id} .gbr-balloon.gbr-trail {
    transform : translate(-50%, -50%) translate(0, 0) rotate(var(--gbr-rot-start)) scale(0);
    animation : glassBalloonTrailRise var(--gbr-dur) ease-out var(--gbr-delay) forwards;
  }

  /*
   * Smooth release: transform is only anchored at 0% and 100%, so the
   * browser interpolates translate/rotate/scale as one continuous arc
   * (driven by the cubic-bezier(0.16,1,0.3,1) "ease-out" curve above)
   * instead of visibly stepping through mid-animation checkpoints.
   * Opacity gets its own gentle in/out fade so the balloon materializes
   * softly rather than popping in at full scale.
   */
  @keyframes glassBalloonBurstRise {
    0% {
      transform : translate(-50%, -50%) translate(0, 0) rotate(var(--gbr-rot-start)) scale(0.4);
      opacity   : 0;
    }
    10% {
      opacity   : 1;
    }
    82% {
      opacity   : 1;
    }
    100% {
      transform : translate(-50%, -50%)
                  translate(var(--gbr-tx), var(--gbr-ty))
                  rotate(calc(var(--gbr-rot-start) + var(--gbr-rot-delta)))
                  scale(var(--gbr-scale));
      opacity   : 0;
    }
  }

  @keyframes glassBalloonTrailRise {
    0% {
      transform : translate(-50%, -50%) translate(0, 0) rotate(var(--gbr-rot-start)) scale(0);
      opacity   : 0;
    }
    25% {
      opacity   : 1;
      transform : translate(-50%, -50%)
                  translate(calc(var(--gbr-tx) * 0.3), calc(var(--gbr-ty) * 0.3))
                  rotate(calc(var(--gbr-rot-start) + var(--gbr-rot-delta) * 0.3))
                  scale(var(--gbr-scale));
    }
    100% {
      transform : translate(-50%, -50%)
                  translate(var(--gbr-tx), var(--gbr-ty))
                  rotate(calc(var(--gbr-rot-start) + var(--gbr-rot-delta)))
                  scale(calc(var(--gbr-scale) * 0.85));
      opacity   : 0;
    }
  }

  @media (prefers-reduced-motion: reduce) {
    #${id} .gbr-balloon { animation-duration: 0.4s !important; animation-timing-function: ease-out !important; }
  }
</style>

<div id="${id}" class="glass-balloonrelease-overlay${extra ? ' ' + extra : ''}" aria-hidden="true"></div>

<script>
(function () {
  // ── Shared global API: window.glassBalloonRelease.release/.drift(id, origin, overrides)
  // Reused across every glass:balloonRelease instance on the page — the
  // engine below is only defined once (first instance wins) and every
  // subsequent instance just registers its config into the shared registry.
  window.glassBalloonRelease = window.glassBalloonRelease || (function () {
    var registry = {};

    function rand(min, max) { return Math.random() * (max - min) + min; }

    function resolveOrigin(origin) {
      if (!origin) return { x: window.innerWidth / 2, y: window.innerHeight / 2 };
      if (typeof origin.clientX === 'number' && typeof origin.clientY === 'number') {
        return { x: origin.clientX, y: origin.clientY };
      }
      if (origin.touches && origin.touches.length) {
        return { x: origin.touches[0].clientX, y: origin.touches[0].clientY };
      }
      if (typeof origin.x === 'number' && typeof origin.y === 'number') {
        return { x: origin.x, y: origin.y };
      }
      if (origin.getBoundingClientRect) {
        var r = origin.getBoundingClientRect();
        return { x: r.left + r.width / 2, y: r.top + r.height / 2 };
      }
      return { x: window.innerWidth / 2, y: window.innerHeight / 2 };
    }

    var ROUND_SVG =
      '<svg viewBox="0 0 24 34" xmlns="http://www.w3.org/2000/svg">' +
      '<ellipse cx="12" cy="12" rx="10" ry="12" fill="currentColor"/>' +
      '<ellipse cx="8.3" cy="7" rx="3" ry="4" fill="rgba(255,255,255,0.35)"/>' +
      '<path d="M10.3 23.6 L12 27 L13.7 23.6 Z" fill="currentColor"/>' +
      '<path d="M12 27 L11 34 M12 27 L13 34" stroke="rgba(0,0,0,0.3)" stroke-width="0.6" fill="none"/>' +
      '</svg>';

    var HEART_SVG =
      '<svg viewBox="0 0 24 34" xmlns="http://www.w3.org/2000/svg">' +
      '<path d="M12 22C12 22 2 15.8 2 8.6A6.4 6.4 0 0 1 12 3.4A6.4 6.4 0 0 1 22 8.6C22 15.8 12 22 12 22Z" fill="currentColor"/>' +
      '<ellipse cx="8" cy="8.2" rx="2.4" ry="3.2" fill="rgba(255,255,255,0.3)"/>' +
      '<path d="M11 22 L12 25 L13 22 Z" fill="currentColor"/>' +
      '<path d="M12 25 L11 34 M12 25 L13 34" stroke="rgba(0,0,0,0.3)" stroke-width="0.6" fill="none"/>' +
      '</svg>';

    function shapeSvg(cfg) {
      return cfg.shape === 'heart' ? HEART_SVG : ROUND_SVG;
    }

    function spawnEl(overlay, cfg, cls, x, y, opts) {
      var el = document.createElement('span');
      el.className = 'gbr-balloon ' + cls;

      var size = rand(cfg.minSize, cfg.maxSize);
      var color = cfg.colors[Math.floor(Math.random() * cfg.colors.length)];
      var rotStart = rand(-8, 8);
      var rotDelta = rand(-18, 18);
      var scale = rand(0.9, 1.15);
      var dur = cfg.duration * rand(0.85, 1.2);
      var delay = (opts.delay || 0);

      el.style.setProperty('--gbr-size', size + 'px');
      el.style.setProperty('--gbr-color', color);
      el.style.setProperty('--gbr-tx', opts.tx.toFixed(1) + 'px');
      el.style.setProperty('--gbr-ty', opts.ty.toFixed(1) + 'px');
      el.style.setProperty('--gbr-rot-start', rotStart.toFixed(1) + 'deg');
      el.style.setProperty('--gbr-rot-delta', rotDelta.toFixed(1) + 'deg');
      el.style.setProperty('--gbr-scale', scale.toFixed(2));
      el.style.setProperty('--gbr-dur', dur.toFixed(0) + 'ms');
      el.style.setProperty('--gbr-delay', delay.toFixed(0) + 'ms');
      el.style.left = x + 'px';
      el.style.top = y + 'px';

      el.innerHTML = shapeSvg(cfg);

      el.addEventListener('animationend', function () {
        if (el.parentNode) el.parentNode.removeChild(el);
      });

      overlay.appendChild(el);
    }

    function runBurst(cfg, origin) {
      var overlay = document.getElementById(cfg.id);
      if (!overlay) return;
      var pt = resolveOrigin(origin);
      for (var i = 0; i < cfg.balloonCount; i++) {
        var tx, ty;
        if (cfg.rise) {
          // tight, mostly-upward release — like letting go of a bouquet
          tx = rand(-cfg.spread * 0.4, cfg.spread * 0.4);
          ty = -rand(cfg.spread * 1.2, cfg.spread * 2);
        } else {
          // wider fan-out, still net-upward
          var angle = rand(Math.PI * 1.1, Math.PI * 1.9); // upper half of circle
          var dist = cfg.spread * rand(0.6, 1.3);
          tx = Math.cos(angle) * dist;
          ty = Math.sin(angle) * dist;
        }
        spawnEl(overlay, cfg, 'gbr-burst', pt.x, pt.y, { tx: tx, ty: ty, delay: i * 34 + rand(-6, 6) });
      }
    }

    function runDrift(cfg, origin) {
      var overlay = document.getElementById(cfg.id);
      if (!overlay) return;
      var pt = resolveOrigin(origin);
      var jitterX = rand(-cfg.trailJitter, cfg.trailJitter);
      var jitterY = rand(-cfg.trailJitter, cfg.trailJitter);
      var tx = rand(-30, 30);
      var ty = -rand(cfg.spread * 0.6, cfg.spread * 1.1);
      spawnEl(overlay, cfg, 'gbr-trail', pt.x + jitterX, pt.y + jitterY, { tx: tx, ty: ty, delay: 0 });
    }

    return {
      /** Called once per glass:balloonRelease tag instance to register its config. */
      _register: function (id, cfg) {
        registry[id] = cfg;
      },
      /** Burst mode: many balloons at once from an origin point. `origin`
       *  may be a MouseEvent/TouchEvent (uses clientX/Y), a DOM element
       *  (uses its bounding-rect center), a {x, y} object, or omitted
       *  (viewport center). `overrides` optionally replaces any subset of
       *  that instance's config for just this call. */
      release: function (id, origin, overrides) {
        if (!registry[id]) {
          if (window.console) console.warn('glassBalloonRelease.release: no balloonRelease instance found for id "' + id + '"');
          return;
        }
        var cfg = {};
        for (var k in registry[id]) cfg[k] = registry[id][k];
        if (overrides) for (var k2 in overrides) cfg[k2] = overrides[k2];
        runBurst(cfg, origin);
      },
      /** Trail mode: a single balloon at the given position. Same `origin`
       *  and `overrides` rules as release(). Intended to be called
       *  repeatedly (typically via the auto-wired attachTo pointer-move
       *  listener) to build up a continuous stream. */
      drift: function (id, origin, overrides) {
        if (!registry[id]) {
          if (window.console) console.warn('glassBalloonRelease.drift: no balloonRelease instance found for id "' + id + '"');
          return;
        }
        var cfg = {};
        for (var k in registry[id]) cfg[k] = registry[id][k];
        if (overrides) for (var k2 in overrides) cfg[k2] = overrides[k2];
        runDrift(cfg, origin);
      }
    };
  })();

  window.glassBalloonRelease._register('${id}', {
    id: '${id}',
    mode: '${mode}',
    shape: '${shape}',
    balloonCount: ${balloonCount},
    minSize: ${minSize},
    maxSize: ${maxSize},
    spread: ${spread},
    duration: ${durationMs},
    rise: ${rise},
    trailJitter: ${trailJitter},
    colors: [${colorsJs}]
  });

  ${attachTo ? (mode == 'trail' ? """
  // Trail mode: throttled pointer-move listener, delegated on document so
  // it also covers elements matching '${attachTo}' added to the DOM later.
  // Handles both mouse (mousemove) and touch (touchmove, via
  // elementFromPoint since a touchmove event's own target never changes
  // from the element touchstart began on).
  (function () {
    var lastDrift = 0;
    function maybeDrift(x, y, target) {
      if (!target || !target.closest || !target.closest('${attachTo}')) return;
      var now = Date.now();
      if (now - lastDrift < ${trailInterval}) return;
      lastDrift = now;
      window.glassBalloonRelease.drift('${id}', { x: x, y: y });
    }
    document.addEventListener('mousemove', function (evt) {
      maybeDrift(evt.clientX, evt.clientY, evt.target);
    });
    document.addEventListener('touchmove', function (evt) {
      if (!evt.touches || !evt.touches.length) return;
      var t = evt.touches[0];
      var el = document.elementFromPoint(t.clientX, t.clientY);
      maybeDrift(t.clientX, t.clientY, el);
    }, { passive: true });
  })();
  """ : """
  // Burst mode: click releases from the click position. Event delegation
  // on document so it also covers elements matching '${attachTo}' added
  // to the DOM later.
  document.addEventListener('click', function (evt) {
    var target = evt.target.closest && evt.target.closest('${attachTo}');
    if (target) {
      window.glassBalloonRelease.release('${id}', evt);
    }
  });
  """) : ''}

  ${trigger == 'auto' && mode == 'burst' ? """
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', function () { window.glassBalloonRelease.release('${id}'); });
  } else {
    window.glassBalloonRelease.release('${id}');
  }
  """ : ''}
})();
</script>
"""
  }
}
