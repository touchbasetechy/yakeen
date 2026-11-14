package com.murad.app.matrimony

class FloatingToolbarTagLib {
  static namespace = "glass"

  private static final String DEFAULT_SLOT_REQUEST_KEY = 'glass_defaultSlot'

  /**
   * Builds a link for a toolbar button. If a slot is supplied, the link is
   * generated as "/u/$slot/$controller/$action" (matching the multi-identity
   * UrlMappings pattern) instead of going through normal reverse-mapping.
   *
   * @param ctrl controller name (may be null if only action-less onClick used)
   * @param act action name (optional)
   * @param prms Map of query params (optional)
   * @param slotV slot value (String/Integer) — null/blank means "no slot"
   */
  private String buildActionLink(String ctrl, String act, Map prms, def slotV) {
    if (!ctrl) return ''

    boolean hasSlot = slotV != null && slotV.toString().trim() ==~ /\d+/

    if (!hasSlot) {
      return act ? createLink(controller: ctrl, action: act, params: prms ?: [:]) :
        createLink(controller: ctrl, params: prms ?: [:])
    }

    def path = new StringBuilder("/u/${slotV}/${ctrl}")
    if (act) {
      path << "/${act}"
    }
    if (prms) {
      def query = prms.collect { k, v ->
        "${URLEncoder.encode(k.toString(), 'UTF-8')}=${URLEncoder.encode(v?.toString() ?: '', 'UTF-8')}"
      }.join('&')
      path << "?${query}"
    }
    createLink(uri: path.toString())
  }

  private String buildButtonHtml(String icon, String image, String clickHandler, String title) {
    def safeTitle = title?.encodeAsHTML() ?: ''
    def buttonContent
    if (image) {
      buttonContent = """<img src="${image}" alt="${safeTitle}">"""
    } else if (icon) {
      buttonContent = """<i class="${icon}"></i>"""
    } else {
      buttonContent = """<i class="fas fa-circle"></i>"""
    }

    """
  <button class="toolbar-button" onclick="${clickHandler}" title="${safeTitle}">
    ${buttonContent}
  </button>
"""
  }

  /**
   * Renders a draggable floating toolbar with customizable buttons
   *
   * @attr buttons - List of maps containing button configurations (optional if using body)
   *        Each button map can have:
   *          - icon: FontAwesome icon class (e.g., 'fas fa-user')
   *          - image: Image URL/path (alternative to icon)
   *          - onClick: JavaScript function to call
   *          - controller: Controller name for redirection
   *          - action: Action name for redirection
   *          - params: Map of URL parameters (optional)
   *          - slot: overrides the tag-level slot attr for this button only (optional)
   *          - title: Tooltip text (optional)
   * @attr minimizable - Whether toolbar can be minimized (default: true)
   * @attr position - Initial position object with x and y coordinates (optional)
   * @attr slot - default multi-identity slot applied to every button unless a
   *        button overrides it with its own 'slot' key. When present, links
   *        are generated as "/u/$slot/$controller/$action" (optional). This
   *        default is honored both for buttons passed via the 'buttons' list
   *        AND for <glass:toolbarButton> tags nested in the body, since the
   *        latter is propagated via a request attribute for the duration of
   *        body rendering (see toolbarButton below).
   *
   * Body content: Can contain <glass:toolbarButton> tags for individual buttons
   */
  def floatingToolbar = { attrs, body ->
    def buttons = attrs.buttons ?: []
    def minimizable = attrs.minimizable != null ? attrs.minimizable : true
    def defaultSlot = attrs.slot

    out << """
<!-- Floating Toolbar -->
<div class="floating-toolbar" id="floatingToolbar">
"""

    // Render buttons from attributes list if provided
    buttons.each { button ->
      def icon = button.icon
      def image = button.image
      def onClick = button.onClick
      def controller = button.controller
      def action = button.action
      def btnParams = button.params ?: [:]
      def title = button.title ?: ''
      def slot = button.containsKey('slot') ? button.slot : defaultSlot

      def clickHandler = onClick ?: "window.location.href='${buildActionLink(controller, action, btnParams, slot)}'"

      out << buildButtonHtml(icon, image, clickHandler, title)
    }

    // Render buttons from body content if provided. GSP tag bodies don't
    // inherit the parent tag's attrs directly, so nested <glass:toolbarButton>
    // tags can't see `defaultSlot` on their own. Stash it on the request for
    // the duration of body() rendering — nested tag invocations share the
    // same request — and restore whatever was there before (supports nested
    // <glass:floatingToolbar> usage and avoids leaking state afterwards).
    if (body) {
      def previousSlot = request.getAttribute(DEFAULT_SLOT_REQUEST_KEY)
      def hadPreviousSlot = request.getAttributeNames().toList().contains(DEFAULT_SLOT_REQUEST_KEY)
      request.setAttribute(DEFAULT_SLOT_REQUEST_KEY, defaultSlot)
      try {
        out << body()
      } finally {
        if (hadPreviousSlot) {
          request.setAttribute(DEFAULT_SLOT_REQUEST_KEY, previousSlot)
        } else {
          request.removeAttribute(DEFAULT_SLOT_REQUEST_KEY)
        }
      }
    }

    if (minimizable) {
      out << """
  <button class="minimize-button" onclick="toggleMinimize()">
    <i class="fa-solid fa-minus"></i>
  </button>
"""
    }

    out << """
</div>
"""
  }

