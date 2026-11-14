package com.murad.app.matrimony

class GlassBadgeTagLib {
  static namespace = "glass"

  /**
   * Renders a glassmorphic badge
   *
   * Attributes:
   * - text: The badge text (required)
   * - variant: Badge color variant (default, primary, success, danger, warning, info, dark)
   * - size: Badge size (sm, md, lg) - default is md
   * - pill: Whether to make badge pill-shaped (true/false)
   * - icon: Optional icon class (e.g., "fa fa-check")
   * - iconPosition: Icon position (left/right) - default is left
   * - className: Additional CSS classes
   * - id: Optional ID attribute
   * - style: Optional inline styles
   * - count: Optional count number (displays as notification badge)
   * - clickable: Whether badge is clickable (true/false)
   * - href: Optional link URL
   * - onClick: Optional JavaScript onclick handler
   *
   * Examples:
   * <glass:badge text="New"/>
   * <glass:badge text="Success" variant="success"/>
   * <glass:badge text="5" variant="danger" pill="true"/>
   * <glass:badge text="Premium" variant="primary" icon="fa fa-star"/>
   */
  def badge = { attrs ->
    def text = attrs.text ?: ''
    def variant = attrs.variant ?: 'default'
    def size = attrs.size ?: 'md'
    def pill = attrs.pill == 'true' || attrs.pill == true
    def icon = attrs.icon ?: ''
    def iconPosition = attrs.iconPosition ?: 'left'
    def className = attrs.className ?: ''
    def id = attrs.id ? "id=\"${attrs.id}\"" : ''
    def style = attrs.style ? "style=\"${attrs.style}\"" : ''
    def count = attrs.count ?: ''
    def clickable = attrs.clickable == 'true' || attrs.clickable == true
    def href = attrs.href ?: ''
    def onClick = attrs.onClick ? "onclick=\"${attrs.onClick}\"" : ''

    // Build CSS classes
    def classes = ['glass-badge']
    classes << "glass-badge-${variant}"
    classes << "glass-badge-${size}"
    if (pill) classes << 'glass-badge-pill'
    if (clickable || href) classes << 'glass-badge-clickable'
    if (className) classes << className

    def classString = classes.join(' ')

    // Build badge content
    def badgeContent = ''

    if (icon && iconPosition == 'left') {
      badgeContent += "<i class=\"${icon} glass-badge-icon\"></i> "
    }

    badgeContent += text

    if (icon && iconPosition == 'right') {
      badgeContent += " <i class=\"${icon} glass-badge-icon\"></i>"
    }

    if (count) {
      badgeContent += " <span class=\"glass-badge-count\">${count}</span>"
    }

    // Render badge
    if (href) {
      out << "<a href=\"${href}\" class=\"${classString}\" ${id} ${style}>${badgeContent}</a>"
    } else {
      out << "<span class=\"${classString}\" ${id} ${style} ${onClick}>${badgeContent}</span>"
    }
  }

