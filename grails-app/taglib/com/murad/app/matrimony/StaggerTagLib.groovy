package com.murad.app.matrimony

/**
 * StaggerTagLib
 * ---------------
 * A self-contained, glassmorphic "Stagger-In" content list taglib for GSP.
 * Cards fade + slide in one after another as they mount, instead of all
 * appearing at once.
 *
 * Design language:
 *   - Frosted glass cards on a radial dark backdrop, thin gradient
 *     hairline borders, soft accent-colored glow per card.
 *   - Type scale: Space Grotesk (display) + Inter (body) + JetBrains Mono
 *     (meta/labels), with system-font fallbacks so nothing breaks if
 *     webfonts are disabled or blocked.
 *   - Stagger reveal: each card observes its own entry into the viewport
 *     (IntersectionObserver). Cards that become visible together (e.g. on
 *     initial mount, or a fast scroll past several at once) are batched
 *     and given an increasing transition-delay so they cascade in, one
 *     after another, capped so long lists never produce absurd delays.
 *     Cards that appear later, spaced out by a slower scroll, reveal
 *     immediately rather than inheriting a stale queue position.
 *   - Progressive enhancement: reveal-hiding is only switched on once the
 *     script confirms it can run (adds a class to <html> synchronously).
 *     With JS disabled/blocked, every card is simply visible - no
 *     noscript workaround needed.
 *   - Fully responsive two ways at once: CSS Grid auto-fill/minmax handles
 *     column count intrinsically, AND a ResizeObserver-driven "compact"
 *     class tightens type/padding/columns based on the *container's* own
 *     width (works inside a narrow sidebar on a wide viewport, not just
 *     at the page/viewport breakpoint), with a media-query mirror as a
 *     fallback for browsers without ResizeObserver.
 *   - Touch-first: 44px+ tap targets, whole-card tap targets via a
 *     stretched link when `href` is set, `touch-action: manipulation`,
 *     `:active` press feedback (since touch has no `:hover`).
 *   - Cross-browser: -webkit- fallbacks for backdrop-filter with an
 * @supports guard for browsers without it; feature-detected
 *     IntersectionObserver/ResizeObserver with graceful degrade to an
 *     instantly-visible, statically-sized layout; prefers-reduced-motion
 *     collapses the stagger to an instant, undelayed reveal.
 *
 * Tags:
 *   <stagger:styles/>                       - injects CSS + JS once per page
 *   <stagger:list items="${list}">           - renders a full stagger grid
 *   <stagger:item .../>                      - a single card (usable standalone)
 *
 * <stagger:list> attributes:
 *   title, subtitle    header text
 *   items              collection of maps/beans (uses item attributes below)
 *   columns            auto | 1 | 2 | 3   (default: auto, via CSS Grid auto-fill)
 *   minColumnWidth     px value used by auto-fill sizing (default: 240)
 *   gap                px value between cards (default: 16)
 *   staggerDelay       ms added per card within a reveal batch (default: 90)
 *   staggerMax         ms cap on any single card's delay (default: 640)
 *
 * Item attributes (also read as bean/map properties when using `items`):
 *   icon         check | star | bolt | doc | image | tag | dot (built-ins) - optional
 *   iconHtml     raw SVG/HTML to fully override the icon
 *   avatar       image URL for an avatar instead of an icon
 *   accent       violet | teal | amber | rose | success | default
 *   title        main line of text (required)
 *   description  secondary/body text
 *   meta         small mono-font caption line, e.g. a date or category
 *   badge        small pill label, e.g. "New"
 *   href         makes the whole card a tap/click target
 */
class StaggerTagLib {

  static namespace = "stagger"

  // ---------------------------------------------------------------
  // Helpers
  // ---------------------------------------------------------------

  private prop(item, String name, defaultValue = null) {
    if (item == null) return defaultValue
    def value = (item instanceof Map) ? item[name] : (item.hasProperty(name) ? item[name] : null)
    return (value == null) ? defaultValue : value
  }

