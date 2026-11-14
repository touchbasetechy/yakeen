package com.murad.app.matrimony.eyecandy

import groovy.util.logging.Slf4j

/**
 * Confetti / Success Micro-Animation — GSP TagLib
 *
 * Usage in GSP:
 *   <glass:confetti id="matchConfetti" title="It's a Match!" subtitle="You both liked each other"/>
 *
 *   <!-- fire it from anywhere, e.g. after an AJAX success response -->
 *   <script>
 *     fetch('...').then(r => r.json()).then(data => {
 *       if (data.success) glassConfetti.fire('matchConfetti');
 *     });
 *   </script>
 *
 *   <!-- or auto-play as soon as the page loads (e.g. a "request sent" landing page) -->
 *   <glass:confetti id="welcomeBurst" trigger="auto" checkmark="false"/>
 *
 * What it renders
 * ─────────────────────────────────────────────────────────────────────────
 * A fixed, full-viewport, pointer-events:none overlay containing:
 *   1. a <canvas> confetti particle burst (small squares/circles/triangles,
 *      randomized color/velocity/rotation/gravity, fading out over `duration`)
 *   2. an OPTIONAL centered "success badge" — a circular checkmark that pops
 *      in with a scale/draw animation, with an optional title/subtitle
 *      underneath it (great for "It's a match!", "Request sent!", etc.)
 * Both pieces play together as one micro-animation, then the whole overlay
 * hides itself and is ready to be fired again.
 *
 * Nothing plays automatically unless trigger="auto". Otherwise, call it from
 * your own JS (form success handler, AJAX callback, etc.) via the small
 * global API this tag registers:
 *
 *   glassConfetti.fire('yourId')          // play confetti + badge (if any)
 *   glassConfetti.fire('yourId', {        // one-off overrides for this call
 *     particleCount: 300,
 *     colors: ['#ff6b8b', '#ffd166']
 *   })
 *   glassConfetti.fire()                  // no id needed if only one
 *                                         // <glass:confetti> exists on the page
 *
 * Attributes
 * ─────────────────────────────────────────────────────────────────────────
 *   id             (String, optional) — unique id for this instance; needed
 *                                       to target it with glassConfetti.fire('id')
 *                                       when more than one exists on the page.
 *                                       Auto-generated when omitted.
 *   trigger        (String, optional) — "manual" (default) or "auto". "auto"
 *                                       fires the animation once, automatically,
 *                                       on DOMContentLoaded.
 *   particleCount  (int, optional)    — number of confetti pieces (default: 150,
 *                                       clamped 1..1000).
 *   duration       (ms, optional)     — total animation length before the
 *                                       overlay clears itself (default: 3000,
 *                                       clamped >= 300).
 *   spread         (degrees, optional)— how wide the initial burst cone is,
 *                                       centered straight up (default: 70,
 *                                       clamped 1..360).
 *   originX        (0..1, optional)   — horizontal burst origin as a fraction
 *                                       of viewport width (default: 0.5, center).
 *   originY        (0..1, optional)   — vertical burst origin as a fraction of
 *                                       viewport height (default: 0.5, center).
 *   colors         (String, optional) — comma-separated CSS colors for the
 *                                       confetti pieces (default: a warm/violet
 *                                       palette matching this app's glass theme).
 *   zIndex         (int, optional)    — stacking order of the overlay (default: 9999).
 *   checkmark      (Boolean, optional)— show the circular checkmark success
 *                                       badge alongside the confetti (default: true).
 *   title          (String, optional) — text under the checkmark badge.
 *   subtitle       (String, optional) — smaller text under the title.
 *   titleHtml      (Boolean, optional, default: false) — render title as raw HTML.
 *   subtitleHtml   (Boolean, optional, default: false) — render subtitle as raw HTML.
 *   accent         (String, optional) — Bootstrap variant name (primary, secondary,
 *                                       success, danger, warning, info, dark, light)
 *                                       resolving to var(--bs-{name}), or any raw
 *                                       CSS color. Drives the checkmark badge's
 *                                       ring/glow color. Default: "success"
 *   class          (String, optional) — extra CSS classes appended to the overlay
 */
