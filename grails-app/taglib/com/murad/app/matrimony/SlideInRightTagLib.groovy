package com.murad.app.matrimony

import grails.gsp.TagLib

/**
 * SlideInRight  —  Slide-in-from-right CSS animation system for Grails 6 GSP
 * Namespace    : glass
 * CSS prefix   : sir   (avoids collision with sit / sil prefixes)
 * Window guards: window.__sirReady / window.__sirIO
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * QUICK START
 * ═══════════════════════════════════════════════════════════════════════════
 *
 *  1. Emit styles once in your layout <head>:
 *       <glass:slideInRightAnim/>
 *
 *  2. Wrap any element:
 *       <glass:slideInRightAnimWrap>
 *         <div class="card">…</div>
 *       </glass:slideInRightAnimWrap>
 *
 *  3. Stagger a list with <g:each>:
 *       <g:each in="${items}" var="item" status="i">
 *         <glass:slideInRightAnimItem index="${i}">
 *           <div>${item.name}</div>
 *         </glass:slideInRightAnimItem>
 *       </g:each>
 *
 *  4. Auto-stagger a group of children:
 *       <glass:slideInRightAnimGroup tag="ul" variant="soft" staggerMs="70">
 *         <li>Alpha</li>
 *         <li>Beta</li>
 *         <li>Gamma</li>
 *       </glass:slideInRightAnimGroup>
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * TAG REFERENCE
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * <glass:slideInRightAnim/>
 *   Writes global CSS keyframes and the IntersectionObserver bootstrap script.
 *   Call ONCE per page, inside <head>.
 *   Safe alongside <glass:slideInTopAnim/> and <glass:slideInLeftAnim/> —
 *   uses isolated CSS classes (sir-*) and separate window guards (__sirReady,
 *   __sirIO).
 *
 * ───────────────────────────────────────────────────────────────────────────
 * <glass:slideInRightAnimWrap
 *     [variant   = "default"]   Animation variant (see list below)
 *     [delay     = "0"]         Start delay in ms
 *     [duration  = "380"]       Animation duration in ms  (mobile-first default)
 *     [distance  = "28"]        Travel distance in px     (mobile-first default)
 *     [easing    = ""]          Override CSS easing / cubic-bezier string
 *     [once      = "false"]     "true" → animate only when element enters viewport
 *     [tag       = "div"]       HTML wrapper element tag name
 *     [id        = ""]          id attribute on the wrapper
 *     [class     = ""]          Extra CSS classes on the wrapper
 *     [style     = ""]          Extra inline CSS on the wrapper
 * >body</glass:slideInRightAnimWrap>
 *
 *   Wraps body content in a single animated element.
 *
 * ───────────────────────────────────────────────────────────────────────────
 * <glass:slideInRightAnimItem
 *     [index     = "0"]         Stagger position (0-based). delay = index × staggerMs
 *     [staggerMs = "60"]        Milliseconds between each stagger step
 *     [delay     = "0"]         Additional base delay added on top of stagger
 *     [variant   = "default"]
 *     [duration  = "380"]
 *     [distance  = "28"]
 *     [easing    = ""]
 *     [once      = "false"]
 *     [tag       = "div"]
 *     [id        = ""] [class  = ""] [style = ""]
 * >body</glass:slideInRightAnimItem>
 *
 *   Use inside <g:each status="i"> for staggered lists.
 *   Total delay = (index × staggerMs) + delay
 *
 * ───────────────────────────────────────────────────────────────────────────
 * <glass:slideInRightAnimGroup
 *     [staggerMs = "60"]        Delay step between each direct child
 *     [delay     = "0"]         Base delay before first child
 *     [variant   = "default"]
 *     [duration  = "380"]
 *     [distance  = "28"]
 *     [easing    = ""]
 *     [once      = "false"]
 *     [tag       = "div"]
 *     [id        = ""] [class  = ""] [style = ""]
 * >children</glass:slideInRightAnimGroup>
 *
 *   The group wrapper itself does NOT animate. Each direct child gets sir
 *   classes and staggered delays injected via an inline <script>.
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * VARIANTS
 * ═══════════════════════════════════════════════════════════════════════════
 *
 *   default    —  clean translateX + fade, cubic ease-out
 *   soft       —  translateX + fade + subtle scale-up (0.97 → 1)
 *   bounce     —  slides in then gently overshoots leftward and settles
 *   overshoot  —  fast dramatic entry with elastic scaleX recoil
 *   fade       —  opacity only, no movement
 *   blur       —  translateX + fade + blur(8px → 0)
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * CROSS-BROWSER COMPATIBILITY
 * ═══════════════════════════════════════════════════════════════════════════
 *
 *   CSS:
 *     • @-webkit-keyframes for every variant (Safari < 9, Android 4.x WebKit)
 *     • -webkit-transform alongside transform in every keyframe
 *     • -webkit-filter alongside filter in the blur variant
 *     • -webkit-animation on every variant class (static fallback values
 *       for engines that support the prefix but not CSS custom properties)
 *     • var(--x, fallback) on every var() call
 *     • -webkit-animation-fill-mode / -webkit-animation-play-state
 *     • -ms-transform: none in the prefers-reduced-motion reset (IE 10/11)
 *
 *   JavaScript:
 *     • NodeList.forEach polyfill (IE 11)
 *     • CSS.supports() detection — when CSS custom properties are unsupported
 *       (IE 11), animation-delay and animation-duration are set as explicit
 *       inline styles after reading the --sir-* values from the style attr
 *     • requestAnimationFrame with setTimeout(cb,16) fallback — prevents
 *       Safari first-frame glitch
 *     • document.readyState guard — safe when script runs after DOMContentLoaded
 *     • typeof MutationObserver guard (IE 10 / old Android)
 *     • Array.prototype.forEach.call() throughout instead of NodeList.forEach
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * MOBILE-FIRST DESIGN
 * ═══════════════════════════════════════════════════════════════════════════
 *
 *   • Default distance 28px (vs 36px in SlideInLeft) — subtler on narrow screens
 *   • Default duration 380ms — snappier on lower-powered mobile CPUs
 *   • touch-action: pan-y on animated elements — lets vertical scroll pass
 *     through uninterrupted during the animation
 *   • overflow-x: hidden on .sir-group — prevents horizontal scroll flash when
 *     off-screen elements slide in from the right on narrow viewports
 *   • IntersectionObserver rootMargin "0px 0px -40px 0px" — fires 40px before
 *     the element fully enters, giving mobile a head start on the animation
 *   • will-change: transform, opacity — promotes to GPU composite layer on
 *     mobile, avoiding paint-triggered jank
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * SCROLL-TRIGGERED (once="true")
 * ═══════════════════════════════════════════════════════════════════════════
 *
 *   When once="true" the animation is paused on render. The IntersectionObserver
 *   (bootstrapped by <glass:slideInRightAnim/>) unpauses it on first entry.
 *   Falls back to immediate play on browsers without IntersectionObserver.
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * REDUCED MOTION
 * ═══════════════════════════════════════════════════════════════════════════
 *
 *   All animations are fully disabled when prefers-reduced-motion: reduce
 *   is set. Elements render at full opacity with no transform or filter.
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * COEXISTENCE WITH OTHER SLIDE TAGLIBS
 * ═══════════════════════════════════════════════════════════════════════════
 *
 *   SlideInTop   → sit-*   __sitReady  __sitIO
 *   SlideInLeft  → sil-*   __silReady  __silIO
 *   SlideInRight → sir-*   __sirReady  __sirIO
 *
 *   All three can be used on the same page without any conflict.
 */
