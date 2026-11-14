package com.murad.app.matrimony.toggle

import grails.gsp.TagLib

/**
 * GlassToggleTagLib
 *
 * Renders a cutting-edge glassmorphic toggle (switch) button inline in the HTML document.
 * Assets (stylesheet + script) are emitted once per request via sentinels.
 *
 * Namespace : glassToggle
 *
 * @attr id           DOM element id (auto-generated when omitted)
 * @attr name         Form input name attribute
 * @attr checked      true | false — initial state  (default: false)
 * @attr disabled     true | false  (default: false)
 * @attr label        Optional label text shown beside the toggle
 * @attr labelPos     left | right — label position relative to toggle  (default: right)
 * @attr size         sm | md | lg  (default: md)
 * @attr color        accent colour hex or CSS value  (default: #00d4ff)
 * @attr onChange     Inline JS expression called on change, receives (checked) arg
 * @attr class        Additional CSS classes for the root element
 *
 * JS API (per instance):
 *   __GT__.instances['<id>'].toggle(true)   // programmatically turn on
 *   __GT__.instances['<id>'].toggle(false)  // programmatically turn off
 */
@TagLib
class GlassToggleTagLib {

  // ── Constants ────────────────────────────────────────────────────────

  static namespace = 'glassToggle'

  private static final String STYLE_SENTINEL = 'glassToggleStyleInjected'
  private static final String SCRIPT_SENTINEL = 'glassToggleScriptInjected'

  private static final Map<String, Map> SIZE_MAP = [
    sm: [trackW: 36, trackH: 20, thumbD: 14, thumbOff: 3, thumbOn: 19],
    md: [trackW: 52, trackH: 28, thumbD: 20, thumbOff: 4, thumbOn: 28],
    lg: [trackW: 68, trackH: 36, thumbD: 26, thumbOff: 5, thumbOn: 37],
  ].asImmutable()

  // ── Tag implementation ────────────────────────────────────────────────

