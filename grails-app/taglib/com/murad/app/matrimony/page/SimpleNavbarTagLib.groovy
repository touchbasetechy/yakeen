package com.murad.app.matrimony.page

/**
 * Simple Glassmorphic Navbar Tag Library
 * Minimal navbar with only brand/logo display
 */
class SimpleNavbarTagLib {

  static namespace = "glass"

  /**
   * Renders a minimal glassmorphic navigation bar with only brand/logo
   *
   * @attr brand Brand name or logo text (required)
   * @attr brandUrl URL for the brand/logo (default: "/")
   * @attr brandIcon SVG or HTML icon to display before brand text
   * @attr brandImage URL to brand logo image
   * @attr position Position of navbar: "fixed-top", "fixed-bottom", "sticky-top" (default: "fixed-top")
   * @attr transparent Boolean to make navbar fully transparent initially (default: false)
   * @attr theme Color theme: "light", "dark", "auto" (default: "auto")
   * @attr height Height of navbar (default: "70px")
   * @attr blurAmount Blur amount (default: "12px")
   * @attr cssClass Additional CSS classes to apply
   * @attr container Container type: "fluid", "fixed" (default: "fluid")
   */
  def simplenavbar = { attrs ->

    // Extract attributes with defaults
    String brand = attrs.brand ?: ''
    String brandUrl = attrs.brandUrl ?: '/'
    String brandImage = attrs.brandImage ?: ''
    String brandIcon = attrs.brandIcon ?: ''
    String position = attrs.position ?: 'fixed-top'
    String theme = attrs.theme ?: 'auto'
    String height = attrs.height ?: '70px'
    String blurAmount = attrs.blurAmount ?: '12px'
    String cssClass = attrs.cssClass ?: ''
    String container = attrs.container ?: 'fluid'

    boolean transparent = attrs.transparent instanceof Boolean ? attrs.transparent : false

    // Generate unique ID for this navbar
    String navbarId = "simple-navbar-${System.currentTimeMillis()}"

    // Build the HTML output
    out << """
<nav class="simple-navbar ${position} ${theme} ${transparent ? 'transparent' : ''} ${cssClass}" id="${navbarId}">
    <style>
        .simple-navbar {
            width: 100%;
            height: ${height};
            display: flex;
            align-items: center;
            z-index: 1000;
            transition: all 0.3s ease;
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
        }

        .simple-navbar.fixed-top {
            position: fixed;
            top: 0;
            left: 0;
            background: transparent;
            border: none;
            box-shadow: none;
        }

        .simple-navbar.fixed-bottom {
            position: fixed;
            bottom: 0;
            left: 0;
            background: transparent;
            border: none;
            box-shadow: none;
        }

        .simple-navbar.sticky-top {
            position: sticky;
            top: 0;
            background: transparent;
            border: none;
            box-shadow: none;
        }

        .simple-navbar.transparent {
            background: transparent;
            backdrop-filter: none;
            -webkit-backdrop-filter: none;
            border: none;
            box-shadow: none;
        }

        .simple-navbar.scrolled {
            background: rgba(255, 255, 255, 0.75);
            backdrop-filter: blur(${blurAmount});
            -webkit-backdrop-filter: blur(${blurAmount});
            border-bottom: 1px solid rgba(255, 255, 255, 0.3);
            box-shadow: 0 4px 30px rgba(0, 0, 0, 0.1);
        }

        .simple-navbar-container {
            width: 100%;
            max-width: ${container == 'fluid' ? '100%' : '1200px'};
            margin: 0 auto;
            padding: 0 1.5rem;
            display: flex;
            align-items: center;
            justify-content: flex-start;
        }

        .simple-navbar-brand {
            display: flex;
            align-items: center;
            gap: 0.75rem;
            text-decoration: none;
            font-size: 1.5rem;
            font-weight: 700;
            color: #ffffff;
            transition: all 0.3s ease;
            outline: none;
        }

        .simple-navbar-brand:hover {
            opacity: 0.9;
            transform: translateY(-2px);
        }

        .simple-navbar-brand:active {
            transform: translateY(0);
        }

        .simple-navbar-brand:focus {
            outline: none;
            border: none;
        }

        .simple-navbar-brand-icon {
            width: 32px;
            height: 32px;
            flex-shrink: 0;
            display: flex;
            align-items: center;
            justify-content: center;
            transition: all 0.3s ease;
        }

        .simple-navbar-brand-icon svg {
            width: 100%;
            height: 100%;
        }

        .simple-navbar-brand-image {
            height: 40px;
            width: auto;
            object-fit: contain;
        }

        .simple-navbar-brand-text {
            font-size: 1.5rem;
            font-weight: 700;
            letter-spacing: -0.025em;
            position: relative;
            background: linear-gradient(135deg, #ffffff 0%, #ffffff 100%);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
            transition: all 0.6s ease;
        }

        .simple-navbar-brand:hover .simple-navbar-brand-text,
        .simple-navbar-brand:focus .simple-navbar-brand-text,
        .simple-navbar-brand:active .simple-navbar-brand-text {
            background: linear-gradient(135deg, #ffffff 0%, #00d4ff 100%);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
        }

        /* Dark theme */
        .simple-navbar.dark {
            background: transparent;
            border: none;
        }

        .simple-navbar.dark.scrolled {
            background: rgba(17, 24, 39, 0.75);
            border-bottom-color: rgba(75, 85, 99, 0.4);
        }

        .simple-navbar.dark .simple-navbar-brand {
            color: #f3f4f6;
        }

        /* Light theme */
        .simple-navbar.light {
            background: transparent;
            border: none;
        }

        .simple-navbar.light.scrolled {
            background: rgba(255, 255, 255, 0.75);
            border-bottom-color: rgba(255, 255, 255, 0.3);
        }

        .simple-navbar.light .simple-navbar-brand {
            color: #1a202c;
        }

        /* Auto theme - responds to system preference */
        @media (prefers-color-scheme: dark) {
            .simple-navbar.auto {
                background: transparent;
                border: none;
            }

            .simple-navbar.auto.scrolled {
                background: rgba(17, 24, 39, 0.75);
                border-bottom-color: rgba(75, 85, 99, 0.4);
            }

            .simple-navbar.auto .simple-navbar-brand {
                color: #f3f4f6;
            }
        }

        @media (prefers-color-scheme: light) {
            .simple-navbar.auto {
                background: transparent;
                border: none;
            }

            .simple-navbar.auto.scrolled {
                background: rgba(255, 255, 255, 0.75);
                border-bottom-color: rgba(255, 255, 255, 0.3);
            }

            .simple-navbar.auto .simple-navbar-brand {
                color: #1a202c;
            }
        }

        /* Responsive */
        @media (max-width: 768px) {
            .simple-navbar {
                height: calc(${height} * 0.85);
            }

            .simple-navbar-brand {
                font-size: 1.25rem;
            }

            .simple-navbar-brand-icon {
                width: 28px;
                height: 28px;
            }

            .simple-navbar-brand-image {
                height: 36px;
            }

            .simple-navbar-brand-text {
                font-size: 1.25rem;
            }

            .simple-navbar-container {
                padding: 0 1rem;
            }
        }

        @media (max-width: 480px) {
            .simple-navbar {
                height: calc(${height} * 0.75);
            }

            .simple-navbar-brand {
                font-size: 1.125rem;
                gap: 0.5rem;
            }

            .simple-navbar-brand-icon {
                width: 24px;
                height: 24px;
            }

            .simple-navbar-brand-image {
                height: 32px;
            }

            .simple-navbar-brand-text {
                font-size: 1.125rem;
            }
        }
    </style>

    <div class="simple-navbar-container">
        ${(brand || brandImage || brandIcon) ? """
        <a href="${brandUrl}" class="simple-navbar-brand">
            ${brandIcon ? "<span class=\"simple-navbar-brand-icon\">${brandIcon}</span>" : ''}
            ${brandImage ? "<img src=\"${brandImage}\" alt=\"${brand}\" class=\"simple-navbar-brand-image\" />" : ''}
            ${brand ? "<span class=\"simple-navbar-brand-text\">${brand.encodeAsHTML()}</span>" : ''}
        </a>
        """ : ''}
    </div>

    <script>
        (function() {
            var navbar = document.getElementById('${navbarId}');
            if (navbar && navbar.classList.contains('transparent')) {
                window.addEventListener('scroll', function() {
                    if (window.scrollY > 50) {
                        navbar.classList.add('scrolled');
                    } else {
                        navbar.classList.remove('scrolled');
                    }
                });
            }
        })();
    </script>
</nav>
"""
  }
}
