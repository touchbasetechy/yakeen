package com.murad.app.matrimony

import grails.gsp.TagLib

/**
 * SlideInLeft  —  Slide-in-from-left CSS animation system for Grails 6 GSP
 * Namespace : glass
 *
 * CSS token prefix : sil  (avoids collision with SlideInTop's "sit" prefix)
 * Window guards    : window.__silReady / window.__silIO
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * QUICK START
 * ═══════════════════════════════════════════════════════════════════════════
 *
 *  1. Emit styles once in your layout <head>:
 *       <glass:slideInLeftAnim/>
 *
 *  2. Wrap any element:
 *       <glass:slideInLeftAnimWrap>
 *         <div class="card">…</div>
 *       </glass:slideInLeftAnimWrap>
 *
 *  3. Stagger a list with <g:each>:
 *       <g:each in="${items}" var="item" status="i">
 *         <glass:slideInLeftAnimItem index="${i}">
 *           <div>${item.name}</div>
 *         </glass:slideInLeftAnimItem>
 *       </g:each>
 *
 *  4. Auto-stagger a group of children:
 *       <glass:slideInLeftAnimGroup tag="ul" variant="soft" staggerMs="70">
 *         <li>Alpha</li>
 *         <li>Beta</li>
 *         <li>Gamma</li>
 *       </glass:slideInLeftAnimGroup>
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * TAG REFERENCE
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * <glass:slideInLeftAnim/>
 *   Writes global CSS keyframes and the IntersectionObserver script.
 *   Call ONCE per page, inside <head>.
 *   Safe to include alongside <glass:slideInTopAnim/> — uses isolated
 *   CSS class names (sil-*) and a separate window guard (__silReady).
 *
 * ───────────────────────────────────────────────────────────────────────────
 * <glass:slideInLeftAnimWrap
 *     [variant   = "default"]   Animation variant (see list below)
 *     [delay     = "0"]         Start delay in ms
 *     [duration  = "420"]       Animation duration in ms
 *     [distance  = "36"]        Travel distance in px (element starts this far to the left)
 *     [easing    = ""]          Override CSS easing / cubic-bezier string
 *     [once      = "false"]     "true" → animate only when element enters viewport
 *     [tag       = "div"]       HTML wrapper element tag name
 *     [id        = ""]          id attribute on the wrapper
 *     [class     = ""]          Extra CSS classes on the wrapper
 *     [style     = ""]          Extra inline CSS on the wrapper
 * >body</glass:slideInLeftAnimWrap>
 *
 *   Wraps body content in a single animated element.
 *
 * ───────────────────────────────────────────────────────────────────────────
 * <glass:slideInLeftAnimItem
 *     [index     = "0"]         Stagger position (0-based). delay = index × staggerMs
 *     [staggerMs = "65"]        Milliseconds between each stagger step
 *     [delay     = "0"]         Additional base delay added on top of stagger
 *     [variant   = "default"]
 *     [duration  = "420"]
 *     [distance  = "36"]
 *     [easing    = ""]
 *     [once      = "false"]
 *     [tag       = "div"]
 *     [id        = ""] [class  = ""] [style = ""]
 * >body</glass:slideInLeftAnimItem>
 *
 *   Use inside <g:each status="i"> for staggered lists.
 *   Total delay applied = (index × staggerMs) + delay
 *
 * ───────────────────────────────────────────────────────────────────────────
 * <glass:slideInLeftAnimGroup
 *     [staggerMs = "65"]        Delay step between each direct child
 *     [delay     = "0"]         Base delay before first child
 *     [variant   = "default"]
 *     [duration  = "420"]
 *     [distance  = "36"]
 *     [easing    = ""]
 *     [once      = "false"]
 *     [tag       = "div"]
 *     [id        = ""] [class  = ""] [style = ""]
 * >children</glass:slideInLeftAnimGroup>
 *
 *   The wrapper itself does NOT animate. Each direct child receives sil
 *   classes and staggered delays applied automatically via an inline script.
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * VARIANTS
 * ═══════════════════════════════════════════════════════════════════════════
 *
 *   default    —  clean translateX + fade, cubic ease-out
 *   soft       —  translateX + fade + subtle scale-up (0.97 → 1)
 *   bounce     —  slides in then gently overshoots and settles horizontally
 *   overshoot  —  fast dramatic entry with elastic scaleX recoil
 *   fade       —  opacity only, no movement
 *   blur       —  translateX + fade + blur(8px → 0)
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * SCROLL-TRIGGERED (once="true")
 * ═══════════════════════════════════════════════════════════════════════════
 *
 *   When once="true" the animation is paused on render. The IntersectionObserver
 *   (bootstrapped by <glass:slideInLeftAnim/>) unpauses it the first time the
 *   element scrolls into view. Elements already visible on load animate immediately.
 *   Falls back to immediate play on browsers without IntersectionObserver support.
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * REDUCED MOTION
 * ═══════════════════════════════════════════════════════════════════════════
 *
 *   All animations are disabled automatically when the user has
 *   prefers-reduced-motion: reduce set in their OS / browser.
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * COEXISTENCE WITH SlideInTop
 * ═══════════════════════════════════════════════════════════════════════════
 *
 *   Both taglibss share namespace = 'glass' but use distinct prefixes:
 *     SlideInTop  → CSS classes: sit-*   window globals: __sitReady, __sitIO
 *     SlideInLeft → CSS classes: sil-*   window globals: __silReady, __silIO
 *   They can both be used on the same page without any conflict.
 */