  private esc(value) {
    value == null ? '' : value.toString().encodeAsHTML()
  }

  private static final Map<String, String> ICONS = [
    check: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><path d="M20 6 9 17l-5-5"/></svg>',
    star : '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="m12 2 3.1 6.6 7.1.9-5.3 4.9 1.5 7-6.4-3.6-6.4 3.6 1.5-7-5.3-4.9 7.1-.9z"/></svg>',
    bolt : '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><path d="M13 2 3 14h8l-1 8 10-12h-8z"/></svg>',
    doc  : '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><path d="M14 2v6h6M9 13h6M9 17h6"/></svg>',
    image: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="3" width="18" height="18" rx="2"/><circle cx="9" cy="9" r="2"/><path d="m21 15-5-5L5 21"/></svg>',
    tag  : '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20.6 12.6 12 21.2a2 2 0 0 1-2.8 0l-6.4-6.4a2 2 0 0 1 0-2.8L11.4 3.4a2 2 0 0 1 1.4-.6H19a2 2 0 0 1 2 2v6.4a2 2 0 0 1-.6 1.4Z"/><circle cx="16.5" cy="7.5" r="1.5"/></svg>',
    dot  : '<svg viewBox="0 0 24 24" fill="currentColor"><circle cx="12" cy="12" r="5"/></svg>'
  ]

  private static final List<String> ACCENTS = ['violet', 'teal', 'amber', 'rose', 'success', 'default']

  // ---------------------------------------------------------------
  // <stagger:styles/>  -- injects CSS + JS once
  // ---------------------------------------------------------------

  def styles = { attrs ->
    if (request.getAttribute('__sg_assets_rendered__')) return
    request.setAttribute('__sg_assets_rendered__', true)

    boolean webfonts = attrs.webfonts == null ? true : Boolean.valueOf(attrs.webfonts.toString())

    if (webfonts) {
      out << '<link rel="preconnect" href="https://fonts.googleapis.com">'
      out << '<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>'
      out << '<link href="https://fonts.googleapis.com/css2?family=Space+Grotesk:wght@500;600;700&family=Inter:wght@400;500;600&family=JetBrains+Mono:wght@400;500&display=swap" rel="stylesheet">'
    }

    out << SG_CSS
    out << '<script>' << SG_JS << '</script>'
  }

  // ---------------------------------------------------------------
  // <stagger:list>  -- wraps a whole stagger-reveal grid
  // ---------------------------------------------------------------

  def list = { attrs, body ->
    String id = attrs.id ?: "sg-${(int) (Math.random() * 1000000)}"
    String title = attrs.title
    String subtitle = attrs.subtitle
    List items = (attrs.items instanceof Collection) ? attrs.items as List : []

    String columns = (attrs.columns ?: 'auto').toString()
    int minColumnWidth = (attrs.minColumnWidth ?: 240) as int
    int gap = (attrs.gap ?: 16) as int
    int staggerDelay = (attrs.staggerDelay ?: 90) as int
    int staggerMax = (attrs.staggerMax ?: 640) as int

    String gridTemplate = (columns in ['1', '2', '3'])
      ? "repeat(${columns}, minmax(0, 1fr))"
      : "repeat(auto-fill, minmax(min(${minColumnWidth}px, 100%), 1fr))"

    out << "<section class=\"sg-wrap\" id=\"${esc(id)}\" data-sg-root>\n"

    if (title || subtitle) {
      out << "<header class=\"sg-header\">\n"
      if (title) out << "<h3 class=\"sg-title\">${esc(title)}</h3>\n"
      if (subtitle) out << "<p class=\"sg-subtitle\">${esc(subtitle)}</p>\n"
      out << "</header>\n"
    }

    out << "<div class=\"sg-grid\" data-sg-grid data-sg-delay=\"${staggerDelay}\" data-sg-max=\"${staggerMax}\" " +
      "style=\"--sg-columns:${esc(gridTemplate)};--sg-gap:${gap}px;\">\n"

    if (body && body()) {
      out << body()
    } else if (items) {
      items.each { out << renderItem(it) }
    } else {
      out << '''
                <div class="sg-empty">
                    <div class="sg-empty-glyph">&#10022;</div>
                    <p class="sg-empty-title">Nothing to show yet</p>
                    <p class="sg-empty-sub">Content will appear here as it becomes available.</p>
                </div>
            '''
    }

    out << "</div>\n</section>\n"
  }

