package com.murad.app.matrimony.card

import groovy.util.logging.Slf4j

/**
 * Modern Glassmorphic Card TagLib
 *
 * Provides GSP tags for creating next-generation glassmorphism-styled cards
 * with sophisticated animations, gradients, and modern design patterns.
 *
 * Usage examples:
 * <glass:cardLink title="Hobbies" image="${resource(dir: 'images', file: 'icon.png')}" controller="DBoard" action="listHobbiesToEdit" />
 * <glass:cardLink title="Profile" variant="primary" hover="lift" />
 * <glass:cardLink title="Settings" faIcon="gear" variant="gradient" glow="true" />
 * <glass:cardLink title="Settings" faIcon="gear" faStyle="far" variant="gradient" glow="true" />
 */
@Slf4j
class ModernCardTagLib {

  static namespace = "glass"

  /**
   * Builds a slot-aware URL of the form /u/$slot/$controller/$action,
   * including optional query string (from params) and fragment.
   *
   * NOTE: must NOT be private — see resolveSlot() above for why.
   *
   * @param slot the resolved slot value
   * @param controller the controller name
   * @param action the action name
   * @param params optional map of query params
   * @param fragment optional URL fragment
   * @return the constructed URL string
   */
  def buildSlotUrl(slot, controller, action, params, fragment) {
    def slotUrl = "/u/${slot}/${controller ?: ''}/${action ?: ''}"

    if (params) {
      def queryString = params.collect { k, v -> "${k.encodeAsURL()}=${v?.toString()?.encodeAsURL()}" }.join('&')
      if (queryString) slotUrl += "?${queryString}"
    }
    if (fragment) slotUrl += "#${fragment}"

    return slotUrl
  }

  /**
   * Resolves the icon CSS class to render, supporting two ways of specifying
   * an icon:
   *
   *  - attrs.icon: a full icon class string, e.g. 'fas fa-heart' (unchanged
   *    from prior behavior, and always wins if present)
   *  - attrs.faIcon: just the FontAwesome icon name, e.g. 'heart', combined
   *    with attrs.faStyle (default 'fas') to build 'fas fa-heart'
   *
   * NOTE: must NOT be private — called via owner.resolveIcon() from tag
   * closures, same reasoning as buildSlotUrl() above.
   *
   * @param attrs the tag's attribute map
   * @return the resolved icon class string, or null if neither attr is set
   */
  def resolveIcon(attrs) {
    if (attrs.icon) return attrs.icon
    if (attrs.faIcon) {
      def faStyle = attrs.faStyle ?: 'fas'
      return "${faStyle} fa-${attrs.faIcon}"
    }
    return null
  }

