package com.murad.app.matrimony.page

/**
 * Simple Glassmorphic Footer Tag Library
 * Minimal footer with copyright display
 */
class SimpleFooterTagLib {

  static namespace = "glass"

  /**
   * Renders a minimal glassmorphic footer with copyright text
   *
   * @attr companyName Company name for copyright (required)
   * @attr position Position of footer: "fixed-bottom", "relative" (default: "relative")
   * @attr transparent Boolean to make footer fully transparent (default: true)
   * @attr theme Color theme: "light", "dark", "auto" (default: "dark")
   * @attr height Height of footer (default: "60px")
   * @attr blurAmount Blur amount (default: "12px")
   * @attr cssClass Additional CSS classes to apply
   * @attr container Container type: "fluid", "fixed" (default: "fluid")
   * @attr textAlign Text alignment: "left", "center", "right" (default: "center")
   */
  def simplefooter = { attrs ->

    // Extract attributes with defaults
    String companyName = attrs.companyName ?: ''
    String position = attrs.position ?: 'relative'
    String theme = attrs.theme ?: 'dark'
    String height = attrs.height ?: '60px'
    String blurAmount = attrs.blurAmount ?: '12px'
    String cssClass = attrs.cssClass ?: ''
    String container = attrs.container ?: 'fluid'
    String textAlign = attrs.textAlign ?: 'center'

    boolean transparent = attrs.transparent instanceof Boolean ? attrs.transparent : true

    // Generate unique ID for this footer
    String footerId = "simple-footer-${System.currentTimeMillis()}"

    // Build the HTML output
    out << """
<footer class="simple-footer ${position} ${theme} ${transparent ? 'transparent' : ''} ${cssClass}" id="${footerId}">
    <style>
        .simple-footer {
            width: 100%;
            min-height: ${height};
            display: flex;
            align-items: center;
            z-index: 999;
            transition: all 0.3s ease;
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
            background: rgba(17, 24, 39, 0.75);
            backdrop-filter: blur(${blurAmount});
            -webkit-backdrop-filter: blur(${blurAmount});
            border-top: 1px solid rgba(255, 255, 255, 0.1);
            box-shadow: 0 -4px 30px rgba(0, 0, 0, 0.1);
        }

        .simple-footer.transparent {
            background: transparent;
            backdrop-filter: none;
            -webkit-backdrop-filter: none;
            border: none;
            box-shadow: none;
        }

        .simple-footer.fixed-bottom {
            position: fixed;
            bottom: 0;
            left: 0;
        }

        .simple-footer.relative {
            position: relative;
        }

        .simple-footer-container {
            width: 100%;
            max-width: ${container == 'fluid' ? '100%' : '1200px'};
            margin: 0 auto;
            padding: 1rem 1.5rem;
            display: flex;
            align-items: center;
            justify-content: ${textAlign == 'left' ? 'flex-start' : (textAlign == 'right' ? 'flex-end' : 'center')};
        }

        .simple-footer-text {
            color: #ffffff;
            font-size: 0.875rem;
            font-weight: 400;
            margin: 0;
            padding: 0;
            letter-spacing: 0.025em;
            line-height: 1.5;
        }

        .simple-footer-text .copyright-year {
            font-weight: 500;
        }

        .simple-footer-text .company-name {
            font-weight: 600;
        }

        /* Light theme */
        .simple-footer.light {
            background: rgba(255, 255, 255, 0.75);
            border-top-color: rgba(0, 0, 0, 0.1);
        }

        .simple-footer.light.transparent {
            background: transparent;
            border: none;
        }

        .simple-footer.light .simple-footer-text {
            color: #ffffff;
        }

        /* Dark theme */
        .simple-footer.dark {
            background: rgba(17, 24, 39, 0.75);
            border-top-color: rgba(255, 255, 255, 0.1);
        }

        .simple-footer.dark.transparent {
            background: transparent;
            border: none;
        }

        .simple-footer.dark .simple-footer-text {
            color: #ffffff;
        }

        /* Auto theme - responds to system preference */
        @media (prefers-color-scheme: dark) {
            .simple-footer.auto {
                background: rgba(17, 24, 39, 0.75);
                border-top-color: rgba(255, 255, 255, 0.1);
            }

            .simple-footer.auto.transparent {
                background: transparent;
                border: none;
            }

            .simple-footer.auto .simple-footer-text {
                color: #ffffff;
            }
        }

        @media (prefers-color-scheme: light) {
            .simple-footer.auto {
                background: rgba(255, 255, 255, 0.75);
                border-top-color: rgba(0, 0, 0, 0.1);
            }

            .simple-footer.auto.transparent {
                background: transparent;
                border: none;
            }

            .simple-footer.auto .simple-footer-text {
                color: #ffffff;
            }
        }

        /* Responsive */
        @media (max-width: 768px) {
            .simple-footer {
                min-height: calc(${height} * 0.85);
            }

            .simple-footer-container {
                padding: 0.875rem 1rem;
            }

            .simple-footer-text {
                font-size: 0.8125rem;
                text-align: ${textAlign};
            }
        }

        @media (max-width: 480px) {
            .simple-footer {
                min-height: calc(${height} * 0.75);
            }

            .simple-footer-container {
                padding: 0.75rem 0.75rem;
            }

            .simple-footer-text {
                font-size: 0.75rem;
                text-align: ${textAlign};
            }
        }
    </style>

    <div class="simple-footer-container">
        <p class="simple-footer-text">
            <span style="font-size: 1.5em;">&#169;</span>&nbsp;<span class="copyright-year"><script>document.write(new Date().getFullYear());</script></span> <span class="company-name">${companyName.encodeAsHTML()}</span>. All rights reserved.
        </p>
    </div>
</footer>
"""
  }
}
