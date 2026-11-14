package com.murad.app.matrimony

/**
 * GlassModal GSP Taglib
 * Glassmorphic Bootstrap Modal Dialog — Mobile-first, cross-browser compatible
 *
 * Usage:
 *   <gm:modal id="myModal" title="Dialog Title" size="md" blur="true" variant="dark">
 *       <p>Your content here</p>
 *   </gm:modal>
 *
 *   <gm:modalTrigger target="myModal" label="Open Modal" icon="✦" btnVariant="glass"/>
 *   <gm:modalAssets/>  <!-- include once in <head> or before </body> -->
 *
 * ─── Animation behaviour ──────────────────────────────────────────────────────
 *
 *  Open  → horizontal flip-in  : dialog starts at rotateY(-90deg) / opacity 0 and
 *           spring-eases to rotateY(0) / opacity 1.
 *  Close → fade-out            : dialog fades straight to opacity 0 with no rotation,
 *           triggered by the .gm-closing class that is applied before teardown.
 *
 * ─── Font Awesome support ─────────────────────────────────────────────────────
 *
 *  Load FA in <head> (or pass fontAwesome="true" to <gm:modalAssets/> to auto-load
 *  the free CDN kit):
 *
 *    <gm:modalAssets fontAwesome="true"/>
 *    <!-- OR manually: -->
 *    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css"/>
 *
 *  Then use faIcon anywhere an icon is accepted (takes precedence over emoji icon):
 *
 *    <!-- Modal title badge -->
 *    <gm:modal id="demo" title="Users" faIcon="fa-solid fa-users" size="lg">
 *
 *    <!-- Trigger button -->
 *    <gm:modalTrigger target="demo" label="Open" faIcon="fa-solid fa-arrow-up-right-from-square"/>
 *
 *    <!-- Footer buttons -->
 *    <gm:dismissBtn label="Cancel" faIcon="fa-solid fa-xmark"/>
 *    <gm:confirmBtn label="Save"   faIcon="fa-solid fa-floppy-disk" variant="success"/>
 *
 *  You can still use plain emoji via icon="🗑" — both attributes are supported on
 *  every tag. If both are provided, faIcon is rendered and icon is ignored.
 *
 * ─── autoClose support ────────────────────────────────────────────────────────
 *
 *  Pass autoClose="<ms>" to auto-dismiss a modal after the given milliseconds.
 *  Omit the attribute (or don't set it) to keep the default manual-close behaviour.
 *
 *    <gm:modal id="successModal" title="Saved!" autoClose="3000">
 *        <p>Your changes were saved successfully.</p>
 *    </gm:modal>
 *
 *  The timer is cancelled immediately if the user closes the modal manually,
 *  so no ghost-close fires after a manual dismiss.
 *
 * ─── autoRedirectUrl support ──────────────────────────────────────────────────
 *
 *  Pass autoRedirectUrl to navigate to a URL whenever the modal closes, whether
 *  closed manually, via autoClose, or programmatically via GlassModal.close().
 *
 *    <gm:modal id="doneModal" title="Done!"
 *              autoRedirectUrl="${createLink(controller: 'DBoard', action: 'index')}">
 *        <p>Redirecting you now…</p>
 *    </gm:modal>
 *
 *  Combine with autoClose for a timed redirect:
 *
 *    <gm:modal id="savedModal" title="Saved!" autoClose="2000"
 *              autoRedirectUrl="${createLink(controller: 'DBoard', action: 'index')}">
 *        <p>Changes saved. Taking you back…</p>
 *    </gm:modal>
 *
 *  Redirect fires after the close animation completes (320 ms), so the user
 *  always sees the full fade-out before navigation.
 *
 * ─── footerColor support ──────────────────────────────────────────────────────
 *
 *  Pass footerColor="<css-color>" to control the text color of all content
 *  rendered inside footerHtml. Accepts any valid CSS color value.
 *  Defaults to #fff when omitted.
 *
 *    <!-- Explicit color -->
 *    <gm:modal id="addContactModal" title="Contacts" variant="light"
 *              footerColor="#1a1a2e" footerHtml="${contactAddFooter}">
 *
 *    <!-- Default white (dark modal) -->
 *    <gm:modal id="infoModal" title="Info" variant="dark"
 *              footerHtml="${someFooter}">
 */
