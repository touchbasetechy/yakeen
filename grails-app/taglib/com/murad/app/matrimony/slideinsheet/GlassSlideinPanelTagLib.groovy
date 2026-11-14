package com.murad.app.matrimony.slideinsheet
/**
 * GSP TagLib for creating glassmorphism slide-in panels with asymmetric design
 *
 * Panel Design Specifications (70% Screen Coverage on desktop, mobile-first responsive):
 * - Left Panel: Wider at top right, narrower at bottom right (desktop only)
 * - Right Panel: Wider at top left, narrower at bottom left (desktop only)
 * - Top Panel: Taller at bottom right, shorter at bottom left (desktop only)
 * - Bottom Panel: Taller at top right, shorter at top left (desktop only)
 *
 * On small screens (< 768px) panels fall back to clean, full-bleed rectangles
 * (no clip-path) so content never gets visually clipped on narrow viewports.
 *
 * All panels feature modern glassmorphism styling with frosted glass effect,
 * with graceful fallbacks for browsers that don't support backdrop-filter
 * (older Firefox, older Edge/IE) and vendor-prefixed properties for Safari/iOS.
 *
 * Usage:
 * <slide:panel id="myPanel" direction="right">
 *     Panel content goes here
 * </slide:panel>
 *
 * <slide:trigger targetId="myPanel">
 *     Click me to open panel
 * </slide:trigger>
 */
class GlassSlideinPanelTagLib {

  static namespace = "slide"

  /**
   * Main panel tag with glassmorphism design
   *
   * Attributes:
   * - id: Required. Unique identifier for the panel
   * - direction: Optional. Direction from which panel slides (top, right, bottom, left). Default: 'right'
   * - overlay: Optional. Show overlay background. Default: 'true'
   * - closeOnOverlay: Optional. Close when clicking overlay. Default: 'true'
   * - closeButton: Optional. Show close button. Default: 'true'
   * - zIndex: Optional. Z-index of panel. Default: '9999'
   * - animationDuration: Optional. Duration of slide animation in ms. Default: '400'
   * - swipeToClose: Optional. Enable touch swipe-to-close on mobile. Default: 'true'
   */
  def panel = { attrs, body ->

    // Required attributes
    def id = attrs.id
    if (!id) {
      throwTagError("Attribute 'id' is required for slide:panel tag")
    }

    // Optional attributes with defaults
    def direction = attrs.direction ?: 'right'
    def overlay = attrs.overlay != null ? attrs.overlay.toBoolean() : true
    def closeOnOverlay = attrs.closeOnOverlay != null ? attrs.closeOnOverlay.toBoolean() : true
    def closeButton = attrs.closeButton != null ? attrs.closeButton.toBoolean() : true
    def zIndex = attrs.zIndex ?: '9999'
    def animationDuration = attrs.animationDuration ?: '400'
    def swipeToClose = attrs.swipeToClose != null ? attrs.swipeToClose.toBoolean() : true

    // Validate direction
    def validDirections = ['top', 'right', 'bottom', 'left']
    if (!validDirections.contains(direction)) {
      throwTagError("Invalid direction '${direction}'. Must be one of: ${validDirections.join(', ')}")
    }

    // Build CSS classes
    def cssClass = "slide-panel slide-panel-${direction} ${attrs.class ?: ''}".trim()

    // Build inline styles
    def inlineStyles = buildPanelStyles(direction, zIndex)

    out << """
<!-- Glassmorphism Slide Panel: ${id} -->
"""

    // Overlay (if enabled)
    if (overlay) {
      out << """
<div id="${id}-overlay" class="slide-panel-overlay" style="display:none; z-index:${zIndex.toInteger() - 1};" ${closeOnOverlay ? "onclick=\"slidePanel.close('${id}')\"" : ''}></div>
"""
    }

    // Panel container
    out << """
<div id="${id}" class="${cssClass}" style="${inlineStyles}" data-direction="${direction}" data-duration="${animationDuration}" data-swipe-close="${swipeToClose}" role="dialog" aria-modal="true" aria-hidden="true">
"""

    // Close button (if enabled)
    if (closeButton) {
      out << """
    <button type="button" class="slide-panel-close" onclick="slidePanel.close('${id}')" aria-label="Close panel" title="Close Panel">
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
            <line x1="18" y1="6" x2="6" y2="18"></line>
            <line x1="6" y1="6" x2="18" y2="18"></line>
        </svg>
    </button>
"""
    }

    // Optional drag handle for touch users (purely visual affordance on mobile)
    out << """
    <div class="slide-panel-handle" aria-hidden="true"></div>
"""

    // Panel content
    out << """
    <div class="slide-panel-content">
"""
    out << body()
    out << """
    </div>
</div>
"""
  }