  /**
   * Renders badge styles (include once in your GSP head)
   *
   * Example:
   * <glass:badgeStyles/>
   */
  def badgeStyles = { attrs ->
    out << '''
<style>
  /* Glass Badge Base Styles */
  .glass-badge,
  a.glass-badge,
  a.glass-badge:link,
  a.glass-badge:visited {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    font-family: \'Outfit\', sans-serif;
    font-weight: 500;
    line-height: 1;
    text-align: center;
    white-space: nowrap;
    vertical-align: baseline;
    background: rgba(255, 255, 255, 0.15);
    backdrop-filter: blur(10px);
    -webkit-backdrop-filter: blur(10px);
    border: 1px solid rgba(255, 255, 255, 0.25);
    color: rgba(255, 255, 255, 0.95);
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
    transition: all 0.3s ease;
    text-decoration: none;
  }

  /* Badge Sizes */
  .glass-badge-sm {
    padding: 4px 10px;
    font-size: 10px;
    border-radius: 8px;
  }

  .glass-badge-md {
    padding: 6px 14px;
    font-size: 12px;
    border-radius: 12px;
  }

  .glass-badge-lg {
    padding: 8px 18px;
    font-size: 14px;
    border-radius: 14px;
  }

  /* Pill Shape */
  .glass-badge-pill {
    border-radius: 50px;
  }

  /* ── Icon Sizing — locked at 1em relative to badge font-size,
     consistent across ALL states: rest, hover, focus, active, visited.
     will-change: font-size prevents GPU recomposite jank.
     flex-shrink: 0 stops the icon from squishing inside the flex row. ── */
  .glass-badge .glass-badge-icon,
  .glass-badge i,
  .glass-badge .glass-badge-fa-icon {
    font-size: 1em;
    line-height: 1;
    flex-shrink: 0;
    color: inherit;
    will-change: font-size;
    /* Prevent any transform on the badge from scaling the icon independently */
    display: inline-flex;
    align-items: center;
    justify-content: center;
    width: 1em;
    height: 1em;
  }

  /* Explicitly re-state icon size on every interactive state so nothing overrides it */
  .glass-badge:hover .glass-badge-icon,
  .glass-badge:hover i,
  .glass-badge:hover .glass-badge-fa-icon,
  .glass-badge:focus .glass-badge-icon,
  .glass-badge:focus i,
  .glass-badge:focus .glass-badge-fa-icon,
  .glass-badge:active .glass-badge-icon,
  .glass-badge:active i,
  .glass-badge:active .glass-badge-fa-icon,
  .glass-badge:visited .glass-badge-icon,
  .glass-badge:visited i,
  .glass-badge:visited .glass-badge-fa-icon,
  a.glass-badge:hover .glass-badge-icon,
  a.glass-badge:hover i,
  a.glass-badge:hover .glass-badge-fa-icon,
  a.glass-badge:focus .glass-badge-icon,
  a.glass-badge:focus i,
  a.glass-badge:focus .glass-badge-fa-icon,
  a.glass-badge:active .glass-badge-icon,
  a.glass-badge:active i,
  a.glass-badge:active .glass-badge-fa-icon {
    font-size: 1em !important;
    line-height: 1 !important;
    width: 1em !important;
    height: 1em !important;
    color: inherit !important;
    transform: none !important;
  }

  /* Badge Variants — base + link/visited states */
  .glass-badge-default,
  a.glass-badge-default,
  a.glass-badge-default:link,
  a.glass-badge-default:visited {
    background: rgba(255, 255, 255, 0.15);
    border-color: rgba(255, 255, 255, 0.25);
    color: rgba(255, 255, 255, 0.95);
  }

  .glass-badge-primary,
  a.glass-badge-primary,
  a.glass-badge-primary:link,
  a.glass-badge-primary:visited {
    background: rgba(102, 126, 234, 0.3);
    border-color: rgba(102, 126, 234, 0.5);
    color: #ffffff;
  }

  .glass-badge-success,
  a.glass-badge-success,
  a.glass-badge-success:link,
  a.glass-badge-success:visited {
    background: rgba(0, 255, 136, 0.2);
    border-color: rgba(0, 255, 136, 0.4);
    color: #00ff88;
  }

  .glass-badge-danger,
  a.glass-badge-danger,
  a.glass-badge-danger:link,
  a.glass-badge-danger:visited {
    background: rgba(255, 82, 82, 0.2);
    border-color: rgba(255, 82, 82, 0.4);
    color: #ff5252;
  }

  .glass-badge-warning,
  a.glass-badge-warning,
  a.glass-badge-warning:link,
  a.glass-badge-warning:visited {
    background: rgba(255, 193, 7, 0.2);
    border-color: rgba(255, 193, 7, 0.4);
    color: #ffc107;
  }

  .glass-badge-info,
  a.glass-badge-info,
  a.glass-badge-info:link,
  a.glass-badge-info:visited {
    background: rgba(33, 150, 243, 0.2);
    border-color: rgba(33, 150, 243, 0.4);
    color: #2196f3;
  }

  .glass-badge-dark,
  a.glass-badge-dark,
  a.glass-badge-dark:link,
  a.glass-badge-dark:visited {
    background: rgba(52, 58, 64, 0.4);
    border-color: rgba(52, 58, 64, 0.6);
    color: rgba(255, 255, 255, 0.95);
  }

  /* ─── Hover / Focus / Active — lock colors, no Bootstrap bleed ─── */

  .glass-badge:hover,
  .glass-badge:focus,
  .glass-badge-clickable:hover,
  .glass-badge-clickable:focus,
  a.glass-badge:hover,
  a.glass-badge:focus {
    background: rgba(255, 255, 255, 0.2);
    border-color: rgba(255, 255, 255, 0.35);
    color: rgba(255, 255, 255, 0.95);
    text-decoration: none;
    outline: none;
    transform: translateY(-2px);
    box-shadow: 0 6px 20px rgba(0, 0, 0, 0.15);
  }

  a.glass-badge-primary:hover,
  a.glass-badge-primary:focus {
    background: rgba(102, 126, 234, 0.4);
    border-color: rgba(102, 126, 234, 0.6);
    color: #ffffff;
    text-decoration: none;
  }

  .glass-badge-primary:hover,
  .glass-badge-primary:focus {
    background: rgba(102, 126, 234, 0.4);
    border-color: rgba(102, 126, 234, 0.6);
    color: #ffffff;
  }

  a.glass-badge-success:hover,
  a.glass-badge-success:focus,
  .glass-badge-success:hover,
  .glass-badge-success:focus {
    background: rgba(0, 255, 136, 0.3);
    border-color: rgba(0, 255, 136, 0.5);
    color: #00ff88;
    text-decoration: none;
  }

  a.glass-badge-danger:hover,
  a.glass-badge-danger:focus,
  .glass-badge-danger:hover,
  .glass-badge-danger:focus {
    background: rgba(255, 82, 82, 0.3);
    border-color: rgba(255, 82, 82, 0.5);
    color: #ff5252;
    text-decoration: none;
  }

  a.glass-badge-warning:hover,
  a.glass-badge-warning:focus,
  .glass-badge-warning:hover,
  .glass-badge-warning:focus {
    background: rgba(255, 193, 7, 0.3);
    border-color: rgba(255, 193, 7, 0.5);
    color: #ffc107;
    text-decoration: none;
  }

  a.glass-badge-info:hover,
  a.glass-badge-info:focus,
  .glass-badge-info:hover,
  .glass-badge-info:focus {
    background: rgba(33, 150, 243, 0.3);
    border-color: rgba(33, 150, 243, 0.5);
    color: #2196f3;
    text-decoration: none;
  }

  a.glass-badge-dark:hover,
  a.glass-badge-dark:focus,
  .glass-badge-dark:hover,
  .glass-badge-dark:focus {
    background: rgba(52, 58, 64, 0.5);
    border-color: rgba(52, 58, 64, 0.7);
    color: rgba(255, 255, 255, 0.95);
    text-decoration: none;
  }

  /* Badge Count */
  .glass-badge-count {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    min-width: 18px;
    height: 18px;
    padding: 0 5px;
    font-size: 10px;
    font-weight: 600;
    line-height: 1;
    background: rgba(255, 255, 255, 0.3);
    border-radius: 9px;
    margin-left: 4px;
  }

  /* Active State */
  .glass-badge:active,
  a.glass-badge:active {
    transform: translateY(0);
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    text-decoration: none;
  }

  /* Disabled State */
  .glass-badge.disabled,
  .glass-badge:disabled {
    opacity: 0.5;
    cursor: not-allowed;
    pointer-events: none;
  }
</style>
'''
  }

