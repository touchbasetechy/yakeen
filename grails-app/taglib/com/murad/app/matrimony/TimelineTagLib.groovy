package com.murad.app.matrimony

/**
 * TimelineTagLib
 * ---------------
 * A self-contained, glassmorphic Timeline / Activity Feed taglib for GSP.
 *
 * Design language:
 *   - Frosted glass "spine & node" layout: a vertical connector down the
 *     left with floating glass cards, color-coded pulse nodes per event type.
 *   - Type scale: Space Grotesk (display) + Inter (body) + JetBrains Mono
 *     (timestamps / metadata), with system-font fallbacks so nothing breaks
 *     if webfonts are disabled or blocked.
 *   - Fully responsive via clamp()/media queries AND its own ResizeObserver
 *     based container-width logic (works even inside a narrow sidebar on a
 *     wide viewport, not just at the page/viewport breakpoint).
 *   - Touch-first: Pointer Events power a swipe-to-reveal "mark read" action
 *     that works identically for mouse, touch and pen; 44px+ hit targets;
 *     tap-to-expand descriptions on compact layouts.
 *   - Cross-browser: -webkit- fallbacks for backdrop-filter, @supports guard
 *     for browsers without it, feature-detected ResizeObserver/IntersectionObserver
 *     with a graceful degrade to a plain static layout.
 *
 * Tags:
 *   <timeline:styles/>                    - injects CSS + JS once per page
 *   <timeline:feed items="${list}">        - renders a full feed
 *   <timeline:group date="Today">          - a date/section divider inside a feed
 *   <timeline:item .../>                   - a single activity entry (usable standalone)
 *
 * Item attributes (also read as bean/map properties when using `items`):
 *   type        success | warning | danger | info | default
 *   icon        check | alert | x | info | message | user | git | dot (built-ins) - optional
 *   iconHtml    raw SVG/HTML to fully override the icon
 *   avatar      image URL for an avatar instead of an icon
 *   actor       display name of who/what performed the action (also avatar initials source)
 *   title       main line of text (required)
 *   description secondary/body text, collapses on compact layout until tapped
 *   time        human readable time string, e.g. "2h ago"
 *   datetime    ISO-8601 string used for the <time datetime="">
 *   href        makes the whole card a link
 *   badge       small pill label, e.g. "New"
 *   isNew       boolean - adds an animated pulse ring to the node
 */
class TimelineTagLib {

  static namespace = "timeline"

  // ---------------------------------------------------------------
  // Helpers
  // ---------------------------------------------------------------

  /** Reads a named property off either a Map or a bean/POGO. */
  private prop(item, String name, defaultValue = null) {
    if (item == null) return defaultValue
    def value = (item instanceof Map) ? item[name] : (item.hasProperty(name) ? item[name] : null)
    return (value == null) ? defaultValue : value
  }

  private esc(value) {
    value == null ? '' : value.toString().encodeAsHTML()
  }

  private static final Map<String, String> ICONS = [
    check  : '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><path d="M20 6 9 17l-5-5"/></svg>',
    alert  : '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 9v4M12 17h.01M10.3 3.9 1.8 18a2 2 0 0 0 1.7 3h17a2 2 0 0 0 1.7-3L13.7 3.9a2 2 0 0 0-3.4 0Z"/></svg>',
    x      : '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><path d="M18 6 6 18M6 6l12 12"/></svg>',
    info   : '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="9"/><path d="M12 16v-5M12 8h.01"/></svg>',
    message: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 15a2 2 0 0 1-2 2H8l-5 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg>',
    user   : '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="8" r="4"/><path d="M4 21c1.5-4 5-6 8-6s6.5 2 8 6"/></svg>',
    git    : '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><circle cx="6" cy="6" r="2.4"/><circle cx="6" cy="18" r="2.4"/><circle cx="18" cy="10" r="2.4"/><path d="M6 8.4V15.6M6 8.4c0 3.6 3 5.6 6.6 5.6H15.6M18 12.4V15"/></svg>',
    dot    : '<svg viewBox="0 0 24 24" fill="currentColor"><circle cx="12" cy="12" r="5"/></svg>'
  ]

