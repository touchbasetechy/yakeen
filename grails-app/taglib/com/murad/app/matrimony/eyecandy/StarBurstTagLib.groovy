package com.murad.app.matrimony.eyecandy

/**
 * Star Burst / Sparkle Trail — GSP TagLib
 *
 * Usage in GSP:
 *   <!-- one-shot burst of stars from a point, e.g. a "verified" badge tap -->
 *   <glass:starBurst id="verifyBurst" mode="burst" attachTo=".verify-badge"/>
 *
 *   <!-- continuous sparkle trail that follows the cursor/finger while
 *        hovering (or touch-dragging) over matching elements -->
 *   <glass:starBurst id="premiumTrail" mode="trail" attachTo=".premium-card"/>
 *
 *   <!-- fire either mode manually from your own JS -->
 *   <script>
 *     glassStarBurst.fire('verifyBurst', clickEvent);   // burst mode: many stars at once
 *     glassStarBurst.spark('premiumTrail', moveEvent);  // trail mode: a single sparkle
 *   </script>
 *
 * What it renders
 * ─────────────────────────────────────────────────────────────────────────
 * A fixed, full-viewport, pointer-events:none overlay <div>, populated with
 * small inline-SVG star/sparkle elements on demand — same DOM+CSS approach
 * as glass:heartBurst (no canvas, no rAF loop; a one-shot animation on a
 * handful of elements at a time doesn't need a physics simulation). Each
 * element removes itself from the DOM on animationend.
 *
 * Two independent things this tag can do, chosen via `mode`:
 *
 *   mode="burst" (default) — `starCount` stars launch outward from a single
 *     origin point and fade, exactly like glass:heartBurst's mechanic.
 *     Good for a one-off celebratory moment (badge earned, profile
 *     verified, boost activated).
 *
 *   mode="trail" — a continuous, throttled stream of single sparkles
 *     spawns at the pointer's current position as it moves over a matching
 *     element (mouse AND touch), each one popping in, drifting slightly,
 *     and fading — a "magic touch" hover trail rather than a one-off burst.
 *
 * `attachTo` auto-wires the appropriate listener for whichever mode is
 * active (click → fire() for burst, pointer movement → spark() for trail)
 * so most usages need zero custom JS.
 *
 * Shape via `shape`: "sparkle" (default) is a 4-point glimmer/diamond star
 * — reads clearly as "sparkle" even at small trail sizes. "star" is a
 * classic 5-point star, better suited to a bigger, slower burst. Inline
 * SVG rather than a ✨/⭐ emoji glyph for the same cross-platform-consistency
 * reason glass:heartBurst uses SVG instead of ❤️.
 *
 * Global API this tag registers (shared across every glass:starBurst
 * instance on the page, same pattern as glass:fireworks / glass:heartBurst):
 *
 *   glassStarBurst.fire('yourId')                    // burst: from viewport center
 *   glassStarBurst.fire('yourId', clickEvent)         // burst: from event.clientX/Y
 *   glassStarBurst.fire('yourId', domElement)         // burst: from element's center
 *   glassStarBurst.fire('yourId', {x: 200, y: 400})   // burst: from explicit coords
 *   glassStarBurst.fire('yourId', clickEvent, {       // burst: one-off overrides
 *     starCount: 20, colors: ['#ffd166', '#fff']
 *   })
 *   glassStarBurst.spark('yourId', moveEvent)         // trail: a single sparkle
 *   glassStarBurst.spark('yourId', {x: 100, y: 50})   // trail: at explicit coords
 *
 * Attributes
 * ─────────────────────────────────────────────────────────────────────────
 *   id            (String, optional)  — unique id; needed to target with
 *                                       glassStarBurst.fire/spark('id') when
 *                                       more than one instance exists on the
 *                                       page. Auto-generated when omitted.
 *   mode          (String, optional)  — "burst" (default) or "trail".
 *   attachTo      (String, optional)  — a CSS selector. Matching elements
 *                                       get the mode-appropriate listener
 *                                       auto-wired (click for burst, pointer
 *                                       move for trail). Uses event
 *                                       delegation on document, so it also
 *                                       covers elements added to the DOM
 *                                       later.
 *   trigger       (String, optional)  — "manual" (default) or "auto" —
 *                                       burst mode only; "auto" fires once,
 *                                       from the viewport center, on
 *                                       DOMContentLoaded.
 *   shape         (String, optional)  — "sparkle" (default, 4-point) or
 *                                       "star" (5-point).
 *   starCount     (int, optional)     — stars per burst — burst mode only
 *                                       (default: 14, clamped 1..60).
 *   colors        (String, optional)  — comma-separated CSS colors, chosen
 *                                       randomly per star (default: a
 *                                       gold/yellow/white palette).
 *   minSize       (int, optional)     — smallest star size in px
 *                                       (default: 10, clamped >= 3).
 *   maxSize       (int, optional)     — largest star size in px — burst
 *                                       mode (default: 22, clamped >=
 *                                       minSize). Trail mode uses minSize/
 *                                       maxSize too, but typically smaller.
 *   spread        (int, optional)     — px travel distance from origin —
 *                                       burst mode only (default: 100,
 *                                       clamped >= 10).
 *   duration      (ms, optional)      — animation length per star — burst
 *                                       mode (default: 900, clamped >=
 *                                       200) — or per sparkle — trail mode
 *                                       (default overridden to 550 when
 *                                       mode="trail" and duration isn't
 *                                       explicitly set).
 *   gravity       (Boolean, optional) — burst mode only. "true" (default)
 *                                       for an upward-biased burst, "false"
 *                                       for a 360° radial burst.
 *   trailInterval (ms, optional)      — trail mode only — minimum gap
 *                                       between spawned sparkles while the
 *                                       pointer keeps moving, so a fast
 *                                       swipe doesn't spawn hundreds of
 *                                       elements (default: 45, clamped
 *                                       >= 8).
 *   trailJitter   (int, optional)     — trail mode only — px of random
 *                                       offset applied to each sparkle's
 *                                       spawn position, so the trail isn't
 *                                       a perfectly straight line of dots
 *                                       (default: 6, clamped >= 0).
 *   zIndex        (int, optional)     — stacking order of the overlay
 *                                       (default: 9999).
 *   class         (String, optional)  — extra CSS classes on the overlay.
 */
