package com.murad.app.matrimony.page

/**
 * SkeletonTagLib.groovy
 * ─────────────────────────────────────────────────────────────────────────────
 * Glassmorphic Futuristic Skeleton Loader GSP Tag Library
 *
 * Namespace : skeleton
 * Requires  : skeleton-loader.css (place in grails-app/assets/stylesheets/)
 *
 * Usage in GSP:
 *   <%@ page tagLib="com.yourdomain.tags.SkeletonTagLib" %>
 *   — or register globally in grails-app/conf/spring/resources.groovy —
 *
 * Quick start:
 *   <skeleton:css />                          <%-- inject stylesheet link --%>
 *   <skeleton:card showMedia="true" lines="3" showAvatar="true" showActions="true" />
 *   <skeleton:list items="4" />
 *   <skeleton:table rows="5" cols="4" />
 *   <skeleton:hero />
 *   <skeleton:product />
 *   <skeleton:profile />
 *   <skeleton:media items="3" />
 *   <skeleton:text lines="5" widths="100,90,80,70,40" />
 *   <skeleton:avatar size="lg" shape="circle" />
 *   <skeleton:wrap loading="${isLoading}">
 *     <p>Real content shown when loading=false</p>
 *   </skeleton:wrap>
 *   <skeleton:grid count="3" type="card" />
 * ─────────────────────────────────────────────────────────────────────────────
 */
class SkeletonTagLib {

  static namespace = 'skeleton'

  // ── Internal helpers ────────────────────────────────────────────────────

  /** Emit a single glass-wrapped skeleton component */
  private String glassWrap(String content, String extraClass = '', String ariaLabel = 'Loading…') {
    """<div class="sk-glass ${extraClass}" role="status" aria-busy="true" aria-label="${ariaLabel.encodeAsHTML()}">
  <span class="sk-sr-only">${ariaLabel.encodeAsHTML()}</span>
${content}
</div>"""
  }

  /** Build a sequence of sk-bone text lines with configurable widths */
  private String boneLines(int count, List<String> widths = [], String extraClass = '') {
    (0..<count).collect { i ->
      String w = widths && i < widths.size() ? "style=\"width:${widths[i]}\"" : ''
      "  <span class=\"sk-bone ${extraClass}\" ${w}></span>"
    }.join('\n')
  }

  // ── <skeleton:css /> ────────────────────────────────────────────────────
  /**
   * Emits the <link> tag for the skeleton stylesheet.
   * @attr href   Override the CSS path (default: /assets/skeleton-loader.css)
   * @attr inline Pass inline="true" to emit a <style> block instead of a link
   */
  def css = { attrs ->
    String href = attrs.href ?: g.assetPath(src: 'skeleton-loader.css')
    out << """<link rel="stylesheet" href="${href.encodeAsHTML()}">\n"""
  }

  // ── <skeleton:bone /> ───────────────────────────────────────────────────
  /**
   * Renders a single skeleton bone (the base primitive).
   * @attr width   CSS width string, e.g. "60%" or "120px"  (default: 100%)
   * @attr height  CSS height string, e.g. "14px"            (default: 14px)
   * @attr radius  CSS border-radius override
   * @attr pulse   Pass pulse="true" for pulse instead of shimmer animation
   * @attr delay   Animation delay in ms, e.g. "200"
   * @attr cls     Additional CSS classes
   */
  def bone = { attrs ->
    String w = attrs.width ?: '100%'
    String h = attrs.height ?: '14px'
    String r = attrs.radius ? "border-radius:${attrs.radius};" : ''
    String d = attrs.delay ? "animation-delay:${attrs.delay}ms;" : ''
    String variant = attrs.pulse == 'true' ? ' sk-bone--pulse' : ''
    String cls = attrs.cls ?: ''
    String style = "width:${w};height:${h};${r}${d}"
    out << """<span class="sk-bone${variant} ${cls}" style="${style.encodeAsHTML()}"></span>\n"""
  }

  // ── <skeleton:avatar /> ─────────────────────────────────────────────────
  /**
   * Renders an avatar placeholder.
   * @attr size   sm | md (default) | lg | xl
   * @attr shape  circle (default) | square
   * @attr delay  Animation delay ms
   */
  def avatar = { attrs ->
    String size = attrs.size ?: 'md'
    String shape = attrs.shape == 'square' ? ' sk-avatar--sq' : ''
    String delay = attrs.delay ? "animation-delay:${attrs.delay}ms;" : ''
    out << """<span class="sk-bone sk-avatar sk-avatar--${size}${shape}" style="${delay}" aria-hidden="true"></span>\n"""
  }

