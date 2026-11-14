package com.murad.app.matrimony.card

/**
 * Tag library for rendering a glassmorphism-styled card container.
 *
 * Usage:
 * <glass:glassCardStyles /> <!-- Add this once in your layout or page head -->
 * <glass:glassCard>
 *   <!-- Your content here -->
 * </glass:glassCard>
 */
class GlassCardTagLib {

  static namespace = "glass"

  /**
   * Renders the CSS styles needed for glassmorphism cards.
   * Add this once in your layout or page <head> section.
   *
   * Usage: <glass:glassCardStyles />
   */
  def glassCardStyles = { attrs ->
    out << '''
<style>
  :root {
    --glass-background: rgba(255, 255, 255, 0.08);
    --glass-border: rgba(255, 255, 255, 0.18);
    --glass-shadow: rgba(0, 0, 0, 0.1);
  }

  /* Glassmorphic card */
  .glass-card {
    background: var(--glass-background);
    backdrop-filter: blur(20px);
    -webkit-backdrop-filter: blur(20px);
    border-radius: 24px;
    border: 1px solid var(--glass-border);
    padding: 48px 40px;
    box-shadow: 0 8px 32px var(--glass-shadow),
    inset 0 1px 0 rgba(255, 255, 255, 0.1);
    position: relative;
    animation: slideUp 0.6s ease-out;
  }

  @keyframes slideUp {
    from {
      opacity: 0;
      transform: translateY(30px);
    }
    to {
      opacity: 1;
      transform: translateY(0);
    }
  }

  /* Additional glass effects */
  .glass-card::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 1px;
    background: linear-gradient(90deg,
    transparent 0%,
    rgba(255, 255, 255, 0.3) 50%,
    transparent 100%);
  }

  /* Responsive design */
  @media (max-width: 640px) {
    .glass-card {
      padding: 36px 28px;
    }
  }

  /* Card variants */
  .glass-card.no-animation {
    animation: none;
  }

  .glass-card.compact {
    padding: 32px 24px;
  }

  .glass-card.spacious {
    padding: 56px 48px;
  }
</style>
'''
  }

  /**
   * Renders a glassmorphism card container.
   *
   * @attr id - The id attribute
   * @attr class - Additional CSS classes to add
   * @attr padding - Custom padding (e.g., "20px", "2rem")
   * @attr borderRadius - Custom border radius (e.g., "16px", "1rem")
   * @attr variant - Predefined variant: 'default', 'compact', 'spacious'
   * @attr animate - Whether to show slide-up animation (defaults to true)
   * @attr style - Additional inline styles
   */
  def glassCard = { attrs, body ->
    // Extract attributes with defaults
    String id = attrs.id ?: ''
    String additionalClasses = attrs.class ?: ''
    String padding = attrs.padding ?: ''
    String borderRadius = attrs.borderRadius ?: ''
    String variant = attrs.variant ?: 'default'
    boolean animate = attrs.animate?.toString()?.toBoolean() != false // defaults to true
    String additionalStyle = attrs.style ?: ''

    // Build CSS classes
    List<String> classList = ['glass-card']

    if (!animate) {
      classList << 'no-animation'
    }

    if (variant == 'compact') {
      classList << 'compact'
    } else if (variant == 'spacious') {
      classList << 'spacious'
    }

    if (additionalClasses) {
      classList << additionalClasses
    }

    String classes = classList.join(' ')

    // Build inline styles
    List<String> styleList = []

    if (padding) {
      styleList << "padding: ${padding}"
    }

    if (borderRadius) {
      styleList << "border-radius: ${borderRadius}"
    }

    if (additionalStyle) {
      styleList << additionalStyle
    }

    String inlineStyles = styleList ? " style=\"${styleList.join('; ')}\"" : ''

    // Build the HTML output
    out << '<div'

    if (id) {
      out << " id=\"${id.encodeAsHTML()}\""
    }

    out << " class=\"${classes.encodeAsHTML()}\""

    if (inlineStyles) {
      out << inlineStyles
    }

    out << '>'

    // Render body content
    out << body()

    out << '</div>'
  }
}