  // ---------------------------------------------------------------
  // <stagger:item>  -- a single card, usable standalone too
  // ---------------------------------------------------------------

  def item = { attrs, body ->
    def merged = new LinkedHashMap(attrs)
    if (body) merged.description = body()
    out << renderItem(merged)
  }

  // ---------------------------------------------------------------
  // Rendering core
  // ---------------------------------------------------------------

  private String renderItem(item) {
    String accent = (prop(item, 'accent', 'default') ?: 'default').toString()
    if (!(accent in ACCENTS)) accent = 'default'

    String iconKey = prop(item, 'icon', 'dot')?.toString()
    String iconHtml = prop(item, 'iconHtml')
    String avatar = prop(item, 'avatar')
    String titleText = prop(item, 'title', '')
    def descriptionRaw = prop(item, 'description')
    String meta = prop(item, 'meta')
    String badge = prop(item, 'badge')
    String href = prop(item, 'href')

    String glyphInner
    if (avatar) {
      glyphInner = "<img class=\"sg-avatar\" src=\"${esc(avatar)}\" alt=\"\" loading=\"lazy\">"
    } else if (iconHtml) {
      glyphInner = "<span class=\"sg-icon-raw\">${iconHtml}</span>"
    } else {
      glyphInner = ICONS[iconKey] ?: ICONS['dot']
    }

    String descriptionHtml = descriptionRaw == null ? '' :
      (descriptionRaw.toString().contains('<') ? descriptionRaw.toString() : esc(descriptionRaw))

    StringBuilder sb = new StringBuilder()
    sb << "<article class=\"sg-item sg-item--${accent}\" data-sg-item tabindex=\"-1\">\n"
    sb << "  <div class=\"sg-card\">\n"
    sb << "    <div class=\"sg-card-glass\"></div>\n"
    if (href) {
      sb << "    <a class=\"sg-card-link\" href=\"${esc(href)}\" aria-label=\"${esc(titleText)}\"></a>\n"
    }
    sb << "    <div class=\"sg-glyph\">${glyphInner}</div>\n"
    sb << "    <div class=\"sg-content\">\n"
    sb << "      <div class=\"sg-row\">\n"
    sb << "        <p class=\"sg-item-title\">${esc(titleText)}</p>\n"
    if (badge) sb << "        <span class=\"sg-badge sg-badge--${accent}\">${esc(badge)}</span>\n"
    sb << "      </div>\n"
    if (descriptionHtml) sb << "      <p class=\"sg-item-desc\">${descriptionHtml}</p>\n"
    if (meta) sb << "      <p class=\"sg-item-meta\">${esc(meta)}</p>\n"
    sb << "    </div>\n"
    sb << "  </div>\n"
    sb << "</article>\n"
    return sb.toString()
  }

  // ---------------------------------------------------------------
  // CSS
  // ---------------------------------------------------------------

