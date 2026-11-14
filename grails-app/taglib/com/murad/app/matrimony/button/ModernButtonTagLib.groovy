package com.murad.app.matrimony.button

/**
 * Modern Glassmorphism Button TagLib
 *
 * Usage examples:
 * <glass:modernBtn text="Click Me" />
 * <glass:modernBtn text="Submit" variant="primary" size="lg" />
 * <glass:modernBtn text="Magic" variant="gradient" icon="fas fa-sparkles" />
 *
 * Include in your GSP:
 * <glass:modernBtnScripts />
 * <glass:orbs />
 * <glass:modernBtnStyles />
 */
class ModernButtonTagLib {

  static namespace = "glass"

  /**
   * Creates a modern glassmorphism button
   *
   * @attr text REQUIRED - Button text content
   * @attr variant - Button color variant: default, primary, success, danger, warning, info, gradient (default: 'default')
   * @attr size - Button size: sm, md, lg, xl (default: 'md')
   * @attr href - If provided, renders as link button
   * @attr onclick - JavaScript onclick handler
   * @attr type - Button type: button, submit, reset (default: 'button')
   * @attr icon - Icon class name (optional, e.g., 'fas fa-heart')
   * @attr iconPosition - Icon position: left, right (default: 'left')
   * @attr iconOnly - Boolean for icon-only button (default: false)
   * @attr disabled - Boolean to disable button
   * @attr class - Additional CSS classes
   * @attr id - Button ID attribute
   * @attr style - Additional inline styles
   * @attr pill - Boolean for fully rounded pill shape (default: false)
   * @attr outline - Boolean for outline style (default: false)
   * @attr loading - Boolean for loading state (default: false)
   * @attr pulse - Boolean for pulse animation (default: false)
   */
  def modernBtn = { attrs, body ->
    def text = attrs.text ?: body()
    def variant = attrs.variant ?: 'default'
    def size = attrs.size ?: 'md'
    def href = attrs.href
    def onclick = attrs.onclick
    def type = attrs.type ?: 'button'
    def icon = attrs.icon
    def iconPosition = attrs.iconPosition ?: 'left'
    def iconOnly = attrs.iconOnly?.toBoolean() ?: false
    def disabled = attrs.disabled ? 'disabled' : ''
    def customClass = attrs.class ?: ''
    def id = attrs.id ? "id='${attrs.id}'" : ''
    def customStyle = attrs.style ?: ''
    def pill = attrs.pill?.toBoolean() ?: false
    def outline = attrs.outline?.toBoolean() ?: false
    def loading = attrs.loading?.toBoolean() ?: false
    def pulse = attrs.pulse?.toBoolean() ?: false

    def cssClasses = [
      'glass-btn',
      "glass-btn-${size}",
      "glass-btn-${variant}",
      pill ? 'glass-btn-pill' : '',
      outline ? 'glass-btn-outline' : '',
      iconOnly ? 'glass-btn-icon' : '',
      loading ? 'loading' : '',
      pulse ? 'pulse' : '',
      customClass
    ].findAll { it }.join(' ')

    def content = ""
    if (loading) {
      content = "<i class='fas fa-circle-notch'></i>"
      if (!iconOnly && text) {
        content += " ${text}"
      }
    } else {
      if (icon && iconPosition == 'left') {
        content += "<i class='${icon}'></i>"
        if (!iconOnly && text) {
          content += " ${text}"
        }
      } else if (icon && iconPosition == 'right') {
        if (text) {
          content += "${text} "
        }
        content += "<i class='${icon}'></i>"
      } else if (iconOnly && icon) {
        content = "<i class='${icon}'></i>"
      } else {
        content = text
      }
    }

    if (href) {
      out << """<a href="${href}" class="${cssClasses}" ${id} style="${customStyle}" ${onclick ? "onclick=\"${onclick}\"" : ''}>${content}</a>"""
    } else {
      out << """<button type="${type}" class="${cssClasses}" ${id} style="${customStyle}" ${disabled} ${onclick ? "onclick=\"${onclick}\"" : ''}>${content}</button>"""
    }
  }

  /**
   * Creates a button group container
   *
   * @attr vertical - Boolean for vertical layout (default: false)
   * @attr class - Additional CSS classes
   */
  def modernBtnGroup = { attrs, body ->
    def vertical = attrs.vertical?.toBoolean() ? 'glass-btn-group-vertical' : 'glass-btn-group'
    def customClass = attrs.class ?: ''
    def cssClasses = "${vertical} ${customClass}".trim()

    out << """<div class="${cssClasses}">${body()}</div>"""
  }

  /**
   * Creates a section container with modern styling
   *
   * @attr title - Section title
   * @attr tag - Optional tag label
   * @attr description - Optional description text
   * @attr class - Additional CSS classes
   */
  def modernBtnSection = { attrs, body ->
    def title = attrs.title
    def tag = attrs.tag
    def description = attrs.description
    def customClass = attrs.class ?: ''
    def cssClasses = "section ${customClass}".trim()

    out << """<section class="${cssClasses}">"""
    if (tag) {
      out << """<span class="tag">${tag}</span>"""
    }
    if (title) {
      out << """<h2 class="section-title">${title}</h2>"""
    }
    if (description) {
      out << """<p class="section-description">${description}</p>"""
    }
    out << body()
    out << """</section>"""
  }

  /**
   * Injects the required JavaScript for modern glassmorphism effects
   * Place this before closing </body> tag
   */
  def modernBtnScripts = { attrs ->
    def includeRipple = attrs.includeRipple != 'false'
    def includeParallax = attrs.includeParallax != 'false'

    out << """<script>"""

    if (includeRipple) {
      out << '''
document.querySelectorAll('.glass-btn').forEach(button => {
    button.addEventListener('click', function(e) {
        const rect = this.getBoundingClientRect();
        const x = e.clientX - rect.left;
        const y = e.clientY - rect.top;

        const ripple = document.createElement('span');
        ripple.style.cssText = `
            position: absolute;
            border-radius: 50%;
            background: rgba(255, 255, 255, 0.5);
            width: 30px;
            height: 30px;
            left: ${x}px;
            top: ${y}px;
            transform: translate(-50%, -50%) scale(0);
            animation: rippleEffect 0.6s ease-out;
            pointer-events: none;
            z-index: 100;
        `;

        this.appendChild(ripple);
        setTimeout(() => ripple.remove(), 600);
    });
});

const style = document.createElement('style');
style.textContent = `
    @keyframes rippleEffect {
        to {
            transform: translate(-50%, -50%) scale(15);
            opacity: 0;
        }
    }
`;
document.head.appendChild(style);
'''
    }

    if (includeParallax) {
      out << '''
document.addEventListener('mousemove', (e) => {
    const orbs = document.querySelectorAll('.glass-orb');
    const mouseX = e.clientX / window.innerWidth;
    const mouseY = e.clientY / window.innerHeight;

    orbs.forEach((orb, index) => {
        const speed = (index + 1) * 20;
        const x = (mouseX - 0.5) * speed;
        const y = (mouseY - 0.5) * speed;
        orb.style.transform = `translate(${x}px, ${y}px)`;
    });
});
'''
    }

    out << """</script>"""
  }

  /**
   * Creates the animated background orbs
   */
  def orbs = { attrs ->
    out << """
<div class="glass-orb glass-orb-1"></div>
<div class="glass-orb glass-orb-2"></div>
<div class="glass-orb glass-orb-3"></div>
"""
  }
}