class GlassModalTagLib {

  static namespace = 'glass'
  static defaultEncodeAs = [taglib: 'none']

  // ─── helper: render an icon span (FA or emoji) ────────────────────────────────
  private String iconHtml(String faIcon, String emoji, String extraCss = '') {
    if (faIcon) {
      def style = extraCss ? " style=\"${extraCss}\"" : ''
      return "<i class=\"${faIcon.encodeAsHTML()}\"${style} aria-hidden=\"true\"></i>"
    }
    if (emoji) {
      return "<span aria-hidden=\"true\">${emoji.encodeAsHTML()}</span>"
    }
    return ''
  }

  // ─── ASSETS ──────────────────────────────────────────────────────────────────
  /**
   * Injects the glassmorphic CSS + JS bundle.
   * Place once in your layout's <head> AFTER Bootstrap CSS.
   *
   * @attr nonce         (optional) CSP nonce value for inline styles/scripts
   * @attr fontAwesome   (optional) true — auto-load Font Awesome 6 Free from cdnjs
   */
  def modalAssets = { attrs ->
    def nonce = attrs.nonce ? " nonce=\"${attrs.nonce}\"" : ''
    def loadFa = attrs.fontAwesome == 'true'

    if (loadFa) {
      out << '<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css" integrity="sha512-SnH5WK+bZxgPHs44uWIX+LLJAJ9/2PkPKZ5QiAj6Ta86w+fsb2TkcmfRyVX3pBnMFcV7oQPJkl9QevSCWr3W==" crossorigin="anonymous" referrerpolicy="no-referrer"/>' + "\n"
    }

    out << """
<style${nonce}>
/* ── Glass Modal System ─────────────────────────────────── */
:root {
  --gm-blur:          18px;
  --gm-blur-heavy:    32px;
  --gm-background-dark:       rgba(10, 10, 20, 0.55);
  --gm-background-light:      rgba(255, 255, 255, 0.20);
  --gm-border-dark:   rgba(255, 255, 255, 0.14);
  --gm-border-light:  rgba(255, 255, 255, 0.55);
  --gm-text-dark:     #e8eaf6;
  --gm-text-light:    #1a1a2e;
  --gm-accent:        #7c83ff;
  --gm-accent-glow:   rgba(124, 131, 255, 0.45);
  --gm-success:       #4ecca3;
  --gm-danger:        #ff6b8a;
  --gm-warning:       #ffd166;
  --gm-radius:        20px;
  --gm-radius-sm:     12px;
  --gm-shadow:        0 8px 40px rgba(0,0,0,0.45), 0 2px 8px rgba(0,0,0,0.25);
  --gm-transition:    cubic-bezier(0.34, 1.56, 0.64, 1);
  --gm-ease:          cubic-bezier(0.4, 0, 0.2, 1);
  --gm-font-display:  'DM Sans', 'Segoe UI', system-ui, sans-serif;
  --gm-font-body:     'DM Sans', 'Segoe UI', system-ui, sans-serif;
}

/* ── Backdrop ───────────────────────────────────────────── */
.gm-backdrop {
  position: fixed;
  inset: 0;
  z-index: 1040;
  background: rgba(4, 4, 15, 0.65);
  -webkit-backdrop-filter: blur(6px) saturate(1.4);
          backdrop-filter: blur(6px) saturate(1.4);
  opacity: 0;
  transition: opacity 320ms var(--gm-ease);
  will-change: opacity;
}
.gm-backdrop.gm-show { opacity: 1; }

/* ── Dialog wrapper ─────────────────────────────────────── */
.gm-modal {
  position: fixed;
  inset: 0;
  z-index: 1050;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: clamp(12px, 4vw, 32px);
  pointer-events: none;
  overflow-y: auto;
  -webkit-overflow-scrolling: touch;
}
.gm-modal.gm-show { pointer-events: auto; }

/* ── 3-D flip-in keyframes ──────────────────────────────── */
@keyframes gm-flip-in {
  0%   { opacity: 0;   transform: perspective(700px) rotateY(-90deg) translateZ(60px) scale(0.92); }
  55%  { opacity: 1;   transform: perspective(700px) rotateY(6deg)   translateZ(0px)  scale(1.01); }
  75%  {               transform: perspective(700px) rotateY(-2.5deg) translateZ(0px) scale(1);    }
  100% { opacity: 1;   transform: perspective(700px) rotateY(0deg)   translateZ(0px)  scale(1);    }
}

/* ── Dialog card ────────────────────────────────────────── */
.gm-dialog {
  position: relative;
  width: 100%;
  max-height: calc(100dvh - 2 * clamp(12px, 4vw, 32px));
  overflow-y: auto;
  overflow-x: hidden;
  border-radius: var(--gm-radius);
  box-shadow: var(--gm-shadow);
  font-family: var(--gm-font-body);
  opacity: 0;
  transform: perspective(700px) rotateY(-90deg) translateZ(60px) scale(0.92);
  transform-origin: center center;
  will-change: transform, opacity;
  outline: none;
  scrollbar-width: thin;
  scrollbar-color: rgba(255,255,255,0.18) transparent;
  backface-visibility: hidden;
  -webkit-backface-visibility: hidden;
}
.gm-dialog::-webkit-scrollbar { width: 5px; }
.gm-dialog::-webkit-scrollbar-thumb {
  background: rgba(255,255,255,0.18);
  border-radius: 99px;
}

/* ── Open state ─────────────────────────────────────────── */
.gm-modal.gm-show .gm-dialog {
  opacity: 1;
  transform: perspective(700px) rotateY(0deg);
  animation: gm-flip-in 500ms var(--gm-transition) both;
}

/* ── Closing state ──────────────────────────────────────── */
.gm-modal.gm-closing .gm-dialog {
  animation: none;
  opacity: 0;
  transform: perspective(700px) rotateY(0deg) scale(1);
  transition: opacity 300ms var(--gm-ease);
}

/* ── Size variants ──────────────────────────────────────── */
.gm-dialog-sm  { max-width: min(360px, 96vw); }
.gm-dialog-md  { max-width: min(540px, 96vw); }
.gm-dialog-lg  { max-width: min(860px, 96vw); }
.gm-dialog-xl  { max-width: min(960px, 96vw); }
.gm-dialog-fs  { max-width: 100%; min-height: 100dvh; border-radius: 0; }

/* ── Theme: dark (default) ──────────────────────────────── */
.gm-dialog-dark {
  background: var(--gm-background-dark);
  border: 1px solid var(--gm-border-dark);
  color: var(--gm-text-dark);
  -webkit-backdrop-filter: blur(var(--gm-blur)) saturate(1.6) brightness(1.05);
          backdrop-filter: blur(var(--gm-blur)) saturate(1.6) brightness(1.05);
}

/* ── Theme: light ───────────────────────────────────────── */
.gm-dialog-light {
  background: var(--gm-background-light);
  border: 1px solid var(--gm-border-light);
  color: var(--gm-text-light);
  -webkit-backdrop-filter: blur(var(--gm-blur)) saturate(1.8) brightness(1.15);
          backdrop-filter: blur(var(--gm-blur)) saturate(1.8) brightness(1.15);
}
.gm-dialog-light .gm-header { border-bottom-color: rgba(0,0,0,0.10); }
.gm-dialog-light .gm-footer { border-top-color: rgba(0,0,0,0.10); color: var(--gm-text-light); }
.gm-dialog-light .gm-title  { color: var(--gm-text-light); }
.gm-dialog-light .gm-close  { color: rgba(0,0,0,0.55); }
.gm-dialog-light .gm-close:hover { color: rgba(0,0,0,0.85); }

/* ── Noise grain overlay ────────────────────────────────── */
.gm-dialog::before {
  content: '';
  position: absolute;
  inset: 0;
  border-radius: inherit;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='200' height='200'%3E%3Cfilter id='n'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.85' numOctaves='4' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='200' height='200' filter='url(%23n)' opacity='0.035'/%3E%3C/svg%3E");
  pointer-events: none;
  z-index: 0;
}

/* ── Accent glow strip ──────────────────────────────────── */
.gm-dialog::after {
  content: '';
  position: absolute;
  top: 0; left: 10%; right: 10%;
  height: 1px;
  background: linear-gradient(90deg, transparent, var(--gm-accent), transparent);
  opacity: 0.7;
  border-radius: 1px;
  pointer-events: none;
}

/* ── Header ─────────────────────────────────────────────── */
.gm-header {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: clamp(16px, 4vw, 24px) clamp(16px, 4vw, 28px);
  border-bottom: 1px solid rgba(255,255,255,0.09);
}

.gm-title {
  margin: 0;
  font-family: var(--gm-font-display);
  font-size: clamp(1.05rem, 2.5vw, 1.25rem);
  font-weight: 700;
  letter-spacing: -0.02em;
  color: #fff;
  line-height: 1.2;
}
.gm-dialog-light .gm-title { color: var(--gm-text-light); }

/* Title icon badge */
.gm-title-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 34px; height: 34px;
  border-radius: 10px;
  background: rgba(124,131,255,0.18);
  border: 1px solid rgba(124,131,255,0.28);
  font-size: 16px;
  flex-shrink: 0;
}
.gm-title-icon i { font-size: 15px; color: var(--gm-accent); }

/* ── Close button ───────────────────────────────────────── */
.gm-close {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 34px; height: 34px;
  border-radius: 10px;
  border: 1px solid rgba(255,255,255,0.12);
  background: rgba(255,255,255,0.07);
  color: rgba(255,255,255,0.6);
  font-size: 18px;
  line-height: 1;
  cursor: pointer;
  flex-shrink: 0;
  transition: background 200ms var(--gm-ease), color 200ms var(--gm-ease), transform 200ms var(--gm-ease);
  -webkit-tap-highlight-color: transparent;
  touch-action: manipulation;
}
.gm-close:hover {
  background: rgba(255,255,255,0.15);
  color: #fff;
  transform: rotate(90deg);
}
.gm-close:focus-visible {
  outline: 2px solid var(--gm-accent);
  outline-offset: 2px;
}

/* ── Body ───────────────────────────────────────────────── */
.gm-body {
  position: relative;
  z-index: 1;
  padding: clamp(16px, 4vw, 28px);
  line-height: 1.65;
  font-size: clamp(0.875rem, 2vw, 0.9375rem);
  opacity: 0.9;
}

/* ── Footer ─────────────────────────────────────────────── */
.gm-footer {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  flex-wrap: wrap;
  gap: 10px;
  padding: clamp(14px, 3.5vw, 20px) clamp(16px, 4vw, 28px);
  border-top: 1px solid rgba(255,255,255,0.09);
  color: #fff;
}
/* Force footerHtml content to inherit white — defeats Bootstrap resets */
.gm-footer p,
.gm-footer span,
.gm-footer small,
.gm-footer label,
.gm-footer a,
.gm-footer li,
.gm-footer div {
  color: inherit;
}
.gm-dialog-light .gm-footer p,
.gm-dialog-light .gm-footer span,
.gm-dialog-light .gm-footer small,
.gm-dialog-light .gm-footer label,
.gm-dialog-light .gm-footer a,
.gm-dialog-light .gm-footer li,
.gm-dialog-light .gm-footer div {
  color: var(--gm-text-light);
}

/* ── Footer color override (set via data-gm-footer-color) ── */
/* When footerColor is provided, all child elements inherit it,
   overriding both the dark and light variant defaults above.  */
.gm-footer[data-gm-footer-color] p,
.gm-footer[data-gm-footer-color] span,
.gm-footer[data-gm-footer-color] small,
.gm-footer[data-gm-footer-color] label,
.gm-footer[data-gm-footer-color] a,
.gm-footer[data-gm-footer-color] li,
.gm-footer[data-gm-footer-color] div {
  color: inherit !important;
}

/* ── Glass Buttons ──────────────────────────────────────── */
.gm-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 7px;
  padding: 9px 20px;
  border-radius: var(--gm-radius-sm);
  font-family: var(--gm-font-body);
  font-size: 0.875rem;
  font-weight: 600;
  letter-spacing: 0.01em;
  cursor: pointer;
  border: 1px solid transparent;
  transition:
    background 220ms var(--gm-ease),
    transform 180ms var(--gm-ease),
    box-shadow 220ms var(--gm-ease);
  -webkit-tap-highlight-color: transparent;
  touch-action: manipulation;
  white-space: nowrap;
  user-select: none;
}
.gm-btn:focus-visible {
  outline: 2px solid var(--gm-accent);
  outline-offset: 3px;
}
.gm-btn:active { transform: scale(0.96); }
.gm-btn i { font-size: 0.85em; line-height: 1; }

/* ghost */
.gm-btn-ghost {
  background: rgba(255,255,255,0.07);
  border-color: rgba(255,255,255,0.14);
  color: rgba(255,255,255,0.75);
}
.gm-btn-ghost:hover {
  background: rgba(255,255,255,0.13);
  color: #fff;
}

/* primary */
.gm-btn-primary {
  background: linear-gradient(135deg, #7c83ff 0%, #5a63e8 100%);
  border-color: rgba(124,131,255,0.5);
  color: #fff;
  box-shadow: 0 4px 20px rgba(124,131,255,0.30);
}
.gm-btn-primary:hover {
  background: linear-gradient(135deg, #9099ff 0%, #6b74f0 100%);
  box-shadow: 0 6px 28px rgba(124,131,255,0.45);
}

/* success */
.gm-btn-success {
  background: linear-gradient(135deg, #4ecca3 0%, #2ea87d 100%);
  border-color: rgba(78,204,163,0.4);
  color: #0a1628;
  box-shadow: 0 4px 20px rgba(78,204,163,0.25);
}
.gm-btn-success:hover { box-shadow: 0 6px 28px rgba(78,204,163,0.4); }

/* danger */
.gm-btn-danger {
  background: linear-gradient(135deg, #ff6b8a 0%, #e03058 100%);
  border-color: rgba(255,107,138,0.4);
  color: #fff;
  box-shadow: 0 4px 20px rgba(255,107,138,0.25);
}
.gm-btn-danger:hover { box-shadow: 0 6px 28px rgba(255,107,138,0.4); }

/* ── Trigger button (glass variant) ────────────────────── */
.gm-trigger-glass {
  background: rgba(255,255,255,0.08);
  border: 1px solid rgba(255,255,255,0.18);
  color: #fff;
  -webkit-backdrop-filter: blur(8px);
          backdrop-filter: blur(8px);
  box-shadow: 0 4px 20px rgba(0,0,0,0.20);
  padding: 10px 22px;
  border-radius: var(--gm-radius-sm);
  font-family: var(--gm-font-body);
  font-weight: 600;
  font-size: 0.9rem;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  transition: background 220ms var(--gm-ease), transform 180ms, box-shadow 220ms;
  -webkit-tap-highlight-color: transparent;
  touch-action: manipulation;
}
.gm-trigger-glass:hover {
  background: rgba(255,255,255,0.15);
  box-shadow: 0 6px 30px rgba(0,0,0,0.28);
}
.gm-trigger-glass:active { transform: scale(0.97); }
.gm-trigger-glass i { font-size: 0.85em; line-height: 1; }

/* ── No-blur fallback ───────────────────────────────────── */
@supports not ((-webkit-backdrop-filter: blur(1px)) or (backdrop-filter: blur(1px))) {
  .gm-dialog-dark  { background: rgba(15, 15, 30, 0.92); }
  .gm-dialog-light { background: rgba(230, 235, 255, 0.94); }
  .gm-backdrop     { background: rgba(4, 4, 15, 0.80); -webkit-backdrop-filter: none; backdrop-filter: none; }
}

/* ── Reduced motion ─────────────────────────────────────── */
@media (prefers-reduced-motion: reduce) {
  .gm-dialog,
  .gm-modal.gm-show .gm-dialog,
  .gm-modal.gm-closing .gm-dialog,
  .gm-backdrop,
  .gm-btn,
  .gm-close {
    transition-duration: 1ms !important;
    animation: none !important;
  }
  .gm-dialog,
  .gm-modal.gm-show   .gm-dialog,
  .gm-modal.gm-closing .gm-dialog { transform: none !important; }
}

/* ── Mobile safe-area ───────────────────────────────────── */
@supports (padding-bottom: env(safe-area-inset-bottom)) {
  .gm-footer { padding-bottom: max(clamp(14px,3.5vw,20px), env(safe-area-inset-bottom)); }
}
</style>
<script${nonce}>
(function(){
  'use strict';

  function uid(){ return 'gm-' + Math.random().toString(36).slice(2,8); }

  var _scrollLockCount = 0;
  var _scrollY = 0;
  function lockScroll(){
    if(++_scrollLockCount === 1){
      _scrollY = window.scrollY || window.pageYOffset;
      document.body.style.cssText +=
        ';overflow:hidden;position:fixed;top:-'+_scrollY+'px;width:100%;';
    }
  }
  function unlockScroll(){
    if(--_scrollLockCount <= 0){
      _scrollLockCount = 0;
      document.body.style.overflow = '';
      document.body.style.position = '';
      document.body.style.top = '';
      document.body.style.width = '';
      window.scrollTo(0, _scrollY);
    }
  }

  var FOCUSABLE =
    'a[href],button:not([disabled]),input:not([disabled]),select:not([disabled]),' +
    'textarea:not([disabled]),[tabindex]:not([tabindex="-1"])';
  function trapFocus(modal, e){
    var els = Array.prototype.slice.call(modal.querySelectorAll(FOCUSABLE));
    if(!els.length) return;
    var first = els[0], last = els[els.length-1];
    if(e.shiftKey){ if(document.activeElement===first){ e.preventDefault(); last.focus(); } }
    else          { if(document.activeElement===last) { e.preventDefault(); first.focus(); } }
  }

  var _stack = [];
  var _autoCloseTimers = {};

  // Apply footer color from data attribute on DOMContentLoaded
  document.addEventListener('DOMContentLoaded', function(){
    document.querySelectorAll('.gm-footer[data-gm-footer-color]').forEach(function(el){
      el.style.color = el.getAttribute('data-gm-footer-color');
    });
  });

  window.GlassModal = {
    open: function(id){
      var modal    = document.getElementById(id);
      var backdrop = document.getElementById(id + '-backdrop');
      if(!modal || modal.getAttribute('aria-hidden')==='false') return;

      lockScroll();
      backdrop.style.display = 'block';
      modal.style.display    = 'flex';
      void backdrop.offsetWidth;
      backdrop.classList.add('gm-show');
      modal.classList.add('gm-show');

      modal.setAttribute('aria-hidden','false');
      modal.removeAttribute('hidden');

      var first = modal.querySelector(FOCUSABLE);
      if(first) setTimeout(function(){ first.focus(); }, 60);

      function onKey(e){
        if(e.key==='Escape'||e.keyCode===27){ window.GlassModal.close(id); }
        if(e.key==='Tab'  ||e.keyCode===9)  { trapFocus(modal, e); }
      }
      modal._gmKeyHandler = onKey;
      document.addEventListener('keydown', onKey);

      function onBackdrop(e){ if(e.target===modal) window.GlassModal.close(id); }
      modal._gmBackdropHandler = onBackdrop;
      modal.addEventListener('click', onBackdrop);

      _stack.push(id);

      var duration = parseInt(modal.getAttribute('data-gm-autoclose'), 10);
      if(!isNaN(duration) && duration > 0){
        if(_autoCloseTimers[id]) clearTimeout(_autoCloseTimers[id]);
        _autoCloseTimers[id] = setTimeout(function(){
          window.GlassModal.close(id);
        }, duration);
      }
    },

    close: function(id){
      var modal    = document.getElementById(id);
      var backdrop = document.getElementById(id + '-backdrop');
      if(!modal || modal.getAttribute('aria-hidden')==='true') return;

      if(_autoCloseTimers[id]){
        clearTimeout(_autoCloseTimers[id]);
        delete _autoCloseTimers[id];
      }

      if(modal._gmKeyHandler)     { document.removeEventListener('keydown', modal._gmKeyHandler); }
      if(modal._gmBackdropHandler){ modal.removeEventListener('click', modal._gmBackdropHandler); }

      backdrop.classList.remove('gm-show');
      modal.classList.remove('gm-show');
      modal.classList.add('gm-closing');
      modal.setAttribute('aria-hidden','true');

      var redirectUrl = modal.getAttribute('data-gm-redirect');

      var dur = 340;
      setTimeout(function(){
        modal.classList.remove('gm-closing');
        backdrop.style.display = 'none';
        modal.style.display    = 'none';
        modal.setAttribute('hidden','');

        if(redirectUrl){
          window.location.href = redirectUrl;
        }
      }, dur);

      unlockScroll();
      _stack = _stack.filter(function(s){ return s !== id; });

      if(!redirectUrl){
        var trigger = document.querySelector('[data-gm-target="'+id+'"]');
        if(trigger) trigger.focus();
      }

      modal.dispatchEvent(new CustomEvent('gm:closed', {bubbles:true}));
    },

    toggle: function(id){
      var modal = document.getElementById(id);
      if(!modal) return;
      modal.getAttribute('aria-hidden')==='false'
        ? window.GlassModal.close(id)
        : window.GlassModal.open(id);
    }
  };

  document.addEventListener('click', function(e){
    var el = e.target.closest('[data-gm-target]');
    if(!el) return;
    e.preventDefault();
    window.GlassModal.toggle(el.getAttribute('data-gm-target'));
  });

  document.addEventListener('click', function(e){
    var el = e.target.closest('[data-gm-dismiss]');
    if(!el) return;
    var modal = el.closest('.gm-modal');
    if(modal) window.GlassModal.close(modal.id);
  });
})();
</script>
"""
  }

