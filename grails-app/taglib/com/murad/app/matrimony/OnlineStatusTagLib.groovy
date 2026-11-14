package com.murad.app.matrimony

import groovy.util.logging.Slf4j

/**
 * Live / Online Status Pulse — GSP TagLib
 *
 * Usage in GSP:
 *   <!-- inline, next to a name -->
 *   <span>Priya <glass:onlineStatus id="priyaStatus" status="online" label="Online now"/></span>
 *
 *   <!-- corner badge on an avatar (avatar wrapper needs position:relative) -->
 *   <div style="position:relative; display:inline-block;">
 *     <img src="..." class="avatar"/>
 *     <glass:onlineStatus id="avatarStatus" status="online" position="corner" label="Online now"/>
 *   </div>
 *
 *   <!-- update it later, e.g. from a websocket presence feed -->
 *   <script>
 *     glassStatus.set('priyaStatus', 'away', 'Active 5m ago');
 *     glassStatus.set('avatarStatus', 'offline', 'Last seen yesterday');
 *   </script>
 *
 * What it renders
 * ─────────────────────────────────────────────────────────────────────────
 * A small colored dot (green=online, amber=away, red=busy, gray=offline by
 * default, all overridable) sitting inside a soft glassmorphic frosted
 * halo. When the status is "online" (or `pulse="true"` is forced), two
 * staggered rings continuously expand outward from the dot and fade —
 * a soft radar-style pulse — while away/busy/offline render as a static
 * dot with no pulse.
 *
 * An optional `label` (e.g. "Online now", "Active 5m ago", "Last seen
 * yesterday") is shown as a small glass tooltip:
 *   - on hover, for mouse/trackpad users,
 *   - on tap, for touch users (hover doesn't exist there, so tapping
 *     toggles the tooltip open/closed instead — tapping elsewhere, or
 *     the dot again, closes it),
 *   - on keyboard focus (the dot is a real, focusable element), and
 *     Escape closes it.
 * The tooltip auto-flips from centered to left/right-aligned if it would
 * otherwise overflow the viewport near a screen edge (checked on show, so
 * it stays correctly positioned across window sizes/orientations).
 *
 * Touch handling matches the rest of the glass:* set: `touch-action:
 * manipulation` and `-webkit-tap-highlight-color: transparent` so taps
 * register immediately with no double-tap-zoom delay or gray flash.
 *
 * Nothing needs to be wired up manually for the tooltip — it's automatic.
 * Use the global API only to change status dynamically (e.g. real-time
 * presence updates):
 *
 *   glassStatus.set('yourId', 'away')                    // change status, keep existing label
 *   glassStatus.set('yourId', 'offline', 'Last seen 2h ago') // change status AND label together
 *   glassStatus.get('yourId')                             // -> { status: 'away', label: '...' }
 *
 * Attributes
 * ─────────────────────────────────────────────────────────────────────────
 *   id         (String, optional)  — unique id for this instance; needed to
 *                                    target it with glassStatus.set('id')
 *                                    when more than one exists on the page
 *                                    (the common case). Auto-generated when omitted.
 *   status     (String, optional)  — "online" | "away" | "busy" | "offline"
 *                                    (default: "online"). Unrecognized values
 *                                    fall back to "offline".
 *   label      (String, optional)  — tooltip text, e.g. "Online now". Omit for
 *                                    no tooltip (dot renders with no interaction).
 *   size       (px, optional)      — dot diameter (default: 10, clamped 6..24).
 *   position   (String, optional)  — "inline" (default, sits in normal text
 *                                    flow) or "corner" (absolutely positioned
 *                                    bottom-right — nest it inside a
 *                                    position:relative avatar wrapper).
 *   glass      (Boolean, optional) — soft frosted halo behind the dot
 *                                    (default: true).
 *   pulse      (Boolean, optional) — force the radiating pulse on/off,
 *                                    overriding the default (on for "online",
 *                                    off otherwise).
 *   onlineColor, awayColor, busyColor, offlineColor (String, optional) —
 *                                    per-status color overrides. Each accepts
 *                                    a Bootstrap variant name (primary, secondary,
 *                                    success, danger, warning, info, dark, light)
 *                                    resolving to var(--bs-{name}), or any raw
 *                                    CSS color. Defaults: green / amber / red / gray.
 *   ringColor  (Boolean, optional) — the ring/border around the dot separating
 *                                    it from its background (default: a subtle
 *                                    white — good on both light and dark
 *                                    backgrounds/avatars).
 *   class      (String, optional)  — extra CSS classes appended to the wrapper.
 */
