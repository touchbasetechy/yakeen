package com.murad.app.matrimony

class GlassmorphicBadgeTagLib {
  static namespace = "glass"

  /**
   * Glassmorphic Badge Link Tag
   *
   * Usage:
   * <glass:badgeLink text="Premium" variant="primary" />
   * <glass:badgeLink text="New" variant="success" icon="★" />
   * <glass:badgeLink text="Messages" variant="primary" faIcon="fa-solid fa-envelope" />
   * <glass:badgeLink text="Star" variant="warning" faIcon="fa-solid fa-star" iconPosition="left" />
   * <glass:badgeLink text="Delete" variant="danger" faIcon="fa-solid fa-trash" iconPosition="right" />
   * <glass:badgeLink text="Limited" variant="gradient" animated="true" />
   * <glass:badgeLink text="Dashboard" variant="primary" controller="dashboard" action="index" />
   * <glass:badgeLink text="Settings" variant="info" faIcon="fa-solid fa-gear" controller="settings" action="show" params="[id: 1]" />
   * <glass:badgeLink text="Inbox" variant="primary" controller="messages" action="index" slot="2" />
   *
   * Slot-aware URL resolution (checked in this order):
   * 1. If a `slot` attribute is passed, the link is built as /u/$slot/$controller/$action
   * 2. Else if the request has an attribute named `currentSlot`, the link is built as
   *    /u/$currentSlot/$controller/$action
   * 3. Else, falls back to a normal controller/action link (via g.link) with no /u/ prefix.
   *
   * `id` and `params` still apply on top of a slot-based URL: `id` is appended as an extra
   * path segment, and `params` is appended as a query string.
   *
   * Icon Notes:
   * - Use `faIcon` for Font Awesome icons (e.g., "fa-solid fa-envelope", "fa-regular fa-bell", "fa-brands fa-github")
   * - Use `icon` for plain text/emoji icons (legacy, e.g., "★")
   * - `faIcon` takes priority over `icon` if both are provided
   * - `iconPosition`: "left" (default) or "right"
   * - `iconOnly`: set to "true" to render only the icon with no text (good for compact badges)
   */
  def badgeLink = { attrs, body ->
    def text = attrs.text ?: body() ?: 'Badge'
    def variant = attrs.variant ?: 'primary'
    def faIcon = attrs.faIcon ?: ''          // e.g. "fa-solid fa-envelope"
    def icon = attrs.icon ?: ''             // legacy plain-text/emoji icon
    def iconPosition = attrs.iconPosition ?: 'left' // "left" or "right"
    def iconOnly = attrs.iconOnly == 'true' || attrs.iconOnly == true
    def size = attrs.size ?: 'md'
    def animated = attrs.animated == 'true' || attrs.animated == true
    def glow = attrs.glow == 'true' || attrs.glow == true
    def pulse = attrs.pulse == 'true' || attrs.pulse == true
    def customClass = attrs.class ?: ''

    // Link attributes
    def controller = attrs.controller
    def action = attrs.action
    def params = attrs.params
    def id = attrs.id
    def fragment = attrs.fragment
    def mapping = attrs.mapping
    def url = attrs.url

    // Slot-aware routing
    def rawSlot = request?.getAttribute('currentSlot')
    def slot = (rawSlot != null) ? rawSlot : attrs.slot

    def variantClasses = [
      'primary' : 'glass-badge-primary',
      'success' : 'glass-badge-success',
      'warning' : 'glass-badge-warning',
      'danger'  : 'glass-badge-danger',
      'info'    : 'glass-badge-info',
      'gradient': 'glass-badge-gradient',
      'aurora'  : 'glass-badge-aurora',
      'neon'    : 'glass-badge-neon'
    ]

    def sizeClasses = [
      'sm': 'glass-badge-sm',
      'md': 'glass-badge-md',
      'lg': 'glass-badge-lg',
      'xl': 'glass-badge-xl'
    ]

    def classes = "glass-badge ${variantClasses[variant] ?: 'glass-badge-primary'} ${sizeClasses[size] ?: 'glass-badge-md'}"
    if (animated) classes += " glass-badge-animated"
    if (glow) classes += " glass-badge-glow"
    if (pulse) classes += " glass-badge-pulse"
    if (iconOnly) classes += " glass-badge-icon-only"
    if (customClass) classes += " ${customClass}"

    // ── Resolve icon markup ──────────────────────────────────────────────────
    // faIcon takes priority; falls back to legacy plain-text icon
    def resolvedIconMarkup = ''
    if (faIcon) {
      resolvedIconMarkup = "<i class='${faIcon} glass-badge-fa-icon' style='color: inherit;'></i>"
    } else if (icon) {
      resolvedIconMarkup = "<span class='glass-badge-icon' style='color: inherit;'>${icon}</span>"
    }

    // ── Build badge content ──────────────────────────────────────────────────
    def badgeContent = ''

    if (resolvedIconMarkup && iconPosition == 'left') {
      badgeContent += resolvedIconMarkup
    }

    if (!iconOnly) {
      badgeContent += "<span class='glass-badge-text'>${text}</span>"
    }

    if (resolvedIconMarkup && iconPosition == 'right') {
      badgeContent += resolvedIconMarkup
    }

    badgeContent += "<span class='glass-badge-shine'></span>"

    // ── Render: slot-scoped link / normal link / span ────────────────────────
    if ((slot != null) && (controller || action)) {
      // /u/$slot/$controller/$action[/$id][?params]
      def slotUrl = "/u/${slot}/${controller ?: ''}/${action ?: ''}"
      if (id != null) {
        slotUrl += "/${id}"
      }
      if (params instanceof Map && params) {
        def query = params.collect { k, v -> "${k.encodeAsURL()}=${v?.toString()?.encodeAsURL()}" }.join('&')
        slotUrl += "?${query}"
      }
      out << """<a href="${slotUrl}" class="${classes}">${badgeContent}</a>"""
    } else if (controller || action || url) {
      def linkAttrs = [:]
      if (controller) linkAttrs.controller = controller
      if (action) linkAttrs.action = action
      if (params) linkAttrs.params = params
      if (id) linkAttrs.id = id
      if (fragment) linkAttrs.fragment = fragment
      if (mapping) linkAttrs.mapping = mapping
      if (url) linkAttrs.url = url
      linkAttrs.class = classes

      out << g.link(linkAttrs) { badgeContent }
    } else {
      out << """<span class="${classes}">${badgeContent}</span>"""
    }
  }

