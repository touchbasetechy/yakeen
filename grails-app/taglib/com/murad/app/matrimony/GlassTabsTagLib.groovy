package com.murad.app.matrimony

/**
 * ╔══════════════════════════════════════════════════════════════════════╗
 * ║              GlassTabsTagLib — Glassmorphic Tab System               ║
 * ║      Cutting-edge · Cross-browser · Mobile-first · Bootstrap 5       ║
 * ╚══════════════════════════════════════════════════════════════════════╝
 *
 * NAMESPACE : glass
 * TAGS      : glass:tabs  /  glass:tab
 *
 * ── USAGE ──────────────────────────────────────────────────────────────
 *
 *  <!-- Basic -->
 *  <glass:tabs id="myTabs">
 *    <glass:tab title="Profile" icon="fa-solid fa-user" active="true">
 *      <p>Profile content</p>
 *    </glass:tab>
 *    <glass:tab title="Settings" icon="fa-solid fa-gear">
 *      <p>Settings content</p>
 *    </glass:tab>
 *  </glass:tabs>
 *
 *  <!-- With badge -->
 *  <glass:tab title="Inbox" icon="fa-solid fa-envelope" badge="9">
 *    ...
 *  </glass:tab>
 *
 *  <!-- Disabled tab -->
 *  <glass:tab title="Locked" icon="fa-solid fa-lock" disabled="true">
 *    ...
 *  </glass:tab>
 *
 *  <!-- Pills variant -->
 *  <glass:tabs id="pillTabs" variant="pills">...</glass:tabs>
 *
 *  <!-- Underline variant -->
 *  <glass:tabs id="ulTabs" variant="underline">...</glass:tabs>
 *
 *  <!-- Vertical layout -->
 *  <glass:tabs id="vtTabs" orientation="vertical">...</glass:tabs>
 *
 *  <!-- Justified / fill full width -->
 *  <glass:tabs id="fillTabs" fill="true">...</glass:tabs>
 *
 *  <!-- Dark tinted glass -->
 *  <glass:tabs id="darkTabs" theme="dark">...</glass:tabs>
 *
 *  <!-- Accent colour override (any CSS colour) -->
 *  <glass:tabs id="roseTabs" accent="#f43f5e">...</glass:tabs>
 *
 *  <!-- Shadow disabled (default) -->
 *  <glass:tabs id="flatTabs" useShadow="false">...</glass:tabs>
 *
 *  <!-- Shadow explicitly enabled -->
 *  <glass:tabs id="shadowTabs" useShadow="true">...</glass:tabs>
 *
 *  <!-- Fully transparent nav + content panel (default) -->
 *  <glass:tabs id="clearTabs" isSemiTransparent="false">...</glass:tabs>
 *
 *  <!-- Semitransparent glass nav + content panel with borders -->
 *  <glass:tabs id="glassTabs" isSemiTransparent="true">...</glass:tabs>
 *
 * ── ATTRIBUTES: glass:tabs ─────────────────────────────────────────────
 *  id                (required) Unique HTML id for this tab group
 *  variant           tabs | pills | underline          default: tabs
 *  orientation       horizontal | vertical             default: horizontal
 *  fill              true | false                      default: false
 *  theme             light | dark                      default: light
 *  accent            any CSS colour                    default: #ffffff
 *  useShadow         true | false                      default: false
 *  isSemiTransparent true | false                      default: false
 *  class             extra CSS classes on wrapper
 *
 * ── ATTRIBUTES: glass:tab ──────────────────────────────────────────────
 *  title       (required) Tab button label
 *  icon        FA-6 class string  e.g. "fa-solid fa-user"
 *  badge       number / short text shown as a pill badge
 *  active      true | false   — first tab is active by default
 *  disabled    true | false
 */

class GlassTabsTagLib {

  static String namespace = 'glass'