@TagLib
@SuppressWarnings(['unused'])
class SlideInLeftTagLib {

  static namespace = 'glass'

  // ═══════════════════════════════════════════════════════════════
  // slideInLeftAnim  —  emit once in <head>
  // ═══════════════════════════════════════════════════════════════
  def slideInLeftAnim = { attrs ->
    out << '''<style>
/* ── SlideInLeft — cross-browser keyframes (prefix: sil) ────────── */

/* Webkit-prefixed keyframes for Safari < 9 / older Android WebKit */
@-webkit-keyframes sil-default {
  from { opacity: 0; -webkit-transform: translateX(-36px); transform: translateX(-36px); }
  to   { opacity: 1; -webkit-transform: translateX(0);     transform: translateX(0); }
}
@keyframes sil-default {
  from { opacity: 0; -webkit-transform: translateX(var(--sil-dist, -36px)); transform: translateX(var(--sil-dist, -36px)); }
  to   { opacity: 1; -webkit-transform: translateX(0);                      transform: translateX(0); }
}

@-webkit-keyframes sil-soft {
  from { opacity: 0; -webkit-transform: translateX(-36px) scale(0.97); transform: translateX(-36px) scale(0.97); }
  to   { opacity: 1; -webkit-transform: translateX(0) scale(1);        transform: translateX(0) scale(1); }
}
@keyframes sil-soft {
  from { opacity: 0; -webkit-transform: translateX(var(--sil-dist, -36px)) scale(0.97); transform: translateX(var(--sil-dist, -36px)) scale(0.97); }
  to   { opacity: 1; -webkit-transform: translateX(0) scale(1);                         transform: translateX(0) scale(1); }
}

@-webkit-keyframes sil-bounce {
  0%   { opacity: 0; -webkit-transform: translateX(-36px); transform: translateX(-36px); }
  55%  { opacity: 1; -webkit-transform: translateX(5px);   transform: translateX(5px); }
  72%  {             -webkit-transform: translateX(-3px);  transform: translateX(-3px); }
  88%  {             -webkit-transform: translateX(2px);   transform: translateX(2px); }
  100% {             -webkit-transform: translateX(0);     transform: translateX(0); }
}
@keyframes sil-bounce {
  0%   { opacity: 0; -webkit-transform: translateX(var(--sil-dist, -36px)); transform: translateX(var(--sil-dist, -36px)); }
  55%  { opacity: 1; -webkit-transform: translateX(5px);                    transform: translateX(5px); }
  72%  {             -webkit-transform: translateX(-3px);                   transform: translateX(-3px); }
  88%  {             -webkit-transform: translateX(2px);                    transform: translateX(2px); }
  100% {             -webkit-transform: translateX(0);                      transform: translateX(0); }
}

@-webkit-keyframes sil-overshoot {
  0%   { opacity: 0; -webkit-transform: translateX(-36px) scaleX(0.93);  transform: translateX(-36px) scaleX(0.93); }
  48%  { opacity: 1; -webkit-transform: translateX(7px) scaleX(1.04);    transform: translateX(7px) scaleX(1.04); }
  68%  {             -webkit-transform: translateX(-4px) scaleX(0.98);   transform: translateX(-4px) scaleX(0.98); }
  84%  {             -webkit-transform: translateX(2px) scaleX(1.01);    transform: translateX(2px) scaleX(1.01); }
  100% {             -webkit-transform: translateX(0) scaleX(1);         transform: translateX(0) scaleX(1); }
}
@keyframes sil-overshoot {
  0%   { opacity: 0; -webkit-transform: translateX(var(--sil-dist, -36px)) scaleX(0.93); transform: translateX(var(--sil-dist, -36px)) scaleX(0.93); }
  48%  { opacity: 1; -webkit-transform: translateX(7px) scaleX(1.04);                    transform: translateX(7px) scaleX(1.04); }
  68%  {             -webkit-transform: translateX(-4px) scaleX(0.98);                   transform: translateX(-4px) scaleX(0.98); }
  84%  {             -webkit-transform: translateX(2px) scaleX(1.01);                    transform: translateX(2px) scaleX(1.01); }
  100% {             -webkit-transform: translateX(0) scaleX(1);                         transform: translateX(0) scaleX(1); }
}

@-webkit-keyframes sil-fade {
  from { opacity: 0; }
  to   { opacity: 1; }
}
@keyframes sil-fade {
  from { opacity: 0; }
  to   { opacity: 1; }
}

@-webkit-keyframes sil-blur {
  from { opacity: 0; -webkit-transform: translateX(-36px); transform: translateX(-36px); -webkit-filter: blur(8px); filter: blur(8px); }
  to   { opacity: 1; -webkit-transform: translateX(0);     transform: translateX(0);     -webkit-filter: blur(0);   filter: blur(0); }
}
@keyframes sil-blur {
  from { opacity: 0; -webkit-transform: translateX(var(--sil-dist, -36px)); transform: translateX(var(--sil-dist, -36px)); -webkit-filter: blur(8px); filter: blur(8px); }
  to   { opacity: 1; -webkit-transform: translateX(0);                      transform: translateX(0);                      -webkit-filter: blur(0);   filter: blur(0); }
}

/* ── Base ─────────────────────────────────────────────────────────────
   CSS custom properties with static fallback values inside var() for
   browsers that support custom properties but not a specific shorthand.
   IE 11 does not support custom properties at all; the -webkit-animation
   fallback on each variant class below handles graceful degradation.
   ──────────────────────────────────────────────────────────────────── */
.sil {
  --sil-dur:   420ms;
  --sil-delay: 0ms;
  --sil-dist:  -36px;
  --sil-ease:  cubic-bezier(0.22, 1, 0.36, 1);
  will-change: transform, opacity;
  -webkit-animation-fill-mode: both;
          animation-fill-mode: both;
}

/* Paused / playing states for scroll-triggered (once="true") elements */
.sil-paused  {
  -webkit-animation-play-state: paused;
          animation-play-state: paused;
}
.sil-playing {
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
.sil-default {
  -webkit-animation: sil-default 420ms cubic-bezier(0.22, 1, 0.36, 1) 0ms both;
          animation: sil-default var(--sil-dur, 420ms) var(--sil-ease, cubic-bezier(0.22, 1, 0.36, 1)) var(--sil-delay, 0ms) both;
}
.sil-soft {
  -webkit-animation: sil-soft 420ms cubic-bezier(0.34, 1.02, 0.64, 1) 0ms both;
          animation: sil-soft var(--sil-dur, 420ms) cubic-bezier(0.34, 1.02, 0.64, 1) var(--sil-delay, 0ms) both;
}
.sil-bounce {
  -webkit-animation: sil-bounce 420ms linear 0ms both;
          animation: sil-bounce var(--sil-dur, 420ms) linear var(--sil-delay, 0ms) both;
}
.sil-overshoot {
  -webkit-animation: sil-overshoot 420ms cubic-bezier(0.16, 1, 0.3, 1) 0ms both;
          animation: sil-overshoot var(--sil-dur, 420ms) cubic-bezier(0.16, 1, 0.3, 1) var(--sil-delay, 0ms) both;
}
.sil-fade {
  -webkit-animation: sil-fade 420ms ease-out 0ms both;
          animation: sil-fade var(--sil-dur, 420ms) ease-out var(--sil-delay, 0ms) both;
}
.sil-blur {
  -webkit-animation: sil-blur 420ms cubic-bezier(0.22, 1, 0.36, 1) 0ms both;
          animation: sil-blur var(--sil-dur, 420ms) cubic-bezier(0.22, 1, 0.36, 1) var(--sil-delay, 0ms) both;
}

/* ── Reduced-motion : disable all animations ─────────────────────── */
@media (prefers-reduced-motion: reduce) {
  .sil, .sil-default, .sil-soft, .sil-bounce,
  .sil-overshoot, .sil-fade, .sil-blur {
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
/* ── SlideInLeft — cross-browser bootstrap ───────────────────────── */
(function () {
  'use strict';

  /* ── NodeList.forEach polyfill (IE 11) ───── */
  if (typeof NodeList !== 'undefined' && NodeList.prototype && !NodeList.prototype.forEach) {
    NodeList.prototype.forEach = Array.prototype.forEach;
  }

  /* Guard: bootstrap only once even if tag is emitted multiple times */
  if (window.__silReady) { return; }
  window.__silReady = true;

  /* ── CSS custom-property support detection ──
     IE 11 does not support CSS custom properties.
     When unsupported, animation-delay cannot be set via --sil-delay, so
     we apply it as an explicit animation-delay inline style instead.      */
  var supportsVars = (function () {
    try {
      return typeof window.CSS !== 'undefined' &&
             typeof window.CSS.supports === 'function' &&
             window.CSS.supports('--x', '0');
    } catch (ignored) { return false; }
  })();

  /* Apply computed animation-delay/duration as explicit properties when
     CSS custom properties are not supported (IE 11).                      */
  function applyFallbackDelay(el) {
    if (supportsVars) { return; }
    var style = el.getAttribute('style') || '';
    var match = style.match(/--sil-delay:[ \t]*([0-9.]+)ms/);
    if (match) {
      el.style.webkitAnimationDelay = match[1] + 'ms';
      el.style.animationDelay       = match[1] + 'ms';
    }
    var durMatch = style.match(/--sil-dur:[ \t]*([0-9.]+)ms/);
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
        el.classList.remove('sil-paused');
        el.classList.add('sil-playing');
      }
    });
  }

  function playAll(scope) {
    var nodes = (scope || document).querySelectorAll('.sil-paused');
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

  /* Stored on window so slideInLeftAnimGroup inline scripts can reach it */
  window.__silIO = io;

  function observeAll(root) {
    var nodes = (root || document).querySelectorAll('.sil-paused');
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
          if (n.classList && n.classList.contains('sil-paused')) { io.observe(n); }
          if (n.querySelectorAll) {
            var nested = n.querySelectorAll('.sil-paused');
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

  /** Parse an integer from attrs[key]; return fallback on null/error. */
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

  /** Build the inline style string carrying CSS custom-property values.
   *  Uses plain string concatenation — avoids StringBuilder.append()
   *  access-rights warnings from Groovy's static checker.              */
  static String silStyle(Map attrs, int delayMs) {
    def duration = resolveMs(attrs, 'duration', 420)
    def distance = resolveMs(attrs, 'distance', 36)
    def easing = (attrs.easing ?: '').toString().trim()
    def extra = (attrs.style ?: '').toString().trim()

    String css = '--sil-dur:' + duration + 'ms;' +
      '--sil-delay:' + delayMs + 'ms;' +
      '--sil-dist:-' + distance + 'px'
    if (easing) {
      css = css + ';--sil-ease:' + easing
    }
    if (extra) {
      css = css + (extra.endsWith(';') ? '' : ';') + extra
    }
    return css
  }

  /** Build the CSS class string: base + variant + paused flag + extras. */
  static String silClass(Map attrs) {
    def variant = (attrs.variant ?: 'default').toString().trim()
    def once = attrs.once?.toString() == 'true'
    def extraClass = (attrs['class'] ?: '').toString().trim()

    String cls = 'sil sil-' + variant
    if (once) {
      cls = cls + ' sil-paused'
    }
    if (extraClass) {
      cls = cls + ' ' + extraClass
    }
    return cls
  }

  /** Render an HTML opening tag with optional id / class / style. */
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
  // slideInLeftAnimWrap  —  single animated wrapper
  // ═══════════════════════════════════════════════════════════════
  def slideInLeftAnimWrap = { attrs, body ->
    def tag = (attrs.tag ?: 'div').toString()
    def id = (attrs.id ?: '').toString()
    def baseMs = resolveMs(attrs, 'delay', 0)

    out << openTag(tag, id, silClass(attrs), silStyle(attrs, baseMs))
    out << body()
    out << '</' + tag + '>'
  }

  // ═══════════════════════════════════════════════════════════════
  // slideInLeftAnimItem  —  one item in a staggered list
  //   total delay = (index × staggerMs) + delay
  // ═══════════════════════════════════════════════════════════════
  def slideInLeftAnimItem = { attrs, body ->
    def tag = (attrs.tag ?: 'div').toString()
    def id = (attrs.id ?: '').toString()
    def index = resolveMs(attrs, 'index', 0)
    def staggerMs = resolveMs(attrs, 'staggerMs', 65)
    def baseDelay = resolveMs(attrs, 'delay', 0)
    def delayMs = baseDelay + (index * staggerMs)

    out << openTag(tag, id, silClass(attrs), silStyle(attrs, delayMs))
    out << body()
    out << '</' + tag + '>'
  }

  // ═══════════════════════════════════════════════════════════════
  // slideInLeftAnimGroup  —  wrapper whose direct children are auto-staggered
  // ═══════════════════════════════════════════════════════════════
  def slideInLeftAnimGroup = { attrs, body ->
    def tag = (attrs.tag ?: 'div').toString()
    def id = (attrs.id ?: 'sil-g-' + UUID.randomUUID().toString().take(8)).toString()
    def staggerMs = resolveMs(attrs, 'staggerMs', 65)
    def baseDelay = resolveMs(attrs, 'delay', 0)
    def duration = resolveMs(attrs, 'duration', 420)
    def distance = resolveMs(attrs, 'distance', 36)
    def variant = (attrs.variant ?: 'default').toString().trim()
    def easing = (attrs.easing ?: '').toString().trim()
    def once = attrs.once?.toString() == 'true'
    def extraCls = (attrs['class'] ?: '').toString().trim()
    def extraSty = (attrs.style ?: '').toString().trim()

    // The group wrapper itself does NOT animate — only its direct children do
    def wrapCls = extraCls ? 'sil-group ' + extraCls : 'sil-group'
    String openHtml = '<' + tag +
      ' id="' + id.encodeAsHTML() + '"' +
      ' class="' + wrapCls.encodeAsHTML() + '"' +
      (extraSty ? ' style="' + extraSty.encodeAsHTML() + '"' : '') +
      '>'

    out << openHtml
    out << body()
    out << '</' + tag + '>'

    // Inline script runs immediately after the group tag so all children
    // already exist in the DOM. Uses plain string concatenation — no GStrings —
    // to avoid ':' expected / GString-end parser errors.
    out << '<script>'
    out << '(function(){'
    out << 'var g=document.getElementById(' + "'" + id + "'" + ');'
    out << 'if(!g){return;}'
    out << 'Array.from(g.children).forEach(function(c,i){'
    out << 'var d=' + baseDelay + '+(i*' + staggerMs + ');'
    out << 'c.classList.add(' + "'sil'" + ',' + "'sil-" + variant + "'" + ');'
    if (once) {
      out << 'c.classList.add(' + "'sil-paused'" + ');'
    }
    out << 'c.style.setProperty(' + "'--sil-dur'" + ',' + "'" + duration + "ms'" + ');'
    out << 'c.style.setProperty(' + "'--sil-delay'" + ',d+' + "'ms'" + ');'
    out << 'c.style.setProperty(' + "'--sil-dist'" + "," + "'-" + distance + "px'" + ');'
    if (easing) {
      out << 'c.style.setProperty(' + "'--sil-ease'" + ',' + "'" + easing + "'" + ');'
    }
    if (once) {
      out << 'if(window.__silIO){window.__silIO.observe(c);}'
    }
    out << '});'
    out << '})();'
    out << '</script>'
  }
}