  /**
   * Renders a single button for the floating toolbar
   * Must be used inside <glass:floatingToolbar> body
   *
   * @attr icon - FontAwesome icon class (e.g., 'fas fa-user')
   * @attr image - Image URL/path (alternative to icon)
   * @attr onClick - JavaScript function to call on click
   * @attr controller - Controller name for redirection
   * @attr action - Action name for redirection
   * @attr params - Map of URL parameters (optional)
   * @attr slot - multi-identity slot. When present, link is generated as
   *        "/u/$slot/$controller/$action" instead of a normal createLink
   *        (optional). If omitted, falls back to the enclosing
   *        <glass:floatingToolbar slot="..."> default, if any.
   * @attr title - Tooltip text (optional)
   *
   * Note: Either (icon OR image) should be provided
   *       Either (onClick OR controller/action) should be provided
   */
  def toolbarButton = { attrs ->
    def icon = attrs.icon
    def image = attrs.image
    def onClick = attrs.onClick
    def controller = attrs.controller
    def action = attrs.action
    def btnParams = attrs.params ?: [:]
    def title = attrs.title ?: ''
    def slot = attrs.containsKey('slot') ? attrs.slot : request.getAttribute(DEFAULT_SLOT_REQUEST_KEY)

    def clickHandler = onClick ?: "window.location.href='${buildActionLink(controller, action, btnParams, slot)}'"

    out << buildButtonHtml(icon, image, clickHandler, title)
  }

  /**
   * Renders the CSS styles for the floating toolbar
   */
  def floatingToolbarStyles = { attrs ->
    out << """
<style>
  /* Floating Toolbar Styles */
  .floating-toolbar {
    position: fixed;
    background: rgba(0, 0, 0, 0.4);
    border-radius: 25px;
    padding: 8px;
    display: flex;
    gap: 8px;
    align-items: center;
    backdrop-filter: blur(10px);
    border: 1px solid rgba(255, 255, 255, 0.2);
    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.3);
    cursor: move;
    user-select: none;
    transition: all 0.3s ease;
    z-index: 1000;
  }

  .floating-toolbar.dragging {
    cursor: grabbing;
  }

  .floating-toolbar.minimized {
    width: 50px;
    height: 50px;
    border-radius: 50%;
    padding: 0;
    justify-content: center;
    top: 20px !important;
    left: 50% !important;
    transform: translateX(-50%);
  }

  .toolbar-button {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    background: linear-gradient(135deg, #667eea, #764ba2);
    border: none;
    color: white;
    font-size: 18px;
    cursor: pointer;
    transition: all 0.3s ease;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
  }

  .toolbar-button:hover {
    transform: scale(1.1);
    box-shadow: 0 6px 20px rgba(0, 0, 0, 0.3);
  }

  .toolbar-button img {
    width: 20px;
    height: 20px;
  }

  .minimize-button {
    width: 24px;
    height: 24px;
    border-radius: 50%;
    background: linear-gradient(135deg, #ff6b6b, #ee5a24);
    border: none;
    color: white;
    font-size: 14px;
    cursor: pointer;
    transition: all 0.3s ease;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-left: 8px;
  }

  .minimize-button:hover {
    transform: scale(1.1);
    background: linear-gradient(135deg, #ff5252, #d63031);
  }

  .floating-toolbar.minimized .toolbar-button {
    display: none;
  }

  .floating-toolbar.minimized .minimize-button {
    display: flex;
    width: 32px;
    height: 32px;
    font-size: 16px;
    margin: 0;
    background: linear-gradient(135deg, #667eea, #764ba2);
  }
</style>
"""
  }