  // ─────────────────────────────────────────────────────────────────────
  // glass:tabs
  // ─────────────────────────────────────────────────────────────────────
  def tabs = { attrs, body ->

    def tabsId = attrs.id ?: "glassTabs_${System.nanoTime()}"
    def variant = attrs.variant ?: 'tabs'
    def orientation = attrs.orientation ?: 'horizontal'
    def fill = attrs.fill == 'true'
    def theme = attrs.theme ?: 'light'
    def accent = attrs.accent ?: '#ffffff'
    def useShadow = attrs.useShadow == 'true'         // defaults to false
    def isSemiTransparent = attrs.isSemiTransparent == 'true' // defaults to false
    def extraClass = attrs['class'] ?: ''

    request["__glassTabs_${tabsId}_items"] = []
    request["__glassTabs_${tabsId}_accent"] = accent
    request["__glassTabs_${tabsId}_theme"] = theme

    def bodyContent = body()

    def items = request["__glassTabs_${tabsId}_items"] as List

    boolean hasActive = items.any { it.active }
    if (!hasActive && items) items[0].active = true

    if (!request['__glassTabsCssInjected']) {
      request['__glassTabsCssInjected'] = true
      out << glassTabsCSS()
    }

    def isVertical = (orientation == 'vertical')
    def navClass = buildNavClass(variant, fill, isVertical)

    out << """
<div class="glass-tabs-wrapper ${isVertical ? 'glass-tabs--vertical' : ''} ${extraClass}"
     style="--gt-accent:${accent};"
     data-gt-theme="${theme}"
     data-gt-shadow="${useShadow ? 'true' : 'false'}"
     data-gt-semi="${isSemiTransparent ? 'true' : 'false'}">
"""
    out << """  <ul class="${navClass} glass-nav" id="${tabsId}" role="tablist"
       aria-orientation="${isVertical ? 'vertical' : 'horizontal'}">
"""
    items.eachWithIndex { item, i ->
      def btnId = "${tabsId}-tab-${i}"
      def paneId = "${tabsId}-pane-${i}"
      def isActive = item.active as boolean
      def isDisabled = item.disabled as boolean

      out << """    <li class="nav-item${isVertical ? ' w-100' : ''}" role="presentation">
      <button class="nav-link glass-nav__btn${isActive ? ' active' : ''}${isDisabled ? ' disabled' : ''}"
              id="${btnId}"
              data-bs-toggle="tab"
              data-bs-target="#${paneId}"
              type="button"
              role="tab"
              aria-controls="${paneId}"
              aria-selected="${isActive}"
              ${isDisabled ? 'tabindex="-1" aria-disabled="true"' : ''}>
"""
      if (item.icon) {
        out << """        <span class="glass-nav__icon"><i class="${item.icon}"></i></span>"""
      }
      out << """        <span class="glass-nav__label">${item.title}</span>"""
      if (item.badge) {
        out << """        <span class="glass-nav__badge">${item.badge}</span>"""
      }
      out << """
      </button>
    </li>
"""
    }
    out << """  </ul>

"""
    out << """  <div class="tab-content glass-content${isVertical ? ' glass-content--vertical' : ''}">
"""
    items.eachWithIndex { item, i ->
      def paneId = "${tabsId}-pane-${i}"
      def btnId = "${tabsId}-tab-${i}"
      def isActive = item.active as boolean

      out << """    <div class="tab-pane fade glass-pane${isActive ? ' show active' : ''}"
         id="${paneId}"
         role="tabpanel"
         aria-labelledby="${btnId}"
         tabindex="0">
${item.content}
    </div>
"""
    }
    out << """  </div>
</div>
"""
    if (!request['__glassTabsJsInjected']) {
      request['__glassTabsJsInjected'] = true
      out << glassTabsJS()
    }
  }

  // ─────────────────────────────────────────────────────────────────────
  // glass:tab
  // ─────────────────────────────────────────────────────────────────────
  def tab = { attrs, body ->

    def keyList = request.attributeNames.toList().findAll { String key ->
      key.startsWith('__glassTabs_') && key.endsWith('_items')
    }.sort()

    if (!keyList) {
      throwTagError('glass:tab must be nested inside glass:tabs')
    }

    def tabsId = keyList.last()
      .replace('__glassTabs_', '')
      .replace('_items', '')

    def items = request["__glassTabs_${tabsId}_items"] as List
    items << [
      title   : attrs.title ?: 'Tab',
      icon    : attrs.icon ?: '',
      badge   : attrs.badge ?: '',
      active  : attrs.active == 'true',
      disabled: attrs.disabled == 'true',
      content : body()
    ]
  }