  // ─── MODAL ───────────────────────────────────────────────────────────────────
  /**
   * Renders a glassmorphic modal.
   *
   * @attr id               (required) Unique HTML id
   * @attr title            (optional) Header title text
   * @attr icon             (optional) Emoji/character shown in the title badge
   * @attr faIcon           (optional) Font Awesome classes for the title badge icon
   * @attr size             (optional) sm | md (default) | lg | xl | fs (fullscreen)
   * @attr variant          (optional) dark (default) | light
   * @attr closable         (optional) true (default) | false — show × button
   * @attr footerHtml       (optional) Raw HTML for custom footer content
   * @attr footerColor      (optional) Any valid CSS color for footer text (default: #fff).
   *                                   Overrides both dark and light variant defaults,
   *                                   cascading to all child elements inside footerHtml.
   * @attr labelledby       (optional) Override aria-labelledby id
   * @attr autoClose        (optional) Duration in ms — auto-dismisses the modal after
   *                                   the given time. Timer is cancelled on manual close.
   * @attr autoRedirectUrl  (optional) URL to navigate to whenever the modal closes,
   *                                   regardless of how it was closed (manually, via
   *                                   autoClose, or GlassModal.close()). Redirect fires
   *                                   after the close animation (340 ms) so the full
   *                                   fade-out is always visible before navigation.
   */
  def modal = { attrs, body ->
    def id = attrs.id ?: 'gm-' + System.currentTimeMillis()
    def title = attrs.title ?: ''
    def icon = attrs.icon ?: ''
    def faIcon = attrs.faIcon ?: ''
    def size = attrs.size ?: 'md'
    def variant = attrs.variant ?: 'dark'
    def closable = attrs.closable != 'false'
    def footer = attrs.footerHtml ?: ''
    def footerColor = attrs.footerColor ?: '#fff'
    def titleId = attrs.labelledby ?: (id + '-title')
    def autoClose = attrs.autoClose ?: ''
    def autoRedirectUrl = attrs.autoRedirectUrl ?: ''

    def validSizes = ['sm', 'md', 'lg', 'xl', 'fs']
    def validVariants = ['dark', 'light']
    if (!(size in validSizes)) size = 'md'
    if (!(variant in validVariants)) variant = 'dark'

    def autoCloseAttr = autoClose ? " data-gm-autoclose=\"${autoClose.encodeAsHTML()}\"" : ''
    def autoRedirectAttr = autoRedirectUrl ? " data-gm-redirect=\"${autoRedirectUrl.encodeAsHTML()}\"" : ''

    out << """
<!-- ── GlassModal: #${id} ── -->
<div id="${id}-backdrop" class="gm-backdrop" aria-hidden="true" style="display:none;"></div>
<div id="${id}"
     class="gm-modal"
     role="dialog"
     aria-modal="true"
     aria-labelledby="${titleId}"
     aria-hidden="true"
     hidden
     style="display:none;"${autoCloseAttr}${autoRedirectAttr}>
  <div class="gm-dialog gm-dialog-${size} gm-dialog-${variant}" tabindex="-1">
"""
    if (title || closable) {
      out << "    <div class=\"gm-header\">\n"
      out << "      <div style=\"display:flex;align-items:center;gap:10px;min-width:0;\">\n"
      if (faIcon || icon) {
        out << "        <span class=\"gm-title-icon\">${iconHtml(faIcon, icon)}</span>\n"
      }
      if (title) {
        out << "        <h5 class=\"gm-title\" id=\"${titleId}\">${title.encodeAsHTML()}</h5>\n"
      }
      out << "      </div>\n"
      if (closable) {
        out << "      <button class=\"gm-close\" data-gm-dismiss aria-label=\"Close dialog\" type=\"button\">&#x2715;</button>\n"
      }
      out << "    </div>\n"
    }

    out << "    <div class=\"gm-body\">\n"
    out << body()
    out << "\n    </div>\n"

    if (footer) {
      out << "    <div class=\"gm-footer\" data-gm-footer-color=\"${footerColor.encodeAsHTML()}\" style=\"color:${footerColor.encodeAsHTML()};\">\n${footer}\n    </div>\n"
    }

    out << "  </div>\n</div>\n<!-- ── /GlassModal: #${id} ── -->\n"
  }

