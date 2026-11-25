package com.murad.app.matrimony.alert

import grails.gsp.TagLib

/**
 * GlassAlert2TagLib
 *
 * Renders a glassmorphic, neon-bordered alert inline in the HTML document.
 * Assets (stylesheet + script) are emitted once per request via sentinels.
 *
 * Namespace : glass2
 *
 * @attr message  REQUIRED  Alert body text.
 * @attr type               info | success | warning | error  (default: info)
 * @attr duration           Auto-dismiss in ms; 0 = persistent  (default: 5000)
 * @attr id                 DOM element id (auto-generated when omitted)
 * @attr class              Additional CSS classes for the root element
 */
@TagLib
class GlassAlert2TagLib {

  // ── Constants ────────────────────────────────────────────────────────

  static namespace = 'glass2'

  private static final String STYLE_SENTINEL = 'glass2AlertStyleInjected'
  private static final String SCRIPT_SENTINEL = 'glass2AlertScriptInjected'
  private static final int DEFAULT_DURATION = 5000
  private static final Set<String> VALID_TYPES = ['info', 'success', 'warning', 'error'].toSet().asImmutable()

  /** FontAwesome icon class per alert type */
  private static final Map<String, String> TYPE_ICONS = [
    info   : 'fa-solid fa-circle-info',
    success: 'fa-solid fa-circle-check',
    warning: 'fa-solid fa-triangle-exclamation',
    error  : 'fa-solid fa-circle-xmark'
  ].asImmutable()

  /**
   * Neon colour tokens per type.
   * Each entry: [ border-hex, r, g, b ] — used to build CSS custom properties.
   */
  private static final Map<String, Map> TYPE_COLORS = [
    info   : [hex: '#00d4ff', r: 0, g: 212, b: 255],
    success: [hex: '#00ff9d', r: 0, g: 255, b: 157],
    warning: [hex: '#ffb700', r: 255, g: 183, b: 0],
    error  : [hex: '#ff2d6b', r: 255, g: 45, b: 107]
  ].asImmutable()

  // ── Tag implementation ────────────────────────────────────────────────

  def alert = { Map attrs, body ->

    final String message = (attrs.message ?: body()?.toString()?.trim() ?: '') as String
    final String type = VALID_TYPES.contains(attrs.type) ? attrs.type as String : 'info'
    final int duration = attrs.duration != null ? (attrs.duration as int) : DEFAULT_DURATION
    final String elementId = (attrs.id ?: "ga2-${UUID.randomUUID().toString().replace('-', '').take(10)}") as String
    final String extraClass = (attrs.'class' ?: '') as String

    if (!message) return

    emitSharedAssets()

    final String escapedMsg = message
      .replace('&', '&amp;')
      .replace('<', '&lt;')
      .replace('>', '&gt;')
      .replace('"', '&quot;')

    final Map color = TYPE_COLORS[type]
    final String iconClass = TYPE_ICONS[type]
    final String alertClass = "ga2-alert ga2-${type}${extraClass ? ' ' + extraClass : ''}"
    final String progressHtml = duration > 0 ? '<div class="ga2-progress" aria-hidden="true"></div>' : ''

    out << """<div id="${elementId}"
     class="${alertClass}"
     role="alert"
     aria-live="polite"
     style="--ga2-color:${color.hex};--ga2-r:${color.r};--ga2-g:${color.g};--ga2-b:${color.b}">
  <i class="ga2-icon ${iconClass}" aria-hidden="true"></i>
  <span class="ga2-message">${escapedMsg}</span>
  <button class="ga2-close" type="button" aria-label="Dismiss">
    <i class="fa-solid fa-xmark" aria-hidden="true"></i>
  </button>
  ${progressHtml}
</div>
<script>(function(){function r(){window.__GA2__.init('${elementId}',${duration})}document.readyState==='loading'?document.addEventListener('DOMContentLoaded',r):r()})();</script>
"""
  }

  // ── Private helpers ───────────────────────────────────────────────────

  private void emitSharedAssets() {
    emitStyle()
    emitScript()
  }

