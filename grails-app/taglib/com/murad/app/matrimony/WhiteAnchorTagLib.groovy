package com.murad.app.matrimony

/**
 * Tag library for rendering a glassmorphism-styled white anchor link.
 *
 * Usage:
 * <glass:whiteAnchorStyles /> <!-- Add this once in your layout or page head -->
 * <glass:whiteAnchor text="Forgot Password?" controller="passwordReset" action="forgotPassword" />
 */
class WhiteAnchorTagLib {

    static namespace = "glass"

    /**
     * Renders the CSS styles needed for white anchor links.
     * Add this once in your layout or page <head> section.
     *
     * Usage: <glass:whiteAnchorStyles />
     */
    def whiteAnchorStyles = { attrs ->
        out << '''
<style>
  :root {
    --text-secondary: rgba(255, 255, 255, 0.7);
    --accent-primary: #00d4ff;
  }

  /* White anchor link positioning */
  .white-anchor-container {
    position: absolute;
    right: 0;
    bottom: -25px;
  }

  /* White anchor link styling */
  .white-anchor-link {
    color: var(--accent-primary);
    text-decoration: none;
    font-size: 13px;
    font-weight: 500;
    transition: color 0.2s ease;
  }

  .white-anchor-link:hover,
  .white-anchor-link:active,
  .white-anchor-link:focus {
    color: #ffffff !important;
    text-decoration: none !important;
  }

  .white-anchor-link:focus {
    outline: 2px solid var(--accent-primary);
    outline-offset: 2px;
    border-radius: 2px;
  }

  /* Override any parent styles */
  .signup-link .white-anchor-link,
  div .white-anchor-link,
  a.white-anchor-link {
    color: var(--accent-primary);
  }

  .signup-link .white-anchor-link:hover,
  .signup-link .white-anchor-link:active,
  .signup-link .white-anchor-link:focus,
  div .white-anchor-link:hover,
  div .white-anchor-link:active,
  div .white-anchor-link:focus,
  a.white-anchor-link:hover,
  a.white-anchor-link:active,
  a.white-anchor-link:focus {
    color: #ffffff !important;
    text-decoration: none !important;
  }
</style>
'''
    }

    /**
     * Renders a white anchor link with glassmorphism styling.
     *
     * @attr text REQUIRED - Link text
     * @attr controller - Controller name for the link
     * @attr action - Action name for the link
     * @attr url - Direct URL (if not using controller/action)
     * @attr id - The id attribute
     * @attr class - Additional CSS classes to add to the link
     * @attr containerClass - Additional CSS classes to add to the container
     * @attr position - Position style: 'absolute' (default), 'relative', 'static'
     * @attr align - Alignment when not using absolute positioning: 'left', 'right', 'center'
     * @attr wrapper - Whether to wrap in container div (defaults to true)
     * @attr params - Map of URL parameters
     * @attr target - Link target attribute (e.g., '_blank')
     */
    def whiteAnchor = { attrs ->
        // Required attribute validation
        if (!attrs.text && !attrs.body) {
            throwTagError("Tag [whiteAnchor] is missing required attribute [text]")
        }

        // Extract attributes with defaults
        String text = attrs.text ?: ''
        String controller = attrs.controller ?: ''
        String action = attrs.action ?: ''
        String url = attrs.url ?: ''
        String id = attrs.id ?: ''
        String linkClass = attrs.class ?: ''
        String containerClass = attrs.containerClass ?: ''
        String position = attrs.position ?: 'absolute'
        String align = attrs.align ?: ''
        String target = attrs.target ?: ''
        boolean wrapper = attrs.wrapper?.toString()?.toBoolean() != false // defaults to true
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
        String linkClasses = "white-anchor-link ${linkClass}".trim()

        // Build container classes and inline styles
        String containerClasses = "white-anchor-container ${containerClass}".trim()
        List<String> containerStyles = []

        if (position != 'absolute') {
            containerStyles << "position: ${position}"
        }

        if (align) {
            containerStyles << "text-align: ${align}"
            if (position == 'absolute') {
                // Override absolute positioning if align is specified
                containerStyles << "position: static"
            }
        }

        String inlineStyles = containerStyles ? " style=\"${containerStyles.join('; ')}\"" : ''

        // Build the HTML output
        if (wrapper) {
            out << "<div class=\"${containerClasses.encodeAsHTML()}\""
            if (inlineStyles) {
                out << inlineStyles
            }
            out << '>'
        }

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

        if (wrapper) {
            out << '</div>'
        }
    }
}