  // ─── TRIGGER BUTTON ──────────────────────────────────────────────────────────
  /**
   * @attr target      (required) id of the target <gm:modal>
   * @attr label       (optional) Button text  (default: "Open")
   * @attr icon        (optional) Leading emoji/character
   * @attr faIcon      (optional) Font Awesome classes
   * @attr btnVariant  (optional) glass | primary | ghost (default: primary)
   * @attr extraClass  (optional) Additional CSS classes
   * @attr type        (optional) button (default) | submit
   */
  def modalTrigger = { attrs ->
    def target = attrs.target ?: ''
    def label = attrs.label ?: 'Open'
    def icon = attrs.icon ?: ''
    def faIcon = attrs.faIcon ?: ''
    def btnVariant = attrs.btnVariant ?: 'primary'
    def extraClass = attrs.extraClass ?: ''
    def type = attrs.type ?: 'button'

    def validBtn = ['glass', 'primary', 'ghost', 'success', 'danger']
    def cls = (btnVariant == 'glass')
      ? "gm-trigger-glass ${extraClass}"
      : "gm-btn gm-btn-${btnVariant in validBtn ? btnVariant : 'primary'} ${extraClass}"

    out << "<button type=\"${type}\" class=\"${cls.trim()}\" data-gm-target=\"${target}\" aria-haspopup=\"dialog\">"
    out << iconHtml(faIcon, icon)
    out << label.encodeAsHTML()
    out << "</button>"
  }

