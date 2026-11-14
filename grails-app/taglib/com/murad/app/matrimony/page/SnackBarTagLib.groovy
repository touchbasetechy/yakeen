package com.murad.app.matrimony.page
/**
 * Tag library for rendering modern slide-in snackbar notifications
 * with support for info, warning, error, and success messages.
 *
 * Usage:
 * <glass:snackbarStyles /> <!-- Add this once in your layout -->
 * <glass:show message="Operation successful!" type="success" />
 * <glass:flashMessages /> <!-- Automatically shows flash messages -->
 */
class SnackBarTagLib {

  static namespace = "glass"

  /**
   * Renders the CSS styles and JavaScript needed for snackbar notifications.
   * Add this once in your layout or page <head> section.
   *
   * Usage: <glass:snackbarStyles />
   */
  def snackbarStyles = { attrs ->
    out << '''
<style>
    /* Snackbar Container */
    #snackbar-container {
        position: fixed;
        bottom: 24px;
        right: 24px;
        z-index: 9999;
        display: flex;
        flex-direction: column;
        gap: 12px;
        max-width: 400px;
        pointer-events: none;
    }

    /* Snackbar Base */
    .snackbar {
        background: rgba(255, 255, 255, 0.95);
        backdrop-filter: blur(10px);
        border-radius: 12px;
        padding: 16px 20px;
        box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
        display: flex;
        align-items: center;
        gap: 12px;
        min-width: 300px;
        max-width: 400px;
        pointer-events: auto;
        transform: translateX(450px);
        opacity: 0;
        transition: all 0.4s cubic-bezier(0.68, -0.55, 0.265, 1.55);
        border-left: 4px solid;
        position: relative;
        overflow: hidden;
    }

    .snackbar.show {
        transform: translateX(0);
        opacity: 1;
    }

    .snackbar.hiding {
        transform: translateX(450px);
        opacity: 0;
    }

    /* Snackbar Icon */
    .snackbar-icon {
        flex-shrink: 0;
        width: 24px;
        height: 24px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 18px;
        font-weight: bold;
    }

    /* Snackbar Content */
    .snackbar-content {
        flex: 1;
        color: #2d3748;
        font-size: 14px;
        line-height: 1.5;
        font-weight: 500;
    }

    /* Close Button */
    .snackbar-close {
        flex-shrink: 0;
        background: rgba(0, 0, 0, 0.1);
        border: none;
        width: 24px;
        height: 24px;
        border-radius: 50%;
        cursor: pointer;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 16px;
        color: #2d3748;
        transition: all 0.2s ease;
        padding: 0;
        line-height: 1;
    }

    .snackbar-close:hover {
        background: rgba(0, 0, 0, 0.2);
        transform: scale(1.1);
    }

    .snackbar-close:active {
        transform: scale(0.95);
    }

    /* Progress Bar */
    .snackbar-progress {
        position: absolute;
        bottom: 0;
        left: 0;
        height: 3px;
        background: currentColor;
        opacity: 0.3;
        transition: width linear;
    }

    /* Snackbar Types */
    .snackbar.info {
        border-left-color: #3b82f6;
    }

    .snackbar.info .snackbar-icon {
        color: #3b82f6;
    }

    .snackbar.success {
        border-left-color: #10b981;
    }

    .snackbar.success .snackbar-icon {
        color: #10b981;
    }

    .snackbar.warning {
        border-left-color: #f59e0b;
    }

    .snackbar.warning .snackbar-icon {
        color: #f59e0b;
    }

    .snackbar.error {
        border-left-color: #ef4444;
    }

    .snackbar.error .snackbar-icon {
        color: #ef4444;
    }

    /* Mobile Responsive */
    @media (max-width: 480px) {
        #snackbar-container {
            left: 12px;
            right: 12px;
            bottom: 12px;
            max-width: none;
        }

        .snackbar {
            min-width: auto;
            max-width: none;
        }
    }
</style>

<script>
    // Snackbar System
    if (typeof window.SnackbarSystem === \'undefined\') {
        window.SnackbarSystem = {
            container: null,
            queue: [],
            activeSnackbars: [],

            init: function() {
                if (!this.container) {
                    this.container = document.createElement(\'div\');
                    this.container.id = \'snackbar-container\';
                    document.body.appendChild(this.container);
                }
            },

            show: function(message, type = \'info\', duration = 5000) {
                this.init();

                const snackbar = this.createSnackbar(message, type, duration);
                this.container.appendChild(snackbar);
                this.activeSnackbars.push(snackbar);

                // ── Trigger animation ─────────────────────────────────────
                // Double rAF guarantees the browser has committed the
                // element\'s initial off-screen styles (transform/opacity)
                // before .show is added, giving the CSS transition a real
                // from-state to animate from.
                //
                // setTimeout(..., 10) is NOT reliable: when the browser is
                // busy the callback can fire before the first paint, so the
                // transition sees no style change and the snackbar snaps in
                // instantly without sliding.
                requestAnimationFrame(() => {
                    requestAnimationFrame(() => {
                        snackbar.classList.add(\'show\');

                        // Start the progress bar inside the same rAF pair
                        // so its width transition also has a committed
                        // from-state and drains smoothly from 100% → 0%.
                        if (duration > 0) {
                            const progressBar = snackbar.querySelector(\'.snackbar-progress\');
                            if (progressBar) {
                                progressBar.style.width = \'100%\';
                                progressBar.style.transition = `width ${duration}ms linear`;
                                requestAnimationFrame(() => {
                                    progressBar.style.width = \'0%\';
                                });
                            }
                        }
                    });
                });

                // Auto dismiss
                if (duration > 0) {
                    setTimeout(() => this.dismiss(snackbar), duration);
                }

                return snackbar;
            },

            createSnackbar: function(message, type, duration) {
                const snackbar = document.createElement(\'div\');
                snackbar.className = `snackbar ${type}`;

                const icon = this.getIcon(type);

                snackbar.innerHTML = `
                    <div class="snackbar-icon">${icon}</div>
                    <div class="snackbar-content">${this.escapeHtml(message)}</div>
                    <button class="snackbar-close" aria-label="Close">×</button>
                    ${duration > 0 ? \'<div class="snackbar-progress"></div>\' : \'\'}
                `;

                const closeBtn = snackbar.querySelector(\'.snackbar-close\');
                closeBtn.addEventListener(\'click\', () => this.dismiss(snackbar));

                return snackbar;
            },

            getIcon: function(type) {
                const icons = {
                    info: \'&#9432;\',      // ℹ
                    success: \'&#10004;\',   // ✓
                    warning: \'&#9888;\',    // ⚠
                    error: \'&#10006;\'      // ✖
                };
                return icons[type] || icons.info;
            },

            dismiss: function(snackbar) {
                snackbar.classList.add(\'hiding\');
                snackbar.classList.remove(\'show\');

                setTimeout(() => {
                    if (snackbar.parentNode) {
                        snackbar.parentNode.removeChild(snackbar);
                    }
                    const index = this.activeSnackbars.indexOf(snackbar);
                    if (index > -1) {
                        this.activeSnackbars.splice(index, 1);
                    }
                }, 400);
            },

            dismissAll: function() {
                this.activeSnackbars.forEach(snackbar => this.dismiss(snackbar));
            },

            escapeHtml: function(text) {
                const div = document.createElement(\'div\');
                div.textContent = text;
                return div.innerHTML;
            }
        };

        // Convenience methods
        window.showSnackbar = function(message, type, duration) {
            return window.SnackbarSystem.show(message, type, duration);
        };

        window.showInfo = function(message, duration) {
            return window.SnackbarSystem.show(message, \'info\', duration);
        };

        window.showSuccess = function(message, duration) {
            return window.SnackbarSystem.show(message, \'success\', duration);
        };

        window.showWarning = function(message, duration) {
            return window.SnackbarSystem.show(message, \'warning\', duration);
        };

        window.showError = function(message, duration) {
            return window.SnackbarSystem.show(message, \'error\', duration);
        };
    }
</script>
'''
  }