  /**
   * Trigger tag to open/close a panel
   *
   * Attributes:
   * - targetId: Required. ID of the panel to control
   * - action: Optional. 'open', 'close', or 'toggle'. Default: 'toggle'
   */
  def trigger = { attrs, body ->
    def targetId = attrs.targetId
    if (!targetId) {
      throwTagError("Attribute 'targetId' is required for slide:trigger tag")
    }

    def action = attrs.action ?: 'toggle'

    // Only add default role/tabindex if the caller hasn't already supplied them,
    // so the trigger is keyboard- and screen-reader-accessible by default.
    def hasRole = attrs.containsKey('role')
    def hasTabindex = attrs.containsKey('tabindex')

    // Build additional attributes (everything except targetId/action)
    def additionalAttrs = attrs.findAll {
      it.key != 'targetId' && it.key != 'action'
    }.collect { k, v ->
      """${k}="${v}\""""
    }.join(' ')

    def defaultA11yAttrs = ""
    if (!hasRole) {
      defaultA11yAttrs += ' role="button"'
    }
    if (!hasTabindex) {
      defaultA11yAttrs += ' tabindex="0"'
    }

    def keyHandler = "if(event.key==='Enter'||event.key===' '||event.keyCode===13||event.keyCode===32){event.preventDefault();slidePanel.${action}('${targetId}');}"

    out << """<span onclick="slidePanel.${action}('${targetId}')" onkeydown="${keyHandler}" style="cursor:pointer;"${defaultA11yAttrs} ${additionalAttrs}>"""
    out << body()
    out << """</span>"""
  }