  // ── <skeleton:text /> ───────────────────────────────────────────────────
  /**
   * Renders stacked text-line skeletons.
   * @attr lines   Number of lines (default: 3)
   * @attr widths  Comma-separated width values, e.g. "100%,85%,60%"
   * @attr title   Pass title="true" to prepend a large title bone
   * @attr glass   Wrap in glass container (default: false)
   */
  def text = { attrs ->
    int lines = (attrs.lines ?: '3').toInteger()
    boolean title = attrs.title == 'true'
    boolean glass = attrs.glass == 'true'
    List<String> widths = attrs.widths ? attrs.widths.split(',').collect { it.trim() } : []

    String inner = """<div class="sk-text" aria-hidden="true">
${title ? '  <span class="sk-bone sk-text__title"></span>\n' : ''}${boneLines(lines, widths, 'sk-text__line')}
</div>"""

    out << (glass ? glassWrap(inner, '', 'Loading text…') : inner) << '\n'
  }

  // ── <skeleton:card /> ───────────────────────────────────────────────────
  /**
   * Renders a content card skeleton.
   * @attr showMedia    Show image/media area at top    (default: true)
   * @attr mediaHeight  Override media height in px     (default: CSS-driven)
   * @attr showAvatar   Show avatar + meta header       (default: true)
   * @attr lines        Number of body text lines       (default: 3)
   * @attr showActions  Show CTA button row at bottom   (default: false)
   * @attr float        Animate with floating effect    (default: false)
   * @attr glass        Wrap in glass container         (default: true)
   * @attr delay        Stagger delay in ms
   */
  def card = { attrs ->
    boolean showMedia = attrs.showMedia != 'false'
    boolean showAvatar = attrs.showAvatar != 'false'
    boolean showActions = attrs.showActions == 'true'
    boolean glass = attrs.glass != 'false'
    boolean floatAnim = attrs.float == 'true'
    int lines = (attrs.lines ?: '3').toInteger()
    String delay = attrs.delay ? "animation-delay:${attrs.delay}ms;" : ''
    String floatCls = floatAnim ? 'style="animation:sk-float 4s ease-in-out infinite;"' : ''
    String mediaStyle = attrs.mediaHeight ? "style=\"height:${attrs.mediaHeight}px\"" : ''

    StringBuilder sb = new StringBuilder()
    sb << """<div class="sk-card" ${floatCls} aria-hidden="true">\n"""
    if (showMedia) sb << """  <span class="sk-bone sk-card__media" ${mediaStyle}></span>\n"""
    if (showAvatar) {
      sb << """  <div class="sk-card__header">
    <span class="sk-bone sk-card__avatar"></span>
    <div class="sk-card__meta">
      <span class="sk-bone sk-card__meta-name"></span>
      <span class="sk-bone sk-card__meta-sub"></span>
    </div>
  </div>\n"""
    }
    sb << """  <div class="sk-card__lines">\n"""
    lines.times { i ->
      String w = i == lines - 1 ? 'width:55%' : (i % 2 == 0 ? '' : 'width:88%')
      sb << """    <span class="sk-bone sk-card__line" style="${w}"></span>\n"""
    }
    sb << """  </div>\n"""
    if (showActions) {
      sb << """  <div class="sk-card__actions">
    <span class="sk-bone sk-card__btn"></span>
    <span class="sk-bone sk-card__btn" style="max-width:90px"></span>
  </div>\n"""
    }
    sb << """</div>"""

    out << (glass ? glassWrap(sb.toString(), '', 'Loading card…') : sb.toString()) << '\n'
  }

  // ── <skeleton:list /> ───────────────────────────────────────────────────
  /**
   * Renders a stacked list of row skeletons.
   * @attr items       Number of rows         (default: 4)
   * @attr showIcon    Show leading icon bone  (default: true)
   * @attr showBadge   Show trailing badge     (default: true)
   * @attr glass       Wrap in glass container (default: true)
   */
  def list = { attrs ->
    int items = (attrs.items ?: '4').toInteger()
    boolean showIcon = attrs.showIcon != 'false'
    boolean showBadge = attrs.showBadge != 'false'
    boolean glass = attrs.glass != 'false'

    StringBuilder sb = new StringBuilder()
    sb << """<div class="sk-list" aria-hidden="true">\n"""
    items.times { i ->
      double delay = i * 0.06
      sb << """  <div class="sk-list__item">
${showIcon ? "    <span class=\"sk-bone sk-list__icon\" style=\"animation-delay:${delay}s\"></span>\n" : ''}    <div class="sk-list__body">
      <span class="sk-bone sk-list__title" style="animation-delay:${delay + 0.03}s"></span>
      <span class="sk-bone sk-list__sub" style="animation-delay:${delay + 0.06}s"></span>
    </div>
${showBadge ? "    <span class=\"sk-bone sk-list__badge\" style=\"animation-delay:${delay + 0.09}s\"></span>\n" : ''}  </div>\n"""
    }
    sb << """</div>"""

    out << (glass ? glassWrap(sb.toString(), '', 'Loading list…') : sb.toString()) << '\n'
  }

