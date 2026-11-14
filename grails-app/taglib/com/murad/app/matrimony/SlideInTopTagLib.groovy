package com.murad.app.matrimony


/**
 * SlideInTop  —  Slide-in-from-top CSS animation system for Grails 6 GSP
 * Namespace : glass
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * QUICK START
 * ═══════════════════════════════════════════════════════════════════════════
 *
 *  1. Emit styles once in your layout <head>:
 *       <glass:slideInTopAnim/>
 *
 *  2. Wrap any element:
 *       <glass:slideInTopAnimWrap>
 *         <div class="card">…</div>
 *       </glass:slideInTopAnimWrap>
 *
 *  3. Stagger a list with <g:each>:
 *       <g:each in="${items}" var="item" status="i">
 *         <glass:slideInTopAnimItem index="${i}">
 *           <div>${item.name}</div>
 *         </glass:slideInTopAnimItem>
 *       </g:each>
 *
 *  4. Auto-stagger a group of children:
 *       <glass:slideInTopAnimGroup tag="ul" variant="soft" staggerMs="70">
 *         <li>Alpha</li>
 *         <li>Beta</li>
 *         <li>Gamma</li>
 *       </glass:slideInTopAnimGroup>
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * TAG REFERENCE
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * <glass:slideInTopAnim/>
 *   Writes the global CSS keyframes and the IntersectionObserver script.
 *   Call ONCE per page, inside <head>.
 *
 * ───────────────────────────────────────────────────────────────────────────
 * <glass:slideInTopAnimWrap
 *     [variant   = "default"]   Animation variant (see list below)
 *     [delay     = "0"]         Start delay in ms
 *     [duration  = "420"]       Animation duration in ms
 *     [distance  = "28"]        Travel distance in px (element starts this far above)
 *     [easing    = ""]          Override CSS easing / cubic-bezier string
 *     [once      = "false"]     "true" → animate only when element enters viewport
 *     [tag       = "div"]       HTML wrapper element tag name
 *     [id        = ""]          id attribute on the wrapper
 *     [class     = ""]          Extra CSS classes on the wrapper
 *     [style     = ""]          Extra inline CSS on the wrapper
 * >body</glass:slideInTopAnimWrap>
 *
 *   Wraps body content in a single animated element.
 *
 * ───────────────────────────────────────────────────────────────────────────
 * <glass:slideInTopAnimItem
 *     [index     = "0"]         Stagger position (0-based). delay = index × staggerMs
 *     [staggerMs = "65"]        Milliseconds between each stagger step
 *     [delay     = "0"]         Additional base delay added on top of stagger
 *     [variant   = "default"]
 *     [duration  = "420"]
 *     [distance  = "28"]
 *     [easing    = ""]
 *     [once      = "false"]
 *     [tag       = "div"]
 *     [id        = ""] [class  = ""] [style = ""]
 * >body</glass:slideInTopAnimItem>
 *
 *   Use inside <g:each status="i"> for staggered lists.
 *   delay applied = (index × staggerMs) + delay
 *
 * ───────────────────────────────────────────────────────────────────────────
 * <glass:slideInTopAnimGroup
 *     [staggerMs = "65"]        Delay step between each direct child
 *     [delay     = "0"]         Base delay before first child
 *     [variant   = "default"]
 *     [duration  = "420"]
 *     [distance  = "28"]
 *     [easing    = ""]
 *     [once      = "false"]
 *     [tag       = "div"]
 *     [id        = ""] [class  = ""] [style = ""]
 * >children</glass:slideInTopAnimGroup>
 *
 *   The wrapper itself does NOT animate. Each direct child gets sit classes
 *   and staggered delays applied automatically via an inline <script>.
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * VARIANTS
 * ═══════════════════════════════════════════════════════════════════════════
 *
 *   default    —  clean translateY + fade, cubic ease-out
 *   soft       —  translateY + fade + subtle scale-up (0.97 → 1)
 *   bounce     —  slides in then gently overshoots and settles
 *   overshoot  —  fast dramatic entry with elastic scaleY recoil
 *   fade       —  opacity only, no movement
 *   blur       —  translateY + fade + blur(8px → 0)
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * SCROLL-TRIGGERED (once="true")
 * ═══════════════════════════════════════════════════════════════════════════
 *
 *   When once="true" the animation is paused on render. The IntersectionObserver
 *   (bootstrapped by <glass:slideInTopAnim/>) unpauses it the first time the element
 *   scrolls into view. Elements already in the viewport on load animate immediately.
 *   Falls back to immediate play on browsers without IntersectionObserver support.
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * REDUCED MOTION
 * ═══════════════════════════════════════════════════════════════════════════
 *
 *   All animations are disabled automatically when the user has
 *   prefers-reduced-motion: reduce set in their OS / browser.
 */