  private static final String SG_CSS = '''
<style>
.sg-wrap {
  --sg-bg: #0b0e1a;
  --sg-glass: rgba(255,255,255,0.06);
  --sg-glass-strong: rgba(255,255,255,0.11);
  --sg-glass-border: rgba(255,255,255,0.14);
  --sg-glass-highlight: rgba(255,255,255,0.35);
  --sg-text: #edeff7;
  --sg-text-muted: #9aa1b8;
  --sg-violet: #7c6fff;
  --sg-teal: #4fd8eb;
  --sg-amber: #ffc65c;
  --sg-rose: #ff6b81;
  --sg-success: #37e29a;
  --sg-radius: 18px;
  --sg-gap: 16px;
  --sg-columns: repeat(auto-fill, minmax(240px, 1fr));
  --sg-font-display: 'Space Grotesk', 'Segoe UI', system-ui, sans-serif;
  --sg-font-body: 'Inter', 'Segoe UI', system-ui, -apple-system, sans-serif;
  --sg-font-mono: 'JetBrains Mono', 'SFMono-Regular', Consolas, monospace;

  position: relative;
  width: 100%;
  margin: 0 auto;
  padding: clamp(16px, 3vw, 28px);
  border-radius: calc(var(--sg-radius) + 6px);
  background: radial-gradient(120% 140% at 8% 0%, #161b2e 0%, var(--sg-bg) 60%);
  color: var(--sg-text);
  font-family: var(--sg-font-body);
  box-sizing: border-box;
  -webkit-text-size-adjust: 100%;
}
.sg-wrap *, .sg-wrap *::before, .sg-wrap *::after { box-sizing: border-box; }

.sg-header { margin-bottom: clamp(14px, 2.4vw, 22px); padding: 0 2px; }
.sg-title {
  font-family: var(--sg-font-display);
  font-weight: 600;
  font-size: clamp(1.05rem, 1.6vw + 0.7rem, 1.5rem);
  margin: 0 0 4px;
  letter-spacing: -0.01em;
  background: linear-gradient(90deg, var(--sg-text) 0%, var(--sg-text) 60%, var(--sg-teal) 130%);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
}
.sg-subtitle { margin: 0; font-size: 0.85rem; color: var(--sg-text-muted); }

.sg-grid {
  display: grid;
  grid-template-columns: var(--sg-columns);
  gap: var(--sg-gap);
  align-items: stretch;
}

/* ---------- Reveal state: only armed once JS confirms it will run
   (html.sg-js is added synchronously by the script below). Without JS,
   items are simply visible - no flash-of-hidden-content, no noscript
   needed. ---------- */
html.sg-js .sg-item {
  opacity: 0;
  transform: translateY(16px) scale(0.98);
}
html.sg-js .sg-item.is-in {
  opacity: 1;
  transform: translateY(0) scale(1);
}
.sg-item {
  transition: opacity 0.55s cubic-bezier(.2,.7,.2,1), transform 0.55s cubic-bezier(.2,.7,.2,1);
  transition-delay: 0ms;
  outline: none;
}
@media (prefers-reduced-motion: reduce) {
  html.sg-js .sg-item { opacity: 1; transform: none; transition: none !important; }
}

.sg-item:focus-visible .sg-card { box-shadow: 0 0 0 2px var(--sg-teal), 0 16px 34px -14px rgba(0,0,0,0.6); }

.sg-card {
  position: relative;
  height: 100%;
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 16px;
  border-radius: var(--sg-radius);
  border: 1px solid var(--sg-glass-border);
  background: var(--sg-glass);
  -webkit-backdrop-filter: blur(16px) saturate(140%);
  backdrop-filter: blur(16px) saturate(140%);
  transition: transform 0.25s cubic-bezier(.2,.8,.2,1), background 0.25s ease, box-shadow 0.25s ease, border-color 0.25s ease;
  touch-action: manipulation;
  -webkit-tap-highlight-color: transparent;
  min-height: 44px;
}
@supports not ((backdrop-filter: blur(1px)) or (-webkit-backdrop-filter: blur(1px))) {
  .sg-card { background: rgba(22,26,44,0.92); }
}
.sg-card::before {
  content: '';
  position: absolute;
  inset: 0;
  border-radius: inherit;
  padding: 1px;
  background: linear-gradient(135deg, var(--sg-glass-highlight), transparent 45%);
  -webkit-mask: linear-gradient(#000 0 0) content-box, linear-gradient(#000 0 0);
  -webkit-mask-composite: xor;
  mask-composite: exclude;
  opacity: 0.5;
  pointer-events: none;
}
.sg-card:hover { background: var(--sg-glass-strong); transform: translateY(-3px); box-shadow: 0 16px 34px -16px rgba(0,0,0,0.55); }
.sg-card:active { transform: translateY(-1px) scale(0.986); }

.sg-card-link {
  position: absolute;
  inset: 0;
  border-radius: inherit;
  z-index: 3;
  -webkit-tap-highlight-color: transparent;
}

.sg-glyph {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border-radius: 12px;
  background: linear-gradient(145deg, var(--sg-glass-strong), var(--sg-glass));
  border: 1px solid var(--sg-glass-border);
  color: var(--sg-teal);
  flex: none;
}
.sg-glyph svg { width: 18px; height: 18px; }
.sg-avatar { width: 100%; height: 100%; border-radius: 12px; object-fit: cover; display: block; }

.sg-item--violet  .sg-glyph { color: var(--sg-violet); }
.sg-item--teal    .sg-glyph { color: var(--sg-teal); }
.sg-item--amber   .sg-glyph { color: var(--sg-amber); }
.sg-item--rose    .sg-glyph { color: var(--sg-rose); }
.sg-item--success .sg-glyph { color: var(--sg-success); }

.sg-item--violet  .sg-card { border-top: 2px solid var(--sg-violet); }
.sg-item--teal    .sg-card { border-top: 2px solid var(--sg-teal); }
.sg-item--amber   .sg-card { border-top: 2px solid var(--sg-amber); }
.sg-item--rose    .sg-card { border-top: 2px solid var(--sg-rose); }
.sg-item--success .sg-card { border-top: 2px solid var(--sg-success); }
.sg-item--default .sg-card { border-top: 2px solid var(--sg-glass-border); }

.sg-content { position: relative; z-index: 1; flex: 1 1 auto; min-width: 0; }
.sg-row { display: flex; align-items: flex-start; justify-content: space-between; gap: 8px; }
.sg-item-title { margin: 0; font-size: 0.94rem; font-weight: 500; line-height: 1.4; color: var(--sg-text); word-break: break-word; }
.sg-item-desc { margin: 6px 0 0; font-size: 0.82rem; line-height: 1.55; color: var(--sg-text-muted); }
.sg-item-meta {
  margin: 10px 0 0;
  font-family: var(--sg-font-mono);
  font-size: 0.68rem;
  letter-spacing: 0.02em;
  color: var(--sg-text-muted);
  opacity: 0.85;
}

.sg-badge {
  flex: none;
  font-family: var(--sg-font-mono);
  font-size: 0.6rem;
  letter-spacing: 0.06em;
  text-transform: uppercase;
  padding: 3px 8px;
  border-radius: 999px;
  border: 1px solid var(--sg-glass-border);
  color: var(--sg-text);
  background: var(--sg-glass-strong);
  white-space: nowrap;
}
.sg-badge--violet  { color: var(--sg-violet); }
.sg-badge--teal    { color: var(--sg-teal); }
.sg-badge--amber   { color: var(--sg-amber); }
.sg-badge--rose    { color: var(--sg-rose); }
.sg-badge--success { color: var(--sg-success); }

.sg-empty {
  grid-column: 1 / -1;
  padding: 40px 20px;
  text-align: center;
  border: 1px dashed var(--sg-glass-border);
  border-radius: var(--sg-radius);
}
.sg-empty-glyph { font-size: 1.4rem; color: var(--sg-violet); margin-bottom: 8px; }
.sg-empty-title { margin: 0 0 2px; font-weight: 500; }
.sg-empty-sub { margin: 0; font-size: 0.82rem; color: var(--sg-text-muted); }

/* ---------- Compact layout: driven by JS via ResizeObserver on the
   container itself (works regardless of viewport, e.g. narrow sidebars),
   mirrored with a media query fallback for browsers without RO. ---------- */
.sg-wrap.sg--compact { --sg-columns: 1fr; padding: 14px; }
.sg-wrap.sg--compact .sg-title { font-size: 1.05rem; }
.sg-wrap.sg--compact .sg-card { padding: 14px; }
.sg-wrap.sg--compact .sg-glyph { width: 36px; height: 36px; }

@media (max-width: 480px) {
  .sg-wrap { --sg-columns: 1fr; border-radius: var(--sg-radius); padding: 14px; }
  .sg-card { padding: 14px; }
}
@media (min-width: 900px) {
  .sg-wrap { padding: 30px; }
}
</style>
'''