  // ── <skeleton:table /> ──────────────────────────────────────────────────
  /**
   * Renders a table skeleton with header + rows.
   * @attr rows      Number of data rows  (default: 5)
   * @attr cols      Number of columns    (default: 4)
   * @attr showHead  Render header row    (default: true)
   * @attr glass     Wrap in glass container (default: true)
   */
  def table = { attrs ->
    int rows = (attrs.rows ?: '5').toInteger()
    int cols = (attrs.cols ?: '4').toInteger()
    boolean showHead = attrs.showHead != 'false'
    boolean glass = attrs.glass != 'false'

    StringBuilder sb = new StringBuilder()
    sb << """<div class="sk-table-wrap" aria-hidden="true">\n"""
    if (showHead) {
      sb << """  <div class="sk-table__head">\n"""
      cols.times { sb << """    <span class="sk-bone sk-table__hcell"></span>\n""" }
      sb << """  </div>\n"""
    }
    rows.times { r ->
      double delay = r * 0.07
      sb << """  <div class="sk-table__row">\n"""
      cols.times { c ->
        String w = c == 0 ? '' : (c == cols - 1 ? 'width:60%' : '')
        sb << """    <span class="sk-bone sk-table__cell" style="animation-delay:${delay + c * 0.02}s;${w}"></span>\n"""
      }
      sb << """  </div>\n"""
    }
    sb << """</div>"""

    out << (glass ? glassWrap(sb.toString(), '', 'Loading table…') : sb.toString()) << '\n'
  }

  // ── <skeleton:hero /> ───────────────────────────────────────────────────
  /**
   * Renders a full hero/banner skeleton.
   * @attr showCtas  Render CTA button placeholders (default: true)
   * @attr glass     Wrap in glass container         (default: true)
   */
  def hero = { attrs ->
    boolean showCtas = attrs.showCtas != 'false'
    boolean glass = attrs.glass != 'false'

    String inner = """<div class="sk-hero" aria-hidden="true">
  <span class="sk-bone sk-hero__eyebrow"></span>
  <span class="sk-bone sk-hero__h1"></span>
  <span class="sk-bone sk-hero__h1b"></span>
  <span class="sk-bone sk-hero__sub"></span>
  <span class="sk-bone sk-hero__subb"></span>
${showCtas ? """  <div class="sk-hero__ctas">
    <span class="sk-bone sk-hero__cta"></span>
    <span class="sk-bone sk-hero__cta-ghost"></span>
  </div>""" : ''}
</div>"""

    out << (glass ? glassWrap(inner, '', 'Loading hero…') : inner) << '\n'
  }

  // ── <skeleton:product /> ────────────────────────────────────────────────
  /**
   * Renders a product card skeleton.
   * @attr glass  Wrap in glass container (default: true)
   */
  def product = { attrs ->
    boolean glass = attrs.glass != 'false'
    String inner = """<div class="sk-product" aria-hidden="true">
  <span class="sk-bone sk-product__img"></span>
  <span class="sk-bone sk-product__tag"></span>
  <span class="sk-bone sk-product__name"></span>
  <span class="sk-bone sk-product__nameb"></span>
  <span class="sk-bone sk-product__price"></span>
  <span class="sk-bone sk-product__btn"></span>
</div>"""
    out << (glass ? glassWrap(inner, '', 'Loading product…') : inner) << '\n'
  }