  /**
   * Creates a modern glassmorphic card
   *
   * @attr title REQUIRED - Card title/label text
   * @attr image - Image URL/path for card image
   * @attr icon - Icon class name (e.g., 'fas fa-heart') - alternative to image
   * @attr faIcon - FontAwesome icon name without prefix (e.g., 'heart') - alternative to icon; combined with faStyle
   * @attr faStyle - FontAwesome style prefix: fas, far, fab, fal, fad (default: 'fas') - only used with faIcon
   * @attr variant - Card color variant: default, primary, success, danger, warning, info, gradient (default: 'default')
   * @attr size - Card size: sm, md, lg, xl (default: 'md')
   * @attr hover - Hover effect: lift, glow, tilt, none (default: 'lift')
   * @attr glow - Boolean for glow effect (default: false)
   * @attr pulse - Boolean for pulse animation (default: false)
   * @attr shimmer - Boolean for shimmer effect (default: false)
   * @attr badge - Badge text to display in corner
   * @attr badgeVariant - Badge color variant (default: 'primary')
   * @attr class - Additional CSS classes
   * @attr id - Card ID attribute
   * @attr style - Additional inline styles
   *
   * Link attributes (Grails):
   * @attr controller - Grails controller name
   * @attr action - Grails action name
   * @attr params - Grails params map
   * @attr fragment - URL fragment
   * @attr url - Direct URL (alternative to controller/action)
   * @attr href - Direct href (alternative to Grails link)
   * @attr slot - Optional slot identifier. Only used when the 'currentSlot' request
   *              attribute is NOT set. When a slot is resolved (from either source)
   *              and controller and/or action are used, the generated link uses the
   *              pattern /u/$slot/$controller/$action instead of the standard Grails
   *              controller/action link. Does not apply when only 'url' or 'href' is used.
   *
   * Content attributes:
   * @attr description - Optional description text below title
   * @attr footer - Optional footer text
   */
  def cardLink = { attrs, body ->
    def title = attrs.title ?: ''
    def image = attrs.image
    def icon = owner.resolveIcon(attrs)
    def variant = attrs.variant ?: 'default'
    def size = attrs.size ?: 'md'
    def hover = attrs.hover ?: 'lift'
    def glow = attrs.glow?.toBoolean() ?: false
    def pulse = attrs.pulse?.toBoolean() ?: false
    def shimmer = attrs.shimmer?.toBoolean() ?: false
    def badge = attrs.badge
    def badgeVariant = attrs.badgeVariant ?: 'primary'
    def customClass = attrs.class ?: ''
    def cardId = attrs.id ? "id='${attrs.id}'" : ''
    def customStyle = attrs.style ?: ''
    def description = attrs.description
    def footer = attrs.footer

    // Link attributes
    def controller = attrs.controller
    def action = attrs.action
    def params = attrs.params
    def fragment = attrs.fragment
    def url = attrs.url
    def href = attrs.href

    // Slot resolution: 'currentSlot' request attribute takes priority over the
    // 'slot' tag attribute. See resolveSlot() for the full priority order.
    def rawSlot = request?.getAttribute('currentSlot')
    def slot = (rawSlot != null) ? rawSlot : attrs.slot

    // Build CSS classes
    def cssClasses = [
      'glass-card',
      "glass-card-${size}",
      "glass-card-${variant}",
      hover != 'none' ? "glass-card-hover-${hover}" : '',
      glow ? 'glass-card-glow' : '',
      pulse ? 'glass-card-pulse' : '',
      shimmer ? 'glass-card-shimmer' : '',
      customClass
    ].findAll { it }.join(' ')

    // Generate card content
    def cardContent = """
            <div class="glass-card-body">
                ${badge ? "<span class='glass-card-badge glass-card-badge-${badgeVariant}'>${badge}</span>" : ''}
                ${image ? "<img src='${image}' class='glass-card-img' alt='${title}' />" : ''}
                ${icon && !image ? "<div class='glass-card-icon-wrapper'><i class='${icon} glass-card-icon'></i></div>" : ''}
                ${title ? "<div class='glass-card-title'>${title}</div>" : ''}
                ${description ? "<div class='glass-card-description'>${description}</div>" : ''}
                ${body() ? "<div class='glass-card-content'>${body()}</div>" : ''}
                ${footer ? "<div class='glass-card-footer'>${footer}</div>" : ''}
                <span class='glass-card-shine'></span>
            </div>
        """

    // Determine link type and render
    def hasLink = controller || action || url || href

    if (hasLink) {
      log.debug("hi!!11111  {}", slot)
      if ((controller || action) && slot != null) {
        log.debug("hi!!2222  {}, {}, {}, {}, {}", slot, controller, action, (controller || action), ((controller || action) && slot))
        // Slot-aware URL: /u/$slot/$controller/$action
        // (slot resolved from 'currentSlot' request attribute, or 'slot' attr as fallback)
        def slotUrl = owner.buildSlotUrl(slot, controller, action, params, fragment)

        out << """<a href="${slotUrl}" class="${cssClasses}" ${cardId} style="${customStyle}">${cardContent}</a>"""
        log.debug("with slot:  {}", slotUrl)
      } else if (controller || action || url) {
        log.debug("hi!!33333  {}, {}, {}, {}, {}", slot, controller, action, (controller || action), ((controller || action) && slot))
        // No slot resolved (or no controller/action) - use standard Grails link.
        //
        // NOTE: We deliberately do NOT pass attrs.id into g.link's attribute map.
        // Grails treats a link tag's 'id' attribute as a REST-style URL path
        // segment (e.g. /controller/action/id), not as an HTML element id — so
        // doing so would leak the card's HTML id into the generated URL. Instead
        // we resolve the href via createLink and build the <a> tag ourselves,
        // applying cardId purely as an HTML attribute.
        def linkAttrs = [:]
        if (controller) linkAttrs.controller = controller
        if (action) linkAttrs.action = action
        if (params) linkAttrs.params = params
        if (fragment) linkAttrs.fragment = fragment
        if (url) linkAttrs.url = url

        def grailsHref = g.createLink(linkAttrs)

        out << """<a href="${grailsHref}" class="${cssClasses}" ${cardId} style="${customStyle}">${cardContent}</a>"""
      } else if (href) {
        log.debug("hi!!555555")
        // Use regular anchor tag
        out << """<a href="${href}" class="${cssClasses}" ${cardId} style="${customStyle}">${cardContent}</a>"""
      }
    } else {
      // No link, just render div
      out << """<div class="${cssClasses}" ${cardId} style="${customStyle}">${cardContent}</div>"""
    }
  }

  /**
   * Creates a card grid container
   *
   * @attr columns - Number of columns: 1, 2, 3, 4, 5, 6 (default: 3)
   * @attr gap - Gap size: sm, md, lg, xl (default: 'md')
   * @attr class - Additional CSS classes
   */
  def cardGrid = { attrs, body ->
    def columns = attrs.columns ?: '3'
    def gap = attrs.gap ?: 'md'
    def customClass = attrs.class ?: ''
    def cssClasses = "glass-card-grid glass-card-grid-cols-${columns} glass-card-grid-gap-${gap} ${customClass}".trim()

    out << """<div class="${cssClasses}">${body()}</div>"""
  }