  /**
   * Show a snackbar notification.
   *
   * @attr message REQUIRED - The message to display
   * @attr type - Type of snackbar: 'info', 'success', 'warning', or 'error' (default: 'info')
   * @attr duration - Duration in milliseconds (default: 5000, use 0 for persistent)
   * @attr id - Optional ID for the snackbar element
   */
  def show = { attrs ->
    // Required attribute validation
    if (!attrs.message) {
      throwTagError("Tag [show] is missing required attribute [message]")
    }

    String message = attrs.message
    String type = attrs.type ?: 'info'
    String duration = attrs.duration ?: '5000'
    String id = attrs.id ? ", '${attrs.id}'" : ''

    out << """
<script>
    document.addEventListener('DOMContentLoaded', function() {
        window.SnackbarSystem.show('${message.encodeAsJavaScript()}', '${type}', ${duration});
    });
</script>
"""
  }

  /**
   * Automatically show flash messages as snackbars.
   * This will display flash.message, flash.error, flash.warning, flash.success, and flash.info
   *
   * Usage: <glass:flashMessages />
   */
  def flashMessages = { attrs ->
    def flash = request.getAttribute('org.codehaus.groovy.grails.FLASH_SCOPE') ?: [:]

    def messages = []

    if (flash.success) {
      messages << [message: flash.success, type: 'success']
    }
    if (flash.error) {
      messages << [message: flash.error, type: 'error']
    }
    if (flash.warning) {
      messages << [message: flash.warning, type: 'warning']
    }
    if (flash.info) {
      messages << [message: flash.info, type: 'info']
    }
    if (flash.message && !flash.success && !flash.error && !flash.warning && !flash.info) {
      messages << [message: flash.message, type: 'info']
    }

    if (messages) {
      out << '<script>'
      out << 'document.addEventListener("DOMContentLoaded", function() {'
      messages.eachWithIndex { msg, index ->
        def delay = index * 150 // Stagger multiple messages
        out << "setTimeout(function() { "
        out << "window.SnackbarSystem.show('${msg.message.toString().encodeAsJavaScript()}', '${msg.type}', 5000);"
        out << " }, ${delay});"
      }
      out << '});'
      out << '</script>'
    }
  }

  /**
   * Create a trigger button that shows a snackbar when clicked.
   *
   * @attr message REQUIRED - The message to display
   * @attr type - Type of snackbar: 'info', 'success', 'warning', or 'error' (default: 'info')
   * @attr duration - Duration in milliseconds (default: 5000)
   * @attr label - Button label (default: "Show Message")
   * @attr class - Additional CSS classes for the button
   */
  def trigger = { attrs ->
    if (!attrs.message) {
      throwTagError("Tag [trigger] is missing required attribute [message]")
    }

    String message = attrs.message.encodeAsJavaScript()
    String type = attrs.type ?: 'info'
    String duration = attrs.duration ?: '5000'
    String label = attrs.label ?: 'Show Message'
    String cssClass = attrs.class ?: 'btn btn-primary'

    out << """<button type="button" class="${cssClass.encodeAsHTML()}" onclick="window.SnackbarSystem.show('${message}', '${type}', ${duration});">${label.encodeAsHTML()}</button>"""
  }
}