  // ─────────────────────────────────────────────────────────────────────
  // Helpers
  // ─────────────────────────────────────────────────────────────────────
  private String buildNavClass(String variant, boolean fill, boolean vertical) {
    def cls = ['nav']
    switch (variant) {
      case 'pills': cls << 'nav-pills'; break
      case 'underline': cls << 'nav-underline'; break
      default: cls << 'nav-tabs'; break
    }
    if (fill && !vertical) cls << 'nav-fill'
    if (vertical) cls << 'flex-column'
    return cls.join(' ')
  }

  // ─────────────────────────────────────────────────────────────────────
  // CSS
  // ─────────────────────────────────────────────────────────────────────
  private String glassTabsCSS() {
    return """
<style id="glass-tabs-styles">
/* ── Design tokens ───────────────────────────────────────────────────── */
.glass-tabs-wrapper {
  --gt-accent        : #ffffff;
  --gt-accent-rgb    : 255, 255, 255;
  --gt-accent-soft   : rgba(255,255,255,0.18);
  --gt-accent-border : rgba(255,255,255,0.45);
  --gt-accent-glow   : rgba(255,255,255,0.12);
  --gt-glass-background      : rgba(255,255,255,0.12);
  --gt-glass-border  : rgba(255,255,255,0.25);
  --gt-glass-shadow  : 0 8px 32px rgba(0,0,0,0.12);
  --gt-blur          : 18px;
  --gt-radius        : 16px;
  --gt-nav-radius    : 14px;
  --gt-active-background     : rgba(255,255,255,0.22);
  --gt-text          : rgba(255,255,255,0.82);
  --gt-text-muted    : rgba(255,255,255,0.45);
  --gt-text-active   : #ffffff;
  --gt-badge-background      : var(--gt-accent);
  --gt-transition    : 0.28s cubic-bezier(0.4,0,0.2,1);
  font-family        : 'DM Sans', 'Outfit', system-ui, sans-serif;
}
.glass-tabs-wrapper[data-gt-theme="dark"] {
  --gt-glass-background      : rgba(15,15,30,0.45);
  --gt-glass-border  : rgba(255,255,255,0.10);
  --gt-glass-shadow  : 0 8px 40px rgba(0,0,0,0.45);
  --gt-active-background     : rgba(255,255,255,0.10);
  --gt-text          : rgba(230,230,255,0.80);
  --gt-text-muted    : rgba(200,200,255,0.38);
  --gt-text-active   : #e8e8ff;
}

/* ── useShadow="false" (default) ─────────────────────────────────────── */
.glass-tabs-wrapper[data-gt-shadow="false"] {
  --gt-glass-shadow : none;
}
.glass-tabs-wrapper[data-gt-shadow="false"] .glass-nav__btn.active,
.glass-tabs-wrapper[data-gt-shadow="false"] .glass-nav__btn:hover:not(.active):not(.disabled),
.glass-tabs-wrapper[data-gt-shadow="false"] .glass-nav__badge {
  box-shadow : none;
}

/* ── isSemiTransparent="false" (default) — fully transparent ─────────── */
/* Nav bar: no background, no border, no blur, no shadow                 */
.glass-tabs-wrapper[data-gt-semi="false"] .glass-nav {
  background              : transparent;
  border                  : none;
  box-shadow              : none;
  -webkit-backdrop-filter : none;
          backdrop-filter : none;
}
/* Content panel: no background, no border, no blur, no shadow           */
.glass-tabs-wrapper[data-gt-semi="false"] .glass-content {
  background              : transparent;
  border                  : none;
  box-shadow              : none;
  -webkit-backdrop-filter : none;
          backdrop-filter : none;
}
/* Underline variant bottom rule also removed                            */
.glass-tabs-wrapper[data-gt-semi="false"] .nav-underline.glass-nav {
  border-bottom : none;
}

/* ── isSemiTransparent="true" — glass effect, no borders ─────────────── */
.glass-tabs-wrapper[data-gt-semi="true"] .glass-nav {
  background              : var(--gt-glass-background);
  border                  : none;
  box-shadow              : var(--gt-glass-shadow);
  -webkit-backdrop-filter : blur(var(--gt-blur)) saturate(160%);
          backdrop-filter : blur(var(--gt-blur)) saturate(160%);
}
.glass-tabs-wrapper[data-gt-semi="true"] .glass-content {
  background              : var(--gt-glass-background);
  border                  : none;
  box-shadow              : var(--gt-glass-shadow);
  -webkit-backdrop-filter : blur(var(--gt-blur)) saturate(160%);
          backdrop-filter : blur(var(--gt-blur)) saturate(160%);
}
/* Underline variant: no border in this state either */
.glass-tabs-wrapper[data-gt-semi="true"] .nav-underline.glass-nav {
  background              : transparent;
  border                  : none;
  border-bottom           : none;
  box-shadow              : none;
  -webkit-backdrop-filter : none;
          backdrop-filter : none;
}
/* Backdrop fallback for browsers without backdrop-filter support        */
@supports not (backdrop-filter: blur(1px)) {
  .glass-tabs-wrapper[data-gt-semi="true"] .glass-nav,
  .glass-tabs-wrapper[data-gt-semi="true"] .glass-content {
    background : rgba(245,245,255,0.92);
  }
}

/* ── Layout ──────────────────────────────────────────────────────────── */
.glass-tabs-wrapper {
  width          : 100%;
  position       : relative;
  container-type : inline-size;
}
.glass-tabs-wrapper.glass-tabs--vertical {
  display     : flex;
  gap         : 1rem;
  align-items : flex-start;
}

/* ── Nav shell (structure only — appearance driven by data-gt-semi) ─── */
.glass-tabs-wrapper .glass-nav {
  border-radius      : var(--gt-nav-radius);
  padding            : 6px;
  gap                : 4px;
  flex-wrap          : nowrap;
  overflow-x         : auto;
  overflow-y         : hidden;
  scrollbar-width    : none;
  -ms-overflow-style : none;
  position           : relative;
  z-index            : 1;
}
.glass-tabs-wrapper .glass-nav::-webkit-scrollbar { display: none; }
.glass-tabs-wrapper.glass-tabs--vertical .glass-nav {
  min-width   : 180px;
  overflow-x  : hidden;
  overflow-y  : auto;
  flex-shrink : 0;
}

/* ── Tab buttons ─────────────────────────────────────────────────────── */
.glass-tabs-wrapper .glass-nav__btn {
  display         : flex;
  align-items     : center;
  gap             : 8px;
  padding         : 9px 18px;
  border-radius   : calc(var(--gt-nav-radius) - 4px);
  border          : 1px solid transparent;
  background      : transparent;
  color           : var(--gt-text);
  font-size       : 0.875rem;
  font-weight     : 500;
  letter-spacing  : 0.01em;
  white-space     : nowrap;
  transition      : background var(--gt-transition),
                    color var(--gt-transition),
                    border-color var(--gt-transition),
                    box-shadow var(--gt-transition),
                    transform var(--gt-transition);
  position        : relative;
  overflow        : hidden;
  cursor          : pointer;
  -webkit-tap-highlight-color: transparent;
}
/* Ripple spotlight */
.glass-tabs-wrapper .glass-nav__btn::before {
  content        : '';
  position       : absolute;
  inset          : 0;
  background     : radial-gradient(circle at var(--rx,50%) var(--ry,50%),
                     var(--gt-accent-soft) 0%, transparent 70%);
  opacity        : 0;
  transition     : opacity 0.4s ease;
  pointer-events : none;
}
.glass-tabs-wrapper .glass-nav__btn:hover::before { opacity: 1; }
.glass-tabs-wrapper .glass-nav__btn:hover:not(.active):not(.disabled) {
  background   : var(--gt-accent-soft);
  border-color : var(--gt-glass-border);
  color        : var(--gt-text-active);
  transform    : translateY(-1px);
  box-shadow   : 0 4px 14px rgba(0,0,0,0.08);
}
.glass-tabs-wrapper .glass-nav__btn.active {
  background   : var(--gt-active-background);
  border-color : var(--gt-accent-border);
  color        : var(--gt-text-active);
  box-shadow   : 0 2px 16px var(--gt-accent-glow),
                 inset 0 1px 0 rgba(255,255,255,0.4);
  font-weight  : 600;
  transform    : translateY(-1px);
}
/* Active underline bar */
.glass-tabs-wrapper .glass-nav__btn.active::after {
  content       : '';
  position      : absolute;
  bottom        : 0;
  left          : 20%;
  right         : 20%;
  height        : 2.5px;
  background    : linear-gradient(90deg, transparent,
                    var(--gt-accent) 30%, var(--gt-accent) 70%, transparent);
  border-radius : 2px 2px 0 0;
  animation     : gt-bar-in 0.3s cubic-bezier(0.4,0,0.2,1) forwards;
}
.glass-tabs-wrapper.glass-tabs--vertical .glass-nav__btn.active::after {
  top           : 20%;
  bottom        : 20%;
  left          : 0;
  right         : auto;
  width         : 2.5px;
  height        : auto;
  background    : linear-gradient(180deg, transparent,
                    var(--gt-accent) 30%, var(--gt-accent) 70%, transparent);
  border-radius : 0 2px 2px 0;
  animation     : gt-bar-in-v 0.3s cubic-bezier(0.4,0,0.2,1) forwards;
}
@keyframes gt-bar-in {
  from { opacity: 0; transform: scaleX(0); }
  to   { opacity: 1; transform: scaleX(1); }
}
@keyframes gt-bar-in-v {
  from { opacity: 0; transform: scaleY(0); }
  to   { opacity: 1; transform: scaleY(1); }
}
.glass-tabs-wrapper .glass-nav__btn.disabled,
.glass-tabs-wrapper .glass-nav__btn:disabled {
  opacity    : 0.38;
  cursor     : not-allowed;
  transform  : none !important;
  box-shadow : none !important;
}

/* ── Icon bubble ─────────────────────────────────────────────────────── */
.glass-tabs-wrapper .glass-nav__icon {
  display         : flex;
  align-items     : center;
  justify-content : center;
  width           : 22px;
  height          : 22px;
  border-radius   : 6px;
  background      : var(--gt-accent-soft);
  color           : #ffffff;
  font-size       : 0.8rem;
  flex-shrink     : 0;
  transition      : background var(--gt-transition), transform var(--gt-transition);
}
.glass-tabs-wrapper .glass-nav__icon i,
.glass-tabs-wrapper .glass-nav__icon svg {
  display         : inline-flex !important;
  align-items     : center !important;
  justify-content : center !important;
  color           : #ffffff !important;
  fill            : #ffffff !important;
  font-size       : 0.8rem !important;
  width           : 1em !important;
  height          : 1em !important;
  line-height     : 1 !important;
}
.glass-tabs-wrapper .glass-nav__btn.active .glass-nav__icon {
  background : rgba(255,255,255,0.28);
  color      : #ffffff;
  transform  : scale(1.08);
}
.glass-tabs-wrapper .glass-nav__label { line-height: 1.2; }

/* ── Badge ───────────────────────────────────────────────────────────── */
.glass-tabs-wrapper .glass-nav__badge {
  display         : inline-flex;
  align-items     : center;
  justify-content : center;
  min-width       : 18px;
  height          : 18px;
  padding         : 0 5px;
  border-radius   : 999px;
  background      : rgba(255,255,255,0.25);
  color           : #ffffff;
  font-size       : 0.68rem;
  font-weight     : 700;
  letter-spacing  : 0.02em;
  line-height     : 1;
  margin-left     : 2px;
  box-shadow      : 0 2px 6px rgba(255,255,255,0.35);
  animation       : gt-badge-pop 0.35s cubic-bezier(0.34,1.56,0.64,1) both;
}
@keyframes gt-badge-pop {
  from { transform: scale(0); opacity: 0; }
  to   { transform: scale(1); opacity: 1; }
}

/* ── Content panel (structure only) ─────────────────────────────────── */
.glass-tabs-wrapper .glass-content {
  border-radius : var(--gt-radius);
  margin-top    : 10px;
  overflow      : hidden;
}
.glass-tabs-wrapper.glass-tabs--vertical .glass-content {
  flex       : 1;
  margin-top : 0;
}
.glass-tabs-wrapper .glass-content--vertical { min-height: 200px; }
.glass-tabs-wrapper .glass-pane {
  padding    : 1.5rem;
  color      : var(--gt-text);
  min-height : 80px;
}
.glass-tabs-wrapper .glass-pane.show.active {
  animation : gt-pane-in 0.32s cubic-bezier(0.4,0,0.2,1) both;
}
@keyframes gt-pane-in {
  from { opacity: 0; transform: translateY(10px); }
  to   { opacity: 1; transform: translateY(0);    }
}

/* ── Underline variant ───────────────────────────────────────────────── */
.glass-tabs-wrapper .nav-underline.glass-nav {
  border-radius           : 0;
  padding                 : 0;
}
.glass-tabs-wrapper .nav-underline .glass-nav__btn {
  border-radius  : 0;
  border         : none;
  border-bottom  : 2.5px solid transparent;
  padding-bottom : 12px;
  background     : transparent !important;
  box-shadow     : none !important;
}
.glass-tabs-wrapper .nav-underline .glass-nav__btn.active {
  border-bottom-color : var(--gt-accent);
  color               : var(--gt-text-active);
  transform           : none;
}
.glass-tabs-wrapper .nav-underline .glass-nav__btn.active::after { display: none; }

/* ── Pills variant ───────────────────────────────────────────────────── */
.glass-tabs-wrapper .nav-pills.glass-nav       { border-radius: 999px; padding: 5px; }
.glass-tabs-wrapper .nav-pills .glass-nav__btn { border-radius: 999px; }
.glass-tabs-wrapper .nav-pills .glass-nav__btn.active::after { display: none; }

/* ── Responsive — compact container ─────────────────────────────────── */
@container (max-width: 480px) {
  .glass-tabs-wrapper .glass-nav__btn {
    padding        : 8px 10px;
    flex-direction : column;
    gap            : 4px;
    font-size      : 0.72rem;
  }
  .glass-tabs-wrapper .glass-nav__icon { width: 26px; height: 26px; }
  .glass-tabs-wrapper .glass-pane      { padding: 1rem; }
}

/* ── Responsive — mobile ─────────────────────────────────────────────── */
@media (max-width: 575.98px) {
  .glass-tabs-wrapper:not(.glass-tabs--vertical) .glass-nav {
    flex-wrap                  : nowrap;
    overflow-x                 : auto;
    -webkit-overflow-scrolling : touch;
    scroll-snap-type           : x proximity;
  }
  .glass-tabs-wrapper:not(.glass-tabs--vertical) .glass-nav .nav-item {
    scroll-snap-align : start;
    flex-shrink       : 0;
  }
  .glass-tabs-wrapper.glass-tabs--vertical { flex-direction: column; }
  .glass-tabs-wrapper.glass-tabs--vertical .glass-nav {
    flex-direction : row !important;
    min-width      : unset;
    width          : 100%;
    overflow-x     : auto;
    overflow-y     : hidden;
  }
  .glass-tabs-wrapper.glass-tabs--vertical .glass-nav .nav-item { width: auto !important; }
  .glass-tabs-wrapper.glass-tabs--vertical .glass-nav__btn.active::after {
    top           : auto;
    bottom        : 0;
    left          : 20%;
    right         : 20%;
    width         : auto;
    height        : 2.5px;
    background    : linear-gradient(90deg, transparent,
                      var(--gt-accent) 30%, var(--gt-accent) 70%, transparent);
    border-radius : 2px 2px 0 0;
    animation     : gt-bar-in 0.3s cubic-bezier(0.4,0,0.2,1) forwards;
  }
  .glass-tabs-wrapper.glass-tabs--vertical .glass-content { margin-top: 10px; }
}

/* ── Focus ring ──────────────────────────────────────────────────────── */
.glass-tabs-wrapper .glass-nav__btn:focus-visible {
  outline        : 2px solid var(--gt-accent);
  outline-offset : 2px;
  box-shadow     : 0 0 0 4px rgba(255,255,255,0.22);
}

/* ── Reduced motion ──────────────────────────────────────────────────── */
@media (prefers-reduced-motion: reduce) {
  .glass-tabs-wrapper .glass-pane.show.active,
  .glass-tabs-wrapper .glass-nav__btn,
  .glass-tabs-wrapper .glass-nav__btn::after,
  .glass-tabs-wrapper .glass-nav__badge {
    animation  : none !important;
    transition : none !important;
  }
}
</style>
"""
  }

