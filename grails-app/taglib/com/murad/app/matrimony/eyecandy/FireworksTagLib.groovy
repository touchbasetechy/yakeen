package com.murad.app.matrimony

import groovy.util.logging.Slf4j

/**
 * Fireworks Burst — GSP TagLib
 *
 * Usage in GSP:
 *   <glass:fireworks id="engagementShow" shellCount="5" title="Congratulations!"
 *                     subtitle="Wishing you both a lifetime of happiness" badge="true"/>
 *
 *   <!-- fire it from anywhere -->
 *   <script>
 *     glassFireworks.fire('engagementShow');
 *   </script>
 *
 *   <!-- or auto-play once, on page load -->
 *   <glass:fireworks id="landingShow" trigger="auto" shellCount="3"/>
 *
 * What it renders
 * ─────────────────────────────────────────────────────────────────────────
 * A fixed, full-viewport, pointer-events:none <canvas> overlay that runs a
 * small fireworks show: `shellCount` rockets launch one after another from
 * random positions near the bottom of the viewport, rise with a fading
 * trail, then burst into a radial shell of `particlesPerShell` sparks that
 * fall under gravity while fading out — same overall trail-fade technique
 * real fireworks sims use (repeatedly erasing a fraction of the canvas's
 * alpha each frame via `destination-out` compositing, rather than a hard
 * clear, so motion leaves a soft streak instead of a solid line).
 *
 * An OPTIONAL centered success badge (title/subtitle, no checkmark — this
 * component is meant as a bigger, more ambient celebration than
 * glass:confetti's inline success badge) can fade in over the show via
 * `badge="true"`.
 *
 * Nothing plays automatically unless trigger="auto". Otherwise fire it from
 * your own JS via the small global API this tag registers:
 *
 *   glassFireworks.fire('yourId')             // run the full show
 *   glassFireworks.fire('yourId', {           // one-off overrides
 *     shellCount: 8,
 *     colors: ['#ffd166', '#ff6b8b']
 *   })
 *   glassFireworks.fire()                     // no id needed if only one
 *                                             // <glass:fireworks> exists on the page
 *
 * Attributes
 * ─────────────────────────────────────────────────────────────────────────
 *   id                (String, optional) — unique id for this instance; needed
 *                                          to target it with glassFireworks.fire('id')
 *                                          when more than one exists on the page.
 *                                          Auto-generated when omitted.
 *   trigger           (String, optional) — "manual" (default) or "auto". "auto"
 *                                          runs the show once, automatically,
 *                                          on DOMContentLoaded.
 *   shellCount        (int, optional)    — number of rocket/burst shells in the
 *                                          show (default: 5, clamped 1..20).
 *   particlesPerShell (int, optional)    — sparks per burst shell (default: 70,
 *                                          clamped 8..400).
 *   showDuration       (ms, optional)     — total time over which shells are
 *                                          launched, spread out with a little
 *                                          randomized jitter between each
 *                                          (default: 3500, clamped >= 500). The
 *                                          show runs a little longer than this
 *                                          while the final shell's sparks fall
 *                                          and fade.
 *   trail             (0..1, optional)   — how quickly motion trails fade each
 *                                          frame; lower = longer, streakier
 *                                          trails, higher = short, crisp dots
 *                                          (default: 0.12, clamped 0.02..0.9).
 *   colors            (String, optional) — comma-separated CSS colors for the
 *                                          burst sparks (default: a classic
 *                                          fireworks palette — gold, red, blue,
 *                                          green, white).
 *   zIndex            (int, optional)    — stacking order of the overlay
 *                                          (default: 9999).
 *   badge             (Boolean, optional)— show a centered title/subtitle badge
 *                                          fading in over the show (default: false).
 *   title             (String, optional) — text in the badge.
 *   subtitle          (String, optional) — smaller text under the title.
 *   titleHtml         (Boolean, optional, default: false) — render title as raw HTML.
 *   subtitleHtml      (Boolean, optional, default: false) — render subtitle as raw HTML.
 *   accent            (String, optional) — Bootstrap variant name (primary, secondary,
 *                                          success, danger, warning, info, dark, light)
 *                                          resolving to var(--bs-{name}), or any raw
 *                                          CSS color. Drives the badge's glow color.
 *                                          Default: "warning"
 *   class             (String, optional) — extra CSS classes appended to the overlay
 */
@Slf4j
class FireworksTagLib {

  static namespace = 'glass'

  private static final Set<String> BS_VARIANTS = [
    'primary', 'secondary', 'success', 'danger', 'warning', 'info', 'dark', 'light'
  ] as Set