class StarBurstTagLib {

  static namespace = 'glass'

  private static final List<String> DEFAULT_COLORS = [
    '#ffd166', '#ffe66d', '#fff3b0', '#ffffff', '#ffb703'
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
   * Renders a star-burst / sparkle-trail overlay and registers it with the
   * page-level window.glassStarBurst.fire(id, origin, overrides) and
   * .spark(id, origin, overrides) API.
   *
   * @attr id             Unique id for this instance (auto-generated if omitted).
   * @attr mode             "burst" (default) or "trail".
   * @attr attachTo            CSS selector — matching elements auto-wire the
   *                           mode-appropriate listener.
   * @attr trigger                "manual" (default) or "auto" — burst mode only.
   * @attr shape                     "sparkle" (default, 4-point) or "star" (5-point).
   * @attr starCount                    Stars per burst — burst mode only (default: 14).
   * @attr colors                          Comma-separated CSS colors for the stars.
   * @attr minSize                            Smallest star size in px (default: 10).
   * @attr maxSize                               Largest star size in px (default: 22).
   * @attr spread                                   Px travel distance — burst mode only (default: 100).
   * @attr duration                                    Animation length in ms.
   * @attr gravity                                        "true"/"false" — burst mode only (default: true).
   * @attr trailInterval                                     Ms between sparkles — trail mode only (default: 45).
   * @attr trailJitter                                          Px random offset — trail mode only (default: 6).
   * @attr zIndex                                                  Overlay stacking order (default: 9999).
   * @attr class                                                      Extra CSS classes for the overlay.
   */
  def starBurst = { attrs, body ->
    String id = (attrs.remove('id') ?: "glassStarBurst_${System.currentTimeMillis()}_${(int) (Math.random() * 9999)}").toString()
    String extra = (attrs.remove('class') ?: '').toString()
    String attachTo = (attrs.remove('attachTo') ?: '').toString()

    String mode = (attrs.remove('mode') ?: 'burst').toString().toLowerCase()
    if (!(mode in ['burst', 'trail'])) mode = 'burst'

    String trigger = (attrs.remove('trigger') ?: 'manual').toString().toLowerCase()
    if (!(trigger in ['manual', 'auto'])) trigger = 'manual'

    String shape = (attrs.remove('shape') ?: 'sparkle').toString().toLowerCase()
    if (!(shape in ['sparkle', 'star'])) shape = 'sparkle'

    int starCount = clampInt(toIntOrDefault(attrs.remove('starCount'), 14), 1, 60)
    int minSize = Math.max(3, toIntOrDefault(attrs.remove('minSize'), 10))
    int maxSize = Math.max(minSize, toIntOrDefault(attrs.remove('maxSize'), 22))
    int spread = Math.max(10, toIntOrDefault(attrs.remove('spread'), 100))

    int defaultDuration = (mode == 'trail') ? 550 : 900
    int durationMs = Math.max(200, toIntOrDefault(attrs.remove('duration'), defaultDuration))

    boolean gravity = attrs.containsKey('gravity') ? Boolean.parseBoolean(attrs.remove('gravity').toString()) : true
    int trailInterval = Math.max(8, toIntOrDefault(attrs.remove('trailInterval'), 45))
    int trailJitter = Math.max(0, toIntOrDefault(attrs.remove('trailJitter'), 6))
    int zIndex = toIntOrDefault(attrs.remove('zIndex'), 9999)

    String colorsAttr = (attrs.remove('colors') ?: '').toString()
    List<String> colors = colorsAttr ? colorsAttr.split(',').collect { it.trim() }.findAll { it } : DEFAULT_COLORS
    String colorsJs = colors.collect { "\"${it.replace('"', '\\"')}\"" }.join(',')

    out << """
<style>
  #${id}.glass-starburst-overlay {
    position       : fixed;
    inset          : 0;
    z-index        : ${zIndex};
    pointer-events : none;
    overflow       : hidden;
  }

  #${id} .gsb-star {
    position    : absolute;
    top         : 0;
    left        : 0;
    width       : var(--gsb-size);
    height      : var(--gsb-size);
    color       : var(--gsb-color);
    opacity     : 0;
    will-change : transform, opacity;
    filter      : drop-shadow(0 1px 3px rgba(0,0,0,0.3));
  }
  #${id} .gsb-star svg { width: 100%; height: 100%; display: block; fill: currentColor; }

  #${id} .gsb-star.gsb-burst {
    transform : translate(-50%, -50%) translate(0, 0) rotate(var(--gsb-rot-start)) scale(0);
    animation : glassStarBurstFloat var(--gsb-dur) cubic-bezier(0.15, 0.7, 0.3, 1) var(--gsb-delay) forwards;
  }

  #${id} .gsb-star.gsb-trail {
    transform : translate(-50%, -50%) translate(0, 0) rotate(var(--gsb-rot-start)) scale(0);
    animation : glassStarTrailFade var(--gsb-dur) ease-out var(--gsb-delay) forwards;
  }

  @keyframes glassStarBurstFloat {
    0% {
      transform : translate(-50%, -50%) translate(0, 0) rotate(var(--gsb-rot-start)) scale(0);
      opacity   : 0;
    }
    15% {
      opacity   : 1;
      transform : translate(-50%, -50%)
                  translate(calc(var(--gsb-tx) * 0.3), calc(var(--gsb-ty) * 0.3))
                  rotate(calc(var(--gsb-rot-start) + var(--gsb-rot-delta) * 0.3))
                  scale(var(--gsb-scale));
    }
    70% { opacity: 1; }
    100% {
      transform : translate(-50%, -50%)
                  translate(var(--gsb-tx), var(--gsb-ty))
                  rotate(calc(var(--gsb-rot-start) + var(--gsb-rot-delta)))
                  scale(calc(var(--gsb-scale) * 0.4));
      opacity   : 0;
    }
  }

  @keyframes glassStarTrailFade {
    0% {
      transform : translate(-50%, -50%) translate(0, 0) rotate(var(--gsb-rot-start)) scale(0);
      opacity   : 0;
    }
    30% {
      opacity   : 1;
      transform : translate(-50%, -50%)
                  translate(calc(var(--gsb-tx) * 0.4), calc(var(--gsb-ty) * 0.4))
                  rotate(calc(var(--gsb-rot-start) + var(--gsb-rot-delta) * 0.5))
                  scale(var(--gsb-scale));
    }
    100% {
      transform : translate(-50%, -50%)
                  translate(var(--gsb-tx), var(--gsb-ty))
                  rotate(calc(var(--gsb-rot-start) + var(--gsb-rot-delta)))
                  scale(0);
      opacity   : 0;
    }
  }

  @media (prefers-reduced-motion: reduce) {
    #${id} .gsb-star { animation-duration: 0.3s !important; animation-timing-function: ease-out !important; }
  }
</style>

<div id="${id}" class="glass-starburst-overlay${extra ? ' ' + extra : ''}" aria-hidden="true"></div>

<script>
(function () {
  // ── Shared global API: window.glassStarBurst.fire/.spark(id, origin, overrides)
  // Reused across every glass:starBurst instance on the page — the engine
  // below is only defined once (first instance wins) and every subsequent
  // instance just registers its config into the shared registry.
  window.glassStarBurst = window.glassStarBurst || (function () {
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

    var STAR_PATH = 'M12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2Z';
    var SPARKLE_PATH = 'M12 0 14.5 9.5 24 12 14.5 14.5 12 24 9.5 14.5 0 12 9.5 9.5 12 0Z';

    function shapePath(cfg) {
      return cfg.shape === 'star' ? STAR_PATH : SPARKLE_PATH;
    }

    function spawnEl(overlay, cfg, cls, x, y, opts) {
      var el = document.createElement('span');
      el.className = 'gsb-star ' + cls;

      var size = rand(cfg.minSize, cfg.maxSize);
      var color = cfg.colors[Math.floor(Math.random() * cfg.colors.length)];
      var rotStart = rand(-25, 25);
      var rotDelta = rand(-70, 70);
      var scale = rand(0.85, 1.25);
      var dur = cfg.duration * rand(0.85, 1.15);
      var delay = (opts.delay || 0);

      el.style.setProperty('--gsb-size', size + 'px');
      el.style.setProperty('--gsb-color', color);
      el.style.setProperty('--gsb-tx', opts.tx.toFixed(1) + 'px');
      el.style.setProperty('--gsb-ty', opts.ty.toFixed(1) + 'px');
      el.style.setProperty('--gsb-rot-start', rotStart.toFixed(1) + 'deg');
      el.style.setProperty('--gsb-rot-delta', rotDelta.toFixed(1) + 'deg');
      el.style.setProperty('--gsb-scale', scale.toFixed(2));
      el.style.setProperty('--gsb-dur', dur.toFixed(0) + 'ms');
      el.style.setProperty('--gsb-delay', delay.toFixed(0) + 'ms');
      el.style.left = x + 'px';
      el.style.top = y + 'px';

      el.innerHTML = '<svg viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg"><path d="' + shapePath(cfg) + '"/></svg>';

      el.addEventListener('animationend', function () {
        if (el.parentNode) el.parentNode.removeChild(el);
      });

      overlay.appendChild(el);
    }

    function runBurst(cfg, origin) {
      var overlay = document.getElementById(cfg.id);
      if (!overlay) return;
      var pt = resolveOrigin(origin);
      for (var i = 0; i < cfg.starCount; i++) {
        var tx, ty;
        if (cfg.gravity) {
          tx = rand(-cfg.spread, cfg.spread);
          ty = -rand(cfg.spread * 0.6, cfg.spread * 1.4);
        } else {
          var angle = rand(0, Math.PI * 2);
          var dist = cfg.spread * rand(0.5, 1);
          tx = Math.cos(angle) * dist;
          ty = Math.sin(angle) * dist;
        }
        spawnEl(overlay, cfg, 'gsb-burst', pt.x, pt.y, { tx: tx, ty: ty, delay: i * rand(4, 16) });
      }
    }

    function runSpark(cfg, origin) {
      var overlay = document.getElementById(cfg.id);
      if (!overlay) return;
      var pt = resolveOrigin(origin);
      var jitterX = rand(-cfg.trailJitter, cfg.trailJitter);
      var jitterY = rand(-cfg.trailJitter, cfg.trailJitter);
      var tx = rand(-12, 12);
      var ty = -rand(6, 22);
      spawnEl(overlay, cfg, 'gsb-trail', pt.x + jitterX, pt.y + jitterY, { tx: tx, ty: ty, delay: 0 });
    }

    return {
      /** Called once per glass:starBurst tag instance to register its config. */
      _register: function (id, cfg) {
        registry[id] = cfg;
      },
      /** Burst mode: many stars at once from an origin point. `origin` may
       *  be a MouseEvent/TouchEvent (uses clientX/Y), a DOM element (uses
       *  its bounding-rect center), a {x, y} object, or omitted (viewport
       *  center). `overrides` optionally replaces any subset of that
       *  instance's config for just this call. */
      fire: function (id, origin, overrides) {
        if (!registry[id]) {
          if (window.console) console.warn('glassStarBurst.fire: no starBurst instance found for id "' + id + '"');
          return;
        }
        var cfg = {};
        for (var k in registry[id]) cfg[k] = registry[id][k];
        if (overrides) for (var k2 in overrides) cfg[k2] = overrides[k2];
        runBurst(cfg, origin);
      },
      /** Trail mode: a single sparkle at the given position. Same `origin`
       *  and `overrides` rules as fire(). Intended to be called repeatedly
       *  (typically via the auto-wired attachTo pointer-move listener) to
       *  build up a trail. */
      spark: function (id, origin, overrides) {
        if (!registry[id]) {
          if (window.console) console.warn('glassStarBurst.spark: no starBurst instance found for id "' + id + '"');
          return;
        }
        var cfg = {};
        for (var k in registry[id]) cfg[k] = registry[id][k];
        if (overrides) for (var k2 in overrides) cfg[k2] = overrides[k2];
        runSpark(cfg, origin);
      }
    };
  })();

  window.glassStarBurst._register('${id}', {
    id: '${id}',
    mode: '${mode}',
    shape: '${shape}',
    starCount: ${starCount},
    minSize: ${minSize},
    maxSize: ${maxSize},
    spread: ${spread},
    duration: ${durationMs},
    gravity: ${gravity},
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
    var lastSpark = 0;
    function maybeSpark(x, y, target) {
      if (!target || !target.closest || !target.closest('${attachTo}')) return;
      var now = Date.now();
      if (now - lastSpark < ${trailInterval}) return;
      lastSpark = now;
      window.glassStarBurst.spark('${id}', { x: x, y: y });
    }
    document.addEventListener('mousemove', function (evt) {
      maybeSpark(evt.clientX, evt.clientY, evt.target);
    });
    document.addEventListener('touchmove', function (evt) {
      if (!evt.touches || !evt.touches.length) return;
      var t = evt.touches[0];
      var el = document.elementFromPoint(t.clientX, t.clientY);
      maybeSpark(t.clientX, t.clientY, el);
    }, { passive: true });
  })();
  """ : """
  // Burst mode: click fires from the click position. Event delegation on
  // document so it also covers elements matching '${attachTo}' added to
  // the DOM later.
  document.addEventListener('click', function (evt) {
    var target = evt.target.closest && evt.target.closest('${attachTo}');
    if (target) {
      window.glassStarBurst.fire('${id}', evt);
    }
  });
  """) : ''}

  ${trigger == 'auto' && mode == 'burst' ? """
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', function () { window.glassStarBurst.fire('${id}'); });
  } else {
    window.glassStarBurst.fire('${id}');
  }
  """ : ''}
})();
</script>
"""
  }
}