@TagLib
@SuppressWarnings(['unused'])
class SlideInRightTagLib {

  static namespace = 'glass'

  // ═══════════════════════════════════════════════════════════════
  // slideInRightAnim  —  emit once in <head>
  // ═══════════════════════════════════════════════════════════════
  def slideInRightAnim = { attrs ->
    out << '''<style>
/* ── SlideInRight — cross-browser keyframes (prefix: sir) ───────────
   Element starts to the RIGHT: translateX is POSITIVE in the from-frame.
   ──────────────────────────────────────────────────────────────────── */

/* ── default : translateX + fade ──────────────────────────────────── */
@-webkit-keyframes sir-default {
  from { opacity: 0; -webkit-transform: translateX(28px);  transform: translateX(28px); }
  to   { opacity: 1; -webkit-transform: translateX(0);     transform: translateX(0); }
}
@keyframes sir-default {
  from { opacity: 0; -webkit-transform: translateX(var(--sir-dist, 28px)); transform: translateX(var(--sir-dist, 28px)); }
  to   { opacity: 1; -webkit-transform: translateX(0);                     transform: translateX(0); }
}

/* ── soft : translateX + fade + subtle scale ──────────────────────── */
@-webkit-keyframes sir-soft {
  from { opacity: 0; -webkit-transform: translateX(28px) scale(0.97); transform: translateX(28px) scale(0.97); }
  to   { opacity: 1; -webkit-transform: translateX(0) scale(1);       transform: translateX(0) scale(1); }
}
@keyframes sir-soft {
  from { opacity: 0; -webkit-transform: translateX(var(--sir-dist, 28px)) scale(0.97); transform: translateX(var(--sir-dist, 28px)) scale(0.97); }
  to   { opacity: 1; -webkit-transform: translateX(0) scale(1);                        transform: translateX(0) scale(1); }
}

/* ── bounce : slide in, overshoot left, settle ─────────────────────
   Overshoot is negative (left) because element came from the right.
   ──────────────────────────────────────────────────────────────────── */
@-webkit-keyframes sir-bounce {
  0%   { opacity: 0; -webkit-transform: translateX(28px);  transform: translateX(28px); }
  55%  { opacity: 1; -webkit-transform: translateX(-5px);  transform: translateX(-5px); }
  72%  {             -webkit-transform: translateX(3px);   transform: translateX(3px); }
  88%  {             -webkit-transform: translateX(-2px);  transform: translateX(-2px); }
  100% {             -webkit-transform: translateX(0);     transform: translateX(0); }
}
@keyframes sir-bounce {
  0%   { opacity: 0; -webkit-transform: translateX(var(--sir-dist, 28px)); transform: translateX(var(--sir-dist, 28px)); }
  55%  { opacity: 1; -webkit-transform: translateX(-5px);                  transform: translateX(-5px); }
  72%  {             -webkit-transform: translateX(3px);                   transform: translateX(3px); }
  88%  {             -webkit-transform: translateX(-2px);                  transform: translateX(-2px); }
  100% {             -webkit-transform: translateX(0);                     transform: translateX(0); }
}

/* ── overshoot : fast entry + elastic scaleX recoil ──────────────── */
@-webkit-keyframes sir-overshoot {
  0%   { opacity: 0; -webkit-transform: translateX(28px) scaleX(0.93);  transform: translateX(28px) scaleX(0.93); }
  48%  { opacity: 1; -webkit-transform: translateX(-7px) scaleX(1.04);  transform: translateX(-7px) scaleX(1.04); }
  68%  {             -webkit-transform: translateX(4px) scaleX(0.98);   transform: translateX(4px) scaleX(0.98); }
  84%  {             -webkit-transform: translateX(-2px) scaleX(1.01);  transform: translateX(-2px) scaleX(1.01); }
  100% {             -webkit-transform: translateX(0) scaleX(1);        transform: translateX(0) scaleX(1); }
}
@keyframes sir-overshoot {
  0%   { opacity: 0; -webkit-transform: translateX(var(--sir-dist, 28px)) scaleX(0.93); transform: translateX(var(--sir-dist, 28px)) scaleX(0.93); }
  48%  { opacity: 1; -webkit-transform: translateX(-7px) scaleX(1.04);                  transform: translateX(-7px) scaleX(1.04); }
  68%  {             -webkit-transform: translateX(4px) scaleX(0.98);                   transform: translateX(4px) scaleX(0.98); }
  84%  {             -webkit-transform: translateX(-2px) scaleX(1.01);                  transform: translateX(-2px) scaleX(1.01); }
  100% {             -webkit-transform: translateX(0) scaleX(1);                        transform: translateX(0) scaleX(1); }
}

/* ── fade : opacity only ──────────────────────────────────────────── */
@-webkit-keyframes sir-fade {
  from { opacity: 0; }
  to   { opacity: 1; }
}
@keyframes sir-fade {
  from { opacity: 0; }
  to   { opacity: 1; }
}

/* ── blur : translateX + fade + blur ─────────────────────────────── */
@-webkit-keyframes sir-blur {
  from { opacity: 0; -webkit-transform: translateX(28px); transform: translateX(28px); -webkit-filter: blur(8px); filter: blur(8px); }
  to   { opacity: 1; -webkit-transform: translateX(0);    transform: translateX(0);    -webkit-filter: blur(0);   filter: blur(0); }
}
@keyframes sir-blur {
  from { opacity: 0; -webkit-transform: translateX(var(--sir-dist, 28px)); transform: translateX(var(--sir-dist, 28px)); -webkit-filter: blur(8px); filter: blur(8px); }
  to   { opacity: 1; -webkit-transform: translateX(0);                     transform: translateX(0);                     -webkit-filter: blur(0);   filter: blur(0); }
}

/* ── Base ─────────────────────────────────────────────────────────────
   Mobile-first defaults:
     duration  380ms  — snappier on lower-powered mobile CPUs
     distance  +28px  — subtler travel on narrow screens
   CSS custom properties with static fallback values in var() for engines
   that support custom properties but not a specific shorthand.
   IE 11 has no custom property support; the -webkit-animation fallback on
   each variant class handles graceful degradation.
   ──────────────────────────────────────────────────────────────────── */
.sir {
  --sir-dur:   380ms;
  --sir-delay: 0ms;
  --sir-dist:  28px;
  --sir-ease:  cubic-bezier(0.22, 1, 0.36, 1);
  /* GPU compositing on mobile — avoids paint-triggered jank */
  will-change: transform, opacity;
  /* Let vertical scroll pass through uninterrupted on touch devices */
  -ms-touch-action: pan-y;
      touch-action: pan-y;
  -webkit-animation-fill-mode: both;
          animation-fill-mode: both;
}

/* Paused / playing states for scroll-triggered (once="true") elements */
.sir-paused {
  -webkit-animation-play-state: paused;
          animation-play-state: paused;
}
.sir-playing {
  -webkit-animation-play-state: running;
          animation-play-state: running;
}

/* ── Variant definitions ──────────────────────────────────────────────
   Two animation declarations per variant:
     1. -webkit-animation with static values (Safari < 9, old Android)
     2.         animation with var() tokens   (modern browsers)
   The -webkit- line acts as a fallback for engines that support the prefix
   but not CSS custom properties.
   ──────────────────────────────────────────────────────────────────── */
.sir-default {
  -webkit-animation: sir-default 380ms cubic-bezier(0.22, 1, 0.36, 1) 0ms both;
          animation: sir-default var(--sir-dur, 380ms) var(--sir-ease, cubic-bezier(0.22, 1, 0.36, 1)) var(--sir-delay, 0ms) both;
}
.sir-soft {
  -webkit-animation: sir-soft 380ms cubic-bezier(0.34, 1.02, 0.64, 1) 0ms both;
          animation: sir-soft var(--sir-dur, 380ms) cubic-bezier(0.34, 1.02, 0.64, 1) var(--sir-delay, 0ms) both;
}
.sir-bounce {
  -webkit-animation: sir-bounce 380ms linear 0ms both;
          animation: sir-bounce var(--sir-dur, 380ms) linear var(--sir-delay, 0ms) both;
}
.sir-overshoot {
  -webkit-animation: sir-overshoot 380ms cubic-bezier(0.16, 1, 0.3, 1) 0ms both;
          animation: sir-overshoot var(--sir-dur, 380ms) cubic-bezier(0.16, 1, 0.3, 1) var(--sir-delay, 0ms) both;
}
.sir-fade {
  -webkit-animation: sir-fade 380ms ease-out 0ms both;
          animation: sir-fade var(--sir-dur, 380ms) ease-out var(--sir-delay, 0ms) both;
}
.sir-blur {
  -webkit-animation: sir-blur 380ms cubic-bezier(0.22, 1, 0.36, 1) 0ms both;
          animation: sir-blur var(--sir-dur, 380ms) cubic-bezier(0.22, 1, 0.36, 1) var(--sir-delay, 0ms) both;
}

/* ── Group wrapper — mobile overflow guard ───────────────────────────
   Prevents horizontal scroll flash when off-screen right-side elements
   animate in on narrow viewports. Applied to the group wrapper only,
   not to individual sir elements.
   ──────────────────────────────────────────────────────────────────── */
.sir-group {
  overflow-x: hidden;
}

/* ── Reduced-motion : disable all animations ─────────────────────── */
@media (prefers-reduced-motion: reduce) {
  .sir, .sir-default, .sir-soft, .sir-bounce,
  .sir-overshoot, .sir-fade, .sir-blur {
    -webkit-animation: none !important;
            animation: none !important;
    opacity: 1 !important;
    -webkit-transform: none !important;
        -ms-transform: none !important;
            transform: none !important;
    -webkit-filter: none !important;
            filter: none !important;
  }
}
</style>
<script>
/* ── SlideInRight — cross-browser + mobile-first bootstrap ──────────
   Prefix  : sir
   Guards  : window.__sirReady / window.__sirIO
   ──────────────────────────────────────────────────────────────────── */
(function () {
  'use strict';

  /* ── NodeList.forEach polyfill (IE 11) ──────────────────────────── */
  if (typeof NodeList !== 'undefined' && NodeList.prototype && !NodeList.prototype.forEach) {
    NodeList.prototype.forEach = Array.prototype.forEach;
  }

  /* Guard — bootstrap only once even if tag is emitted multiple times */
  if (window.__sirReady) { return; }
  window.__sirReady = true;

  /* ── CSS custom-property detection ─────────────────────────────────
     IE 11 has no CSS custom property support. When unsupported, the
     --sir-delay / --sir-dur values set via inline style cannot drive
     animation-delay / animation-duration, so we read them back from
     the style attribute and apply them as explicit properties.          */
  var supportsVars = (function () {
    try {
      return typeof window.CSS !== 'undefined' &&
             typeof window.CSS.supports === 'function' &&
             window.CSS.supports('--x', '0');
    } catch (ignored) { return false; }
  })();

  /* Read --sir-delay / --sir-dur from inline style and apply directly.
     Regex uses [ \\t]* and [0-9] instead of \\s / \\d - no Groovy
     illegal-escape-character errors inside triple-quoted string literals. */
  function applyFallbackDelay(el) {
    if (supportsVars) { return; }
    var style = el.getAttribute('style') || '';
    var delayMatch = style.match(/--sir-delay:[ \\t]*([0-9.]+)ms/);
    if (delayMatch) {
      el.style.webkitAnimationDelay = delayMatch[1] + 'ms';
      el.style.animationDelay       = delayMatch[1] + 'ms';
    }
    var durMatch = style.match(/--sir-dur:[ \\t]*([0-9.]+)ms/);
    if (durMatch) {
      el.style.webkitAnimationDuration = durMatch[1] + 'ms';
      el.style.animationDuration       = durMatch[1] + 'ms';
    }
  }

  /* Un-pause a single element safely across all browsers.
     rAF flush prevents Safari first-frame glitch where the element
     briefly appears at its final resting position before animating.    */
  function playEl(el) {
    var raf = window.requestAnimationFrame || function (cb) { setTimeout(cb, 16); };
    raf(function () {
      applyFallbackDelay(el);
      if (el.classList) {
        el.classList.remove('sir-paused');
        el.classList.add('sir-playing');
      }
    });
  }

  function playAll(scope) {
    var nodes = (scope || document).querySelectorAll('.sir-paused');
    Array.prototype.forEach.call(nodes, playEl);
  }

  /* ── Fallback: no IntersectionObserver → play everything immediately */
  if (!('IntersectionObserver' in window)) {
    if (document.readyState === 'loading') {
      document.addEventListener('DOMContentLoaded', function () { playAll(); });
    } else {
      playAll();
    }
    return;
  }

  /* ── IntersectionObserver ────────────────────────────────────────────
     rootMargin "0px 0px -40px 0px":
       Fires 40px BEFORE the element fully enters the viewport bottom.
       On mobile this gives the animation a head-start so it completes
       by the time the user's eye reaches the element.
     threshold 0.04:
       Trigger on the very first pixel (4%) visible — important on mobile
       where elements may be taller than the viewport.
     ──────────────────────────────────────────────────────────────────── */
  var io = new IntersectionObserver(function (entries) {
    Array.prototype.forEach.call(entries, function (e) {
      if (!e.isIntersecting) { return; }
      playEl(e.target);
      io.unobserve(e.target);
    });
  }, { threshold: 0.04, rootMargin: '0px 0px -40px 0px' });

  /* Stored on window so slideInRightAnimGroup inline scripts can reach it */
  window.__sirIO = io;

  function observeAll(root) {
    var nodes = (root || document).querySelectorAll('.sir-paused');
    Array.prototype.forEach.call(nodes, function (el) { io.observe(el); });
  }

  /* readyState guard — handles deferred scripts and SPA navigation */
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', function () { observeAll(); });
  } else {
    observeAll();
  }

  /* ── MutationObserver: pick up nodes injected after DOMContentLoaded
     (infinite-scroll, AJAX inserts, SPA route changes)                  */
  if (typeof MutationObserver !== 'undefined') {
    new MutationObserver(function (muts) {
      Array.prototype.forEach.call(muts, function (m) {
        Array.prototype.forEach.call(m.addedNodes, function (n) {
          if (!n || n.nodeType !== 1) { return; }
          if (n.classList && n.classList.contains('sir-paused')) { io.observe(n); }
          if (n.querySelectorAll) {
            var nested = n.querySelectorAll('.sir-paused');
            Array.prototype.forEach.call(nested, function (el) { io.observe(el); });
          }
        });
      });
    }).observe(document.documentElement, { childList: true, subtree: true });
  }
})();
</script>'''
  }