  // ─── FOOTER BUTTON HELPERS ───────────────────────────────────────────────────
  /**
   * @attr label      (optional) Text (default: "Cancel")
   * @attr variant    (optional) ghost | primary | success | danger (default: ghost)
   * @attr icon       (optional) Emoji/character
   * @attr faIcon     (optional) Font Awesome classes
   * @attr extraClass (optional) Additional CSS classes
   */
  def dismissBtn = { attrs ->
    def label = attrs.label ?: 'Cancel'
    def variant = attrs.variant ?: 'ghost'
    def icon = attrs.icon ?: ''
    def faIcon = attrs.faIcon ?: ''
    def cls = attrs.extraClass ?: ''
    out << "<button type=\"button\" class=\"gm-btn gm-btn-${variant} ${cls}\" data-gm-dismiss>"
    out << iconHtml(faIcon, icon)
    out << label.encodeAsHTML()
    out << "</button>"
  }

  /**
   * @attr label      (optional) Text (default: "Confirm")
   * @attr variant    (optional) primary (default) | success | danger
   * @attr onclick    (optional) Inline JS
   * @attr icon       (optional) Emoji/character
   * @attr faIcon     (optional) Font Awesome classes
   * @attr extraClass (optional) Additional CSS classes
   */
  def confirmBtn = { attrs ->
    def label = attrs.label ?: 'Confirm'
    def variant = attrs.variant ?: 'primary'
    def onclick = attrs.onclick ?: ''
    def icon = attrs.icon ?: ''
    def faIcon = attrs.faIcon ?: ''
    def cls = attrs.extraClass ?: ''
    def onclickAttr = onclick ? " onclick=\"${onclick.encodeAsHTML()}\"" : ''
    out << "<button type=\"button\" class=\"gm-btn gm-btn-${variant} ${cls}\"${onclickAttr}>"
    out << iconHtml(faIcon, icon)
    out << label.encodeAsHTML()
    out << "</button>"
  }
}