  // ── <skeleton:media /> ──────────────────────────────────────────────────
  /**
   * Renders media row skeletons (video/podcast style).
   * @attr items  Number of rows  (default: 3)
   * @attr glass  Wrap in glass   (default: true)
   */
  def media = { attrs ->
    int items = (attrs.items ?: '3').toInteger()
    boolean glass = attrs.glass != 'false'

    StringBuilder sb = new StringBuilder()
    sb << """<div class="sk-list" aria-hidden="true">\n"""
    items.times { i ->
      double delay = i * 0.08
      sb << """  <div class="sk-media" style="padding:10px">
    <span class="sk-bone sk-media__thumb" style="animation-delay:${delay}s"></span>
    <div class="sk-media__info">
      <span class="sk-bone sk-media__title" style="animation-delay:${delay + 0.04}s"></span>
      <span class="sk-bone sk-media__titleb" style="animation-delay:${delay + 0.08}s"></span>
      <span class="sk-bone sk-media__meta" style="animation-delay:${delay + 0.12}s"></span>
    </div>
  </div>\n"""
    }
    sb << """</div>"""

    out << (glass ? glassWrap(sb.toString(), '', 'Loading media…') : sb.toString()) << '\n'
  }

  // ── <skeleton:profile /> ────────────────────────────────────────────────
  /**
   * Renders a user profile card skeleton.
   * @attr glass  Wrap in glass container (default: true)
   */
  def profile = { attrs ->
    boolean glass = attrs.glass != 'false'
    String inner = """<div class="sk-profile" aria-hidden="true">
  <span class="sk-bone sk-profile__banner"></span>
  <span class="sk-bone sk-profile__av"></span>
  <span class="sk-bone sk-profile__name"></span>
  <span class="sk-bone sk-profile__bio"></span>
  <span class="sk-bone sk-profile__biob"></span>
  <div class="sk-profile__stats">
    <div class="sk-profile__stat">
      <span class="sk-bone sk-profile__num"></span>
      <span class="sk-bone sk-profile__lbl"></span>
    </div>
    <div class="sk-profile__stat">
      <span class="sk-bone sk-profile__num"></span>
      <span class="sk-bone sk-profile__lbl"></span>
    </div>
    <div class="sk-profile__stat">
      <span class="sk-bone sk-profile__num"></span>
      <span class="sk-bone sk-profile__lbl"></span>
    </div>
  </div>
</div>"""
    out << (glass ? glassWrap(inner, '', 'Loading profile…') : inner) << '\n'
  }

  // ── <skeleton:grid /> ───────────────────────────────────────────────────
  /**
   * Renders a responsive grid of skeleton components.
   * @attr count  Number of items   (default: 3)
   * @attr type   card | product | profile | media | text  (default: card)
   * @attr showMedia   Passed to card/product sub-skeletons
   * @attr showActions Passed to card sub-skeleton
   */
  def grid = { attrs ->
    int count = (attrs.count ?: '3').toInteger()
    String type = attrs.type ?: 'card'
    double baseDelay = 0

    out << """<div class="sk-grid">\n"""
    count.times { i ->
      String delayAttr = "delay=\"${(i * 120).toInteger()}\""
      switch (type) {
        case 'product': product(attrs + [glass: 'true']); break
        case 'profile': profile(attrs + [glass: 'true']); break
        case 'media': media(attrs + [glass: 'true', items: '1']); break
        case 'text': text(attrs + [glass: 'true']); break
        default:
          card(attrs + [glass: 'true', delay: "${i * 120}"])
      }
    }
    out << """</div>\n"""
  }

  // ── <skeleton:wrap /> ───────────────────────────────────────────────────
  /**
   * Conditional wrapper — shows skeleton when loading=true, body content when false.
   * @attr loading   Boolean EL expression, e.g. loading="${isLoading}"  (required)
   * @attr type      Skeleton type when loading: card | list | table | hero | text | product | profile | media  (default: card)
   * @attr ariaLive  ARIA live region politeness: polite | assertive  (default: polite)
   * All other attrs forwarded to the chosen skeleton tag.
   */
  def wrap = { attrs, body ->
    boolean loading = attrs.loading instanceof Boolean ? attrs.loading : attrs.loading?.toString() == 'true'
    String type = attrs.type ?: 'card'
    String ariaLive = attrs.ariaLive ?: 'polite'
    Map fwd = attrs - [loading: attrs.loading, type: type, ariaLive: ariaLive]

    out << """<div aria-live="${ariaLive}" aria-busy="${loading}">\n"""
    if (loading) {
      switch (type) {
        case 'list': list(fwd); break
        case 'table': table(fwd); break
        case 'hero': hero(fwd); break
        case 'text': text(fwd); break
        case 'product': product(fwd); break
        case 'profile': profile(fwd); break
        case 'media': media(fwd); break
        default: card(fwd)
      }
    } else {
      out << body()
    }
    out << """</div>\n"""
  }
}
