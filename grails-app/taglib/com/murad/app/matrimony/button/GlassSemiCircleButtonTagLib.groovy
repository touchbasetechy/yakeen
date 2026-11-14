package com.murad.app.matrimony.button

/**
 * <glass:semiCircleButton/>
 *
 * Edge-docked semi-circular glass button. Renders as a "D"-shaped button
 * flush against a container/screen edge, with the rounded side facing inward.
 * Icon (and label, once revealed) are always centered inside the shape.
 * On hover / keyboard focus (desktop) or tap (touch) it slides open to reveal
 * the label, with a subtle background/shadow lift on hover, focus and active
 * states.
 *
 * Attrs:
 *   id        - optional, unique DOM id (auto-generated if omitted)
 *   edge      - left | right | top | bottom (default: right) - which side is docked/flat
 *   variant   - dark | light (default: dark)
 *   size      - sm | md | lg (default: md)
 *   icon      - icon markup, e.g. an <i class="fa-solid fa-heart"></i> string, or raw SVG
 *   label     - text revealed on expand
 *   href      - if present, renders <a>; otherwise renders <button type="button">
 *   onclick   - JS handler, only used when href is absent
 *   badge     - optional count/text shown as a small notification dot
 *   ariaLabel - accessible label (falls back to label, then "action button")
 */
class GlassSemiCircleButtonTagLib {

  static namespace = "glass"
  static encodeAsForTags = [semiCircleButton: [taglib: 'none']]

  private static final Map SIZES = [
    sm: [thickness: 44, diameter: 88, icon: 18, font: 13],
    md: [thickness: 60, diameter: 120, icon: 24, font: 15],
    lg: [thickness: 76, diameter: 152, icon: 30, font: 17]
  ]

