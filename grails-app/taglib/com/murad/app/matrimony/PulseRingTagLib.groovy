package com.murad.app.matrimony

class PulseRingTagLib {

  static namespace = "ui"

  // Ensures the shared <style>/<script> block is emitted once per request,
  // no matter how many <ui:pulseRing/> tags appear on the page.
  private static final String ASSET_FLAG = "ui_pulseRing_assetsRendered"

  /**
   * Renders a glassmorphic icon/button with a single, subtle expanding-ring
   * pulse — good for low-stakes confirmations like "Copied to clipboard".
   * Shares the UIAnim JS namespace with <ui:shakeError/> and <ui:slideDown/>.
   *
   * Attributes:
   *   id         (optional) - element id for the button. Auto-generated
   *                           (e.g. "ui-pulse-a1b2c3d4") if omitted — supply
   *                           your own when you need to reference it from
   *                           other JS (e.g. the manual-trigger example
   *                           below).
   *   label      (optional) - aria-label text, default "Action". Also
   *                           rendered as a visual tooltip above the button
   *                           on hover/keyboard-focus, styled the same way
   *                           as the `message` toast (but shown on
   *                           hover/focus rather than after a click).
   *   message    (optional) - transient confirmation text, e.g. "Copied!"
   *   size       (optional) - "sm" | "md" | "lg" | "responsive" (default)
   *                           "responsive" scales fluidly with viewport width
   *                           via clamp(), with breakpoint steps as a fallback
   *                           for browsers with poor clamp() support.
   *   color      (optional) - CSS color for the icon and the ring/accent,
   *                           default #ffffff (white)
   *   trigger    (optional) - "auto" (default): click/tap fires the pulse
   *                           automatically, no JS wiring needed.
   *                           "manual": only exposes the JS hook, e.g. to
   *                           fire the pulse after navigator.clipboard
   *                           .writeText(...) resolves.
   *   controller (optional) - when supplied (together with `action`), the
   *                           tag builds a navigation URL and clicking the
   *                           button redirects the browser to it (after the
   *                           pulse animation plays, for "auto" trigger).
   *                           If the request attribute "currentSlot" is
   *                           present, the URL is built as
   *                           /u/$currentSlot/$controller/$action?query,
   *                           bypassing Grails' link generation entirely.
   *                           Otherwise the URL is built with the standard
   *                           `g.createLink`.
   *   action     (optional) - action name to pair with `controller`.
   *                           Defaults to "index" when `controller` is set
   *                           but `action` is not.
   *   params     (optional) - Map of extra query-string parameters appended
   *                           to the generated URL, whichever branch built it.
   *
   * Body content is the icon markup (inline SVG, emoji, whatever) rendered
   * inside the button.
   *
   * Usage - zero JS, auto-trigger on click, plain pulse (no navigation):
   *   <ui:pulseRing id="copyBtn" label="Copy link" message="Copied!">
   *     <svg>...</svg>
   *   </ui:pulseRing>
   *
   * Usage - manual trigger, fired after an async action succeeds:
   *   <ui:pulseRing id="copyBtn2" label="Copy" message="Copied!" trigger="manual">
   *     <svg>...</svg>
   *   </ui:pulseRing>
   *   <script>
   *     document.getElementById('copyBtn2').addEventListener('click', function () {
   *       navigator.clipboard.writeText(window.location.href).then(function () {
   *         UIAnim.pulseRing('copyBtn2', 'Copied!');
   *       });
   *     });
   *   </script>
   *
   * Usage - pulse + redirect to a controller/action (slot-aware URL):
   *   <ui:pulseRing id="viewProfileBtn" label="View profile" message="Opening..."
   *                 controller="profile" action="show" params="[id: profile.id]">
   *     <svg>...</svg>
   *   </ui:pulseRing>
   *   <!-- if request.currentSlot == "42" -> /u/42/profile/show?id=99          -->
   *   <!-- otherwise                       -> g.createLink(controller:'profile', action:'show', params:[id:99]) -->
   */
  def pulseRing = { attrs, body ->
    String id = attrs.id ?: generateId()

    String label = attrs.label ?: "Action"
    String message = attrs.message
    String size = attrs.size ?: "responsive"
    String color = attrs.color ?: "#ffffff"
    String trigger = attrs.trigger ?: "auto"
    String icon = body() ?: "&#9679;" // default: filled dot if no icon supplied

    String controllerName = attrs.controller
    String actionName = attrs.action
    Map linkParams = (attrs.params instanceof Map) ? (Map) attrs.params : [:]

    String href = controllerName ? buildNavigationUrl(controllerName, actionName, linkParams) : null

    out << renderAssetsOnce()

    out << "<div class=\"ui-pulse-wrapper ui-pulse-size-${size.encodeAsHTML()}\">"
    out << "<button type=\"button\" id=\"${id.encodeAsHTML()}\" class=\"ui-pulse-btn\""
    out << " style=\"--ui-pulse-color:${color.encodeAsHTML()};\""
    out << " aria-label=\"${label.encodeAsHTML()}\""
    if (trigger == "auto") {
      out << " data-ui-pulse=\"auto\""
      if (message) out << " data-ui-pulse-message=\"${message.encodeAsHTML()}\""
    }
    if (href) {
      out << " data-ui-pulse-href=\"${href.encodeAsHTML()}\""
    }
    out << ">"
    out << "<span class=\"ui-pulse-icon\">${icon}</span>"
    out << "<span class=\"ui-pulse-ring\" aria-hidden=\"true\"></span>"
    out << "</button>"
    out << "<span class=\"ui-pulse-tooltip\" aria-hidden=\"true\">${label.encodeAsHTML()}</span>"
    out << "<span class=\"ui-pulse-toast\" role=\"status\" aria-live=\"polite\"></span>"
    out << "</div>"
  }