  // ═══════════════════════════════════════════════════════════════
  // Internal helpers
  //   • static  — satisfies Groovy "method may be static" analysis
  //   • no StringBuilder — plain String concat avoids "access to
  //     append exceeds access rights" static-checker warnings
  //   • no \s / \d in literals — avoids illegal escape char errors
  // ═══════════════════════════════════════════════════════════════

  /** Parse an integer from attrs[key]; return fallback on null / parse error. */
  static int resolveMs(Map attrs, String key, int fallback) {
    def v = attrs[key]
    if (v == null) {
      return fallback
    }
    try {
      return v.toString().toInteger()
    } catch (ignored) {
      return fallback
    }
  }

  /** Build the inline style string that sets CSS custom-property values.
   *  Distance for right-to-left travel is always POSITIVE (element starts
   *  to the right, translateX(+dist) pushes it off-screen right).        */
  static String sirStyle(Map attrs, int delayMs) {
    def duration = resolveMs(attrs, 'duration', 380)
    def distance = resolveMs(attrs, 'distance', 28)
    def easing = (attrs.easing ?: '').toString().trim()
    def extra = (attrs.style ?: '').toString().trim()

    // Note: --sir-dist is POSITIVE — no leading minus
    String css = '--sir-dur:' + duration + 'ms;' +
      '--sir-delay:' + delayMs + 'ms;' +
      '--sir-dist:' + distance + 'px'
    if (easing) {
      css = css + ';--sir-ease:' + easing
    }
    if (extra) {
      css = css + (extra.endsWith(';') ? '' : ';') + extra
    }
    return css
  }

