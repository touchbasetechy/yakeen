package com.murad.app.matrimony

/**
 * Tag library for rendering a glassmorphism-styled gradient heading.
 *
 * Usage:
 * <glass:gradientHeadingStyles /> <!-- Add this once in your layout or page head -->
 * <glass:gradientHeading text="Sign In" />
 */
class GradientHeadingTagLib {

  static namespace = "glass"

  /**
   * Renders the CSS styles needed for gradient headings.
   * Add this once in your layout or page <head> section.
   *
   * Usage: <glass:gradientHeadingStyles />
   */
  def gradientHeadingStyles = { attrs ->
    out << '''
<style>
  :root {
    --text-primary: #ffffff;
    --accent-primary: #00d4ff;
  }

  /* Title */
  .gradient-heading {
    color: var(--text-primary);
    font-size: 32px;
    font-weight: 700;
    text-align: center;
    margin-bottom: 40px;
    letter-spacing: -0.5px;
    background: linear-gradient(135deg, #ffffff 0%, var(--accent-primary) 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
  }

  /* Responsive design */
  @media (max-width: 640px) {
    .gradient-heading {
      font-size: 28px;
      margin-bottom: 32px;
    }
  }
</style>
'''
  }

  /**
   * Renders a gradient heading element.
   *
   * @attr text REQUIRED - The heading text to display
   * @attr level - Heading level (h1-h6, defaults to h3)
   * @attr class - Additional CSS classes to add
   * @attr id - The id attribute
   * @attr align - Text alignment (left, center, right - defaults to center)
   * @attr fontSize - Custom font size (e.g., "36px", "2rem")
   * @attr marginBottom - Custom bottom margin (e.g., "20px", "2rem")
   */
  def gradientHeading = { attrs ->
    // Required attribute validation
    if (!attrs.text) {
      throwTagError("Tag [gradientHeading] is missing required attribute [text]")
    }

    // Extract attributes with defaults
    String text = attrs.text
    String level = attrs.level ?: 'h3'
    String additionalClasses = attrs.class ?: ''
    String id = attrs.id ?: ''
    String align = attrs.align ?: 'center'
    String fontSize = attrs.fontSize ?: ''
    String marginBottom = attrs.marginBottom ?: ''

    // Validate heading level
    if (!['h1', 'h2', 'h3', 'h4', 'h5', 'h6'].contains(level)) {
      level = 'h3'
    }

    // Build CSS classes
    String classes = "gradient-heading ${additionalClasses}".trim()

    // Build inline styles if custom values provided
    String inlineStyles = ''
    List<String> styleList = []

    if (align && align != 'center') {
      styleList << "text-align: ${align}"
    }

    if (fontSize) {
      styleList << "font-size: ${fontSize}"
    }

    if (marginBottom) {
      styleList << "margin-bottom: ${marginBottom}"
    }

    if (styleList) {
      inlineStyles = " style=\"${styleList.join('; ')}\""
    }

    // Build the HTML output
    out << "<${level}"

    if (id) {
      out << " id=\"${id.encodeAsHTML()}\""
    }

    out << " class=\"${classes.encodeAsHTML()}\""

    if (inlineStyles) {
      out << inlineStyles
    }

    out << ">"
    out << text.encodeAsHTML()
    out << "</${level}>"
  }
}