  private void emitStyle() {
    if (request.getAttribute(STYLE_SENTINEL)) return
    request.setAttribute(STYLE_SENTINEL, true)
    out << '''
<link rel="stylesheet"
      href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css"
      integrity="sha512-SnH5WK+bZxgPHs44uWIX+LLJAJ9/2PkPKZ5QiAj6Ta86w+fsb2TkcmfRyVX3pBnMFcV7oQPJkl9QevSCWr3W=="
      crossorigin="anonymous" referrerpolicy="no-referrer"/>
<style>
@import url(\'https://fonts.googleapis.com/css2?family=DM+Sans:wght@400;500;600&display=swap\');

/*
 * CSS custom properties (--ga2-color, --ga2-r/g/b) are set inline per alert
 * from the Groovy type-colour map, eliminating repetitive type-specific rules.
 */

/* ── Alert card ─────────────────────────────────────────────── */
.ga2-alert {
    /*
     * Vertical margins create clear breathing room so the alert never
     * overlaps adjacent UI components above or below it.
     * These values are also animated during enter/exit so the space
     * itself opens and closes smoothly with the card.
     */
    margin-top: 0.75rem;
    margin-bottom: 0.75rem;
    font-family: \'DM Sans\', sans-serif;
    display: flex;
    align-items: center;
    gap: 0.75rem;
    width: 100%;
    padding: 0.85rem 1rem;
    border-radius: 14px;
    border: 1px solid var(--ga2-color);
    background: rgba(0, 0, 0, 0.15);
    backdrop-filter: blur(24px) saturate(160%) brightness(1.08);
    -webkit-backdrop-filter: blur(24px) saturate(160%) brightness(1.08);
    box-shadow:
        0 0  3px rgba(var(--ga2-r), var(--ga2-g), var(--ga2-b), 0.70),
        0 0  8px rgba(var(--ga2-r), var(--ga2-g), var(--ga2-b), 0.40),
        0 0 18px rgba(var(--ga2-r), var(--ga2-g), var(--ga2-b), 0.18),
        inset 0 0 10px rgba(var(--ga2-r), var(--ga2-g), var(--ga2-b), 0.07);
    position: relative;
    overflow: hidden;
    box-sizing: border-box;
    /* Slide down + fade in on appear */
    animation: ga2In 0.44s cubic-bezier(0.22, 1, 0.36, 1) both;
}

/* Top-edge sheen */
.ga2-alert::before {
    content: \'\';
    position: absolute;
    inset: 0;
    border-radius: inherit;
    background: linear-gradient(160deg,
        rgba(255,255,255,0.07) 0%,
        rgba(255,255,255,0.01) 50%,
        transparent 100%);
    pointer-events: none;
}

/* ── Type icon ──────────────────────────────────────────────── */
.ga2-icon {
    flex-shrink: 0;
    font-size: 1.15rem;
    line-height: 1;
    color: var(--ga2-color);
    filter: drop-shadow(0 0 5px rgba(var(--ga2-r), var(--ga2-g), var(--ga2-b), 0.90));
}

/* ── Message text ───────────────────────────────────────────── */
.ga2-message {
    flex: 1;
    min-width: 0;
    font-size: 1rem;
    font-weight: 500;
    line-height: 1.5;
    word-break: break-word;
    color: color-mix(in srgb, var(--ga2-color) 55%, #fff);
    text-shadow:
        0 0  4px rgba(var(--ga2-r), var(--ga2-g), var(--ga2-b), 0.60),
        0 0 12px rgba(var(--ga2-r), var(--ga2-g), var(--ga2-b), 0.20);
}

/* ── Close button — inline flex item, rightmost in the row ─── */
.ga2-close {
    flex-shrink: 0;
    width: 26px;
    height: 26px;
    border-radius: 6px;
    border: 1px solid rgba(255,255,255,0.22);
    background: rgba(255,255,255,0.10);
    backdrop-filter: blur(12px) saturate(150%);
    -webkit-backdrop-filter: blur(12px) saturate(150%);
    color: rgba(255,255,255,0.55);
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 0.75rem;
    line-height: 1;
    cursor: pointer;
    outline: none;
    transition:
        color        0.2s ease,
        border-color 0.2s ease,
        background   0.2s ease,
        box-shadow   0.2s ease,
        transform    0.2s cubic-bezier(0.34, 1.56, 0.64, 1);
}
.ga2-close:is(:hover, :focus-visible) {
    color: #fff;
    background: rgba(255,255,255,0.18);
    border-color: rgba(255,255,255,0.85);
    transform: scale(1.12);
    box-shadow:
        0 0  5px rgba(255,255,255,0.50),
        0 0 12px rgba(255,255,255,0.15),
        inset 0 0 6px rgba(255,255,255,0.08);
}

/* ── Progress bar ───────────────────────────────────────────── */
.ga2-progress {
    position: absolute;
    inset: auto 0 0 0;
    height: 2px;
    border-radius: 0 0 14px 14px;
    transform-origin: left;
    /* Lighter than the border colour — mixed 70% type colour with white */
    background: color-mix(in srgb, var(--ga2-color) 70%, #fff);
}

/* ── Enter: slide down + fade in ────────────────────────────── */
/*
 * The alert starts collapsed (max-height 0, no padding, no margin) and
 * offset upward. It then expands its layout space first — so surrounding
 * content is pushed aside before the card becomes visible — then slides
 * down into position while fading in. This prevents any jarring jump.
 */
@keyframes ga2In {
    0% {
        opacity: 0;
        transform: translateY(-16px);
        max-height: 0;
        padding-block: 0;
        margin-top: 0;
        margin-bottom: 0;
    }
    25% {
        opacity: 0;
        transform: translateY(-16px);
        max-height: 120px;
        padding-block: 0.85rem;
        margin-top: 0.75rem;
        margin-bottom: 0.75rem;
    }
    100% {
        opacity: 1;
        transform: translateY(0);
        max-height: 120px;
        padding-block: 0.85rem;
        margin-top: 0.75rem;
        margin-bottom: 0.75rem;
    }
}

/* ── Exit: slide up + fade out ──────────────────────────────── */
/*
 * The card fades out and slides upward first, then its layout space
 * (padding, margin, max-height) collapses so surrounding content closes
 * the gap without a sudden snap.
 */
@keyframes ga2Out {
    0% {
        opacity: 1;
        transform: translateY(0);
        max-height: 120px;
        padding-block: 0.85rem;
        margin-top: 0.75rem;
        margin-bottom: var(--ga2-mb, 0.75rem);
    }
    55% {
        opacity: 0;
        transform: translateY(-16px);
        max-height: 120px;
        padding-block: 0.85rem;
        margin-top: 0.75rem;
        margin-bottom: var(--ga2-mb, 0.75rem);
    }
    100% {
        opacity: 0;
        transform: translateY(-16px);
        max-height: 0;
        padding-block: 0;
        margin-top: 0;
        margin-bottom: 0;
    }
}
.ga2-alert.ga2-dismissing {
    overflow: hidden;
    animation: ga2Out 0.44s cubic-bezier(0.4, 0, 0.6, 1) forwards;
    pointer-events: none;
}
</style>
'''
  }