  // ---------------------------------------------------------------
  // JS
  // ---------------------------------------------------------------

  private static final String SG_JS = '''
(function () {
  // Arm the hidden-until-revealed CSS state synchronously, before the
  // grid markup below even paints - if this line never runs (JS
  // disabled/blocked), items stay visible by default.
  document.documentElement.classList.add('sg-js');

  if (window.__sgBound__) return;
  window.__sgBound__ = true;

  function ready(fn) {
    if (document.readyState !== 'loading') fn();
    else document.addEventListener('DOMContentLoaded', fn);
  }

  function initGrid(grid) {
    if (grid.__sgInit__) return;
    grid.__sgInit__ = true;

    var wrap = grid.closest('[data-sg-root]');
    var delayStep = parseFloat(grid.getAttribute('data-sg-delay')) || 90;
    var maxDelay = parseFloat(grid.getAttribute('data-sg-max')) || 640;
    var reduceMotion = window.matchMedia && window.matchMedia('(prefers-reduced-motion: reduce)').matches;

    // --- Container-based responsiveness (own implementation, not just
    //     viewport media queries) -------------------------------------
    if (wrap) {
      var applyWidthClass = function (w) {
        wrap.classList.toggle('sg--compact', w < 420);
      };
      if ('ResizeObserver' in window) {
        var ro = new ResizeObserver(function (entries) {
          for (var i = 0; i < entries.length; i++) applyWidthClass(entries[i].contentRect.width);
        });
        ro.observe(wrap);
      } else {
        var onResize = function () { applyWidthClass(wrap.getBoundingClientRect().width); };
        window.addEventListener('resize', debounce(onResize, 120));
        onResize();
      }
    }

    var items = Array.prototype.slice.call(grid.querySelectorAll('[data-sg-item]'));
    items.forEach(function (item, idx) { item.__sgOrder = idx; });

    if (reduceMotion || !('IntersectionObserver' in window)) {
      items.forEach(function (item) { item.classList.add('is-in'); });
      return;
    }

    // --- Batch same-tick reveals so cards appearing together cascade,
    //     while cards revealed later (slow scroll) don't inherit a stale
    //     queue position and instead reveal promptly. -------------------
    var pending = [];
    var flushTimer = null;

    function flush() {
      pending.sort(function (a, b) { return a.__sgOrder - b.__sgOrder; });
      pending.forEach(function (item, i) {
        var delay = Math.min(i * delayStep, maxDelay);
        item.style.transitionDelay = delay + 'ms';
        item.classList.add('is-in');
      });
      pending = [];
      flushTimer = null;
    }

    var io = new IntersectionObserver(function (entries, obs) {
      entries.forEach(function (entry) {
        if (!entry.isIntersecting) return;
        pending.push(entry.target);
        obs.unobserve(entry.target);
      });
      if (!flushTimer) flushTimer = setTimeout(flush, 50);
    }, { threshold: 0.12, rootMargin: '0px 0px -8% 0px' });

    items.forEach(function (item) { io.observe(item); });
  }

  function debounce(fn, wait) {
    var t;
    return function () {
      clearTimeout(t);
      var args = arguments;
      t = setTimeout(function () { fn.apply(null, args); }, wait);
    };
  }

  function scan() {
    document.querySelectorAll('[data-sg-grid]').forEach(initGrid);
  }

  ready(scan);
  // Re-scan if content is injected later (e.g. AJAX pagination).
  document.addEventListener('sg:rescan', scan);
})();
'''
}
