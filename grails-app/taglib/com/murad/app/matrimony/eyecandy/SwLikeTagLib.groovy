package com.murad.app.matrimony.eyecandy

/**
 * ============================================================================
 *  SwLikeTagLib
 *  "Signal Heart" — a glassmorphic like/favorite button with heart-fill and
 *  bounce/scale, in the classic Instagram tap (and double-tap-to-like) style.
 *  Tapping the heart chip toggles fill with a short spring bounce and a
 *  scatter of small particles; double-tapping a photo it's attached to
 *  always likes (never unlikes) with a large center heart pop, matching
 *  familiar photo-app behavior.
 *
 *  Part of the "Signal" component family (see sw:stepTrail, sw:toast,
 *  sw:crossfade). Shares the same glass/token language as the rest.
 *
 *  USAGE (GSP):
 *  ----------------------------------------------------------------------
 *  Basic like button:
 *
 *    <sw:like id="postLike42" liked="${post.likedByMe}" count="${post.likeCount}" />
 *
 *  Attached to a photo so double-tapping the photo also likes it:
 *
 *    <img id="post42Photo" src="${post.imageUrl}" alt="${post.caption}">
 *    <sw:like id="postLike42" attachTo="post42Photo"
 *             liked="${post.likedByMe}" count="${post.likeCount}" />
 *
 *  Persisting the toggle (optimistic UI — revert on failure):
 *
 *    document.getElementById('postLike42').addEventListener('sw-like-change', function(e){
 *      fetch('/posts/42/like', { method: 'POST', body: JSON.stringify({ liked: e.detail.liked }) })
 *        .catch(function(){ SwLike.set('postLike42', !e.detail.liked, e.detail.previousCount); });
 *    });
 *
 *  Attributes:
 *    id        (required) unique DOM id — needed for attachTo, events, and
 *              the SwLike.toggle()/set() API.
 *    liked     boolean initial state. Default false.
 *    count     initial like count. Omit to render an icon-only button with
 *              no visible number.
 *    size      sm | md (default) | lg
 *    label     accessible label for the button. Default "Like".
 *    attachTo  DOM id of an element (e.g. an <img>) that should also
 *              respond to double-click / double-tap by always liking
 *              (never unliking), with a large center heart pop overlay.
 *    burst     boolean, small particle scatter on like. Default true.
 *    assets    (default true) emit shared CSS/JS. Set false on 2nd+ calls
 *              on the same page to skip re-emitting.
 * ============================================================================
 */
class SwLikeTagLib {

  static namespace = 'sw'

  private static final List<String> VALID_SIZES = ['sm', 'md', 'lg']

  def like = { attrs, body ->
    ensureAssets(attrs)

    String id = (attrs.id ?: "sw-like-${(Math.random() * 1000000) as int}") as String
    boolean liked = attrs.containsKey('liked') ? (attrs.liked as boolean) : false
    boolean hasCount = attrs.containsKey('count') && attrs.count != null && attrs.count != ''
    Long count = hasCount ? (attrs.count as Long) : null
    String size = VALID_SIZES.contains(attrs.size) ? attrs.size : 'md'
    String label = (attrs.label ?: 'Like') as String
    String attachTo = attrs.attachTo ? (attrs.attachTo as String) : ''
    boolean burst = attrs.containsKey('burst') ? (attrs.burst as boolean) : true

    StringBuilder sb = new StringBuilder()
    sb << "<button type=\"button\" class=\"sw-like sw-like--${size}${liked ? ' is-liked' : ''}\" " +
      "id=\"${escAttr(id)}\" data-sw-like " +
      "data-attach-to=\"${escAttr(attachTo)}\" data-burst=\"${burst}\" " +
      "aria-pressed=\"${liked}\" aria-label=\"${escAttr(label)}\">\n"
    sb << "  <span class=\"sw-like-glass\" aria-hidden=\"true\"></span>\n"
    sb << "  <span class=\"sw-like-burst\" aria-hidden=\"true\"></span>\n"
    sb << "  <span class=\"sw-like-icon\" aria-hidden=\"true\">\n"
    sb << "    <svg class=\"sw-heart-outline\" viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"1.8\" stroke-linejoin=\"round\">\n"
    sb << "      <path d=\"${HEART_PATH}\"/>\n"
    sb << "    </svg>\n"
    sb << "    <svg class=\"sw-heart-fill\" viewBox=\"0 0 24 24\">\n"
    sb << "      <path d=\"${HEART_PATH}\"/>\n"
    sb << "    </svg>\n"
    sb << "  </span>\n"
    if (hasCount) {
      sb << "  <span class=\"sw-like-count\" data-sw-like-count>${formatCount(count)}</span>\n"
    }
    sb << "</button>\n"

    out << sb.toString()
  }