  /**
   * Creates an icon card (specialized card for icons)
   *
   * @attr title REQUIRED - Card title
   * @attr icon - Icon class name (REQUIRED unless faIcon is given)
   * @attr faIcon - FontAwesome icon name without prefix (e.g., 'heart') - alternative to icon; combined with faStyle
   * @attr faStyle - FontAwesome style prefix: fas, far, fab, fal, fad (default: 'fas') - only used with faIcon
   * @attr variant - Card color variant (default: 'primary')
   * @attr controller - Grails controller
   * @attr action - Grails action
   * @attr size - Card size: sm, md, lg (default: 'md')
   * @attr slot - Optional slot identifier (see cardLink for full slot resolution rules;
   *              'currentSlot' request attribute takes priority over this attribute)
   */
  def iconCard = { attrs, body ->
    def title = attrs.title
    def icon = owner.resolveIcon(attrs)
    def variant = attrs.variant ?: 'primary'
    def size = attrs.size ?: 'md'
    def controller = attrs.controller
    def action = attrs.action
    def params = attrs.params
    def url = attrs.url
    def fragment = attrs.fragment

    def rawSlot = request?.getAttribute('currentSlot')
    def slot = (rawSlot != null) ? rawSlot : attrs.slot

    def cssClasses = "glass-card glass-card-icon glass-card-${size} glass-card-${variant} glass-card-hover-lift"

    def cardContent = """
            <div class="glass-card-body">
                <div class='glass-card-icon-wrapper'>
                    <i class='${icon} glass-card-icon'></i>
                </div>
                <div class='glass-card-title'>${title}</div>
                ${body() ? "<div class='glass-card-content'>${body()}</div>" : ''}
                <span class='glass-card-shine'></span>
            </div>
        """

    if ((slot != null) && (controller || action)) {
      // Slot-aware URL: /u/$slot/$controller/$action
      def slotUrl = owner.buildSlotUrl(slot, controller, action, params, fragment)
      out << """<a href="${slotUrl}" class="${cssClasses}">${cardContent}</a>"""
    } else if (controller || action || url) {
      def linkAttrs = [class: cssClasses]
      if (controller) linkAttrs.controller = controller
      if (action) linkAttrs.action = action
      if (params) linkAttrs.params = params
      if (url) linkAttrs.url = url

      out << g.link(linkAttrs) { cardContent }
    } else {
      out << """<div class="${cssClasses}">${cardContent}</div>"""
    }
  }

  /**
   * Creates a stat card (for displaying statistics)
   *
   * @attr title REQUIRED - Stat title
   * @attr value REQUIRED - Stat value
   * @attr icon - Icon class name
   * @attr faIcon - FontAwesome icon name without prefix (e.g., 'heart') - alternative to icon; combined with faStyle
   * @attr faStyle - FontAwesome style prefix: fas, far, fab, fal, fad (default: 'fas') - only used with faIcon
   * @attr variant - Card color variant (default: 'primary')
   * @attr trend - Trend indicator: up, down, neutral
   * @attr trendValue - Trend percentage/value
   * @attr slot - Optional slot identifier (see cardLink for full slot resolution rules;
   *              'currentSlot' request attribute takes priority over this attribute)
   */
  def statCard = { attrs, body ->
    def title = attrs.title
    def value = attrs.value
    def icon = owner.resolveIcon(attrs)
    def variant = attrs.variant ?: 'primary'
    def trend = attrs.trend
    def trendValue = attrs.trendValue
    def controller = attrs.controller
    def action = attrs.action

    def rawSlot = request?.getAttribute('currentSlot')
    def slot = (rawSlot != null) ? rawSlot : attrs.slot

    def trendIcon = trend == 'up' ? 'fas fa-arrow-up' : (trend == 'down' ? 'fas fa-arrow-down' : '')
    def trendClass = trend == 'up' ? 'glass-trend-up' : (trend == 'down' ? 'glass-trend-down' : 'glass-trend-neutral')

    def cssClasses = "glass-card glass-card-stat glass-card-md glass-card-${variant} glass-card-hover-glow"

    def cardContent = """
            <div class="glass-card-body">
                ${icon ? "<div class='glass-card-stat-icon'><i class='${icon}'></i></div>" : ''}
                <div class='glass-card-stat-content'>
                    <div class='glass-card-stat-title'>${title}</div>
                    <div class='glass-card-stat-value'>${value}</div>
                    ${trend ? "<div class='glass-card-stat-trend ${trendClass}'><i class='${trendIcon}'></i> ${trendValue}</div>" : ''}
                </div>
                <span class='glass-card-shine'></span>
            </div>
        """

    if ((slot != null) && (controller || action)) {
      // Slot-aware URL: /u/$slot/$controller/$action
      def slotUrl = owner.buildSlotUrl(slot, controller, action, null, null)
      out << """<a href="${slotUrl}" class="${cssClasses}">${cardContent}</a>"""
    } else if (controller || action) {
      def linkAttrs = [class: cssClasses]
      if (controller) linkAttrs.controller = controller
      if (action) linkAttrs.action = action

      out << g.link(linkAttrs) { cardContent }
    } else {
      out << """<div class="${cssClasses}">${cardContent}</div>"""
    }
  }
}