  def semiCircleButton = { attrs, body ->
    String id = (attrs.id ?: "glassSemi_${System.currentTimeMillis()}_${(int) (Math.random() * 9999)}").toString()
    String edge = (attrs.edge ?: 'right').toString().toLowerCase()
    String variant = (attrs.variant ?: 'dark').toString().toLowerCase()
    String sizeKey = (attrs.size ?: 'md').toString().toLowerCase()
    String icon = attrs.icon ?: ''
    String label = (attrs.label ?: '').toString()
    String href = attrs.href
    String onclick = attrs.onclick
    String badge = attrs.badge != null ? attrs.badge.toString() : ''
    String ariaLbl = (attrs.ariaLabel ?: label ?: 'action button').toString()

    if (!(edge in ['left', 'right', 'top', 'bottom'])) edge = 'right'
    if (!(variant in ['dark', 'light'])) variant = 'dark'
    if (!SIZES.containsKey(sizeKey)) sizeKey = 'md'
    def dim = SIZES[sizeKey]

    boolean horizontal = (edge in ['left', 'right'])   // docked to a vertical screen edge
    int thickness = dim.thickness as int   // collapsed size on the growth axis
    int diameter = dim.diameter as int    // fixed size on the cross axis
    int expanded = diameter               // expanded size on the growth axis

    int wCollapsed = horizontal ? thickness : diameter
    int hCollapsed = horizontal ? diameter : thickness
    int wExpanded = horizontal ? expanded : diameter
    int hExpanded = horizontal ? diameter : expanded

    Map radiusByEdge = [
      left  : "0 ${diameter}px ${diameter}px 0",
      right : "${diameter}px 0 0 ${diameter}px",
      top   : "0 0 ${diameter}px ${diameter}px",
      bottom: "${diameter}px ${diameter}px 0 0"
    ]
    String radius = radiusByEdge[edge]

    Map posByEdge = [
      left  : "left:0; top:50%; transform:translateY(-50%);",
      right : "right:0; top:50%; transform:translateY(-50%);",
      top   : "top:0; left:50%; transform:translateX(-50%);",
      bottom: "bottom:0; left:50%; transform:translateX(-50%);"
    ]
    String position = posByEdge[edge]

    // Growth axis decides stacking direction: left/right buttons grow in width,
    // so icon + label sit side by side (row); top/bottom buttons grow in height,
    // so icon + label stack vertically (column). Content is always centered
    // as a group inside the shape, on both axes.
    String flexDir = horizontal ? 'row' : 'column'

    boolean dark = (variant == 'dark')
    String glassBg = dark ? 'rgba(20, 22, 34, 0.45)' : 'rgba(255, 255, 255, 0.55)'
    String glassBgHover = dark ? 'rgba(20, 22, 34, 0.60)' : 'rgba(255, 255, 255, 0.72)'
    String borderCol = dark ? 'rgba(255, 255, 255, 0.22)' : 'rgba(20, 22, 34, 0.14)'
    String textCol = dark ? '#f4f5fb' : '#1c1e2b'
    String glowCol = dark ? 'rgba(150, 175, 255, 0.95)' : 'rgba(90, 110, 240, 0.85)'
    String fallbackBg = dark ? 'rgba(24, 26, 40, 0.92)' : 'rgba(250, 250, 255, 0.94)'

    String tag = href ? 'a' : 'button'
    String hrefAttr = href ? "href=\"${href}\"" : ''
    String typeAttr = href ? '' : 'type="button"'
    String onclickAttr = (!href && onclick) ? "onclick=\"${onclick}\"" : ''

    out << """
<style>
  #${id}.glass-semi-btn {
    --gsb-glass-background: ${glassBg};
    --gsb-glass-background-hover: ${glassBgHover};
    --gsb-border: ${borderCol};
    --gsb-text: ${textCol};
    --gsb-glow: ${glowCol};
    --gsb-fallback-background: ${fallbackBg};
    --gsb-radius: ${radius};

    position: fixed;
    ${position}
    z-index: 1040;
    width: ${wCollapsed}px;
    height: ${hCollapsed}px;
    box-sizing: border-box;
    border: 1px solid var(--gsb-border);
    border-radius: var(--gsb-radius);
    background: var(--gsb-glass-background);
    color: var(--gsb-text) !important;
    text-decoration: none !important;
    cursor: pointer;
    padding: 0;
    isolation: isolate;
    -webkit-backdrop-filter: blur(16px) saturate(160%);
    backdrop-filter: blur(16px) saturate(160%);
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.18), inset 0 1px 0 rgba(255,255,255,0.12);
    transition: width 0.28s cubic-bezier(0.22, 1, 0.36, 1),
                height 0.28s cubic-bezier(0.22, 1, 0.36, 1),
                background 0.2s ease, box-shadow 0.2s ease;
    -webkit-tap-highlight-color: transparent;
    -webkit-user-select: none;
    user-select: none;
    font-family: inherit;
  }

  /* Firefox / older Safari without backdrop-filter support */
  @supports not ((backdrop-filter: blur(1px)) or (-webkit-backdrop-filter: blur(1px))) {
    #${id}.glass-semi-btn { background: var(--gsb-fallback-background); }
  }

  #${id}.glass-semi-btn:hover,
  #${id}.glass-semi-btn:focus-visible,
  #${id}.glass-semi-btn.gsb-expanded {
    width: ${wExpanded}px;
    height: ${hExpanded}px;
    background: var(--gsb-glass-background-hover);
  }

  #${id}.glass-semi-btn:focus-visible {
    outline: 2px solid var(--gsb-glow);
    outline-offset: 3px;
  }

  #${id}.glass-semi-btn:hover,
  #${id}.glass-semi-btn:focus-visible,
  #${id}.glass-semi-btn:active,
  #${id}.glass-semi-btn.gsb-expanded {
    box-shadow: 0 10px 40px rgba(0,0,0,0.22), inset 0 1px 0 rgba(255,255,255,0.16);
  }

  /* Content wrapper owns clipping + centering, kept separate from the
     button's own box so future hover effects can be added without
     disturbing icon/label layout. */
  #${id} .gsb-content {
    position: absolute;
    inset: 0;
    display: flex;
    flex-direction: ${flexDir};
    align-items: center;
    justify-content: center;
    gap: 6px;
    padding: 10px;
    box-sizing: border-box;
    overflow: hidden;
    border-radius: inherit;
  }

  #${id} .gsb-icon {
    flex: 0 0 auto;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    width: ${dim.icon}px;
    height: ${dim.icon}px;
    font-size: ${dim.icon}px;
    line-height: 1;
  }

  #${id} .gsb-label {
    flex: 0 0 auto;
    max-width: 0;
    max-height: 0;
    opacity: 0;
    overflow: hidden;
    white-space: nowrap;
    font-size: ${dim.font}px;
    font-weight: 600;
    letter-spacing: 0.01em;
    transition: max-width 0.28s ease 0.05s, max-height 0.28s ease 0.05s, opacity 0.18s ease 0.08s;
  }

  #${id}.glass-semi-btn:hover .gsb-label,
  #${id}.glass-semi-btn:focus-visible .gsb-label,
  #${id}.glass-semi-btn.gsb-expanded .gsb-label {
    max-width: 160px;
    max-height: 40px;
    opacity: 1;
  }

  #${id} .gsb-badge {
    position: absolute;
    top: 6px;
    ${edge == 'left' ? 'right: 8px;' : 'left: 8px;'}
    min-width: 16px;
    height: 16px;
    padding: 0 4px;
    border-radius: 8px;
    background: #ff4d6d;
    color: #fff;
    font-size: 10px;
    font-weight: 700;
    line-height: 16px;
    text-align: center;
    box-shadow: 0 0 0 2px var(--gsb-glass-background);
  }

  @media (prefers-reduced-motion: reduce) {
    #${id}.glass-semi-btn,
    #${id} .gsb-label { transition: none; }
  }
</style>

<${tag} id="${id}" class="glass-semi-btn" ${hrefAttr} ${typeAttr} ${onclickAttr}
    aria-label="${ariaLbl.replace('"', '&quot;')}" data-glass-edge="${edge}">
  <span class="gsb-content">
    <span class="gsb-icon" aria-hidden="true">${icon}</span>
    <span class="gsb-label">${label.encodeAsHTML()}</span>
  </span>
  ${badge ? "<span class=\"gsb-badge\">${badge.encodeAsHTML()}</span>" : ''}
  ${body() ?: ''}
</${tag}>

<script>
(function () {
  var el = document.getElementById('${id}');
  if (!el) return;
  var isTouch = window.matchMedia('(hover: none), (pointer: coarse)').matches;
  if (!isTouch) return;

  el.addEventListener('click', function (evt) {
    if (!el.classList.contains('gsb-expanded')) {
      evt.preventDefault();
      document.querySelectorAll('.glass-semi-btn.gsb-expanded').forEach(function (other) {
        if (other !== el) other.classList.remove('gsb-expanded');
      });
      el.classList.add('gsb-expanded');
      var collapse = function (e) {
        if (!el.contains(e.target)) {
          el.classList.remove('gsb-expanded');
          document.removeEventListener('click', collapse, true);
        }
      };
      setTimeout(function () { document.addEventListener('click', collapse, true); }, 0);
    }
  }, true);
})();
</script>
"""
  }
}