  private static final String HEART_PATH =
    'M12 20.5s-7.2-4.4-9.8-9C.6 8 1.9 4.3 5.4 3.6c2.2-.5 4.2.6 6.1 2.8 1.9-2.2 3.9-3.3 6.1-2.8 3.5.7 4.8 4.4 3.2 7.9-2.6 4.6-9.8 9-9.8 9z'

  private String formatCount(Long n) {
    if (n == null) return '0'
    if (n < 1000) return n.toString()
    double k = n / 1000.0
    if (n < 1000000) return (k == Math.floor(k) ? "${k as int}" : String.format('%.1f', k)) + 'K'
    double m = n / 1000000.0
    return (m == Math.floor(m) ? "${m as int}" : String.format('%.1f', m)) + 'M'
  }

  private String escAttr(String s) {
    if (s == null) return ''
    s.replace('&', '&amp;').replace('<', '&lt;').replace('>', '&gt;').replace('"', '&quot;')
  }

  private void ensureAssets(attrs) {
    boolean emit = attrs.containsKey('assets') ? (attrs.assets as boolean) : true
    if (emit && !(request.getAttribute('sw-like-assets-written'))) {
      out << likeAssets()
      request.setAttribute('sw-like-assets-written', true)
    }
  }

  /** Shared CSS + the SwLike client runtime, written once per page. */
  private String likeAssets() {
    '''
<style>
/* ==========================================================================
   Signal Heart — glassmorphic like/favorite heart-fill micro-interaction
   Namespaced under .sw-like. Safe to include once per page.
   ========================================================================== */
.sw-like{
  --sw-size: clamp(2.1rem, 1.9rem + 0.6vw, 2.5rem);
  --sw-icon: 52%;
  --sw-glass:rgba(255,255,255,0.07);
  --sw-border:rgba(255,255,255,0.16);
  --sw-heart-1:#FB7185;
  --sw-heart-2:#F43F5E;
  position: relative;
  display: inline-flex;
  align-items: center;
  gap: 0.4rem;
  border: none;
  background: transparent;
  padding: 0;
  cursor: pointer;
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
  color: #ECEEFA;
  -webkit-tap-highlight-color: transparent;
  container-type: inline-size;
  container-name: sw-like;
}
.sw-like *, .sw-like *::before, .sw-like *::after{ box-sizing: border-box; }
.sw-like--sm{ --sw-size: clamp(1.7rem, 1.55rem + 0.5vw, 2rem); }
.sw-like--lg{ --sw-size: clamp(2.5rem, 2.2rem + 0.8vw, 3rem); }

.sw-like-icon{
  position: relative;
  width: var(--sw-size);
  height: var(--sw-size);
  flex: none;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 120ms ease;
}
.sw-like-glass{
  position: absolute;
  inset: 0;
  border-radius: 50%;
  background: var(--sw-glass);
  border: 1px solid var(--sw-border);
  -webkit-backdrop-filter: blur(10px) saturate(140%);
  backdrop-filter: blur(10px) saturate(140%);
  transition: background 200ms ease, border-color 200ms ease, box-shadow 200ms ease;
}
@supports not ((backdrop-filter: blur(1px)) or (-webkit-backdrop-filter: blur(1px))) {
  .sw-like-glass{ background: rgba(30,32,48,0.85); }
}
.sw-like:hover .sw-like-glass{ background: rgba(255,255,255,0.11); }
.sw-like:active .sw-like-icon{ transform: scale(0.88); }
.sw-like:focus-visible .sw-like-glass{ outline: 2px solid var(--sw-heart-1); outline-offset: 2px; }

.sw-like.is-liked .sw-like-glass{
  border-color: rgba(244,63,94,0.4);
  box-shadow: 0 0 0 4px rgba(244,63,94,0.10), 0 0 16px rgba(244,63,94,0.25);
}

.sw-heart-outline, .sw-heart-fill{
  position: absolute;
  width: var(--sw-icon);
  height: var(--sw-icon);
}
.sw-heart-outline{
  color: rgba(236,238,250,0.75);
  opacity: 1;
  transform: scale(1);
  transition: opacity 180ms ease, transform 180ms ease;
}
.sw-heart-fill{
  opacity: 0;
  transform: scale(0.3);
  transform-origin: center;
}
.sw-heart-fill path{
  fill: url(#sw-heart-grad);
}

.sw-like.is-liked .sw-heart-outline{ opacity: 0; transform: scale(0.7); }
.sw-like.is-liked .sw-heart-fill{
  opacity: 1;
  animation: sw-heart-bounce 480ms cubic-bezier(.34,1.56,.64,1) forwards;
}
.sw-like:not(.is-liked) .sw-heart-fill{
  animation: sw-heart-shrink 180ms ease forwards;
}
@keyframes sw-heart-bounce{
  0%{ opacity:0; transform: scale(0.3); }
  45%{ opacity:1; transform: scale(1.25); }
  70%{ transform: scale(0.9); }
  100%{ opacity:1; transform: scale(1); }
}
@keyframes sw-heart-shrink{ from{ opacity:1; transform: scale(1); } to{ opacity:0; transform: scale(0.3); } }

/* ---- particle burst ---- */
.sw-like-burst{
  position: absolute;
  inset: 0;
  pointer-events: none;
  overflow: visible;
}
.sw-burst-particle{
  position: absolute;
  top: 50%; left: 50%;
  width: 6px; height: 6px;
  border-radius: 50%;
  background: var(--sw-heart-1);
  opacity: 0;
  animation: sw-burst-fly 620ms ease-out forwards;
}
@keyframes sw-burst-fly{
  0%{ opacity: 1; transform: translate(-50%,-50%) translate(0,0) scale(1); }
  100%{ opacity: 0; transform: translate(-50%,-50%) translate(var(--sw-bx), var(--sw-by)) scale(0.4); }
}

/* ---- count ---- */
.sw-like-count{
  font-family: 'Space Grotesk', 'Inter', sans-serif;
  font-weight: 600;
  font-size: clamp(0.78rem, 0.72rem + 0.2vw, 0.9rem);
  color: #AEB4D1;
  min-width: 1.6em;
  display: inline-block;
  transition: color 200ms ease;
}
.sw-like.is-liked .sw-like-count{ color: var(--sw-heart-1); }
.sw-like-count.sw-bump{ animation: sw-count-bump 320ms ease; }
@keyframes sw-count-bump{
  0%{ opacity: 0.3; transform: translateY(4px); }
  100%{ opacity: 1; transform: translateY(0); }
}

/* ---- large center heart pop, for double-tap-to-like on an attached photo ---- */
.sw-doubletap-heart{
  position: absolute;
  z-index: 20;
  width: clamp(4rem, 18vw, 6.5rem);
  height: clamp(4rem, 18vw, 6.5rem);
  pointer-events: none;
  opacity: 0;
  transform: translate(-50%,-50%) scale(0.4);
  filter: drop-shadow(0 6px 18px rgba(0,0,0,0.35));
  animation: sw-doubletap-pop 800ms cubic-bezier(.2,.9,.3,1) forwards;
}
.sw-doubletap-heart path{ fill: url(#sw-heart-grad); }
@keyframes sw-doubletap-pop{
  0%{ opacity: 0; transform: translate(-50%,-50%) scale(0.4); }
  25%{ opacity: 1; transform: translate(-50%,-50%) scale(1.15); }
  40%{ transform: translate(-50%,-50%) scale(0.98); }
  75%{ opacity: 1; transform: translate(-50%,-50%) scale(1); }
  100%{ opacity: 0; transform: translate(-50%,-50%) scale(1.05); }
}

/* ==========================================================================
   Responsive — container query on the like button's own box (own
   implementation) with a media-query fallback for narrower viewports.
   ========================================================================== */
@container sw-like (max-width: 60px){
  .sw-like-count{ display: none; }
}
@media (max-width: 380px){
  .sw-like--lg{ --sw-size: clamp(2.2rem, 2rem + 0.7vw, 2.6rem); }
}

@media (prefers-reduced-motion: reduce){
  .sw-heart-fill, .sw-heart-outline, .sw-burst-particle, .sw-doubletap-heart, .sw-like-count.sw-bump{
    animation-duration: 0.01ms !important;
    transition-duration: 0.01ms !important;
  }
}
</style>

<svg width="0" height="0" style="position:absolute" aria-hidden="true">
  <defs>
    <linearGradient id="sw-heart-grad" x1="0%" y1="0%" x2="100%" y2="100%">
      <stop offset="0%" stop-color="#FB7185"/>
      <stop offset="100%" stop-color="#F43F5E"/>
    </linearGradient>
  </defs>
</svg>

<script>
(function(){
  if (window.SwLike) return;

  var HEART_PATH = 'M12 20.5s-7.2-4.4-9.8-9C.6 8 1.9 4.3 5.4 3.6c2.2-.5 4.2.6 6.1 2.8 1.9-2.2 3.9-3.3 6.1-2.8 3.5.7 4.8 4.4 3.2 7.9-2.6 4.6-9.8 9-9.8 9z';
  var inited = new WeakSet();
  var lastTap = {};

  function formatCount(n) {
    n = Number(n) || 0;
    if (n < 1000) return String(n);
    if (n < 1000000) {
      var k = n / 1000;
      return (Number.isInteger(k) ? k : k.toFixed(1)) + 'K';
    }
    var m = n / 1000000;
    return (Number.isInteger(m) ? m : m.toFixed(1)) + 'M';
  }

  function spawnBurst(btn) {
    if (btn.getAttribute('data-burst') === 'false') return;
    var container = btn.querySelector('.sw-like-burst');
    if (!container) return;
    var count = 7;
    for (var i = 0; i < count; i++) {
      var p = document.createElement('span');
      p.className = 'sw-burst-particle';
      var angle = (Math.PI * 2 * i / count) + (Math.random() * 0.5 - 0.25);
      var dist = 20 + Math.random() * 14;
      p.style.setProperty('--sw-bx', (Math.cos(angle) * dist).toFixed(1) + 'px');
      p.style.setProperty('--sw-by', (Math.sin(angle) * dist).toFixed(1) + 'px');
      p.style.animationDelay = (Math.random() * 60) + 'ms';
      container.appendChild(p);
      (function(el){ setTimeout(function(){ if (el.parentNode) el.parentNode.removeChild(el); }, 750); })(p);
    }
  }

  function bumpCount(btn) {
    var el = btn.querySelector('[data-sw-like-count]');
    if (!el) return;
    el.classList.remove('sw-bump');
    void el.offsetWidth;
    el.classList.add('sw-bump');
  }

  function setState(btn, liked, count) {
    var wasLiked = btn.classList.contains('is-liked');
    var countEl = btn.querySelector('[data-sw-like-count]');
    var previousCount = countEl ? parseCount(countEl.textContent) : null;

    btn.classList.toggle('is-liked', liked);
    btn.setAttribute('aria-pressed', String(liked));

    if (typeof count === 'number' && countEl) {
      countEl.textContent = formatCount(count);
      bumpCount(btn);
    } else if (countEl && wasLiked !== liked) {
      var current = parseCount(countEl.textContent);
      var next = liked ? current + 1 : Math.max(0, current - 1);
      countEl.textContent = formatCount(next);
      bumpCount(btn);
    }

    if (liked && !wasLiked) spawnBurst(btn);

    btn.dispatchEvent(new CustomEvent('sw-like-change', {
      bubbles: true,
      detail: { id: btn.id, liked: liked, previousCount: previousCount }
    }));
  }

  function parseCount(text) {
    if (!text) return 0;
    text = text.trim();
    var mult = 1;
    if (/K$/i.test(text)) { mult = 1000; text = text.slice(0, -1); }
    else if (/M$/i.test(text)) { mult = 1000000; text = text.slice(0, -1); }
    var n = parseFloat(text);
    if (isNaN(n)) return 0;
    return Math.round(n * mult);
  }

  function toggle(idOrEl) {
    var btn = typeof idOrEl === 'string' ? document.getElementById(idOrEl) : idOrEl;
    if (!btn) return;
    setState(btn, !btn.classList.contains('is-liked'));
  }

  function set(idOrEl, liked, count) {
    var btn = typeof idOrEl === 'string' ? document.getElementById(idOrEl) : idOrEl;
    if (!btn) return;
    setState(btn, !!liked, typeof count === 'number' ? count : undefined);
  }

  function showDoubleTapHeart(target) {
    var host = target.closest('[data-sw-like-host]') || target.parentElement || target;
    var style = window.getComputedStyle(host);
    if (style.position === 'static') host.style.position = 'relative';
    host.style.overflow = host.style.overflow || 'hidden';

    var rect = target.getBoundingClientRect
      ? { width: target.offsetWidth, height: target.offsetHeight }
      : { width: 100, height: 100 };

    var heart = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
    heart.setAttribute('viewBox', '0 0 24 24');
    heart.setAttribute('class', 'sw-doubletap-heart');
    heart.style.left = (rect.width / 2) + 'px';
    heart.style.top = (rect.height / 2) + 'px';
    var path = document.createElementNS('http://www.w3.org/2000/svg', 'path');
    path.setAttribute('d', HEART_PATH);
    heart.appendChild(path);
    host.appendChild(heart);
    setTimeout(function(){ if (heart.parentNode) heart.parentNode.removeChild(heart); }, 850);
  }

  function attachDoubleTap(target, likeBtn) {
    target.setAttribute('data-sw-like-host', '');
    target.addEventListener('dblclick', function(e){
      e.preventDefault();
      showDoubleTapHeart(target);
      if (!likeBtn.classList.contains('is-liked')) setState(likeBtn, true);
    });
    target.addEventListener('touchend', function(e){
      var now = Date.now();
      var key = likeBtn.id;
      if (lastTap[key] && (now - lastTap[key]) < 300) {
        showDoubleTapHeart(target);
        if (!likeBtn.classList.contains('is-liked')) setState(likeBtn, true);
        lastTap[key] = 0;
      } else {
        lastTap[key] = now;
      }
    }, { passive: true });
  }

  function initBtn(btn) {
    if (inited.has(btn)) return;
    inited.add(btn);

    btn.addEventListener('click', function(){
      setState(btn, !btn.classList.contains('is-liked'));
    });

    var attachId = btn.getAttribute('data-attach-to');
    if (attachId) {
      var target = document.getElementById(attachId);
      if (target) attachDoubleTap(target, btn);
    }
  }

  function scan(root) {
    var nodes = (root || document).querySelectorAll('[data-sw-like]');
    Array.prototype.forEach.call(nodes, initBtn);
  }

  function start() {
    scan(document);
    if ('MutationObserver' in window) {
      var mo = new MutationObserver(function(mutations){
        mutations.forEach(function(m){
          Array.prototype.forEach.call(m.addedNodes || [], function(node){
            if (node.nodeType !== 1) return;
            if (node.matches && node.matches('[data-sw-like]')) initBtn(node);
            if (node.querySelectorAll) scan(node);
          });
        });
      });
      mo.observe(document.body, { childList: true, subtree: true });
    }
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', start);
  } else {
    start();
  }

  window.SwLike = { toggle: toggle, set: set };
})();
</script>
'''
  }
}