import grails.gsp.TagLib

@TagLib
@SuppressWarnings(['unused'])
class SlideInTopTagLib {

  static namespace = 'glass'

  // ═══════════════════════════════════════════════════════════════
  // slideInTopAnim  —  emit once in <head>
  // ═══════════════════════════════════════════════════════════════
  def slideInTopAnim = { attrs ->
    out << '''<style>
/* ── SlideInTop — cross-browser keyframes (prefix: sit) ─────────── */

/* Webkit-prefixed keyframes for Safari < 9 / older Android WebKit */
@-webkit-keyframes sit-default {
  from { opacity: 0; -webkit-transform: translateY(-28px); transform: translateY(-28px); }
  to   { opacity: 1; -webkit-transform: translateY(0);     transform: translateY(0); }
}
@keyframes sit-default {
  from { opacity: 0; -webkit-transform: translateY(var(--sit-dist, -28px)); transform: translateY(var(--sit-dist, -28px)); }
  to   { opacity: 1; -webkit-transform: translateY(0);                      transform: translateY(0); }
}

@-webkit-keyframes sit-soft {
  from { opacity: 0; -webkit-transform: translateY(-28px) scale(0.97); transform: translateY(-28px) scale(0.97); }
  to   { opacity: 1; -webkit-transform: translateY(0) scale(1);        transform: translateY(0) scale(1); }
}
@keyframes sit-soft {
  from { opacity: 0; -webkit-transform: translateY(var(--sit-dist, -28px)) scale(0.97); transform: translateY(var(--sit-dist, -28px)) scale(0.97); }
  to   { opacity: 1; -webkit-transform: translateY(0) scale(1);                         transform: translateY(0) scale(1); }
}

@-webkit-keyframes sit-bounce {
  0%   { opacity: 0; -webkit-transform: translateY(-28px); transform: translateY(-28px); }
  55%  { opacity: 1; -webkit-transform: translateY(5px);   transform: translateY(5px); }
  72%  {             -webkit-transform: translateY(-3px);  transform: translateY(-3px); }
  88%  {             -webkit-transform: translateY(2px);   transform: translateY(2px); }
  100% {             -webkit-transform: translateY(0);     transform: translateY(0); }
}
@keyframes sit-bounce {
  0%   { opacity: 0; -webkit-transform: translateY(var(--sit-dist, -28px)); transform: translateY(var(--sit-dist, -28px)); }
  55%  { opacity: 1; -webkit-transform: translateY(5px);                    transform: translateY(5px); }
  72%  {             -webkit-transform: translateY(-3px);                   transform: translateY(-3px); }
  88%  {             -webkit-transform: translateY(2px);                    transform: translateY(2px); }
  100% {             -webkit-transform: translateY(0);                      transform: translateY(0); }
}

@-webkit-keyframes sit-overshoot {
  0%   { opacity: 0; -webkit-transform: translateY(-28px) scaleY(0.93);    transform: translateY(-28px) scaleY(0.93); }
  48%  { opacity: 1; -webkit-transform: translateY(7px) scaleY(1.03);      transform: translateY(7px) scaleY(1.03); }
  68%  {             -webkit-transform: translateY(-4px) scaleY(0.99);      transform: translateY(-4px) scaleY(0.99); }
  84%  {             -webkit-transform: translateY(2px) scaleY(1.005);      transform: translateY(2px) scaleY(1.005); }
  100% {             -webkit-transform: translateY(0) scaleY(1);            transform: translateY(0) scaleY(1); }
}
@keyframes sit-overshoot {
  0%   { opacity: 0; -webkit-transform: translateY(var(--sit-dist, -28px)) scaleY(0.93); transform: translateY(var(--sit-dist, -28px)) scaleY(0.93); }
  48%  { opacity: 1; -webkit-transform: translateY(7px) scaleY(1.03);                    transform: translateY(7px) scaleY(1.03); }
  68%  {             -webkit-transform: translateY(-4px) scaleY(0.99);                   transform: translateY(-4px) scaleY(0.99); }
  84%  {             -webkit-transform: translateY(2px) scaleY(1.005);                   transform: translateY(2px) scaleY(1.005); }
  100% {             -webkit-transform: translateY(0) scaleY(1);                         transform: translateY(0) scaleY(1); }
}

@-webkit-keyframes sit-fade {
  from { opacity: 0; }
  to   { opacity: 1; }
}
@keyframes sit-fade {
  from { opacity: 0; }
  to   { opacity: 1; }
}

@-webkit-keyframes sit-blur {
  from { opacity: 0; -webkit-transform: translateY(-28px); transform: translateY(-28px); -webkit-filter: blur(8px); filter: blur(8px); }
  to   { opacity: 1; -webkit-transform: translateY(0);     transform: translateY(0);     -webkit-filter: blur(0);   filter: blur(0); }
}
@keyframes sit-blur {
  from { opacity: 0; -webkit-transform: translateY(var(--sit-dist, -28px)); transform: translateY(var(--sit-dist, -28px)); -webkit-filter: blur(8px); filter: blur(8px); }
  to   { opacity: 1; -webkit-transform: translateY(0);                      transform: translateY(0);                      -webkit-filter: blur(0);   filter: blur(0); }
}

/* ── Base ─────────────────────────────────────────────────────────────
   CSS custom properties with static fallback values inside var() for
   browsers that support custom properties but not a specific shorthand.
   IE 11 does not support custom properties at all; the -webkit-animation
   fallback on each variant class below handles graceful degradation.
   ──────────────────────────────────────────────────────────────────── */
.sit {
  --sit-dur:   420ms;
  --sit-delay: 0ms;
  --sit-dist:  -28px;
  --sit-ease:  cubic-bezier(0.22, 1, 0.36, 1);
  /* IE 11 / very old Edge — will-change not supported, safe to omit */
  will-change: transform, opacity;
  -webkit-animation-fill-mode: both;
          animation-fill-mode: both;
}

/* Paused / playing states for scroll-triggered (once="true") elements */
.sit-paused  {
  -webkit-animation-play-state: paused;
          animation-play-state: paused;
}
.sit-playing {
  -webkit-animation-play-state: running;
          animation-play-state: running;
}

/* ── Variant definitions ──────────────────────────────────────────────
   Each rule uses:
     1. -webkit-animation  (Safari < 9, older Android)
     2.         animation  with var() tokens (modern browsers)
   The static duration / easing values in the -webkit- line act as
   fallback for any engine that supports -webkit- but not var().
   ──────────────────────────────────────────────────────────────────── */
.sit-default {
  -webkit-animation: sit-default 420ms cubic-bezier(0.22, 1, 0.36, 1) 0ms both;
          animation: sit-default var(--sit-dur, 420ms) var(--sit-ease, cubic-bezier(0.22, 1, 0.36, 1)) var(--sit-delay, 0ms) both;
}
.sit-soft {
  -webkit-animation: sit-soft 420ms cubic-bezier(0.34, 1.02, 0.64, 1) 0ms both;
          animation: sit-soft var(--sit-dur, 420ms) cubic-bezier(0.34, 1.02, 0.64, 1) var(--sit-delay, 0ms) both;
}
.sit-bounce {
  -webkit-animation: sit-bounce 420ms linear 0ms both;
          animation: sit-bounce var(--sit-dur, 420ms) linear var(--sit-delay, 0ms) both;
}
.sit-overshoot {
  -webkit-animation: sit-overshoot 420ms cubic-bezier(0.16, 1, 0.3, 1) 0ms both;
          animation: sit-overshoot var(--sit-dur, 420ms) cubic-bezier(0.16, 1, 0.3, 1) var(--sit-delay, 0ms) both;
}
.sit-fade {
  -webkit-animation: sit-fade 420ms ease-out 0ms both;
          animation: sit-fade var(--sit-dur, 420ms) ease-out var(--sit-delay, 0ms) both;
}
.sit-blur {
  -webkit-animation: sit-blur 420ms cubic-bezier(0.22, 1, 0.36, 1) 0ms both;
          animation: sit-blur var(--sit-dur, 420ms) cubic-bezier(0.22, 1, 0.36, 1) var(--sit-delay, 0ms) both;
}

/* ── Reduced-motion : disable all animations ─────────────────────── */
@media (prefers-reduced-motion: reduce) {
  .sit, .sit-default, .sit-soft, .sit-bounce,
  .sit-overshoot, .sit-fade, .sit-blur {
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
/* ── SlideInTop — cross-browser bootstrap ────────────────────────── */
(function () {
  'use strict';

  /* ── NodeList.forEach polyfill (IE 11) ───── */
  if (typeof NodeList !== 'undefined' && NodeList.prototype && !NodeList.prototype.forEach) {
    NodeList.prototype.forEach = Array.prototype.forEach;
  }

  /* Guard: bootstrap only once even if tag is emitted multiple times */
  if (window.__sitReady) { return; }
  window.__sitReady = true;

  /* ── CSS custom-property support detection ──
     IE 11 does not support CSS custom properties.
     When unsupported, animation-delay cannot be set via --sit-delay, so
     we apply it as an explicit animation-delay inline style instead.      */
  var supportsVars = (function () {
    try {
      return typeof window.CSS !== 'undefined' &&
             typeof window.CSS.supports === 'function' &&
             window.CSS.supports('--x', '0');
    } catch (ignored) { return false; }
  })();

  /* Apply computed animation-delay as an explicit property when vars
     are not supported. Reads the value already written as a CSS var
     inline style and converts it to animation-delay directly.           */
  function applyFallbackDelay(el) {
    if (supportsVars) { return; }
    var style = el.getAttribute('style') || '';
    var match = style.match(/--sit-delay:[ \t]*([0-9.]+)ms/);
    if (match) {
      el.style.webkitAnimationDelay = match[1] + 'ms';
      el.style.animationDelay       = match[1] + 'ms';
    }
    var durMatch = style.match(/--sit-dur:[ \t]*([0-9.]+)ms/);
    if (durMatch) {
      el.style.webkitAnimationDuration = durMatch[1] + 'ms';
      el.style.animationDuration       = durMatch[1] + 'ms';
    }
  }

  /* Un-pause a single element safely across all browsers */
  function playEl(el) {
    /* rAF flush prevents Safari first-frame glitch where the element
       briefly appears at its final position before animating          */
    var raf = window.requestAnimationFrame || function (cb) { setTimeout(cb, 16); };
    raf(function () {
      applyFallbackDelay(el);
      if (el.classList) {
        el.classList.remove('sit-paused');
        el.classList.add('sit-playing');
      }
    });
  }

  function playAll(scope) {
    var nodes = (scope || document).querySelectorAll('.sit-paused');
    /* Use Array.prototype.forEach for safety (NodeList polyfill above covers IE 11) */
    Array.prototype.forEach.call(nodes, playEl);
  }

  /* ── Fallback: no IntersectionObserver — play everything immediately */
  if (!('IntersectionObserver' in window)) {
    if (document.readyState === 'loading') {
      document.addEventListener('DOMContentLoaded', function () { playAll(); });
    } else {
      playAll();
    }
    return;
  }

  /* ── IntersectionObserver (all modern browsers + Edge 16+) ───────── */
  var io = new IntersectionObserver(function (entries) {
    Array.prototype.forEach.call(entries, function (e) {
      if (!e.isIntersecting) { return; }
      playEl(e.target);
      io.unobserve(e.target);
    });
  }, { threshold: 0.06 });

  /* Stored on window so slideInTopAnimGroup inline scripts can reach it */
  window.__sitIO = io;

  function observeAll(root) {
    var nodes = (root || document).querySelectorAll('.sit-paused');
    Array.prototype.forEach.call(nodes, function (el) { io.observe(el); });
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', function () { observeAll(); });
  } else {
    observeAll();
  }

  /* ── MutationObserver: watch for dynamically injected nodes ─────── */
  if (typeof MutationObserver !== 'undefined') {
    new MutationObserver(function (muts) {
      Array.prototype.forEach.call(muts, function (m) {
        Array.prototype.forEach.call(m.addedNodes, function (n) {
          if (!n || n.nodeType !== 1) { return; }
          if (n.classList && n.classList.contains('sit-paused')) { io.observe(n); }
          if (n.querySelectorAll) {
            var nested = n.querySelectorAll('.sit-paused');
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
  // Internal helpers — static so Groovy static analysis is satisfied
  // ═══════════════════════════════════════════════════════════════

  /** Resolve a numeric ms value from an attrs key, with a fallback default. */
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

  /** Build the inline style string that carries CSS custom-property values.
   *  Uses plain string concatenation — avoids StringBuilder.append()
   *  access-rights warnings from Groovy's static checker.              */
  static String sitStyle(Map attrs, int delayMs) {
    def duration = resolveMs(attrs, 'duration', 420)
    def distance = resolveMs(attrs, 'distance', 28)
    def easing = (attrs.easing ?: '').toString().trim()
    def extra = (attrs.style ?: '').toString().trim()

    String css = '--sit-dur:' + duration + 'ms;' +
      '--sit-delay:' + delayMs + 'ms;' +
      '--sit-dist:-' + distance + 'px'
    if (easing) {
      css = css + ';--sit-ease:' + easing
    }
    if (extra) {
      css = css + (extra.endsWith(';') ? '' : ';') + extra
    }
    return css
  }

  /** Build the CSS class string for a sit element. */
  static String sitClass(Map attrs) {
    def variant = (attrs.variant ?: 'default').toString().trim()
    def once = attrs.once?.toString() == 'true'
    def extraClass = (attrs['class'] ?: '').toString().trim()

    String cls = 'sit sit-' + variant
    if (once) {
      cls = cls + ' sit-paused'
    }
    if (extraClass) {
      cls = cls + ' ' + extraClass
    }
    return cls
  }

  /** Render the opening HTML tag with id / class / style attributes. */
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

  // ═══════════════════════════════════════════════════════════════
  // slideInTopAnimWrap  —  single animated wrapper
  // ═══════════════════════════════════════════════════════════════
  def slideInTopAnimWrap = { attrs, body ->
    def tag = (attrs.tag ?: 'div').toString()
    def id = (attrs.id ?: '').toString()
    def baseMs = resolveMs(attrs, 'delay', 0)

    out << openTag(tag, id, sitClass(attrs), sitStyle(attrs, baseMs))
    out << body()
    out << '</' + tag + '>'
  }

  // ═══════════════════════════════════════════════════════════════
  // slideInTopAnimItem  —  single element in a staggered list
  //   total delay = (index × staggerMs) + delay
  // ═══════════════════════════════════════════════════════════════
  def slideInTopAnimItem = { attrs, body ->
    def tag = (attrs.tag ?: 'div').toString()
    def id = (attrs.id ?: '').toString()
    def index = resolveMs(attrs, 'index', 0)
    def staggerMs = resolveMs(attrs, 'staggerMs', 65)
    def baseDelay = resolveMs(attrs, 'delay', 0)
    def delayMs = baseDelay + (index * staggerMs)

    out << openTag(tag, id, sitClass(attrs), sitStyle(attrs, delayMs))
    out << body()
    out << '</' + tag + '>'
  }

  // ═══════════════════════════════════════════════════════════════
  // slideInTopAnimGroup  —  wrapper whose direct children are auto-staggered
  // ═══════════════════════════════════════════════════════════════
  def slideInTopAnimGroup = { attrs, body ->
    def tag = (attrs.tag ?: 'div').toString()
    // Auto-generate a stable id if none supplied so the inline script can find it
    def id = (attrs.id ?: 'sit-g-' + UUID.randomUUID().toString().take(8)).toString()
    def staggerMs = resolveMs(attrs, 'staggerMs', 65)
    def baseDelay = resolveMs(attrs, 'delay', 0)
    def duration = resolveMs(attrs, 'duration', 420)
    def distance = resolveMs(attrs, 'distance', 28)
    def variant = (attrs.variant ?: 'default').toString().trim()
    def easing = (attrs.easing ?: '').toString().trim()
    def once = attrs.once?.toString() == 'true'
    def extraCls = (attrs['class'] ?: '').toString().trim()
    def extraSty = (attrs.style ?: '').toString().trim()

    // The group wrapper itself does NOT animate — only its children do
    def wrapCls = extraCls ? 'sit-group ' + extraCls : 'sit-group'
    String openHtml = '<' + tag +
      ' id="' + id.encodeAsHTML() + '"' +
      ' class="' + wrapCls.encodeAsHTML() + '"' +
      (extraSty ? ' style="' + extraSty.encodeAsHTML() + '"' : '') +
      '>'

    out << openHtml
    out << body()
    out << '</' + tag + '>'

    // Inline script: applied immediately after the group so children exist in DOM.
    // All values are plain Groovy — no GStrings, no quote-escaping issues.
    out << '<script>'
    out << '(function(){'
    out << 'var g=document.getElementById(' + "'" + id + "'" + ');'
    out << 'if(!g){return;}'
    out << 'Array.from(g.children).forEach(function(c,i){'
    out << 'var d=' + baseDelay + '+(i*' + staggerMs + ');'
    out << 'c.classList.add(' + "'sit'" + ',' + "'sit-" + variant + "'" + ');'
    if (once) {
      out << 'c.classList.add(' + "'sit-paused'" + ');'
    }
    out << 'c.style.setProperty(' + "'--sit-dur'" + ',' + "'" + duration + "ms'" + ');'
    out << 'c.style.setProperty(' + "'--sit-delay'" + ',d+' + "'ms'" + ');'
    out << 'c.style.setProperty(' + "'--sit-dist'" + "," + "'-" + distance + "px'" + ');'
    if (easing) {
      out << 'c.style.setProperty(' + "'--sit-ease'" + ',' + "'" + easing + "'" + ');'
    }
    if (once) {
      out << 'if(window.__sitIO){window.__sitIO.observe(c);}'
    }
    out << '});'
    out << '})();'
    out << '</script>'
  }
}