  /**
   * Include required CSS styles for glassmorphism panels
   * Mobile-first: base rules target small screens; larger-screen rules are
   * layered on top with min-width media queries. Vendor prefixes and
   * @supports fallbacks are included for cross-browser compatibility
   * (Safari/iOS, older Firefox, older Edge/Chromium, and graceful
   * degradation where backdrop-filter/clip-path are unsupported).
   */
  def styles = { attrs ->
    out << """
<style>
/* =========================================================
   Base / Mobile-first styles (default: small screens, < 768px)
   ========================================================= */

* {
    box-sizing: border-box;
}

/* Slide Panel Overlay - Glassmorphism */
.slide-panel-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    height: 100dvh;
    background: rgba(0, 0, 0, 0.45);
    -webkit-backdrop-filter: blur(4px);
    backdrop-filter: blur(4px);
    opacity: 0;
    transition: opacity 0.3s ease;
    touch-action: none;
}

/* Slide Panel Base - Glassmorphism Effect (mobile: simple rectangles, full-bleed) */
.slide-panel {
    position: fixed;
    background: rgba(30, 30, 40, 0.55);
    -webkit-backdrop-filter: blur(20px) saturate(150%);
    backdrop-filter: blur(20px) saturate(150%);
    border: 1px solid rgba(255, 255, 255, 0.3);
    -webkit-box-shadow: 0 8px 32px rgba(0, 0, 0, 0.25);
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.25);
    -webkit-transition: -webkit-transform 0.4s cubic-bezier(0.4, 0, 0.2, 1);
    transition: transform 0.4s cubic-bezier(0.4, 0, 0.2, 1);
    overflow-y: auto;
    overflow-x: hidden;
    -webkit-overflow-scrolling: touch;
    overscroll-behavior: contain;
    touch-action: pan-y;
    max-width: 100vw;
    max-height: 100vh;
    max-height: 100dvh;
}

/* Fallback for browsers without backdrop-filter support: raise opacity so
   the panel still reads clearly against page content. */
@supports not ((backdrop-filter: blur(1px)) or (-webkit-backdrop-filter: blur(1px))) {
    .slide-panel {
        background: rgba(30, 30, 40, 0.92);
    }
    .slide-panel-overlay {
        background: rgba(0, 0, 0, 0.6);
    }
    .slide-panel-close {
        background: rgba(50, 50, 60, 0.85);
    }
}

/* Respect users who prefer reduced motion */
@media (prefers-reduced-motion: reduce) {
    .slide-panel,
    .slide-panel-overlay,
    .slide-panel-close {
        -webkit-transition-duration: 0.01ms !important;
        transition-duration: 0.01ms !important;
    }
}

/* --- Mobile default geometry: full-width/full-height sheets, no clip-path --- */

.slide-panel-left,
.slide-panel-right {
    top: 0;
    height: 100vh;
    height: 100dvh;
    width: 100%;
}

.slide-panel-left {
    left: 0;
    -webkit-transform: translateX(-100%);
    transform: translateX(-100%);
}

.slide-panel-right {
    right: 0;
    -webkit-transform: translateX(100%);
    transform: translateX(100%);
}

.slide-panel-top,
.slide-panel-bottom {
    left: 0;
    width: 100%;
    height: 75vh;
    height: 75dvh;
}

.slide-panel-top {
    top: 0;
    -webkit-transform: translateY(-100%);
    transform: translateY(-100%);
    border-radius: 0 0 16px 16px;
}

.slide-panel-bottom {
    bottom: 0;
    -webkit-transform: translateY(100%);
    transform: translateY(100%);
    border-radius: 16px 16px 0 0;
}

.slide-panel.open {
    -webkit-transform: translate(0, 0);
    transform: translate(0, 0);
}

/* Touch drag handle, shown only on small screens for top/bottom sheets */
.slide-panel-handle {
    display: none;
}

@media (max-width: 767.98px) {
    .slide-panel-top .slide-panel-handle,
    .slide-panel-bottom .slide-panel-handle {
        display: block;
        width: 40px;
        height: 4px;
        border-radius: 4px;
        background: rgba(255, 255, 255, 0.5);
        margin: 10px auto 0 auto;
    }
}

/* Close Button - Glassmorphism Style, sized for comfortable touch targets */
.slide-panel-close {
    position: absolute;
    top: 16px;
    right: 16px;
    background: rgba(255, 255, 255, 0.2);
    -webkit-backdrop-filter: blur(10px);
    backdrop-filter: blur(10px);
    border: 2px solid rgba(255, 255, 255, 0.3);
    border-radius: 50%;
    cursor: pointer;
    padding: 0;
    color: white;
    -webkit-transition: all 0.3s ease;
    transition: all 0.3s ease;
    z-index: 10;
    width: 44px;
    height: 44px;
    min-width: 44px;
    min-height: 44px;
    display: -webkit-flex;
    display: flex;
    -webkit-align-items: center;
    align-items: center;
    -webkit-justify-content: center;
    justify-content: center;
    -webkit-box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
    touch-action: manipulation;
    -webkit-tap-highlight-color: transparent;
}

.slide-panel-close:hover,
.slide-panel-close:focus-visible {
    background: rgba(255, 255, 255, 0.3);
    border-color: rgba(255, 255, 255, 0.5);
    -webkit-transform: rotate(90deg) scale(1.1);
    transform: rotate(90deg) scale(1.1);
    box-shadow: 0 6px 20px rgba(0, 0, 0, 0.2);
    outline: none;
}

/* Content Area - generous top padding on mobile to clear the close button/handle */
.slide-panel-content {
    padding: 64px 20px 32px 20px;
    height: 100%;
    color: white;
    -webkit-text-size-adjust: 100%;
}

/* Glassmorphism Scrollbar (WebKit) */
.slide-panel::-webkit-scrollbar {
    width: 8px;
}

.slide-panel::-webkit-scrollbar-track {
    background: rgba(255, 255, 255, 0.1);
    border-radius: 10px;
}

.slide-panel::-webkit-scrollbar-thumb {
    background: rgba(255, 255, 255, 0.3);
    border-radius: 10px;
}

.slide-panel::-webkit-scrollbar-thumb:hover {
    background: rgba(255, 255, 255, 0.5);
}

/* Firefox scrollbar */
.slide-panel {
    scrollbar-width: thin;
    scrollbar-color: rgba(255, 255, 255, 0.3) rgba(255, 255, 255, 0.1);
}

/* =========================================================
   Tablet and up (>= 768px): restore the original 70%-coverage,
   asymmetric clip-path design
   ========================================================= */
@media (min-width: 768px) {
    .slide-panel-left,
    .slide-panel-right {
        top: 15vh;
        height: 70vh;
        height: 70dvh;
        width: 450px;
        border-radius: 0;
    }

    .slide-panel-left {
        -webkit-clip-path: polygon(0 0, 100% 0, 80% 100%, 0 100%);
        clip-path: polygon(0 0, 100% 0, 80% 100%, 0 100%);
    }

    .slide-panel-right {
        -webkit-clip-path: polygon(0 0, 100% 0, 100% 100%, 20% 100%);
        clip-path: polygon(0 0, 100% 0, 100% 100%, 20% 100%);
    }

    .slide-panel-top,
    .slide-panel-bottom {
        left: 15vw;
        width: 70vw;
        height: 350px;
        border-radius: 0;
    }

    .slide-panel-top {
        -webkit-clip-path: polygon(0 0, 100% 0, 100% 100%, 0 70%);
        clip-path: polygon(0 0, 100% 0, 100% 100%, 0 70%);
    }

    .slide-panel-bottom {
        -webkit-clip-path: polygon(0 30%, 100% 0, 100% 100%, 0 100%);
        clip-path: polygon(0 30%, 100% 0, 100% 100%, 0 100%);
    }

    .slide-panel-handle {
        display: none !important;
    }

    .slide-panel-content {
        padding: 80px 40px 40px 40px;
    }

    .slide-panel-close {
        top: 20px;
        right: 20px;
    }
}

/* Extra-safety fallback: if clip-path is unsupported at all (very old
   browsers), drop back to plain rectangles instead of a broken shape. */
@supports not ((clip-path: inset(0)) or (-webkit-clip-path: inset(0))) {
    .slide-panel-left,
    .slide-panel-right,
    .slide-panel-top,
    .slide-panel-bottom {
        clip-path: none;
    }
}

/* Landscape phones: cap sheet height so content + keyboard remain usable */
@media (max-width: 767.98px) and (orientation: landscape) {
    .slide-panel-top,
    .slide-panel-bottom {
        height: 92vh;
        height: 92dvh;
    }
}
</style>
"""
  }