  private void emitScript() {
    if (request.getAttribute(SCRIPT_SENTINEL)) return
    request.setAttribute(SCRIPT_SENTINEL, true)
    out << '''
<script>
!function(G){
  G.dismiss=function(id){
    var el=document.getElementById(id);
    if(!el||el.dataset.dismissed)return;
    el.dataset.dismissed=\'1\';
    /* Snapshot the live margin-bottom before the animation zeroes it out */
    el.style.setProperty(\'--ga2-mb\',getComputedStyle(el).marginBottom);
    el.classList.add(\'ga2-dismissing\');
    setTimeout(function(){el.remove()},460);
  };
  G.init=function(id,dur){
    var el=document.getElementById(id);
    if(!el)return;
    var btn=el.querySelector(\'.ga2-close\');
    if(btn)btn.addEventListener(\'click\',function(){G.dismiss(id)});
    if(dur>0){
      var p=el.querySelector(\'.ga2-progress\');
      if(p){
        p.style.transition=\'transform \'+dur+\'ms linear\';
        requestAnimationFrame(function(){requestAnimationFrame(function(){p.style.transform=\'scaleX(0)\'})});
      }
      setTimeout(function(){G.dismiss(id)},dur);
    }
  };
}(window.__GA2__=window.__GA2__||{});
</script>
'''
  }
}