  /**
   * Renders the JavaScript for floating toolbar functionality
   */
  def floatingToolbarScripts = { attrs ->
    out << """
<script type="text/javascript">
  // Floating Toolbar Functionality
  let toolbarPosition = {
    x: localStorage.getItem("toolbar-x") || window.innerWidth - 100,
    y: localStorage.getItem("toolbar-y") || 100
  };
  let isMinimized = localStorage.getItem("isMinimized") === "true";
  let isDragging = false;
  let dragOffset = {x: 0, y: 0};

  const toolbar = document.getElementById('floatingToolbar');
  const minimizeButton = toolbar.querySelector('.minimize-button');

  function initializeToolbar() {
    toolbar.style.left = toolbarPosition.x + 'px';
    toolbar.style.top = toolbarPosition.y + 'px';
    restoreMinimize();
  }

  function toggleMinimize() {
    isMinimized = !isMinimized;
    restoreMinimize();
  }

  function restoreMinimize() {
    if (isMinimized) {
      toolbar.classList.add('minimized');
      minimizeButton.innerHTML = '<i class="fa-solid fa-plus"></i>';
    } else {
      toolbar.classList.remove('minimized');
      minimizeButton.innerHTML = '<i class="fa-solid fa-minus"></i>';
      toolbar.style.left = toolbarPosition.x + 'px';
      toolbar.style.top = toolbarPosition.y + 'px';
    }
    localStorage.setItem("isMinimized", isMinimized);
  }

  function startDrag(e) {
    if (e.target.classList.contains('toolbar-button') ||
      e.target.classList.contains('minimize-button') ||
      isMinimized) {
      return;
    }

    isDragging = true;
    toolbar.classList.add('dragging');

    const rect = toolbar.getBoundingClientRect();
    dragOffset.x = e.clientX - rect.left;
    dragOffset.y = e.clientY - rect.top;

    e.preventDefault();
  }

  function drag(e) {
    if (!isDragging || isMinimized) return;

    const x = e.clientX - dragOffset.x;
    const y = e.clientY - dragOffset.y;

    const maxX = window.innerWidth - toolbar.offsetWidth;
    const maxY = window.innerHeight - toolbar.offsetHeight;

    const constrainedX = Math.max(0, Math.min(x, maxX));
    const constrainedY = Math.max(0, Math.min(y, maxY));

    toolbar.style.left = constrainedX + 'px';
    toolbar.style.top = constrainedY + 'px';

    toolbarPosition.x = constrainedX;
    toolbarPosition.y = constrainedY;
  }

  function stopDrag() {
    if (!isDragging) return;

    isDragging = false;
    toolbar.classList.remove('dragging');

    localStorage.setItem("toolbar-x", toolbarPosition.x);
    localStorage.setItem("toolbar-y", toolbarPosition.y);
  }

  toolbar.addEventListener('mousedown', startDrag);
  document.addEventListener('mousemove', drag);
  document.addEventListener('mouseup', stopDrag);

  toolbar.addEventListener('touchstart', (e) => {
    const touch = e.touches[0];
    const mouseEvent = new MouseEvent('mousedown', {
      clientX: touch.clientX,
      clientY: touch.clientY
    });
    startDrag(mouseEvent);
    e.preventDefault();
  });

  document.addEventListener('touchmove', (e) => {
    if (!isDragging) return;
    const touch = e.touches[0];
    const mouseEvent = new MouseEvent('mousemove', {
      clientX: touch.clientX,
      clientY: touch.clientY
    });
    drag(mouseEvent);
    e.preventDefault();
  });

  document.addEventListener('touchend', stopDrag);

  window.addEventListener('resize', () => {
    if (isMinimized) return;

    const maxX = window.innerWidth - toolbar.offsetWidth;
    const maxY = window.innerHeight - toolbar.offsetHeight;

    if (toolbarPosition.x > maxX) {
      toolbarPosition.x = maxX;
      toolbar.style.left = maxX + 'px';
    }

    if (toolbarPosition.y > maxY) {
      toolbarPosition.y = maxY;
      toolbar.style.top = maxY + 'px';
    }
  });

  window.addEventListener('load', initializeToolbar);

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initializeToolbar);
  } else {
    initializeToolbar();
  }
</script>
"""
  }
}