@Slf4j
class OnlineStatusTagLib {

  static namespace = 'glass'

  private static final Set<String> BS_VARIANTS = [
    'primary', 'secondary', 'success', 'danger', 'warning', 'info', 'dark', 'light'
  ] as Set

  private static final Map<String, String> DEFAULT_STATUS_COLORS = [
    online : '#3ddc84',
    away   : '#ffc247',
    busy   : '#ff5c72',
    offline: '#9aa0ab'
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

  private int clampInt(int v, int min, int max) {
    return Math.max(min, Math.min(max, v))
  }

  /**
   * Renders a live-status pulse dot (with optional tap/hover/focus
   * tooltip) and registers it with the page-level window.glassStatus API.
   *
   * @attr id            Unique id for this instance (auto-generated if omitted).
   * @attr status         "online" | "away" | "busy" | "offline" (default: "online").
   * @attr label           Tooltip text. Omit for a non-interactive dot.
   * @attr size              Dot diameter in px (default: 10, clamped 6..24).
   * @attr position            "inline" (default) or "corner" (absolute avatar badge).
   * @attr glass                 Soft frosted halo behind the dot (default: true).
   * @attr pulse                   Force the radiating pulse on/off.
   * @attr onlineColor                Override for the "online" dot color.
   * @attr awayColor                     Override for the "away" dot color.
   * @attr busyColor                        Override for the "busy" dot color.
   * @attr offlineColor                        Override for the "offline" dot color.
   * @attr ringColor                               Border ring color around the dot.
   * @attr class                                      Extra CSS classes for the wrapper.
   */
  def onlineStatus = { attrs, body ->
    String id = (attrs.remove('id') ?: "glassStatus_${System.currentTimeMillis()}_${(int) (Math.random() * 9999)}").toString()
    String extra = (attrs.remove('class') ?: '').toString()

    String status = (attrs.remove('status') ?: 'online').toString().toLowerCase()
    if (!DEFAULT_STATUS_COLORS.containsKey(status)) status = 'offline'

    String label = (attrs.remove('label') ?: '').toString()
    int sizePx = clampInt(toIntOrDefault(attrs.remove('size'), 10), 6, 24)

    String position = (attrs.remove('position') ?: 'inline').toString().toLowerCase()
    if (!(position in ['inline', 'corner'])) position = 'inline'

    boolean glass = attrs.containsKey('glass') ? Boolean.parseBoolean(attrs.remove('glass').toString()) : true
    Boolean pulseOverride = attrs.containsKey('pulse') ? Boolean.parseBoolean(attrs.remove('pulse').toString()) : null
    boolean pulse = pulseOverride != null ? pulseOverride : (status == 'online')

    String onlineColor = resolveColor((attrs.remove('onlineColor') ?: DEFAULT_STATUS_COLORS.online).toString())
    String awayColor = resolveColor((attrs.remove('awayColor') ?: DEFAULT_STATUS_COLORS.away).toString())
    String busyColor = resolveColor((attrs.remove('busyColor') ?: DEFAULT_STATUS_COLORS.busy).toString())
    String offlineColor = resolveColor((attrs.remove('offlineColor') ?: DEFAULT_STATUS_COLORS.offline).toString())
    String ringColor = (attrs.remove('ringColor') ?: 'rgba(255,255,255,0.85)').toString()

    Map<String, String> statusColors = [online: onlineColor, away: awayColor, busy: busyColor, offline: offlineColor]
    String dotColor = statusColors[status]

    String renderedLabel = label.encodeAsHTML()
    boolean interactive = label as boolean

    log.debug("onlineStatus: id=${id}, status=${status}, label='${label}', size=${sizePx}, position=${position}, glass=${glass}, pulse=${pulse}")

    out << """
<style>
  #${id}.glass-status-wrap {
    --status-color: ${dotColor};
    --status-ring : ${ringColor};

    position                : ${position == 'corner' ? 'absolute' : 'relative'};
    ${position == 'corner' ? """
    bottom : -2px;
    right  : -2px;
    """ : """
    display                 : -webkit-inline-box;
    display                  : -webkit-inline-flex;
    display                  : -ms-inline-flexbox;
    display                  : inline-flex;
    vertical-align            : middle;
    """}
    -webkit-box-align        : center;
    -webkit-align-items      : center;
        -ms-flex-align       : center;
            align-items      : center;
    font-family                : 'Outfit', sans-serif;
  }

  #${id} .glass-status-halo {
    position                : relative;
    display                 : -webkit-inline-box;
    display                  : -webkit-inline-flex;
    display                  : -ms-inline-flexbox;
    display                  : inline-flex;
    -webkit-box-align        : center;
    -webkit-align-items      : center;
        -ms-flex-align       : center;
            align-items      : center;
    -webkit-box-pack         : center;
    -webkit-justify-content  : center;
        -ms-flex-pack        : center;
            justify-content  : center;

    width                     : ${sizePx + (glass ? 10 : 0)}px;
    height                    : ${sizePx + (glass ? 10 : 0)}px;
    -webkit-border-radius     : 50%;
            border-radius     : 50%;
    ${glass ? """
    background                : color-mix(in srgb, var(--status-color) 20%, rgba(255,255,255,0.06));
    -webkit-backdrop-filter   : blur(6px);
            backdrop-filter   : blur(6px);
    """ : ''}
    cursor                     : ${interactive ? 'pointer' : 'default'};

    -webkit-tap-highlight-color: transparent;
    touch-action                : manipulation;
    -webkit-user-select         : none;
       -moz-user-select         : none;
            user-select         : none;
    outline                     : none;
  }
  @supports not (background: color-mix(in srgb, red 50%, transparent)) {
    #${id} .glass-status-halo { background: rgba(255,255,255,0.10); }
  }
  #${id} .glass-status-halo:focus-visible {
    -webkit-box-shadow: 0 0 0 3px color-mix(in srgb, var(--status-color) 50%, transparent);
            box-shadow: 0 0 0 3px color-mix(in srgb, var(--status-color) 50%, transparent);
  }

  #${id} .glass-status-dot {
    position               : relative;
    width                  : ${sizePx}px;
    height                 : ${sizePx}px;
    -webkit-border-radius  : 50%;
            border-radius  : 50%;
    background              : var(--status-color);
    border                   : 2px solid var(--status-ring);
    -webkit-box-shadow       : 0 1px 3px rgba(0,0,0,0.35);
            box-shadow       : 0 1px 3px rgba(0,0,0,0.35);
    z-index                   : 1;
  }

  /* Radiating pulse rings — two staggered expanding/fading rings via
     box-shadow keyframes on pseudo-elements, so no extra DOM nodes are
     needed and it composites correctly on any background. Always defined
     (not conditional on the initial `pulse` value) and gated by the
     .has-pulse class instead, so glassStatus.set() can turn the pulse on
     or off at runtime — not just at initial render. */
  #${id} .glass-status-dot::before,
  #${id} .glass-status-dot::after {
    content    : "";
    position   : absolute;
    inset      : 0;
    -webkit-border-radius: 50%;
            border-radius: 50%;
    background  : var(--status-color);
    z-index      : 0;
    pointer-events: none;
    opacity       : 0;
  }
  #${id} .glass-status-dot.has-pulse::before {
    -webkit-animation: glassStatusPulse_${id} 2s ease-out infinite;
            animation: glassStatusPulse_${id} 2s ease-out infinite;
  }
  #${id} .glass-status-dot.has-pulse::after {
    -webkit-animation: glassStatusPulse_${id} 2s ease-out 1s infinite;
            animation: glassStatusPulse_${id} 2s ease-out 1s infinite;
  }
  @-webkit-keyframes glassStatusPulse_${id} {
    0%   { -webkit-transform: scale(1);   opacity: 0.55; }
    100% { -webkit-transform: scale(2.6); opacity: 0; }
  }
  @keyframes glassStatusPulse_${id} {
    0%   { transform: scale(1);   opacity: 0.55; }
    100% { transform: scale(2.6); opacity: 0; }
  }

  /* ── Tooltip ─────────────────────────────────────────────────────────── */
  #${id} .glass-status-tooltip {
    position                : absolute;
    bottom                  : calc(100% + 10px);
    left                    : 50%;
    -webkit-transform       : translateX(-50%) translateY(4px);
            transform       : translateX(-50%) translateY(4px);
    min-width                : max-content;
    max-width                 : min(220px, 60vw);
    padding                   : 7px 12px;
    -webkit-border-radius     : 10px;
            border-radius     : 10px;
    background                 : rgba(20, 12, 34, 0.85);
    -webkit-backdrop-filter    : blur(14px);
            backdrop-filter    : blur(14px);
    border                      : 1px solid rgba(255,255,255,0.16);
    -webkit-box-shadow          : 0 10px 26px rgba(0,0,0,0.4);
            box-shadow          : 0 10px 26px rgba(0,0,0,0.4);
    color                        : #fff;
    font-size                    : 0.76rem;
    font-weight                  : 500;
    text-align                    : center;
    white-space                   : nowrap;
    pointer-events                 : none;
    z-index                        : 20;

    opacity                        : 0;
    visibility                      : hidden;
    -webkit-transition: opacity 0.16s ease, -webkit-transform 0.16s ease, visibility 0s linear 0.16s;
            transition: opacity 0.16s ease, transform 0.16s ease, visibility 0s linear 0.16s;
  }
  #${id} .glass-status-tooltip::after {
    content   : "";
    position  : absolute;
    top       : 100%;
    left      : 50%;
    -webkit-transform: translateX(-50%);
            transform: translateX(-50%);
    border    : 5px solid transparent;
    border-top-color: rgba(20, 12, 34, 0.85);
  }
  #${id} .glass-status-tooltip.align-left { left: 0; -webkit-transform: translateX(0) translateY(4px); transform: translateX(0) translateY(4px); }
  #${id} .glass-status-tooltip.align-left::after { left: 14px; -webkit-transform: none; transform: none; }
  #${id} .glass-status-tooltip.align-right { left: auto; right: 0; -webkit-transform: translateX(0) translateY(4px); transform: translateX(0) translateY(4px); }
  #${id} .glass-status-tooltip.align-right::after { left: auto; right: 14px; -webkit-transform: none; transform: none; }

  #${id}.glass-status-wrap.is-tooltip-open .glass-status-tooltip {
    opacity     : 1;
    visibility   : visible;
    -webkit-transform: translateX(-50%) translateY(0);
            transform: translateX(-50%) translateY(0);
    -webkit-transition: opacity 0.16s ease, -webkit-transform 0.16s ease;
            transition: opacity 0.16s ease, transform 0.16s ease;
  }
  #${id}.glass-status-wrap.is-tooltip-open .glass-status-tooltip.align-left,
  #${id}.glass-status-wrap.is-tooltip-open .glass-status-tooltip.align-right {
    -webkit-transform: translateX(0) translateY(0);
            transform: translateX(0) translateY(0);
  }

  @media (prefers-reduced-motion: reduce) {
    #${id} .glass-status-dot::before,
    #${id} .glass-status-dot::after { -webkit-animation: none; animation: none; }
    #${id} .glass-status-tooltip { transition: opacity 0.16s ease; }
  }

  /* Responsive: shrink the tooltip's max-width a little further on very
     narrow viewports so it never crowds against the screen edge. */
  @media (max-width: 420px) {
    #${id} .glass-status-tooltip { max-width: min(180px, 70vw); font-size: 0.72rem; }
  }
</style>

<span id="${id}" class="glass-status-wrap${extra ? ' ' + extra : ''}" data-glass-status="${id}">
  <span class="glass-status-halo" data-glass-status-trigger="${id}"
        ${interactive ? 'tabindex="0" role="status"' : 'role="status"'}
        aria-label="${interactive ? renderedLabel : status.capitalize()}">
    <span class="glass-status-dot${pulse ? ' has-pulse' : ''}" data-glass-status-dot="${id}"></span>
  </span>
  ${interactive ? "<span class=\"glass-status-tooltip\" data-glass-status-tooltip=\"${id}\" role=\"tooltip\">${renderedLabel}</span>" : ''}
</span>

<script>
(function () {
  // ── Shared global API: window.glassStatus.set(id, status, label) ─────────
  // Reused across every glass:onlineStatus instance on the page (typically
  // several — one per online contact) — defined once (first instance
  // wins), every subsequent instance just registers into the registry.
  window.glassStatus = window.glassStatus || (function () {
    var registry = {}; // id -> { status, label, statusColors }
    var openId = null;

    function closeTooltip(id) {
      var wrap = document.querySelector('[data-glass-status="' + id + '"]');
      if (wrap) wrap.classList.remove('is-tooltip-open');
      if (openId === id) openId = null;
    }

    function openTooltip(id) {
      if (openId && openId !== id) closeTooltip(openId);
      var wrap = document.querySelector('[data-glass-status="' + id + '"]');
      var tooltip = document.querySelector('[data-glass-status-tooltip="' + id + '"]');
      if (!wrap || !tooltip) return;

      wrap.classList.add('is-tooltip-open');
      openId = id;

      // Responsive edge-detection: flip the tooltip to left/right-aligned
      // if a centered tooltip would overflow the viewport near an edge.
      tooltip.classList.remove('align-left', 'align-right');
      var rect = tooltip.getBoundingClientRect();
      var margin = 8;
      if (rect.left < margin) {
        tooltip.classList.add('align-left');
      } else if (rect.right > (window.innerWidth - margin)) {
        tooltip.classList.add('align-right');
      }
    }

    function updateDotColor(id) {
      var entry = registry[id];
      var wrap = document.querySelector('[data-glass-status="' + id + '"]');
      var dot = document.querySelector('[data-glass-status-dot="' + id + '"]');
      var halo = document.querySelector('[data-glass-status-trigger="' + id + '"]');
      if (!entry || !wrap || !dot) return;

      var color = entry.statusColors[entry.status] || entry.statusColors.offline;
      wrap.style.setProperty('--status-color', color);

      var shouldPulse = entry.status === 'online';
      if (dot) dot.classList.toggle('has-pulse', shouldPulse);

      if (halo) halo.setAttribute('aria-label', entry.label || (entry.status.charAt(0).toUpperCase() + entry.status.slice(1)));
    }

    // Delegate outside-click / Escape handling once, globally, rather than
    // per-instance — cheaper and avoids listener buildup with many dots
    // (e.g. an online-contacts list).
    document.addEventListener('click', function (e) {
      if (!openId) return;
      var wrap = document.querySelector('[data-glass-status="' + openId + '"]');
      if (wrap && !wrap.contains(e.target)) closeTooltip(openId);
    });
    document.addEventListener('keydown', function (e) {
      if (e.key === 'Escape' && openId) closeTooltip(openId);
    });

    return {
      /** Called once per glass:onlineStatus tag instance to register its config. */
      _register: function (id, cfg) {
        registry[id] = { status: cfg.status, label: cfg.label, statusColors: cfg.statusColors };

        var trigger = document.querySelector('[data-glass-status-trigger="' + id + '"]');
        var hasTooltip = !!document.querySelector('[data-glass-status-tooltip="' + id + '"]');
        if (!trigger || !hasTooltip) return;

        // Touch/click: toggle open/closed.
        trigger.addEventListener('click', function (e) {
          e.stopPropagation();
          if (openId === id) { closeTooltip(id); } else { openTooltip(id); }
        });
        // Mouse hover: show/hide directly (desktop convenience, doesn't
        // fight the tap-toggle behavior touch devices rely on).
        trigger.addEventListener('mouseenter', function () { openTooltip(id); });
        trigger.addEventListener('mouseleave', function () { if (openId === id) closeTooltip(id); });
        // Keyboard focus.
        trigger.addEventListener('focus', function () { openTooltip(id); });
        trigger.addEventListener('blur', function () { if (openId === id) closeTooltip(id); });
      },
      /** Updates status (and optionally the tooltip label) for a registered
       *  instance. Pass label as undefined/omit it to leave the existing
       *  label unchanged. */
      set: function (id, status, label) {
        var entry = registry[id];
        if (!entry) { if (window.console) console.warn('glassStatus.set: no status dot found for id "' + id + '"'); return; }
        entry.status = status;
        if (label !== undefined) {
          entry.label = label;
          var tooltip = document.querySelector('[data-glass-status-tooltip="' + id + '"]');
          if (tooltip) tooltip.textContent = label;
        }
        updateDotColor(id);
      },
      /** Returns { status, label } for a registered instance, or null. */
      get: function (id) {
        var entry = registry[id];
        return entry ? { status: entry.status, label: entry.label } : null;
      }
    };
  })();

  window.glassStatus._register('${id}', {
    status: '${status}',
    label: '${renderedLabel.replace("'", "\\'")}',
    statusColors: {
      online: '${onlineColor}',
      away: '${awayColor}',
      busy: '${busyColor}',
      offline: '${offlineColor}'
    }
  });
})();
</script>
"""
  }
}