@Slf4j
class ConfettiTagLib {

  static namespace = 'glass'

  private static final Set<String> BS_VARIANTS = [
    'primary', 'secondary', 'success', 'danger', 'warning', 'info', 'dark', 'light'
  ] as Set

  private static final List<String> DEFAULT_COLORS = [
    '#ff6b8b', '#ffb347', '#ffd166', '#5ec8ff', '#7c5cff', '#5cffa8', '#c88bff'
  ]

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

  private double clampDouble(double v, double min, double max) {
    return Math.max(min, Math.min(max, v))
  }

  /**
   * Renders a confetti-burst + optional checkmark success overlay, and
   * registers it with the page-level window.glassConfetti.fire(id) API.
   *
   * @attr id             Unique id for this instance (auto-generated if omitted).
   * @attr trigger        "manual" (default) or "auto" — auto fires once on page load.
   * @attr particleCount  Number of confetti pieces (default: 150, clamped 1..1000).
   * @attr duration       Total animation length in ms (default: 3000, clamped >= 300).
   * @attr spread         Burst cone width in degrees (default: 70, clamped 1..360).
   * @attr originX        Horizontal burst origin, 0..1 fraction of viewport width (default: 0.5).
   * @attr originY        Vertical burst origin, 0..1 fraction of viewport height (default: 0.5).
   * @attr colors         Comma-separated CSS colors for the confetti pieces.
   * @attr zIndex         Overlay stacking order (default: 9999).
   * @attr checkmark      "true"/"false" — show the checkmark success badge (default: true).
   * @attr title           Text under the checkmark badge.
   * @attr subtitle         Smaller text under the title.
   * @attr titleHtml          "true" to render title as raw HTML (default: false).
   * @attr subtitleHtml        "true" to render subtitle as raw HTML (default: false).
   * @attr accent                Bootstrap variant or raw CSS color for the badge ring/glow (default: "success").
   * @attr class                    Extra CSS classes for the overlay.
   */
  def confetti = { attrs, body ->
    String id = (attrs.remove('id') ?: "glassConfetti_${System.currentTimeMillis()}_${(int) (Math.random() * 9999)}").toString()
    String extra = (attrs.remove('class') ?: '').toString()

    String trigger = (attrs.remove('trigger') ?: 'manual').toString().toLowerCase()
    if (!(trigger in ['manual', 'auto'])) trigger = 'manual'

    int particleCount = clampInt(toIntOrDefault(attrs.remove('particleCount'), 150), 1, 1000)
    int durationMs = Math.max(300, toIntOrDefault(attrs.remove('duration'), 3000))
    int spreadDeg = clampInt(toIntOrDefault(attrs.remove('spread'), 70), 1, 360)
    double originX = clampDouble(toDoubleOrDefault(attrs.remove('originX'), 0.5d), 0d, 1d)
    double originY = clampDouble(toDoubleOrDefault(attrs.remove('originY'), 0.5d), 0d, 1d)
    int zIndex = toIntOrDefault(attrs.remove('zIndex'), 9999)

    String colorsAttr = (attrs.remove('colors') ?: '').toString()
    List<String> colors = colorsAttr ? colorsAttr.split(',').collect { it.trim() }.findAll { it } : DEFAULT_COLORS

    boolean showCheckmark = attrs.containsKey('checkmark') ? Boolean.parseBoolean(attrs.remove('checkmark').toString()) : true
    String title = (attrs.remove('title') ?: '').toString()
    String subtitle = (attrs.remove('subtitle') ?: '').toString()
    boolean titleHtml = attrs.remove('titleHtml') in [true, 'true', '1', 'yes']
    boolean subtitleHtml = attrs.remove('subtitleHtml') in [true, 'true', '1', 'yes']
    String accent = resolveColor((attrs.remove('accent') ?: 'success').toString())

    String renderedTitle = title ? (titleHtml ? title : title.encodeAsHTML()) : ''
    String renderedSubtitle = subtitle ? (subtitleHtml ? subtitle : subtitle.encodeAsHTML()) : ''

    // JSON-safe color array literal for the inline script, e.g. ["#ff6b8b","#ffb347"]
    String colorsJs = colors.collect { "\"${it.replace('"', '\\"')}\"" }.join(',')

    log.debug("confetti: id=${id}, trigger=${trigger}, particleCount=${particleCount}, duration=${durationMs}, spread=${spreadDeg}, origin=(${originX},${originY}), checkmark=${showCheckmark}, colors=${colors}")

    out << """
<style>
  #${id}.glass-confetti-overlay {
    position         : fixed;
    inset             : 0;
    z-index           : ${zIndex};
    pointer-events    : none;
    display           : none;
  }
  #${id}.glass-confetti-overlay.is-active {
    display: block;
  }
  #${id} canvas.glass-confetti-canvas {
    position : absolute;
    inset    : 0;
    width    : 100%;
    height   : 100%;
    display  : block;
  }

  #${id} .glass-confetti-badge {
    --confetti-accent: ${accent};

    position          : absolute;
    top               : ${(originY * 100)}%;
    left              : ${(originX * 100)}%;
    -webkit-transform : translate(-50%, -50%) scale(0.4);
            transform : translate(-50%, -50%) scale(0.4);
    opacity           : 0;
    text-align        : center;
    color             : #fff;
    font-family       : 'Outfit', sans-serif;

    -webkit-transition:
        -webkit-transform 0.45s cubic-bezier(0.34, 1.56, 0.64, 1),
                transform 0.45s cubic-bezier(0.34, 1.56, 0.64, 1),
        opacity 0.3s ease;
            transition:
        transform 0.45s cubic-bezier(0.34, 1.56, 0.64, 1),
        opacity 0.3s ease;
  }
  #${id}.glass-confetti-overlay.is-active .glass-confetti-badge {
    -webkit-transform : translate(-50%, -50%) scale(1);
            transform : translate(-50%, -50%) scale(1);
    opacity            : 1;
  }

  #${id} .glass-confetti-ring {
    width                    : 84px;
    height                   : 84px;
    margin                   : 0 auto 14px;
    -webkit-border-radius    : 50%;
            border-radius    : 50%;
    display                  : -webkit-inline-box;
    display                  : -webkit-inline-flex;
    display                  : inline-flex;
    -webkit-box-align        : center;
    -webkit-align-items      : center;
            align-items      : center;
    -webkit-box-pack         : center;
    -webkit-justify-content  : center;
            justify-content  : center;
    background               : color-mix(in srgb, var(--confetti-accent) 30%, rgba(20,10,35,0.55));
    -webkit-backdrop-filter  : blur(14px);
            backdrop-filter  : blur(14px);
    border                   : 2px solid color-mix(in srgb, var(--confetti-accent) 70%, white 10%);
    -webkit-box-shadow       : 0 0 0 6px color-mix(in srgb, var(--confetti-accent) 18%, transparent),
                               0 12px 30px rgba(0,0,0,0.35);
            box-shadow       : 0 0 0 6px color-mix(in srgb, var(--confetti-accent) 18%, transparent),
                               0 12px 30px rgba(0,0,0,0.35);
  }
  @supports not (background: color-mix(in srgb, red 50%, transparent)) {
    #${id} .glass-confetti-ring { background: var(--confetti-accent); }
  }

  #${id} .glass-confetti-check {
    width  : 38px;
    height : 38px;
  }
  #${id} .glass-confetti-check path {
    fill              : none;
    stroke            : #ffffff;
    stroke-width      : 5;
    stroke-linecap    : round;
    stroke-linejoin   : round;
    stroke-dasharray  : 40;
    stroke-dashoffset : 40;
  }
  #${id}.glass-confetti-overlay.is-active .glass-confetti-check path {
    -webkit-animation : glassConfettiDraw_${id} 0.4s 0.25s ease forwards;
            animation : glassConfettiDraw_${id} 0.4s 0.25s ease forwards;
  }
  @-webkit-keyframes glassConfettiDraw_${id} { to { stroke-dashoffset: 0; } }
  @keyframes glassConfettiDraw_${id} { to { stroke-dashoffset: 0; } }

  #${id} .glass-confetti-title {
    display        : block;
    font-size      : 1.15rem;
    font-weight    : 700;
    letter-spacing : -0.01em;
    text-shadow    : 0 2px 10px rgba(0,0,0,0.45);
  }
  #${id} .glass-confetti-subtitle {
    display     : block;
    margin-top  : 4px;
    font-size   : 0.85rem;
    font-weight : 300;
    color       : rgba(255,255,255,0.75);
    text-shadow : 0 2px 10px rgba(0,0,0,0.45);
  }

  @media (prefers-reduced-motion: reduce) {
    #${id} .glass-confetti-badge { transition: opacity 0.3s ease; }
    #${id}.glass-confetti-overlay.is-active .glass-confetti-badge {
      -webkit-transform: translate(-50%, -50%) scale(1);
              transform: translate(-50%, -50%) scale(1);
    }
  }
</style>

<div id="${id}" class="glass-confetti-overlay${extra ? ' ' + extra : ''}" aria-hidden="true">
  <canvas class="glass-confetti-canvas"></canvas>
  ${showCheckmark ? """
  <div class="glass-confetti-badge">
    <div class="glass-confetti-ring">
      <svg class="glass-confetti-check" viewBox="0 0 24 24">
        <path d="M4 12.5l5 5L20 6.5"/>
      </svg>
    </div>
    ${renderedTitle ? "<span class=\"glass-confetti-title\">${renderedTitle}</span>" : ''}
    ${renderedSubtitle ? "<span class=\"glass-confetti-subtitle\">${renderedSubtitle}</span>" : ''}
  </div>
  """ : ''}
</div>

<script>
(function () {
  // ── Shared global API: window.glassConfetti.fire(id, overrides) ──────────
  // Reused across every glass:confetti instance on the page, so the script
  // block below only defines it once (first instance wins) and every
  // subsequent instance just registers itself into the shared registry.
  window.glassConfetti = window.glassConfetti || (function () {
    var registry = {};
    var lastId = null;

    function rand(min, max) { return Math.random() * (max - min) + min; }

    function runBurst(cfg) {
      var overlay = document.getElementById(cfg.id);
      if (!overlay) return;
      var canvas = overlay.querySelector('.glass-confetti-canvas');
      var ctx = canvas.getContext && canvas.getContext('2d');

      var dpr = window.devicePixelRatio || 1;
      var w = window.innerWidth, h = window.innerHeight;
      canvas.width = w * dpr;
      canvas.height = h * dpr;
      if (ctx) ctx.scale(dpr, dpr);

      overlay.classList.add('is-active');

      var particleCount = cfg.particleCount;
      var spreadRad = (cfg.spread * Math.PI) / 180;
      var originXPx = w * cfg.originX;
      var originYPx = h * cfg.originY;

      var particles = [];
      for (var i = 0; i < particleCount; i++) {
        var angle = (-Math.PI / 2) + rand(-spreadRad / 2, spreadRad / 2);
        var speed = rand(4, 12);
        particles.push({
          x: originXPx,
          y: originYPx,
          vx: Math.cos(angle) * speed,
          vy: Math.sin(angle) * speed,
          size: rand(5, 10),
          color: cfg.colors[Math.floor(Math.random() * cfg.colors.length)],
          rotation: rand(0, 360),
          rotationSpeed: rand(-8, 8),
          shape: Math.random() < 0.5 ? 'rect' : (Math.random() < 0.5 ? 'circle' : 'triangle'),
          gravity: rand(0.15, 0.28),
          drag: rand(0.005, 0.02)
        });
      }

      var startTime = null;
      var duration = cfg.duration;

      function frame(ts) {
        if (!startTime) startTime = ts;
        var elapsed = ts - startTime;
        var t = Math.min(1, elapsed / duration);

        if (!ctx) {
          // No canvas 2d support (very old browser) — just hold the badge
          // on screen for the duration, then clear. Confetti particles
          // simply won't render, degrading gracefully to badge-only.
          if (elapsed >= duration) { finish(overlay); return; }
          requestAnimationFrame(frame);
          return;
        }

        ctx.clearRect(0, 0, w, h);

        var fadeStart = 0.7; // begin fading pieces out over the last 30% of duration
        var alpha = t < fadeStart ? 1 : Math.max(0, 1 - (t - fadeStart) / (1 - fadeStart));

        for (var j = 0; j < particles.length; j++) {
          var p = particles[j];
          p.vy += p.gravity;
          p.vx *= (1 - p.drag);
          p.x += p.vx;
          p.y += p.vy;
          p.rotation += p.rotationSpeed;

          ctx.save();
          ctx.globalAlpha = alpha;
          ctx.translate(p.x, p.y);
          ctx.rotate((p.rotation * Math.PI) / 180);
          ctx.fillStyle = p.color;

          if (p.shape === 'rect') {
            ctx.fillRect(-p.size / 2, -p.size / 4, p.size, p.size / 2);
          } else if (p.shape === 'circle') {
            ctx.beginPath();
            ctx.arc(0, 0, p.size / 2, 0, Math.PI * 2);
            ctx.fill();
          } else {
            ctx.beginPath();
            ctx.moveTo(0, -p.size / 2);
            ctx.lineTo(p.size / 2, p.size / 2);
            ctx.lineTo(-p.size / 2, p.size / 2);
            ctx.closePath();
            ctx.fill();
          }
          ctx.restore();
        }

        if (elapsed < duration) {
          requestAnimationFrame(frame);
        } else {
          finish(overlay);
        }
      }

      requestAnimationFrame(frame);
    }

    function finish(overlay) {
      overlay.classList.remove('is-active');
      var canvas = overlay.querySelector('.glass-confetti-canvas');
      var ctx = canvas.getContext && canvas.getContext('2d');
      if (ctx) ctx.clearRect(0, 0, canvas.width, canvas.height);
    }

    return {
      /** Called once per glass:confetti tag instance to register its config. */
      _register: function (id, cfg) {
        registry[id] = cfg;
        lastId = id;
      },
      /** Fires a registered instance by id (or the most recently registered
       *  one, if id is omitted). `overrides` optionally replaces any subset
       *  of that instance's config for just this call. */
      fire: function (id, overrides) {
        var targetId = id || lastId;
        if (!targetId || !registry[targetId]) {
          if (window.console) console.warn('glassConfetti.fire: no confetti instance found for id "' + targetId + '"');
          return;
        }
        var cfg = {};
        for (var k in registry[targetId]) cfg[k] = registry[targetId][k];
        if (overrides) for (var k2 in overrides) cfg[k2] = overrides[k2];
        runBurst(cfg);
      }
    };
  })();

  window.glassConfetti._register('${id}', {
    id: '${id}',
    particleCount: ${particleCount},
    duration: ${durationMs},
    spread: ${spreadDeg},
    originX: ${originX},
    originY: ${originY},
    colors: [${colorsJs}]
  });

  ${trigger == 'auto' ? """
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', function () { window.glassConfetti.fire('${id}'); });
  } else {
    window.glassConfetti.fire('${id}');
  }
  """ : ''}
})();
</script>
"""
  }
}
