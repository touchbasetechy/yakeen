package com.murad.app.matrimony

/**
 * Tag library for rendering a glassmorphism-styled green/accent anchor link.
 *
 * Usage:
 * <glass:greenAnchorStyles /> <!-- Add this once in your layout or page head -->
 * <glass:greenAnchor text="Sign up here" controller="auth" action="register" />
 */
class GreenAnchorTagLib {

  static namespace = "glass"

  /**
   * Renders the CSS styles needed for green/accent anchor links.
   * Add this once in your layout or page <head> section.
   *
   * Usage: <glass:greenAnchorStyles />
   */
  def greenAnchorStyles = { attrs ->
    out << '''
<style>
  :root {
    --text-secondary: rgba(255, 255, 255, 0.7);
    --accent-primary: #7c3aed;
    --text-primary: #ffffff;
  }

  /* Green anchor link styling */
  .green-anchor-link {
    color: var(--text-primary);
    text-decoration: none;
    font-weight: 600;
    transition: all 0.2s ease;
  }

  .green-anchor-link:hover {
    color: var(--accent-primary);
    text-decoration: none;
  }

  .green-anchor-link:focus {
    outline: 2px solid var(--accent-primary);
    outline-offset: 2px;
    border-radius: 2px;
  }
</style>
'''
  }

  /**
   * Renders a green/accent anchor link with glassmorphism styling.
   *
   * @attr text REQUIRED - Link text
   * @attr controller - Controller name for the link
   * @attr action - Action name for the link
   * @attr url - Direct URL (if not using controller/action)
   * @attr id - The id attribute
   * @attr class - Additional CSS classes to add to the link
   * @attr params - Map of URL parameters
   * @attr target - Link target attribute (e.g., '_blank')
   */
  def greenAnchor = { attrs ->
    // Required attribute validation
    if (!attrs.text) {
      throwTagError("Tag [greenAnchor] is missing required attribute [text]")
    }

    // Extract attributes with defaults
    String text = attrs.text
    String controller = attrs.controller ?: ''
    String action = attrs.action ?: ''
    String url = attrs.url ?: ''
    String id = attrs.id ?: ''
    String linkClass = attrs.class ?: ''
    String target = attrs.target ?: ''
    Map params = attrs.params ?: [:]

    // Build the link URL
    String linkUrl = ''
    if (url) {
      linkUrl = url
    } else if (controller || action) {
      linkUrl = createLink(controller: controller, action: action, params: params)
    } else {
      linkUrl = '#'
    }

    // Build link classes
    String linkClasses = "green-anchor-link ${linkClass}".trim()

    // Build the HTML output
    out << '<a href="' + linkUrl.encodeAsHTML() + '" '
    out << 'class="' + linkClasses.encodeAsHTML() + '" '

    if (id) {
      out << 'id="' + id.encodeAsHTML() + '" '
    }

    if (target) {
      out << 'target="' + target.encodeAsHTML() + '" '
    }

    out << '>'
    out << text.encodeAsHTML()
    out << '</a>'
  }
}