  /** Build the CSS class string: base + variant + optional paused + extras. */
  static String sirClass(Map attrs) {
    def variant = (attrs.variant ?: 'default').toString().trim()
    def once = attrs.once?.toString() == 'true'
    def extraClass = (attrs['class'] ?: '').toString().trim()

    String cls = 'sir sir-' + variant
    if (once) {
      cls = cls + ' sir-paused'
    }
    if (extraClass) {
      cls = cls + ' ' + extraClass
    }
    return cls
  }

  /** Render an HTML opening tag with optional id / class / style attributes. */
  static String openTag(String tag, String id, String cls, String style) {
    String html = '<' + tag
    if (id) {
      html = html + ' id="' + id.encodeAsHTML() + '"'
    }
    html = html + ' class="' + cls.encodeAsHTML() + '"'
    if (style) {
      html = html + ' style="' + style.encodeAsHTML() + '"'
    }
    return html + '>'
  }

  /** Shared rendering core for wrap and item — eliminates duplicated code fragment.
   *  Resolves tag/id from attrs, writes the opening tag to out.               */
  static void renderAnimTag(writer, Map attrs, int delayMs) {
    def tag = (attrs.tag ?: 'div').toString()
    def id = (attrs.id ?: '').toString()
    writer << openTag(tag, id, sirClass(attrs), sirStyle(attrs, delayMs))
  }