  private static final List<String> DEFAULT_COLORS = [
    '#ffd166', '#ff6b6b', '#5ec8ff', '#5cffa8', '#c88bff', '#ffffff'
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
   * Renders a fireworks-show overlay and registers it with the page-level
   * window.glassFireworks.fire(id) API.
   *
   * @attr id                  Unique id for this instance (auto-generated if omitted).
   * @attr trigger             "manual" (default) or "auto" — auto runs the show once on page load.
   * @attr shellCount          Number of rocket/burst shells (default: 5, clamped 1..20).
   * @attr particlesPerShell   Sparks per burst shell (default: 70, clamped 8..400).
   * @attr showDuration        Total launch-window in ms (default: 3500, clamped >= 500).
   * @attr trail                0..1, trail fade speed — lower = longer trails (default: 0.12).
   * @attr colors               Comma-separated CSS colors for the sparks.
   * @attr zIndex                Overlay stacking order (default: 9999).
   * @attr badge                 "true"/"false" — show a centered title/subtitle badge (default: false).
   * @attr title                  Text in the badge.
   * @attr subtitle                Smaller text under the title.
   * @attr titleHtml                 "true" to render title as raw HTML (default: false).
   * @attr subtitleHtml                "true" to render subtitle as raw HTML (default: false).
   * @attr accent                       Bootstrap variant or raw CSS color for the badge glow (default: "warning").
   * @attr class                          Extra CSS classes for the overlay.
   */
  def fireworks = { attrs, body ->
    String id = (attrs.remove('id') ?: "glassFireworks_${System.currentTimeMillis()}_${(int) (Math.random() * 9999)}").toString()
    String extra = (attrs.remove('class') ?: '').toString()

    String trigger = (attrs.remove('trigger') ?: 'manual').toString().toLowerCase()
    if (!(trigger in ['manual', 'auto'])) trigger = 'manual'

    int shellCount = clampInt(toIntOrDefault(attrs.remove('shellCount'), 5), 1, 20)
    int particlesPerShell = clampInt(toIntOrDefault(attrs.remove('particlesPerShell'), 70), 8, 400)
    int showDurationMs = Math.max(500, toIntOrDefault(attrs.remove('showDuration'), 3500))
    double trail = clampDouble(toDoubleOrDefault(attrs.remove('trail'), 0.12d), 0.02d, 0.9d)
    int zIndex = toIntOrDefault(attrs.remove('zIndex'), 9999)

    String colorsAttr = (attrs.remove('colors') ?: '').toString()
    List<String> colors = colorsAttr ? colorsAttr.split(',').collect { it.trim() }.findAll { it } : DEFAULT_COLORS

    boolean showBadge = attrs.containsKey('badge') ? Boolean.parseBoolean(attrs.remove('badge').toString()) : false
    String title = (attrs.remove('title') ?: '').toString()
    String subtitle = (attrs.remove('subtitle') ?: '').toString()
    boolean titleHtml = attrs.remove('titleHtml') in [true, 'true', '1', 'yes']
    boolean subtitleHtml = attrs.remove('subtitleHtml') in [true, 'true', '1', 'yes']
    String accent = resolveColor((attrs.remove('accent') ?: 'warning').toString())

    String renderedTitle = title ? (titleHtml ? title : title.encodeAsHTML()) : ''
    String renderedSubtitle = subtitle ? (subtitleHtml ? subtitle : subtitle.encodeAsHTML()) : ''

    String colorsJs = colors.collect { "\"${it.replace('"', '\\"')}\"" }.join(',')

    log.debug("fireworks: id=${id}, trigger=${trigger}, shellCount=${shellCount}, particlesPerShell=${particlesPerShell}, showDuration=${showDurationMs}, trail=${trail}, badge=${showBadge}, colors=${colors}")

    out << """
<style>
  #${id}.glass-fireworks-overlay {
    position       : fixed;
    inset          : 0;
    z-index        : ${zIndex};
    pointer-events : none;
    display        : none;
  }
  #${id}.glass-fireworks-overlay.is-active { display: block; }
  #${id} canvas.glass-fireworks-canvas {
    position : absolute;
    inset    : 0;
    width    : 100%;
    height   : 100%;
    display  : block;
  }

  #${id} .glass-fireworks-badge {
    --fireworks-accent: ${accent};

    position          : absolute;
    top               : 38%;
    left              : 50%;
    -webkit-transform : translate(-50%, -50%) scale(0.5);
            transform : translate(-50%, -50%) scale(0.5);
    opacity           : 0;
    text-align        : center;
    color             : #fff;
    font-family       : 'Outfit', sans-serif;
    padding           : 18px 28px;
    -webkit-border-radius: 16px;
            border-radius: 16px;
    background        : color-mix(in srgb, var(--fireworks-accent) 20%, rgba(15, 8, 28, 0.55));
    -webkit-backdrop-filter: blur(16px);
            backdrop-filter: blur(16px);
    border             : 1px solid color-mix(in srgb, var(--fireworks-accent) 55%, white 15%);
    -webkit-box-shadow : 0 0 40px 8px color-mix(in srgb, var(--fireworks-accent) 25%, transparent),
                         0 16px 40px rgba(0,0,0,0.4);
            box-shadow : 0 0 40px 8px color-mix(in srgb, var(--fireworks-accent) 25%, transparent),
                         0 16px 40px rgba(0,0,0,0.4);

    -webkit-transition:
        -webkit-transform 0.6s cubic-bezier(0.22, 0.61, 0.36, 1),
                transform 0.6s cubic-bezier(0.22, 0.61, 0.36, 1),
        opacity 0.5s ease;
            transition:
        transform 0.6s cubic-bezier(0.22, 0.61, 0.36, 1),
        opacity 0.5s ease;
  }
  @supports not (background: color-mix(in srgb, red 50%, transparent)) {
    #${id} .glass-fireworks-badge { background: rgba(15, 8, 28, 0.85); }
  }
  #${id}.glass-fireworks-overlay.is-active .glass-fireworks-badge {
    -webkit-transform : translate(-50%, -50%) scale(1);
            transform : translate(-50%, -50%) scale(1);
    opacity            : 1;
  }

  #${id} .glass-fireworks-title {
    display        : block;
    font-size      : 1.4rem;
    font-weight    : 800;
    letter-spacing : -0.01em;
    text-shadow    : 0 2px 12px rgba(0,0,0,0.5);
  }
  #${id} .glass-fireworks-subtitle {
    display     : block;
    margin-top  : 6px;
    font-size   : 0.9rem;
    font-weight : 300;
    color       : rgba(255,255,255,0.75);
    text-shadow : 0 2px 10px rgba(0,0,0,0.45);
  }

  @media (prefers-reduced-motion: reduce) {
    #${id} .glass-fireworks-badge { transition: opacity 0.5s ease; }
    #${id}.glass-fireworks-overlay.is-active .glass-fireworks-badge {
      -webkit-transform: translate(-50%, -50%) scale(1);
              transform: translate(-50%, -50%) scale(1);
    }
  }
</style>

<div id="${id}" class="glass-fireworks-overlay${extra ? ' ' + extra : ''}" aria-hidden="true">
  <canvas class="glass-fireworks-canvas"></canvas>
  ${showBadge ? """
  <div class="glass-fireworks-badge">
    ${renderedTitle ? "<span class=\"glass-fireworks-title\">${renderedTitle}</span>" : ''}
    ${renderedSubtitle ? "<span class=\"glass-fireworks-subtitle\">${renderedSubtitle}</span>" : ''}
  </div>
  """ : ''}
</div>

<script>
(function () {
  // ── Shared global API: window.glassFireworks.fire(id, overrides) ─────────
  // Reused across every glass:fireworks instance on the page — the engine
  // below is only defined once (first instance wins) and every subsequent
  // instance just registers its config into the shared registry.
  window.glassFireworks = window.glassFireworks || (function () {
    var registry = {};
    var lastId = null;

    function rand(min, max) { return Math.random() * (max - min) + min; }

    function runShow(cfg) {
      var overlay = document.getElementById(cfg.id);
      if (!overlay) return;
      var canvas = overlay.querySelector('.glass-fireworks-canvas');
      var ctx = canvas.getContext && canvas.getContext('2d');

      var dpr = window.devicePixelRatio || 1;
      var w = window.innerWidth, h = window.innerHeight;
      canvas.width = w * dpr;
      canvas.height = h * dpr;
      if (ctx) ctx.scale(dpr, dpr);

      overlay.classList.add('is-active');

      if (!ctx) {
        // No canvas 2d support — hold the badge on screen for the show's
        // duration, then clear, degrading gracefully to badge-only.
        setTimeout(function () { finish(overlay); }, cfg.showDuration + 1200);
        return;
      }

      var rockets = [];   // rising rocket trails, pre-burst
      var sparks = [];    // radial burst particles, post-burst

      function launchShell() {
        var originX = rand(w * 0.2, w * 0.8);
        var burstY = rand(h * 0.18, h * 0.45);
        var color = cfg.colors[Math.floor(Math.random() * cfg.colors.length)];
        rockets.push({
          x: originX,
          y: h + 10,
          vy: -rand(9, 13),
          targetY: burstY,
          color: color
        });
      }

      function burst(x, y, color) {
        for (var i = 0; i < cfg.particlesPerShell; i++) {
          var angle = (Math.PI * 2 * i) / cfg.particlesPerShell + rand(-0.06, 0.06);
          var speed = rand(2.5, 6.5);
          sparks.push({
            x: x,
            y: y,
            vx: Math.cos(angle) * speed,
            vy: Math.sin(angle) * speed,
            color: Math.random() < 0.75 ? color : cfg.colors[Math.floor(Math.random() * cfg.colors.length)],
            size: rand(1.6, 3),
            gravity: rand(0.05, 0.09),
            drag: rand(0.01, 0.025),
            life: 1,
            decay: rand(0.008, 0.018),
            twinkle: Math.random() < 0.3
          });
        }
      }

      // Spread shell launches out across showDuration with light jitter
      var launchGap = cfg.showDuration / cfg.shellCount;
      for (var s = 0; s < cfg.shellCount; s++) {
        (function (delay) {
          setTimeout(launchShell, delay);
        })(s * launchGap + rand(0, launchGap * 0.4));
      }

      function frame() {
        // Trail-fade: erase a fraction of existing alpha each frame instead
        // of a hard clear, via destination-out compositing — this only
        // reduces what's already drawn on THIS canvas, so it works
        // correctly regardless of what's behind the (transparent) overlay.
        ctx.globalCompositeOperation = 'destination-out';
        ctx.fillStyle = 'rgba(0,0,0,' + cfg.trail + ')';
        ctx.fillRect(0, 0, w, h);
        ctx.globalCompositeOperation = 'source-over';

        // Rising rockets
        for (var r = rockets.length - 1; r >= 0; r--) {
          var rk = rockets[r];
          rk.y += rk.vy;
          rk.vy += 0.05; // slight deceleration as it climbs

          ctx.beginPath();
          ctx.fillStyle = rk.color;
          ctx.arc(rk.x, rk.y, 2.2, 0, Math.PI * 2);
          ctx.fill();

          if (rk.y <= rk.targetY || rk.vy >= -1) {
            burst(rk.x, rk.y, rk.color);
            rockets.splice(r, 1);
          }
        }

        // Burst sparks
        for (var p = sparks.length - 1; p >= 0; p--) {
          var sp = sparks[p];
          sp.vy += sp.gravity;
          sp.vx *= (1 - sp.drag);
          sp.vy *= (1 - sp.drag);
          sp.x += sp.vx;
          sp.y += sp.vy;
          sp.life -= sp.decay;

          if (sp.life <= 0) {
            sparks.splice(p, 1);
            continue;
          }

          var alpha = sp.twinkle ? sp.life * rand(0.4, 1) : sp.life;
          ctx.beginPath();
          ctx.globalAlpha = Math.max(0, alpha);
          ctx.fillStyle = sp.color;
          ctx.arc(sp.x, sp.y, sp.size, 0, Math.PI * 2);
          ctx.fill();
          ctx.globalAlpha = 1;
        }

        var stillGoing = rockets.length > 0 || sparks.length > 0;
        var allShellsLaunched = Date.now() - (overlay._glassFireworksStart || Date.now()) > cfg.showDuration;

        if (stillGoing || !allShellsLaunched) {
          requestAnimationFrame(frame);
        } else {
          // fade the residual trail out fully, then stop
          var fadeFrames = 0;
          (function fadeOut() {
            ctx.globalCompositeOperation = 'destination-out';
            ctx.fillStyle = 'rgba(0,0,0,0.2)';
            ctx.fillRect(0, 0, w, h);
            ctx.globalCompositeOperation = 'source-over';
            fadeFrames++;
            if (fadeFrames < 12) {
              requestAnimationFrame(fadeOut);
            } else {
              finish(overlay);
            }
          })();
        }
      }

      overlay._glassFireworksStart = Date.now();
      requestAnimationFrame(frame);
    }

    function finish(overlay) {
      overlay.classList.remove('is-active');
      var canvas = overlay.querySelector('.glass-fireworks-canvas');
      var ctx = canvas.getContext && canvas.getContext('2d');
      if (ctx) ctx.clearRect(0, 0, canvas.width, canvas.height);
    }

    return {
      /** Called once per glass:fireworks tag instance to register its config. */
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
          if (window.console) console.warn('glassFireworks.fire: no fireworks instance found for id "' + targetId + '"');
          return;
        }
        var cfg = {};
        for (var k in registry[targetId]) cfg[k] = registry[targetId][k];
        if (overrides) for (var k2 in overrides) cfg[k2] = overrides[k2];
        runShow(cfg);
      }
    };
  })();

  window.glassFireworks._register('${id}', {
    id: '${id}',
    shellCount: ${shellCount},
    particlesPerShell: ${particlesPerShell},
    showDuration: ${showDurationMs},
    trail: ${trail},
    colors: [${colorsJs}]
  });

  ${trigger == 'auto' ? """
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', function () { window.glassFireworks.fire('${id}'); });
  } else {
    window.glassFireworks.fire('${id}');
  }
  """ : ''}
})();
</script>
"""
  }
}