  /**
   * Renders a notification badge (small circular badge with count)
   *
   * Attributes:
   * - count: The notification count (required)
   * - variant: Badge color variant (default is danger)
   * - max: Maximum count to display (shows "99+" if exceeded)
   * - position: Position (top-right, top-left, bottom-right, bottom-left)
   * - className: Additional CSS classes
   *
   * Example:
   * <glass:notificationBadge count="5"/>
   * <glass:notificationBadge count="150" max="99"/>
   */
  def notificationBadge = { attrs ->
    def count = attrs.count ? attrs.count.toInteger() : 0
    def variant = attrs.variant ?: 'danger'
    def max = attrs.max ? attrs.max.toInteger() : null
    def position = attrs.position ?: 'top-right'
    def className = attrs.className ?: ''

    def displayCount = count
    if (max && count > max) {
      displayCount = "${max}+"
    }

    if (count > 0) {
      out << "<span class=\"glass-notification-badge glass-notification-badge-${position} glass-badge-${variant} ${className}\">${displayCount}</span>"
    }
  }

  /**
   * Renders notification badge styles
   */
  def notificationBadgeStyles = { attrs ->
    out << '''
<style>
  .glass-notification-badge {
    position: absolute;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    min-width: 20px;
    height: 20px;
    padding: 0 6px;
    font-family: \'Outfit\', sans-serif;
    font-size: 11px;
    font-weight: 600;
    line-height: 1;
    color: white;
    background: rgba(255, 82, 82, 0.9);
    backdrop-filter: blur(10px);
    -webkit-backdrop-filter: blur(10px);
    border: 2px solid rgba(255, 255, 255, 0.3);
    border-radius: 10px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
    z-index: 10;
  }

  .glass-notification-badge-top-right {
    top: -8px;
    right: -8px;
  }

  .glass-notification-badge-top-left {
    top: -8px;
    left: -8px;
  }

  .glass-notification-badge-bottom-right {
    bottom: -8px;
    right: -8px;
  }

  .glass-notification-badge-bottom-left {
    bottom: -8px;
    left: -8px;
  }
</style>
'''
  }
}