  /**
   * Builds the navigation URL for a controller/action pair.
   *
   * When the request attribute "currentSlot" is set (non-null, non-blank),
   * the URL is built manually as:
   *     /u/{currentSlot}/{controller}/{action}?k1=v1&k2=v2...
   * bypassing Grails URL mapping entirely. Otherwise it falls back to the
   * standard `g.createLink` tag so normal URL mappings, reverse-proxy
   * context paths, etc. are respected.
   */
  private String buildNavigationUrl(String controllerName, String actionName, Map linkParams) {
    String actionPart = actionName ?: "index"
    def currentSlot = request.getAttribute("currentSlot")

    if (currentSlot != null && currentSlot.toString().trim()) {
      StringBuilder sb = new StringBuilder()
      sb << "/u/" << urlEncode(currentSlot.toString())
      sb << "/" << urlEncode(controllerName)
      sb << "/" << urlEncode(actionPart)

      String queryString = buildQueryString(linkParams)
      if (queryString) {
        sb << "?" << queryString
      }
      return sb.toString()
    }

    // Fall back to Grails' own link generation.
    return g.createLink(controller: controllerName, action: actionPart, params: linkParams)
  }

  private String buildQueryString(Map params) {
    if (!params) return ""
    return params.collect { k, v ->
      "${urlEncode(k?.toString())}=${urlEncode(v?.toString())}"
    }.join("&")
  }