  // ═══════════════════════════════════════════════════════════════
  // slideInRightAnimWrap  —  single animated wrapper
  // ═══════════════════════════════════════════════════════════════
  def slideInRightAnimWrap = { attrs, body ->
    def tag = (attrs.tag ?: 'div').toString()
    def baseMs = resolveMs(attrs, 'delay', 0)
    renderAnimTag(out, attrs, baseMs)
    out << body()
    out << '</' + tag + '>'
  }

  // ═══════════════════════════════════════════════════════════════
  // slideInRightAnimItem  —  one item in a staggered list
  //   total delay = (index x staggerMs) + delay
  // ═══════════════════════════════════════════════════════════════
  def slideInRightAnimItem = { attrs, body ->
    def tag = (attrs.tag ?: 'div').toString()
    def index = resolveMs(attrs, 'index', 0)
    def staggerMs = resolveMs(attrs, 'staggerMs', 60)
    def baseDelay = resolveMs(attrs, 'delay', 0)
    def delayMs = baseDelay + (index * staggerMs)
    renderAnimTag(out, attrs, delayMs)
    out << body()
    out << '</' + tag + '>'
  }

  // ═══════════════════════════════════════════════════════════════
  // slideInRightAnimGroup  —  wrapper whose direct children are auto-staggered
  //
  //   The group wrapper itself is NOT animated — only its direct children are.
  //   An inline <script> runs immediately after the closing tag so all children
  //   are already in the DOM when the script executes.
  //   Plain string concatenation throughout - no GStrings - avoids Groovy
  //   ':' expected / GString-end parser errors.
  // ═══════════════════════════════════════════════════════════════
  def slideInRightAnimGroup = { attrs, body ->
    def tag = (attrs.tag ?: 'div').toString()
    def id = (attrs.id ?: 'sir-g-' + UUID.randomUUID().toString().take(8)).toString()
    def staggerMs = resolveMs(attrs, 'staggerMs', 60)
    def baseDelay = resolveMs(attrs, 'delay', 0)
    def duration = resolveMs(attrs, 'duration', 380)
    def distance = resolveMs(attrs, 'distance', 28)
    def variant = (attrs.variant ?: 'default').toString().trim()
    def easing = (attrs.easing ?: '').toString().trim()
    def once = attrs.once?.toString() == 'true'
    def extraCls = (attrs['class'] ?: '').toString().trim()
    def extraSty = (attrs.style ?: '').toString().trim()

    // Group wrapper: sir-group provides overflow-x:hidden for mobile
    def wrapCls = extraCls ? 'sir-group ' + extraCls : 'sir-group'
    String openHtml = '<' + tag +
      ' id="' + id.encodeAsHTML() + '"' +
      ' class="' + wrapCls.encodeAsHTML() + '"' +
      (extraSty ? ' style="' + extraSty.encodeAsHTML() + '"' : '') +
      '>'

    out << openHtml
    out << body()
    out << '</' + tag + '>'

    // Inline script — stagger children and optionally register with __sirIO
    out << '<script>'
    out << '(function(){'
    out << 'var g=document.getElementById(' + "'" + id + "'" + ');'
    out << 'if(!g){return;}'
    out << 'Array.from(g.children).forEach(function(c,i){'
    out << 'var d=' + baseDelay + '+(i*' + staggerMs + ');'
    out << 'c.classList.add(' + "'sir'" + ',' + "'sir-" + variant + "'" + ');'
    if (once) {
      out << 'c.classList.add(' + "'sir-paused'" + ');'
    }
    // Distance for right: POSITIVE value (no leading minus)
    out << 'c.style.setProperty(' + "'--sir-dur'" + ',' + "'" + duration + "ms'" + ');'
    out << 'c.style.setProperty(' + "'--sir-delay'" + ',d+' + "'ms'" + ');'
    out << 'c.style.setProperty(' + "'--sir-dist'" + ',' + "'" + distance + "px'" + ');'
    if (easing) {
      out << 'c.style.setProperty(' + "'--sir-ease'" + ',' + "'" + easing + "'" + ');'
    }
    if (once) {
      out << 'if(window.__sirIO){window.__sirIO.observe(c);}'
    }
    out << '});'
    out << '})();'
    out << '</script>'
  }
}