  /**
   * Include required JavaScript
   * Written in plain ES5 for maximum cross-browser compatibility (no
   * arrow functions, no let/const, no NodeList.forEach), with a small
   * CustomEvent polyfill for older browsers (IE11/old Android WebView),
   * and optional touch swipe-to-close support for mobile.
   */
  def scripts = { attrs ->
    out << """
<script>
// --- CustomEvent polyfill (IE11 / old Android WebView) ---
(function () {
    if (typeof window.CustomEvent === 'function') { return; }

    function CustomEventPolyfill(event, params) {
        params = params || { bubbles: false, cancelable: false, detail: null };
        var evt = document.createEvent('CustomEvent');
        evt.initCustomEvent(event, params.bubbles, params.cancelable, params.detail);
        return evt;
    }
    CustomEventPolyfill.prototype = window.Event ? window.Event.prototype : {};
    window.CustomEvent = CustomEventPolyfill;
})();

// Slide Panel JavaScript Controller
var slidePanel = (function () {
    'use strict';

    // Cross-browser helper: iterate a NodeList without relying on forEach
    function forEachNode(nodeList, fn) {
        for (var i = 0; i < nodeList.length; i++) {
            fn(nodeList[i], i);
        }
    }

    // Cross-browser helper: attach an event listener, falling back to
    // attachEvent for very old IE, and using a passive listener for touch
    // events where supported (better scroll performance on mobile).
    function on(el, type, handler, passive) {
        if (!el) { return; }
        if (el.addEventListener) {
            var opts = false;
            if (passive) {
                var supportsPassive = false;
                try {
                    var testOpts = Object.defineProperty({}, 'passive', {
                        get: function () { supportsPassive = true; return true; }
                    });
                    window.addEventListener('test-passive', null, testOpts);
                } catch (e) {}
                opts = supportsPassive ? { passive: true } : false;
            }
            el.addEventListener(type, handler, opts);
        } else if (el.attachEvent) {
            el.attachEvent('on' + type, handler);
        }
    }

    function lockBodyScroll() {
        document.body.style.overflow = 'hidden';
        document.body.style.touchAction = 'none';
    }

    function unlockBodyScroll() {
        document.body.style.overflow = '';
        document.body.style.touchAction = '';
    }

    var api = {
        open: function (panelId) {
            var panel = document.getElementById(panelId);
            var overlay = document.getElementById(panelId + '-overlay');

            if (!panel) {
                if (window.console) { console.error('Panel not found: ' + panelId); }
                return;
            }

            // Show overlay
            if (overlay) {
                overlay.style.display = 'block';
                // Force reflow so the transition actually runs
                overlay.offsetHeight;
                overlay.style.opacity = '1';
            }

            // Open panel
            panel.classList.add('open');
            panel.setAttribute('aria-hidden', 'false');

            // Set custom animation duration if specified
            var duration = panel.getAttribute('data-duration');
            if (duration) {
                panel.style.transitionDuration = duration + 'ms';
                panel.style.webkitTransitionDuration = duration + 'ms';
                if (overlay) {
                    overlay.style.transitionDuration = duration + 'ms';
                    overlay.style.webkitTransitionDuration = duration + 'ms';
                }
            }

            lockBodyScroll();

            // Move focus into the panel for keyboard/screen-reader users
            var closeBtn = panel.querySelector('.slide-panel-close');
            if (closeBtn && closeBtn.focus) {
                setTimeout(function () { closeBtn.focus(); }, 50);
            }

            document.dispatchEvent(new CustomEvent('slidePanelOpened', { detail: { panelId: panelId } }));
        },

        close: function (panelId) {
            var panel = document.getElementById(panelId);
            var overlay = document.getElementById(panelId + '-overlay');

            if (!panel) {
                if (window.console) { console.error('Panel not found: ' + panelId); }
                return;
            }

            panel.classList.remove('open');
            panel.setAttribute('aria-hidden', 'true');

            if (overlay) {
                overlay.style.opacity = '0';
                setTimeout(function () {
                    overlay.style.display = 'none';
                }, 300);
            }

            unlockBodyScroll();

            document.dispatchEvent(new CustomEvent('slidePanelClosed', { detail: { panelId: panelId } }));
        },

        toggle: function (panelId) {
            var panel = document.getElementById(panelId);
            if (!panel) {
                if (window.console) { console.error('Panel not found: ' + panelId); }
                return;
            }

            if (panel.classList.contains('open')) {
                this.close(panelId);
            } else {
                this.open(panelId);
            }
        },

        isOpen: function (panelId) {
            var panel = document.getElementById(panelId);
            return panel ? panel.classList.contains('open') : false;
        }
    };

    // --- Keyboard support - ESC to close ---
    on(document, 'keydown', function (e) {
        var key = e.key || e.keyCode;
        if (key === 'Escape' || key === 'Esc' || key === 27) {
            var openPanels = document.querySelectorAll('.slide-panel.open');
            forEachNode(openPanels, function (panel) {
                api.close(panel.id);
            });
        }
    }, false);

    // --- Touch swipe-to-close support ---
    var touchStartX = 0;
    var touchStartY = 0;
    var touchPanel = null;
    var SWIPE_THRESHOLD = 60; // px

    on(document, 'touchstart', function (e) {
        var panel = e.target.closest ? e.target.closest('.slide-panel.open') : null;
        if (!panel || panel.getAttribute('data-swipe-close') === 'false') {
            touchPanel = null;
            return;
        }
        touchPanel = panel;
        touchStartX = e.touches[0].clientX;
        touchStartY = e.touches[0].clientY;
    }, true);

    on(document, 'touchend', function (e) {
        if (!touchPanel) { return; }
        var touch = e.changedTouches[0];
        var deltaX = touch.clientX - touchStartX;
        var deltaY = touch.clientY - touchStartY;
        var direction = touchPanel.getAttribute('data-direction');
        var panelId = touchPanel.id;

        if (direction === 'left' && deltaX < -SWIPE_THRESHOLD && Math.abs(deltaX) > Math.abs(deltaY)) {
            api.close(panelId);
        } else if (direction === 'right' && deltaX > SWIPE_THRESHOLD && Math.abs(deltaX) > Math.abs(deltaY)) {
            api.close(panelId);
        } else if (direction === 'top' && deltaY < -SWIPE_THRESHOLD && Math.abs(deltaY) > Math.abs(deltaX)) {
            api.close(panelId);
        } else if (direction === 'bottom' && deltaY > SWIPE_THRESHOLD && Math.abs(deltaY) > Math.abs(deltaX)) {
            api.close(panelId);
        }

        touchPanel = null;
    }, false);

    return api;
})();
</script>
"""
  }

  /**
   * Helper method to build panel styles
   */
  private String buildPanelStyles(direction, zIndex) {
    def styles = []

    styles << "z-index: ${zIndex}"

    return styles.join('; ') + ';'
  }
}