  /**
   * Badge Group Tag
   *
   * Usage:
   * <glass:badgeGroup>
   *   <glass:badgeLink text="Feature" variant="primary" faIcon="fa-solid fa-bolt" />
   *   <glass:badgeLink text="New" variant="success" faIcon="fa-solid fa-sparkles" />
   * </glass:badgeGroup>
   */
  def badgeGroup = { attrs, body ->
    def alignment = attrs.alignment ?: 'left'
    def spacing = attrs.spacing ?: 'md'

    out << """
        <div class="glass-badge-group glass-badge-align-${alignment} glass-badge-spacing-${spacing}">
            ${body()}
        </div>
        """
  }

  /**
   * Status Badge Tag
   *
   * Usage:
   * <glass:statusBadge status="online"  text="Online" />
   * <glass:statusBadge status="offline" text="Offline" />
   * <glass:statusBadge status="online"  text="View Profile" faIcon="fa-solid fa-circle-dot" controller="profile" action="show" />
   *
   * - `faIcon`: optional Font Awesome icon to replace the default status dot
   * - `showDot`: set "false" to hide the status indicator entirely
   */
  def statusBadge = { attrs, body ->
    def status = attrs.status ?: 'neutral'
    def text = attrs.text ?: body() ?: status.capitalize()
    def showDot = attrs.showDot != 'false'
    def faIcon = attrs.faIcon ?: ''

    // Link attributes
    def controller = attrs.controller
    def action = attrs.action
    def params = attrs.params
    def id = attrs.id
    def url = attrs.url

    def statusColors = [
      'online' : '#10b981',
      'offline': '#6b7280',
      'busy'   : '#f59e0b',
      'away'   : '#eab308',
      'dnd'    : '#ef4444'
    ]

    def dotColor = statusColors[status] ?: '#6b7280'

    def classes = "glass-badge glass-badge-status glass-badge-md"

    // ── Status indicator: FA icon or coloured dot ────────────────────────────
    def indicatorMarkup = ''
    if (showDot) {
      if (faIcon) {
        indicatorMarkup = "<i class='${faIcon} glass-badge-fa-icon' style='color: ${dotColor};'></i>"
      } else {
        indicatorMarkup = "<span class='glass-status-dot' style='background: ${dotColor};'></span>"
      }
    }

    def badgeContent = """
            ${indicatorMarkup}
            <span class='glass-badge-text'>${text}</span>
            <span class='glass-badge-shine'></span>
        """

    if (controller || action || url) {
      def linkAttrs = [:]
      if (controller) linkAttrs.controller = controller
      if (action) linkAttrs.action = action
      if (params) linkAttrs.params = params
      if (id) linkAttrs.id = id
      if (url) linkAttrs.url = url
      linkAttrs.class = classes

      out << g.link(linkAttrs) { badgeContent }
    } else {
      out << """<span class="${classes}">${badgeContent}</span>"""
    }
  }

  /**
   * Counter Badge Tag
   *
   * Usage:
   * <glass:counterBadge count="5" />
   * <glass:counterBadge count="99+" variant="danger" />
   * <glass:counterBadge count="12" variant="primary" faIcon="fa-solid fa-bell" controller="messages" action="index" />
   *
   * - `faIcon`: optional Font Awesome icon shown to the left of the count
   */
  def counterBadge = { attrs, body ->
    def count = attrs.count ?: '0'
    def variant = attrs.variant ?: 'primary'
    def max = attrs.max ? attrs.max as Integer : null
    def faIcon = attrs.faIcon ?: ''

    // Link attributes
    def controller = attrs.controller
    def action = attrs.action
    def params = attrs.params
    def id = attrs.id
    def url = attrs.url

    def displayCount = count
    if (max && count.isNumber() && (count as Integer) > max) {
      displayCount = "${max}+"
    }

    def classes = "glass-badge glass-badge-counter glass-badge-${variant} glass-badge-sm glass-badge-pulse"

    def badgeContent = """
            ${faIcon ? "<i class='${faIcon} glass-badge-fa-icon' style='color: inherit;'></i>" : ''}
            <span class='glass-badge-text'>${displayCount}</span>
            <span class='glass-badge-shine'></span>
        """

    if (controller || action || url) {
      def linkAttrs = [:]
      if (controller) linkAttrs.controller = controller
      if (action) linkAttrs.action = action
      if (params) linkAttrs.params = params
      if (id) linkAttrs.id = id
      if (url) linkAttrs.url = url
      linkAttrs.class = classes

      out << g.link(linkAttrs) { badgeContent }
    } else {
      out << """<span class="${classes}">${badgeContent}</span>"""
    }
  }
}