  // ─────────────────────────────────────────────────────────────────────
  // JS  (unchanged)
  // ─────────────────────────────────────────────────────────────────────
  private String glassTabsJS() {
    return """
<script id="glass-tabs-script">
(function () {
  'use strict';

  document.addEventListener('pointermove', function (e) {
    var btn = e.target.closest('.glass-nav__btn');
    if (!btn) return;
    var r = btn.getBoundingClientRect();
    btn.style.setProperty('--rx', ((e.clientX - r.left) / r.width  * 100) + '%');
    btn.style.setProperty('--ry', ((e.clientY - r.top)  / r.height * 100) + '%');
  }, { passive: true });

  document.addEventListener('shown.bs.tab', function (e) {
    var btn = e.target;
    if (btn && typeof btn.scrollIntoView === 'function') {
      btn.scrollIntoView({ behavior: 'smooth', block: 'nearest', inline: 'center' });
    }
  });

  document.addEventListener('keydown', function (e) {
    var btn = document.activeElement;
    if (!btn || !btn.classList.contains('glass-nav__btn')) return;
    var list = btn.closest('.glass-nav');
    if (!list) return;
    var allBtns  = Array.from(list.querySelectorAll('.glass-nav__btn:not(.disabled)'));
    var idx      = allBtns.indexOf(btn);
    var vertical = list.classList.contains('flex-column');
    var prev     = vertical ? 'ArrowUp'   : 'ArrowLeft';
    var next     = vertical ? 'ArrowDown' : 'ArrowRight';
    if (e.key === next && idx < allBtns.length - 1) {
      e.preventDefault(); allBtns[idx + 1].focus(); allBtns[idx + 1].click();
    } else if (e.key === prev && idx > 0) {
      e.preventDefault(); allBtns[idx - 1].focus(); allBtns[idx - 1].click();
    } else if (e.key === 'Home') {
      e.preventDefault(); allBtns[0].focus(); allBtns[0].click();
    } else if (e.key === 'End') {
      e.preventDefault(); allBtns[allBtns.length - 1].focus(); allBtns[allBtns.length - 1].click();
    }
  });

  function syncAccentRgb(wrapper) {
    var accent = getComputedStyle(wrapper).getPropertyValue('--gt-accent').trim();
    if (!accent) return;
    var hexRe = new RegExp('^#([0-9a-f]{3,8})' + '\$', 'i');
    var m = accent.match(hexRe);
    if (!m) return;
    var hex = m[1];
    if (hex.length === 3) hex = hex.split('').map(function(c){ return c + c; }).join('');
    var r = parseInt(hex.slice(0, 2), 16);
    var g = parseInt(hex.slice(2, 4), 16);
    var b = parseInt(hex.slice(4, 6), 16);
    if (!isNaN(r) && !isNaN(g) && !isNaN(b)) {
      wrapper.style.setProperty('--gt-accent-rgb', r + ',' + g + ',' + b);
    }
  }

  document.querySelectorAll('.glass-tabs-wrapper').forEach(syncAccentRgb);
})();
</script>
"""
  }
}