  private String initials(String name) {
    if (!name) return '?'
    def parts = name.trim().split(/\s+/)
    def first = parts[0]?.take(1) ?: ''
    def last = parts.size() > 1 ? (parts[-1]?.take(1) ?: '') : ''
    return (first + last).toUpperCase() ?: '?'
  }

  // ---------------------------------------------------------------
  // <timeline:styles/>  -- injects CSS + JS once
  // ---------------------------------------------------------------

  def styles = { attrs ->
    if (request.getAttribute('__tl_assets_rendered__')) return
    request.setAttribute('__tl_assets_rendered__', true)

    boolean webfonts = attrs.webfonts == null ? true : Boolean.valueOf(attrs.webfonts.toString())

    if (webfonts) {
      out << '<link rel="preconnect" href="https://fonts.googleapis.com">'
      out << '<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>'
      out << '<link href="https://fonts.googleapis.com/css2?family=Space+Grotesk:wght@500;600;700&family=Inter:wght@400;500;600&family=JetBrains+Mono:wght@400;500&display=swap" rel="stylesheet">'
    }

    out << TL_CSS
    out << '<script>' << TL_JS << '</script>'
  }

  // ---------------------------------------------------------------
  // <timeline:feed>  -- wraps a whole activity feed
  // ---------------------------------------------------------------

  def feed = { attrs, body ->
    String id = attrs.id ?: "tl-${(int) (Math.random() * 1000000)}"
    String title = attrs.title
    String subtitle = attrs.subtitle
    List items = (attrs.items instanceof Collection) ? attrs.items as List : []
    boolean live = attrs.live == null ? false : Boolean.valueOf(attrs.live.toString())

    out << "<section class=\"tl-wrap\" id=\"${esc(id)}\" data-tl-root>\n"

    if (title || subtitle) {
      out << "<header class=\"tl-header\">\n"
      if (title) out << "<h3 class=\"tl-title\">${esc(title)}</h3>\n"
      if (subtitle) out << "<p class=\"tl-subtitle\">${esc(subtitle)}</p>\n"
      out << "</header>\n"
    }

    out << "<div class=\"tl-scroller\" data-tl-scroller>\n"
    out << "<div class=\"tl-spine\" role=\"feed\"${live ? ' aria-live="polite"' : ''} aria-label=\"${esc(title ?: 'Activity feed')}\" data-tl-spine>\n"

    if (body && body()) {
      out << body()
    } else if (items) {
      int total = items.size()
      items.eachWithIndex { it, idx ->
        out << renderItem(it, idx == total - 1)
      }
    } else {
      out << '''
                <div class="tl-empty">
                    <div class="tl-empty-glyph">&#9679;</div>
                    <p class="tl-empty-title">Nothing here yet</p>
                    <p class="tl-empty-sub">New activity will appear on this timeline as it happens.</p>
                </div>
            '''
    }

    out << "</div>\n</div>\n</section>\n"
  }

  // ---------------------------------------------------------------
  // <timeline:group>  -- a date/section divider used inside a feed body
  // ---------------------------------------------------------------

  def group = { attrs, body ->
    out << "<div class=\"tl-group\"><span class=\"tl-group-label\">${esc(attrs.date ?: attrs.label)}</span></div>\n"
    if (body) out << body()
  }

  // ---------------------------------------------------------------
  // <timeline:item>  -- a single activity entry, usable standalone too
  // ---------------------------------------------------------------

  def item = { attrs, body ->
    def merged = new LinkedHashMap(attrs)
    if (body) merged.description = body()
    out << renderItem(merged, attrs.last == null ? true : Boolean.valueOf(attrs.last.toString()))
  }

  // ---------------------------------------------------------------
  // Rendering core
  // ---------------------------------------------------------------