  def toggle = { Map attrs, body ->

    final String elementId = (attrs.id ?: "gt-${UUID.randomUUID().toString().replace('-', '').take(10)}") as String
    final String inputId = "${elementId}-input"
    final String name = (attrs.name ?: elementId) as String
    final boolean checked = (attrs.checked?.toString() == 'true')
    final boolean disabled = (attrs.disabled?.toString() == 'true')
    final String label = (attrs.label ?: '') as String
    final String labelPos = (attrs.labelPos in ['left', 'right'] ? attrs.labelPos : 'right') as String
    final String size = (SIZE_MAP.containsKey(attrs.size) ? attrs.size : 'md') as String
    final String color = (attrs.color ?: '#00d4ff') as String
    final String onChange = (attrs.onChange ?: '') as String
    final String extraClass = (attrs.'class' ?: '') as String

    final Map sz = SIZE_MAP[size]

    emitSharedAssets()

    final String checkedAttr = checked ? ' checked' : ''
    final String disabledAttr = disabled ? ' disabled' : ''
    final String disabledClass = disabled ? ' gt-disabled' : ''
    final String checkedClass = checked ? ' gt-checked' : ''
    final String onChangeCb = onChange ? "function(checked){${onChange}}" : 'null'

    final String labelHtml = label ? """<span class="gt-label gt-label-${labelPos}">${label.replace('<', '&lt;').replace('>', '&gt;')}</span>""" : ''
    final String styleVars = [
      "--gt-color:${color}",
      "--gt-track-w:${sz.trackW}px",
      "--gt-track-h:${sz.trackH}px",
      "--gt-thumb-d:${sz.thumbD}px",
      "--gt-thumb-off:${sz.thumbOff}px",
      "--gt-thumb-on:${sz.thumbOn}px",
    ].join(';')

    final String labelLeft = labelPos == 'left' ? labelHtml : ''
    final String labelRight = labelPos == 'right' ? labelHtml : ''

    out << """<label id="${elementId}"
     class="gt-root gt-${size}${checkedClass}${disabledClass}${extraClass ? ' ' + extraClass : ''}"
     style="${styleVars}"
     aria-checked="${checked}"
     role="switch">
  <input id="${inputId}"
         class="gt-input"
         type="checkbox"
         name="${name}"
         ${checkedAttr}${disabledAttr}
         aria-hidden="true"
         tabindex="-1"/>
  ${labelLeft}
  <span class="gt-track" aria-hidden="true">
    <span class="gt-track-fill"></span>
    <span class="gt-thumb">
      <span class="gt-thumb-inner">
        <span class="gt-thumb-icon gt-icon-off"><svg viewBox="0 0 10 10" fill="none" xmlns="http://www.w3.org/2000/svg"><line x1="3" y1="3" x2="7" y2="7" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/><line x1="7" y1="3" x2="3" y2="7" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg></span>
        <span class="gt-thumb-icon gt-icon-on"><svg viewBox="0 0 10 10" fill="none" xmlns="http://www.w3.org/2000/svg"><polyline points="2,5 4,7 8,3" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/></svg></span>
      </span>
    </span>
    <span class="gt-shimmer" aria-hidden="true"></span>
  </span>
  ${labelRight}
</label>
<script>(function(){window.__GT__.mount('${elementId}', ${onChangeCb})})();</script>
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
<link rel="preconnect" href="https://fonts.googleapis.com"/>
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin/>
<link href="https://fonts.googleapis.com/css2?family=Syne:wght@400;500;600&display=swap" rel="stylesheet"/>
<style>
/* ═══════════════════════════════════════════════════════════════
   GlassToggle — Cutting-edge glassmorphic toggle switch
   All sizing driven by CSS custom properties set inline per instance
   ═══════════════════════════════════════════════════════════════ */

/* ── Root label ─────────────────────────────────────────────── */
.gt-root {
    --gt-radius: 999px;
    --gt-transition: 0.38s cubic-bezier(0.34, 1.56, 0.64, 1);
    --gt-transition-fast: 0.22s cubic-bezier(0.4, 0, 0.2, 1);

    display: inline-flex;
    align-items: center;
    gap: 0.6em;
    cursor: pointer;
    user-select: none;
    -webkit-user-select: none;
    font-family: \'Syne\', sans-serif;
    outline: none;
    position: relative;
}
.gt-root:focus-visible .gt-track {
    outline: 2px solid var(--gt-color);
    outline-offset: 3px;
}
.gt-root.gt-disabled {
    opacity: 0.42;
    cursor: not-allowed;
    pointer-events: none;
}

/* ── Hidden real checkbox ───────────────────────────────────── */
.gt-input {
    position: absolute;
    width: 1px; height: 1px;
    margin: -1px; padding: 0;
    border: 0; overflow: hidden;
    clip: rect(0,0,0,0);
    white-space: nowrap;
}

/* ── Track ──────────────────────────────────────────────────── */
.gt-track {
    position: relative;
    width:  var(--gt-track-w);
    height: var(--gt-track-h);
    border-radius: var(--gt-radius);
    flex-shrink: 0;
    overflow: hidden;

    background: rgba(255, 255, 255, 0.06);
    backdrop-filter: blur(18px) saturate(140%) brightness(1.1);
    -webkit-backdrop-filter: blur(18px) saturate(140%) brightness(1.1);
    border: 1px solid rgba(255, 255, 255, 0.18);

    box-shadow:
        inset 0 1px 0 rgba(255,255,255,0.12),
        inset 0 -1px 0 rgba(0,0,0,0.18),
        0 2px 8px rgba(0,0,0,0.28);

    transition:
        border-color var(--gt-transition-fast),
        box-shadow   var(--gt-transition-fast);
}

/* ── Track fill ─────────────────────────────────────────────── */
.gt-track-fill {
    position: absolute;
    inset: 0;
    border-radius: inherit;
    background: linear-gradient(
        110deg,
        color-mix(in srgb, var(--gt-color) 85%, #000) 0%,
        var(--gt-color) 60%,
        color-mix(in srgb, var(--gt-color) 70%, #fff) 100%
    );
    opacity: 0;
    transition: opacity var(--gt-transition-fast);
}

/* ── Shimmer ────────────────────────────────────────────────── */
.gt-shimmer {
    position: absolute;
    inset: 0;
    border-radius: inherit;
    background: linear-gradient(
        130deg,
        rgba(255,255,255,0.18) 0%,
        rgba(255,255,255,0.04) 40%,
        transparent 70%
    );
    pointer-events: none;
    transition: opacity var(--gt-transition-fast);
}

/* ── Thumb ──────────────────────────────────────────────────── */
.gt-thumb {
    position: absolute;
    top: 50%;
    left: var(--gt-thumb-off);
    transform: translateY(-50%);
    width:  var(--gt-thumb-d);
    height: var(--gt-thumb-d);
    border-radius: 50%;
    transition: left var(--gt-transition), box-shadow var(--gt-transition-fast);
    will-change: left;
    z-index: 2;

    background: rgba(255,255,255,0.92);
    backdrop-filter: blur(8px);
    -webkit-backdrop-filter: blur(8px);
    border: 1px solid rgba(255,255,255,0.70);
    box-shadow:
        0 1px 3px rgba(0,0,0,0.30),
        0 4px 12px rgba(0,0,0,0.20),
        inset 0 1px 0 rgba(255,255,255,0.90);
}

/* ── Thumb inner ────────────────────────────────────────────── */
.gt-thumb-inner {
    position: absolute;
    inset: 0;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    overflow: hidden;
}

/* ── Thumb icons ────────────────────────────────────────────── */
.gt-thumb-icon {
    position: absolute;
    display: flex;
    align-items: center;
    justify-content: center;
    width: 52%;
    height: 52%;
    transition:
        opacity   var(--gt-transition-fast),
        transform var(--gt-transition);
}
.gt-thumb-icon svg { width: 100%; height: 100%; }

.gt-icon-off {
    color: rgba(0,0,0,0.35);
    opacity: 1;
    transform: scale(1) rotate(0deg);
}
.gt-icon-on {
    color: var(--gt-color);
    opacity: 0;
    transform: scale(0.5) rotate(-45deg);
}

/* ── Label ──────────────────────────────────────────────────── */
.gt-label {
    font-size: 0.9rem;
    font-weight: 500;
    letter-spacing: 0.02em;
    color: rgba(255,255,255,0.78);
    transition: color var(--gt-transition-fast);
}

/* ══════════════════════════════════════════════════════════════
   CHECKED STATE
   ══════════════════════════════════════════════════════════════ */
.gt-root.gt-checked .gt-track {
    border-color: color-mix(in srgb, var(--gt-color) 60%, transparent);
    box-shadow:
        inset 0 1px 0 rgba(255,255,255,0.10),
        inset 0 -1px 0 rgba(0,0,0,0.12),
        0 0  4px rgba(var(--gt-color), 0.40),
        0 0 14px color-mix(in srgb, var(--gt-color) 28%, transparent),
        0 0 28px color-mix(in srgb, var(--gt-color) 14%, transparent);
}
.gt-root.gt-checked .gt-track-fill  { opacity: 1; }
.gt-root.gt-checked .gt-thumb {
    left: var(--gt-thumb-on);
    background: rgba(255,255,255,0.97);
    box-shadow:
        0 1px 3px rgba(0,0,0,0.18),
        0 4px 16px color-mix(in srgb, var(--gt-color) 45%, transparent),
        0 0   8px color-mix(in srgb, var(--gt-color) 30%, transparent),
        inset 0 1px 0 rgba(255,255,255,0.90);
}
.gt-root.gt-checked .gt-icon-off { opacity: 0; transform: scale(0.5) rotate(45deg); }
.gt-root.gt-checked .gt-icon-on  { opacity: 1; transform: scale(1) rotate(0deg); }
.gt-root.gt-checked .gt-label    { color: color-mix(in srgb, var(--gt-color) 80%, #fff); }
.gt-root.gt-checked .gt-shimmer  { opacity: 0.5; }

/* ── Hover (unchecked) ──────────────────────────────────────── */
.gt-root:not(.gt-disabled):not(.gt-checked):hover .gt-track {
    border-color: rgba(255,255,255,0.28);
    box-shadow:
        inset 0 1px 0 rgba(255,255,255,0.16),
        inset 0 -1px 0 rgba(0,0,0,0.20),
        0 2px 12px rgba(0,0,0,0.32);
}
.gt-root:not(.gt-disabled):not(.gt-checked):hover .gt-thumb {
    box-shadow:
        0 1px 4px rgba(0,0,0,0.36),
        0 4px 14px rgba(0,0,0,0.24),
        inset 0 1px 0 rgba(255,255,255,0.90);
}

/* ── Active press squish ────────────────────────────────────── */
.gt-root:not(.gt-disabled):active .gt-thumb {
    width: calc(var(--gt-thumb-d) * 1.22);
    border-radius: 999px;
    transition:
        left       var(--gt-transition),
        width      0.15s ease,
        box-shadow var(--gt-transition-fast);
}

/* ── Size variants ──────────────────────────────────────────── */
.gt-root.gt-sm .gt-label { font-size: 0.78rem; }
.gt-root.gt-lg .gt-label { font-size: 1.0rem;  }
</style>
'''
  }

  private void emitScript() {
    if (request.getAttribute(SCRIPT_SENTINEL)) return
    request.setAttribute(SCRIPT_SENTINEL, true)
    out << '''
<script>
!function(G){

  /* Registry of mounted toggle instances keyed by element id */
  G.instances = G.instances || {};

  /* ── toggle(state) — programmatic state setter ───────────────
   *  Usage (inside DOMContentLoaded or anytime after mount):
   *    __GT__.instances['my-toggle-id'].toggle(true);
   *    __GT__.instances['my-toggle-id'].toggle(false);
   * ─────────────────────────────────────────────────────────── */
  function createInstance(root, input, onChangeCb) {
    return {
      toggle: function(state) {
        if (root.classList.contains(\'gt-disabled\')) return;
        var checked = Boolean(state);
        root.classList.toggle(\'gt-checked\', checked);
        root.setAttribute(\'aria-checked\', String(checked));
        input.checked = checked;
        input.dispatchEvent(new Event(\'change\', { bubbles: true }));
        if (typeof onChangeCb === \'function\') onChangeCb(checked);
      }
    };
  }

  G.mount = function(id, onChangeCb) {
    var root  = document.getElementById(id);
    if (!root) return;
    var input = root.querySelector(\'.gt-input\');
    if (!input) return;

    root.setAttribute(\'tabindex\', \'0\');

    /* Create and register the instance so developers can call
       __GT__.instances[id].toggle(true/false) at any time */
    var instance = createInstance(root, input, onChangeCb);
    G.instances[id] = instance;

    function toggle() {
      var next = !root.classList.contains(\'gt-checked\');
      instance.toggle(next);
    }

    root.addEventListener(\'click\', function(e) {
      e.preventDefault();
      toggle();
    });
    root.addEventListener(\'keydown\', function(e) {
      if (e.key === \' \' || e.key === \'Enter\') { e.preventDefault(); toggle(); }
    });
  };

}(window.__GT__ = window.__GT__ || {});
</script>
'''
  }
}