  /**
   * Produces a unique element id for buttons rendered without an explicit
   * `id` attribute. Uses a random suffix rather than a request-scoped
   * counter so ids stay unique even across AJAX-rendered partials that
   * don't share a single top-level request attribute state.
   */
  private String generateId() {
    "ui-pulse-" + java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 8)
  }

  private String urlEncode(String value) {
    if (value == null) return ""
    java.net.URLEncoder.encode(value, "UTF-8")
  }

  private String renderAssetsOnce() {
    if (request.getAttribute(ASSET_FLAG)) return ""
    request.setAttribute(ASSET_FLAG, true)

    return '''
<style>
.ui-pulse-wrapper {
  position: relative;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  --ui-pulse-size: clamp(36px, 6vw, 48px);
}

/* fluid default */
.ui-pulse-size-responsive { --ui-pulse-size: clamp(32px, 6vw, 48px); }

/* fixed steps, also serve as the fallback for browsers with weak
   clamp()/vw support, and as explicit opt-ins */
.ui-pulse-size-sm { --ui-pulse-size: 32px; }
.ui-pulse-size-md { --ui-pulse-size: 44px; }
.ui-pulse-size-lg { --ui-pulse-size: 56px; }

@media (max-width: 480px) {
  .ui-pulse-size-responsive { --ui-pulse-size: 34px; }
}
@media (min-width: 481px) and (max-width: 900px) {
  .ui-pulse-size-responsive { --ui-pulse-size: 42px; }
}
@media (min-width: 901px) {
  .ui-pulse-size-responsive { --ui-pulse-size: 48px; }
}

.ui-pulse-btn {
  position: relative;
  width: var(--ui-pulse-size);
  height: var(--ui-pulse-size);
  border-radius: 50%;
  border: 1px solid rgba(255, 255, 255, 0.35);
  background: rgba(255, 255, 255, 0.14);
  -webkit-backdrop-filter: blur(12px) saturate(180%);
  backdrop-filter: blur(12px) saturate(180%);
  box-shadow:
    0 1px 2px rgba(0, 0, 0, 0.06),
    0 4px 12px rgba(0, 0, 0, 0.08),
    inset 0 1px 0 rgba(255, 255, 255, 0.4);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  touch-action: manipulation;
  -webkit-tap-highlight-color: transparent;
  color: var(--ui-pulse-color, #ffffff);
  font-size: calc(var(--ui-pulse-size) * 0.45);
  line-height: 1;
  transition: transform 0.15s ease, background 0.2s ease;
}

/* Fallback for browsers without backdrop-filter support (older Firefox,
   some in-app webviews): opaque-enough surface so contrast still holds */
@supports not ((backdrop-filter: blur(1px)) or (-webkit-backdrop-filter: blur(1px))) {
  .ui-pulse-btn {
    background: rgba(255, 255, 255, 0.85);
  }
}

.ui-pulse-btn:hover {
  transform: translateY(-1px);
  background: rgba(255, 255, 255, 0.22);
}

.ui-pulse-btn:active {
  transform: translateY(0) scale(0.96);
}

.ui-pulse-btn:focus-visible {
  outline: 2px solid var(--ui-pulse-color, #ffffff);
  outline-offset: 2px;
}

.ui-pulse-icon {
  position: relative;
  z-index: 2;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 55%;
  height: 55%;
}

.ui-pulse-icon svg {
  width: 100%;
  height: 100%;
  fill: currentColor;
}

/* the single expanding ring — subtler than a full multi-ring burst:
   one border, one pass, modest scale, quick fade */
.ui-pulse-ring {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  border-radius: 50%;
  border: 2px solid var(--ui-pulse-color, #ffffff);
  opacity: 0;
  pointer-events: none;
  transform: scale(1);
}

.ui-pulse-ring.ui-pulse-active {
  animation: ui-pulse-expand 0.65s cubic-bezier(0.22, 0.61, 0.36, 1) both;
}

@keyframes ui-pulse-expand {
  0%   { opacity: 0.55; transform: scale(1); }
  100% { opacity: 0;    transform: scale(1.7); }
}

@media (prefers-reduced-motion: reduce) {
  .ui-pulse-ring.ui-pulse-active { animation: none; opacity: 0; }
  .ui-pulse-btn:hover, .ui-pulse-btn:active { transform: none; }
}

.ui-pulse-tooltip {
  position: absolute;
  bottom: calc(100% + 8px);
  left: 50%;
  transform: translateX(-50%) translateY(4px);
  white-space: nowrap;
  font-size: 0.75rem;
  font-weight: 600;
  color: #1e293b;
  background: rgba(255, 255, 255, 0.75);
  -webkit-backdrop-filter: blur(8px);
  backdrop-filter: blur(8px);
  border: 1px solid rgba(255, 255, 255, 0.4);
  padding: 4px 10px;
  border-radius: 999px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  opacity: 0;
  pointer-events: none;
  transition: opacity 0.15s ease, transform 0.15s ease;
  transition-delay: 0s;
  z-index: 3;
}

.ui-pulse-wrapper:hover .ui-pulse-tooltip,
.ui-pulse-btn:focus-visible ~ .ui-pulse-tooltip {
  opacity: 1;
  transform: translateX(-50%) translateY(0);
  transition-delay: 0.35s; /* small delay before showing, like native title tooltips */
}

/* Don't show the hover tooltip and the click confirmation toast at once. */
.ui-pulse-wrapper:has(.ui-pulse-toast-visible) .ui-pulse-tooltip {
  opacity: 0 !important;
  transition-delay: 0s;
}

@media (prefers-reduced-motion: reduce) {
  .ui-pulse-tooltip { transition: none; }
}

.ui-pulse-toast {
  position: absolute;
  bottom: calc(100% + 8px);
  left: 50%;
  transform: translateX(-50%) translateY(4px);
  white-space: nowrap;
  font-size: 0.75rem;
  font-weight: 600;
  color: #1e293b;
  background: rgba(255, 255, 255, 0.75);
  -webkit-backdrop-filter: blur(8px);
  backdrop-filter: blur(8px);
  border: 1px solid rgba(255, 255, 255, 0.4);
  padding: 4px 10px;
  border-radius: 999px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  opacity: 0;
  pointer-events: none;
  transition: opacity 0.2s ease, transform 0.2s ease;
}

.ui-pulse-toast.ui-pulse-toast-visible {
  opacity: 1;
  transform: translateX(-50%) translateY(0);
}
</style>

<script>
window.UIAnim = window.UIAnim || {};

// Single expanding ring pulse. Same "restart even if mid-flight" and
// per-element-guard conventions as UIAnim.shakeError / UIAnim.slideDown.
UIAnim.pulseRing = function (targetId, message) {
  var btn = document.getElementById(targetId);
  if (!btn) return;

  var ring = btn.querySelector('.ui-pulse-ring');
  if (ring) {
    ring.classList.remove('ui-pulse-active');
    void ring.offsetWidth; // force reflow so the animation restarts
    ring.classList.add('ui-pulse-active');
    ring.addEventListener('animationend', function handler() {
      ring.classList.remove('ui-pulse-active');
      ring.removeEventListener('animationend', handler);
    });
  }

  var wrapper = btn.closest('.ui-pulse-wrapper');
  var toast = wrapper ? wrapper.querySelector('.ui-pulse-toast') : null;
  if (toast && message) {
    clearTimeout(toast._uiPulseTimer);
    toast.textContent = message;
    toast.classList.add('ui-pulse-toast-visible');
    toast._uiPulseTimer = setTimeout(function () {
      toast.classList.remove('ui-pulse-toast-visible');
    }, 1400);
  }
};

// Navigates the browser to the URL a pulseRing button was given via its
// `controller`/`action` attributes. If the button also auto-pulses, the
// navigation is deferred briefly so the pulse (and any toast message) is
// actually visible before the page unloads.
UIAnim.pulseNavigate = function (targetId) {
  var btn = document.getElementById(targetId);
  if (!btn) return;
  var href = btn.getAttribute('data-ui-pulse-href');
  if (!href) return;

  var isAutoPulse = btn.getAttribute('data-ui-pulse') === 'auto';
  var delay = isAutoPulse ? 220 : 0;
  setTimeout(function () {
    window.location.href = href;
  }, delay);
};

// Delegated listener: covers mouse, touch, and pen uniformly via the
// browser's unified "click" event, so no separate touchstart handling
// (and no double-firing) is needed for auto-trigger buttons.
document.addEventListener('DOMContentLoaded', function () {
  document.addEventListener('click', function (e) {
    var btn = e.target.closest('[data-ui-pulse="auto"]');
    if (!btn) return;
    UIAnim.pulseRing(btn.id, btn.getAttribute('data-ui-pulse-message'));
    if (btn.hasAttribute('data-ui-pulse-href')) {
      UIAnim.pulseNavigate(btn.id);
    }
  });

  // Buttons that carry a navigation URL but use trigger="manual" (no
  // data-ui-pulse="auto") still redirect on click — they just don't
  // auto-fire the ring animation, matching the existing manual-trigger
  // contract where the developer wires the ring pulse itself.
  document.addEventListener('click', function (e) {
    var btn = e.target.closest('[data-ui-pulse-href]:not([data-ui-pulse="auto"])');
    if (!btn) return;
    UIAnim.pulseNavigate(btn.id);
  });

  // keyboard activation (Enter / Space) for accessibility parity
  document.addEventListener('keydown', function (e) {
    if (e.key !== 'Enter' && e.key !== ' ') return;
    var btn = e.target.closest && e.target.closest('[data-ui-pulse="auto"], [data-ui-pulse-href]');
    if (!btn) return;
    e.preventDefault();
    btn.click();
  });
});
</script>
'''
  }
}