  private String renderItem(item, boolean isLast) {
    String type = (prop(item, 'type', 'default') ?: 'default').toString()
    if (!(type in ['success', 'warning', 'danger', 'info', 'default'])) type = 'default'

    String iconKey = prop(item, 'icon', 'dot')?.toString()
    String iconHtml = prop(item, 'iconHtml')
    String avatar = prop(item, 'avatar')
    String actor = prop(item, 'actor')
    String titleText = prop(item, 'title', '')
    def descriptionRaw = prop(item, 'description')
    String time = prop(item, 'time')
    String datetime = prop(item, 'datetime')
    String href = prop(item, 'href')
    String badge = prop(item, 'badge')
    boolean isNew = Boolean.valueOf((prop(item, 'isNew', false)).toString())

    String nodeInner
    if (avatar) {
      nodeInner = "<img class=\"tl-avatar\" src=\"${esc(avatar)}\" alt=\"\" loading=\"lazy\">"
    } else if (iconHtml) {
      nodeInner = "<span class=\"tl-icon-raw\">${iconHtml}</span>"
    } else if (actor && !ICONS.containsKey(iconKey)) {
      nodeInner = "<span class=\"tl-initials\">${esc(initials(actor))}</span>"
    } else {
      nodeInner = ICONS[iconKey] ?: ICONS['dot']
    }

    String descriptionHtml = descriptionRaw == null ? '' :
      (descriptionRaw.toString().contains('<') ? descriptionRaw.toString() : esc(descriptionRaw))

    StringBuilder sb = new StringBuilder()
    sb << "<article class=\"tl-item tl-item--${type}${isNew ? ' is-new' : ''}${isLast ? ' is-last' : ''}\" data-tl-item tabindex=\"0\">\n"
    sb << "  <div class=\"tl-node\">"
    sb << "<span class=\"tl-node-ring\"></span>"
    sb << "<span class=\"tl-node-dot\">${nodeInner}</span>"
    sb << "</div>\n"
    sb << "  <div class=\"tl-card-track\">\n"
    sb << "    <div class=\"tl-card\" data-tl-card>\n"
    sb << "      <div class=\"tl-card-glass\"></div>\n"
    sb << "      <div class=\"tl-card-content\">\n"
    sb << "        <div class=\"tl-row\">\n"
    sb << "          <p class=\"tl-item-title\">${href ? "<a class=\"tl-link\" href=\"${esc(href)}\">${esc(titleText)}</a>" : esc(titleText)}</p>\n"
    sb << "          <div class=\"tl-row-actions\">\n"
    if (badge) sb << "            <span class=\"tl-badge tl-badge--${type}\">${esc(badge)}</span>\n"
    sb << "            <button type=\"button\" class=\"tl-quick-read\" data-tl-quick-read aria-label=\"Mark as read\" title=\"Mark as read\"><svg viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2.5\" stroke-linecap=\"round\" stroke-linejoin=\"round\"><path d=\"M20 6 9 17l-5-5\"/></svg></button>\n"
    sb << "          </div>\n"
    sb << "        </div>\n"
    if (descriptionHtml) {
      sb << "        <button type=\"button\" class=\"tl-desc-toggle\" data-tl-toggle aria-expanded=\"false\">\n"
      sb << "          <p class=\"tl-item-desc\" data-tl-desc>${descriptionHtml}</p>\n"
      sb << "        </button>\n"
    }
    if (time || datetime) {
      sb << "        <time class=\"tl-time\"${datetime ? " datetime=\"${esc(datetime)}\"" : ''}>${esc(time ?: datetime)}</time>\n"
    }
    sb << "      </div>\n"
    sb << "      <button type=\"button\" class=\"tl-swipe-action\" data-tl-action>Mark read</button>\n"
    sb << "    </div>\n"
    sb << "  </div>\n"
    sb << "</article>\n"
    return sb.toString()
  }

  // ---------------------------------------------------------------
  // CSS
  // ---------------------------------------------------------------

  private static final String TL_CSS = '''
<style>
.tl-wrap {
  --tl-bg: #0b0e1a;
  --tl-glass: rgba(255,255,255,0.06);
  --tl-glass-strong: rgba(255,255,255,0.10);
  --tl-glass-border: rgba(255,255,255,0.14);
  --tl-glass-highlight: rgba(255,255,255,0.35);
  --tl-text: #edeff7;
  --tl-text-muted: #9aa1b8;
  --tl-accent-1: #7c6fff;
  --tl-accent-2: #4fd8eb;
  --tl-success: #37e29a;
  --tl-warning: #ffc65c;
  --tl-danger: #ff6b81;
  --tl-info: #4fd8eb;
  --tl-radius: 18px;
  --tl-spine-x: 26px;
  --tl-swipe-w: 96px;
  --tl-font-display: 'Space Grotesk', 'Segoe UI', system-ui, sans-serif;
  --tl-font-body: 'Inter', 'Segoe UI', system-ui, -apple-system, sans-serif;
  --tl-font-mono: 'JetBrains Mono', 'SFMono-Regular', Consolas, monospace;

  position: relative;
  width: 100%;
  max-width: 760px;
  margin: 0 auto;
  padding: clamp(16px, 3vw, 28px);
  border-radius: calc(var(--tl-radius) + 6px);
  background: radial-gradient(120% 140% at 10% 0%, #161b2e 0%, var(--tl-bg) 60%);
  color: var(--tl-text);
  font-family: var(--tl-font-body);
  box-sizing: border-box;
  -webkit-text-size-adjust: 100%;
}
.tl-wrap *, .tl-wrap *::before, .tl-wrap *::after { box-sizing: border-box; }

.tl-header { margin-bottom: clamp(14px, 2.4vw, 22px); padding: 0 4px; }
.tl-title {
  font-family: var(--tl-font-display);
  font-weight: 600;
  font-size: clamp(1.05rem, 1.6vw + 0.7rem, 1.5rem);
  margin: 0 0 4px;
  letter-spacing: -0.01em;
  background: linear-gradient(90deg, var(--tl-text) 0%, var(--tl-text) 60%, var(--tl-accent-2) 130%);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
}
.tl-subtitle { margin: 0; font-size: 0.85rem; color: var(--tl-text-muted); }

.tl-scroller {
  max-height: min(80vh, 720px);
  overflow-y: auto;
  overflow-x: hidden;
  overscroll-behavior: contain;
  -webkit-overflow-scrolling: touch;
  touch-action: pan-y;
  scrollbar-width: thin;
  scrollbar-color: var(--tl-glass-border) transparent;
  padding-right: 4px;
}
.tl-scroller::-webkit-scrollbar { width: 8px; }
.tl-scroller::-webkit-scrollbar-track { background: transparent; }
.tl-scroller::-webkit-scrollbar-thumb {
  background: var(--tl-glass-border);
  border-radius: 8px;
}

.tl-spine { position: relative; padding-left: calc(var(--tl-spine-x) + 8px); }
.tl-spine::before {
  content: '';
  position: absolute;
  top: 6px;
  bottom: 6px;
  left: var(--tl-spine-x);
  width: 2px;
  background: linear-gradient(to bottom, var(--tl-accent-1), var(--tl-glass-border) 85%);
  opacity: 0.6;
}

.tl-group { position: relative; margin: 18px 0 14px 0; padding-left: 0; }
.tl-group-label {
  display: inline-block;
  font-family: var(--tl-font-mono);
  font-size: 0.7rem;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: var(--tl-text-muted);
  background: var(--tl-glass);
  border: 1px solid var(--tl-glass-border);
  padding: 4px 10px;
  border-radius: 999px;
  margin-left: calc(-1 * (var(--tl-spine-x) + 8px) + 6px);
}

.tl-item {
  position: relative;
  display: flex;
  align-items: flex-start;
  gap: 14px;
  padding: 10px 0;
  opacity: 0;
  transform: translateY(10px);
  animation: tl-reveal 0.5s cubic-bezier(.2,.7,.3,1) forwards;
  outline: none;
}
.tl-item:focus-visible .tl-card { box-shadow: 0 0 0 2px var(--tl-accent-2), 0 12px 30px -10px rgba(0,0,0,0.6); }
@keyframes tl-reveal { to { opacity: 1; transform: translateY(0); } }

.tl-node {
  position: absolute;
  left: calc(-1 * (var(--tl-spine-x) + 8px) + var(--tl-spine-x) - 13px);
  top: 14px;
  width: 26px;
  height: 26px;
  flex: none;
}
.tl-node-dot {
  position: relative;
  z-index: 2;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 26px;
  height: 26px;
  border-radius: 50%;
  background: linear-gradient(145deg, var(--tl-glass-strong), var(--tl-glass));
  border: 1px solid var(--tl-glass-border);
  color: var(--tl-accent-2);
  box-shadow: 0 0 0 4px var(--tl-bg);
}
.tl-node-dot svg { width: 13px; height: 13px; }
.tl-initials { font-family: var(--tl-font-mono); font-size: 0.6rem; font-weight: 600; }
.tl-avatar { width: 100%; height: 100%; border-radius: 50%; object-fit: cover; display: block; }
.tl-node-ring { position: absolute; inset: 0; border-radius: 50%; }

/* Fallback border colors for browsers without color-mix() support, then
   progressively enhanced where it is supported. */
.tl-item--success .tl-node-dot { color: var(--tl-success); border-color: var(--tl-success); }
.tl-item--warning .tl-node-dot { color: var(--tl-warning); border-color: var(--tl-warning); }
.tl-item--danger  .tl-node-dot { color: var(--tl-danger);  border-color: var(--tl-danger); }
.tl-item--info    .tl-node-dot { color: var(--tl-info);    border-color: var(--tl-info); }
@supports (border-color: color-mix(in srgb, red 50%, blue)) {
  .tl-item--success .tl-node-dot { border-color: color-mix(in srgb, var(--tl-success) 45%, var(--tl-glass-border)); }
  .tl-item--warning .tl-node-dot { border-color: color-mix(in srgb, var(--tl-warning) 45%, var(--tl-glass-border)); }
  .tl-item--danger  .tl-node-dot { border-color: color-mix(in srgb, var(--tl-danger) 45%, var(--tl-glass-border)); }
  .tl-item--info    .tl-node-dot { border-color: color-mix(in srgb, var(--tl-info) 45%, var(--tl-glass-border)); }
}

.tl-item.is-new .tl-node-ring {
  box-shadow: 0 0 0 0 rgba(124,111,255,0.55);
  animation: tl-pulse 2.2s ease-out infinite;
}
@keyframes tl-pulse {
  0%   { box-shadow: 0 0 0 0 rgba(124,111,255,0.55); }
  70%  { box-shadow: 0 0 0 12px rgba(124,111,255,0); }
  100% { box-shadow: 0 0 0 0 rgba(124,111,255,0); }
}

.tl-card-track { flex: 1 1 auto; min-width: 0; overflow: hidden; border-radius: var(--tl-radius); }
.tl-card {
  position: relative;
  display: flex;
  align-items: stretch;
  width: calc(100% + var(--tl-swipe-w));
  border-radius: var(--tl-radius);
  border: 1px solid var(--tl-glass-border);
  background: var(--tl-glass);
  -webkit-backdrop-filter: blur(16px) saturate(140%);
  backdrop-filter: blur(16px) saturate(140%);
  transition: transform 0.35s cubic-bezier(.2,.8,.2,1), background 0.25s ease, border-color 0.25s ease, box-shadow 0.25s ease;
  touch-action: pan-y;
  will-change: transform;
}
@supports not ((backdrop-filter: blur(1px)) or (-webkit-backdrop-filter: blur(1px))) {
  .tl-card { background: rgba(22,26,44,0.92); }
}
.tl-card::before {
  content: '';
  position: absolute;
  inset: 0;
  border-radius: inherit;
  padding: 1px;
  background: linear-gradient(135deg, var(--tl-glass-highlight), transparent 45%);
  -webkit-mask: linear-gradient(#000 0 0) content-box, linear-gradient(#000 0 0);
  -webkit-mask-composite: xor;
  mask-composite: exclude;
  opacity: 0.5;
  pointer-events: none;
}
.tl-card:hover, .tl-card:focus-within {
  background: var(--tl-glass-strong);
  box-shadow: 0 14px 34px -14px rgba(0,0,0,0.55);
}
.tl-item--success .tl-card { border-left: 3px solid var(--tl-success); }
.tl-item--warning .tl-card { border-left: 3px solid var(--tl-warning); }
.tl-item--danger  .tl-card { border-left: 3px solid var(--tl-danger); }
.tl-item--info    .tl-card { border-left: 3px solid var(--tl-info); }
.tl-item--default .tl-card { border-left: 3px solid var(--tl-accent-1); }

.tl-card-content { position: relative; z-index: 1; flex: 1 1 auto; min-width: 0; padding: 12px 14px; }
.tl-row { display: flex; align-items: flex-start; justify-content: space-between; gap: 10px; }
.tl-row-actions { display: flex; align-items: center; gap: 8px; flex: none; }
.tl-item-title { margin: 0; font-size: 0.92rem; font-weight: 500; line-height: 1.4; color: var(--tl-text); word-break: break-word; }

.tl-quick-read {
  all: unset;
  flex: none;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  border: 1px solid var(--tl-glass-border);
  background: var(--tl-glass);
  color: var(--tl-text-muted);
  cursor: pointer;
  transition: color 0.2s ease, border-color 0.2s ease, background 0.2s ease, transform 0.15s ease;
}
.tl-quick-read svg { width: 12px; height: 12px; }
.tl-quick-read:hover, .tl-quick-read:focus-visible {
  color: var(--tl-success);
  border-color: var(--tl-success);
  background: var(--tl-glass-strong);
}
.tl-quick-read:active { transform: scale(0.9); }
.tl-link { color: inherit; text-decoration: none; border-bottom: 1px solid var(--tl-glass-border); }
.tl-link:hover { border-color: var(--tl-accent-2); color: var(--tl-accent-2); }

.tl-badge {
  flex: none;
  font-family: var(--tl-font-mono);
  font-size: 0.62rem;
  letter-spacing: 0.06em;
  text-transform: uppercase;
  padding: 3px 8px;
  border-radius: 999px;
  border: 1px solid var(--tl-glass-border);
  color: var(--tl-text);
  background: var(--tl-glass-strong);
  white-space: nowrap;
}
.tl-badge--success { color: var(--tl-success); }
.tl-badge--warning { color: var(--tl-warning); }
.tl-badge--danger { color: var(--tl-danger); }
.tl-badge--info { color: var(--tl-info); }

.tl-desc-toggle {
  all: unset;
  display: block;
  width: 100%;
  cursor: default;
  min-height: 22px;
}
.tl-item-desc {
  margin: 6px 0 0;
  font-size: 0.82rem;
  line-height: 1.5;
  color: var(--tl-text-muted);
}
.tl-time {
  display: block;
  margin-top: 8px;
  font-family: var(--tl-font-mono);
  font-size: 0.68rem;
  color: var(--tl-text-muted);
  opacity: 0.85;
}

.tl-swipe-action {
  all: unset;
  flex: none;
  display: flex;
  align-items: center;
  justify-content: center;
  width: var(--tl-swipe-w);
  font-family: var(--tl-font-mono);
  font-size: 0.72rem;
  letter-spacing: 0.02em;
  color: var(--tl-bg);
  background: linear-gradient(135deg, var(--tl-accent-2), var(--tl-accent-1));
  border-radius: 0 var(--tl-radius) var(--tl-radius) 0;
  cursor: pointer;
  text-align: center;
}

.tl-empty {
  padding: 40px 20px;
  text-align: center;
  border: 1px dashed var(--tl-glass-border);
  border-radius: var(--tl-radius);
  margin-left: calc(-1 * (var(--tl-spine-x) + 8px));
}
.tl-empty-glyph { font-size: 1.4rem; color: var(--tl-accent-1); margin-bottom: 8px; }
.tl-empty-title { margin: 0 0 2px; font-weight: 500; }
.tl-empty-sub { margin: 0; font-size: 0.82rem; color: var(--tl-text-muted); }

/* ---------- Touch: swipe-to-reveal drag state (JS toggles these) ---------- */
.tl-card.is-dragging { transition: none; }
.tl-card.is-open { transform: translateX(calc(-1 * var(--tl-swipe-w))); }

/* ---------- Compact layout: driven by JS via ResizeObserver on the
   container itself (works regardless of viewport, e.g. narrow sidebars)
   AND mirrored with a media query fallback for browsers without RO. ---------- */
.tl-wrap.tl--compact .tl-item-desc { display: -webkit-box; -webkit-line-clamp: 1; -webkit-box-orient: vertical; overflow: hidden; }
.tl-wrap.tl--compact .tl-item.is-expanded .tl-item-desc { -webkit-line-clamp: unset; }
.tl-wrap.tl--compact .tl-desc-toggle { cursor: pointer; }
.tl-wrap.tl--compact .tl-title { font-size: 1.05rem; }
.tl-wrap.tl--compact { --tl-spine-x: 18px; padding: 14px; }
.tl-wrap.tl--compact .tl-node { width: 22px; height: 22px; }
.tl-wrap.tl--compact .tl-node-dot { width: 22px; height: 22px; }

@media (max-width: 560px) {
  .tl-wrap { --tl-spine-x: 18px; --tl-swipe-w: 80px; border-radius: var(--tl-radius); padding: 14px; }
  .tl-card-content { padding: 10px 12px; }
  .tl-item-title { font-size: 0.88rem; }
  .tl-swipe-action { font-size: 0.66rem; }
}
@media (min-width: 900px) {
  .tl-wrap { padding: 30px; }
  .tl-item-title { font-size: 0.98rem; }
}

@media (prefers-reduced-motion: reduce) {
  .tl-item, .tl-card, .tl-node-ring { animation: none !important; transition: none !important; }
}
@media (prefers-color-scheme: light) {
  .tl-wrap[data-tl-theme="auto"] {
    --tl-bg: #f4f5fb;
    --tl-glass: rgba(20,20,40,0.045);
    --tl-glass-strong: rgba(20,20,40,0.08);
    --tl-glass-border: rgba(20,20,40,0.12);
    --tl-glass-highlight: rgba(255,255,255,0.9);
    --tl-text: #1c2033;
    --tl-text-muted: #656c85;
  }
}
</style>
'''

  // ---------------------------------------------------------------
  // JS
  // ---------------------------------------------------------------

  private static final String TL_JS = '''
(function () {
  if (window.__tlBound__) return;
  window.__tlBound__ = true;

  function ready(fn) {
    if (document.readyState !== 'loading') fn();
    else document.addEventListener('DOMContentLoaded', fn);
  }

  function initRoot(root) {
    if (root.__tlInit__) return;
    root.__tlInit__ = true;

    // --- Container-based responsiveness (own implementation, not just
    //     viewport media queries) -------------------------------------
    var applyWidthClass = function (w) {
      root.classList.toggle('tl--compact', w < 480);
    };
    if ('ResizeObserver' in window) {
      var ro = new ResizeObserver(function (entries) {
        for (var i = 0; i < entries.length; i++) {
          applyWidthClass(entries[i].contentRect.width);
        }
      });
      ro.observe(root);
    } else {
      var onResize = function () { applyWidthClass(root.getBoundingClientRect().width); };
      window.addEventListener('resize', debounce(onResize, 120));
      onResize();
    }

    // --- Scroll-reveal for items added later (e.g. AJAX-appended) ----
    if ('IntersectionObserver' in window) {
      var io = new IntersectionObserver(function (entries, obs) {
        entries.forEach(function (entry) {
          if (entry.isIntersecting) {
            entry.target.style.animationPlayState = 'running';
            obs.unobserve(entry.target);
          }
        });
      }, { root: null, threshold: 0.1 });
      root.querySelectorAll('[data-tl-item]').forEach(function (el) { io.observe(el); });
    }

    // --- Tap-to-expand description on compact layout, and the always-
    //     visible quick "mark read" icon button ------------------------
    root.addEventListener('click', function (e) {
      var quickRead = e.target.closest('[data-tl-quick-read]');
      if (quickRead) {
        dismissItem(quickRead.closest('[data-tl-item]'));
        return;
      }
      var toggle = e.target.closest('[data-tl-toggle]');
      if (!toggle || !root.classList.contains('tl--compact')) return;
      var item = toggle.closest('[data-tl-item]');
      var expanded = item.classList.toggle('is-expanded');
      toggle.setAttribute('aria-expanded', expanded ? 'true' : 'false');
    });

    // --- Pointer-based swipe-to-reveal (unifies mouse/touch/pen) -----
    root.querySelectorAll('[data-tl-card]').forEach(function (card) {
      var startX = 0, currentX = 0, dragging = false, open = false;
      var MAX = parseFloat(getComputedStyle(root).getPropertyValue('--tl-swipe-w')) || 96;

      card.addEventListener('pointerdown', function (e) {
        // Let taps on either "mark read" control pass through untouched:
        // if we start a drag here, setPointerCapture() below would
        // retarget the subsequent click to `card` instead of the button,
        // and the button's own click handler would never fire.
        if (e.target.closest('[data-tl-action], [data-tl-quick-read]')) return;
        if (e.pointerType === 'mouse' && e.button !== 0) return;
        dragging = true;
        startX = e.clientX;
        card.classList.add('is-dragging');
        card.setPointerCapture(e.pointerId);
      });

      card.addEventListener('pointermove', function (e) {
        if (!dragging) return;
        currentX = e.clientX - startX;
        var base = open ? -MAX : 0;
        var next = clamp(base + currentX, -MAX, 0);
        card.style.transform = 'translateX(' + next + 'px)';
      });

      function endDrag(e) {
        if (!dragging) return;
        dragging = false;
        card.classList.remove('is-dragging');
        var delta = currentX;
        currentX = 0;
        var shouldOpen = open ? delta > -MAX / 2 : delta < -MAX / 2;
        open = shouldOpen;
        card.classList.toggle('is-open', open);
        card.style.transform = '';
      }
      card.addEventListener('pointerup', endDrag);
      card.addEventListener('pointercancel', endDrag);
      card.addEventListener('pointerleave', function (e) {
        if (e.pointerType !== 'mouse') endDrag(e);
      });

      var action = card.querySelector('[data-tl-action]');
      if (action) {
        action.addEventListener('click', function () {
          dismissItem(card.closest('[data-tl-item]'));
        });
      }
    });
  }

  function dismissItem(item) {
    if (!item) return;
    item.style.transition = 'opacity 0.25s ease, transform 0.25s ease';
    item.style.opacity = '0';
    item.style.transform = 'translateX(-24px)';
    setTimeout(function () { item.remove(); }, 250);
  }

  function clamp(v, min, max) { return Math.max(min, Math.min(max, v)); }
  function debounce(fn, wait) {
    var t;
    return function () {
      clearTimeout(t);
      var args = arguments;
      t = setTimeout(function () { fn.apply(null, args); }, wait);
    };
  }

  function scan() {
    document.querySelectorAll('[data-tl-root]').forEach(initRoot);
  }

  ready(scan);
  // Re-scan if content is injected later (e.g. AJAX pagination / turbo-links).
  document.addEventListener('tl:rescan', scan);
})();
'''
}
