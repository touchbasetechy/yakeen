package com.murad.app.matrimony

/**
 * Daily recommendations carousel - glassmorphic, mobile-first, touch-enabled.
 * Self-contained: CSS and JS are embedded below (no asset includes needed).
 *
 * Location: grails-app/taglib/com/matrimony/DailyRecommendationsTagLib.groovy
 *
 * USAGE (in any GSP):
 *
 *   <mat:dailyRecommendations/>
 *   <mat:dailyRecommendations max="10" title="Your picks for today" autoplay="true" interval="6000"/>
 *   <mat:dailyRecommendations profiles="${matches}" showInterest="false" nonce="${cspNonce}"/>
 *
 * Attributes:
 *   profiles      optional List of profiles; if omitted, recommendationService is called
 *   max           max cards (default 12)
 *   title         heading text (default i18n "recommendations.title")
 *   autoplay      "true" to auto-advance (default false)
 *   interval      autoplay delay in ms (default 5000)
 *   showInterest  "false" to hide the interest buttons on cards (default true)
 *   nonce         CSP nonce added to the inline <style>/<script> (optional)
 *   id            DOM id (default generated)
 *
 * Each profile (domain object or Map) must expose:
 *   id, displayName, age, heightText, city, profession,
 *   photoUrl (nullable), verified (boolean), premium (boolean),
 *   matchPercent (nullable Integer), interestStatus (see InterestTagLib)
 *
 * Expected backend:
 *   RecommendationService.dailyMatches(Long userId, int max) -> List
 *   ProfileController.show (id)  and  PreferencesController.edit
 *
 * Design notes
 *   - Mobile-first CSS; widens at 600px and 1024px. Native momentum swipe via scroll-snap,
 *     44-48px touch targets on coarse pointers, mouse drag-to-scroll on desktop, keyboard arrows.
 *   - Glass effect uses backdrop-filter (with -webkit- prefix). Browsers without it, and users who
 *     enable "reduce transparency", get solid translucent cards instead.
 *   - Honors prefers-color-scheme, [data-theme], prefers-reduced-motion and forced-colors.
 *   - CSS and JS are written once per request even when several carousels are on a page.
 */
class DailyRecommendationsTagLib {

  static namespace = 'mat'
  // Every dynamic value is encoded explicitly through h()
  static defaultEncodeAs = [taglib: 'none']

  private static final String ASSETS_KEY = 'mat.dailyReco.assetsWritten'

  private static final String CHEV_LEFT = '<svg viewBox="0 0 24 24" width="20" height="20" aria-hidden="true" focusable="false"><path d="M15 5l-7 7 7 7" fill="none" stroke="currentColor" stroke-width="2.4" stroke-linecap="round" stroke-linejoin="round"/></svg>'
  private static final String CHEV_RIGHT = '<svg viewBox="0 0 24 24" width="20" height="20" aria-hidden="true" focusable="false"><path d="M9 5l7 7-7 7" fill="none" stroke="currentColor" stroke-width="2.4" stroke-linecap="round" stroke-linejoin="round"/></svg>'

  def recommendationService
  def springSecurityService   // replace if you use another auth plugin

  def dailyRecommendations = { attrs, body ->
    int max = (attrs.max ?: 12) as int
    int interval = (attrs.interval ?: 5000) as int
    boolean autoplay = attrs.autoplay?.toString() == 'true'
    boolean showInterest = attrs.showInterest?.toString() != 'false'
    String title = attrs.title ?: message(code: 'recommendations.title', default: "Today's recommendations")
    String domId = attrs.id ?: "daily-reco-${System.identityHashCode(attrs)}"

    List profiles = attrs.profiles as List
    if (profiles == null) {
      Long me = springSecurityService?.currentUserId as Long
      profiles = me ? recommendationService.dailyMatches(me, max) : []
    }
    profiles = profiles.take(max)

    out << assets(attrs.nonce)

    if (!profiles) {
      out << emptyState(title)
      return
    }

    int total = profiles.size()
    String prevLabel = message(code: 'recommendations.prev', default: 'Previous profiles')
    String nextLabel = message(code: 'recommendations.next', default: 'Next profiles')

    out << """<section id="${h(domId)}" class="daily-reco" role="region" aria-roledescription="carousel"
                     aria-label="${h(title)}" data-autoplay="${autoplay}" data-interval="${interval}"
                     data-refresh-at="${nextMidnight()}">
  <div class="dr-head">
    <div>
      <h2 class="dr-title">${h(title)}</h2>
      <span class="dr-refresh">${h(message(code: 'recommendations.refresh', default: 'New picks in'))}
        <span class="dr-countdown">--</span></span>
    </div>
    <div class="dr-nav">
      <span class="dr-count" aria-hidden="true">1 / ${total}</span>
      <button type="button" class="dr-arrow dr-prev" aria-label="${h(prevLabel)}">${CHEV_LEFT}</button>
      <button type="button" class="dr-arrow dr-next" aria-label="${h(nextLabel)}">${CHEV_RIGHT}</button>
    </div>
  </div>
  <div class="dr-track" tabindex="0" aria-live="${autoplay ? 'off' : 'polite'}">"""

    profiles.eachWithIndex { p, i ->
      out << slide(p, i + 1, total, showInterest)
    }

    out << """</div>
  <div class="dr-progress" aria-hidden="true"><span class="dr-bar"></span></div>
</section>"""
  }

  // ------------------------------------------------------------------
  // Markup helpers
  // ------------------------------------------------------------------
  private String slide(def p, int index, int total, boolean showInterest) {
    String profileUrl = createLink(controller: 'profile', action: 'show', id: p.id)
    String name = p.displayName?.toString() ?: ''
    String initial = name ? name.substring(0, 1).toUpperCase() : '?'

    String photo = p.photoUrl ?
      """<img src="${h(p.photoUrl)}" alt="${h(name)}" loading="lazy" decoding="async">""" :
      """<span aria-hidden="true">${h(initial)}</span>"""

    String match = p.matchPercent != null ?
      """<span class="dr-match">${p.matchPercent as int}% ${h(message(code: 'recommendations.match', default: 'match'))}</span>""" : ''

    String badges = ''
    if (p.verified) {
      badges += """<span class="dr-badge dr-badge--ok">${h(message(code: 'badge.verified', default: 'Verified'))}</span>"""
    }
    if (p.premium) {
      badges += """<span class="dr-badge">${h(message(code: 'badge.premium', default: 'Premium'))}</span>"""
    }

    String meta1 = [p.age ? "${p.age} yrs" : null, p.heightText].findAll { it }.join(' · ')
    String meta2 = [p.city, p.profession].findAll { it }.join(' · ')

    String actions = showInterest ?
      interestButtons(profileId: p.id, status: p.interestStatus, size: 'sm') : ''

    return """<article class="dr-slide" role="group" aria-roledescription="slide"
                    aria-label="${index} / ${total}" data-profile-id="${p.id}">
  <a class="dr-photo" href="${h(profileUrl)}" tabindex="-1" aria-hidden="true">${photo}${match}</a>
  <div class="dr-body">
    <h3 class="dr-name"><a href="${h(profileUrl)}">${h(name)}</a></h3>
    <p class="dr-meta">${h(meta1)}</p>
    <p class="dr-meta">${h(meta2)}</p>
    <div class="dr-badges">${badges}</div>
    <div class="dr-actions">${actions}</div>
  </div>
</article>"""
  }

  private String emptyState(String title) {
    String prefsUrl = createLink(controller: 'preferences', action: 'edit')
    return """<section class="daily-reco dr-emptywrap">
  <div class="dr-empty">
    <h2 class="dr-title">${h(title)}</h2>
    <p>${h(message(code: 'recommendations.empty', default: 'No new recommendations today. Widen your partner preferences to see more matches.'))}</p>
    <a class="btn btn-primary" href="${h(prefsUrl)}">${h(message(code: 'recommendations.editPrefs', default: 'Edit preferences'))}</a>
  </div>
</section>"""
  }

  /** Writes the embedded CSS + JS once per request. */
  private String assets(def nonce) {
    if (request.getAttribute(ASSETS_KEY)) {
      return ''
    }
    request.setAttribute(ASSETS_KEY, true)
    String n = nonce ? " nonce=\"${h(nonce)}\"" : ''
    return "<style${n}>\n${CSS}</style>\n<script${n}>\n${JS}</script>\n"
  }

  /** Epoch millis of the next local midnight (when picks refresh). */
  private long nextMidnight() {
    Calendar c = Calendar.getInstance()
    c.add(Calendar.DAY_OF_YEAR, 1)
    c.set(Calendar.HOUR_OF_DAY, 0)
    c.set(Calendar.MINUTE, 0)
    c.set(Calendar.SECOND, 0)
    c.set(Calendar.MILLISECOND, 0)
    return c.timeInMillis
  }

  private String h(Object value) {
    return value == null ? '' : value.toString().encodeAsHTML()
  }

  // ------------------------------------------------------------------
  // Embedded CSS (mobile-first). Inherits --ink, --muted, --pri, --acc, --ok if the host defines them.
  // ------------------------------------------------------------------
  private static final String CSS = '''
.daily-reco{--dr-ink:var(--ink,#14211f);--dr-muted:var(--muted,#41524f);--dr-pri:var(--pri,#0f5c5c);--dr-acc:var(--acc,#e8a317);--dr-ok:var(--ok,#2f7d4f);--dr-soft:rgba(232,163,23,.22);
--dr-glass:rgba(255,255,255,.5);--dr-solid:rgba(255,255,255,.94);--dr-brd:rgba(255,255,255,.65);--dr-hi:rgba(255,255,255,.8);--dr-shadow:0 10px 30px rgba(15,60,60,.16);--dr-b1:#dff3ef;--dr-b2:#fdf3dc}
@media (prefers-color-scheme:dark){:root:not([data-theme="light"]) .daily-reco{--dr-ink:var(--ink,#e8f1ef);--dr-muted:var(--muted,#a9bdb9);--dr-pri:var(--pri,#3fb5ad);--dr-acc:var(--acc,#f0b73a);--dr-ok:var(--ok,#5cc38a);--dr-soft:rgba(240,183,58,.2);--dr-glass:rgba(24,40,39,.48);--dr-solid:rgba(22,35,34,.95);--dr-brd:rgba(255,255,255,.14);--dr-hi:rgba(255,255,255,.12);--dr-shadow:0 10px 30px rgba(0,0,0,.4);--dr-b1:#0b1f1e;--dr-b2:#1c1a10}}
:root[data-theme="dark"] .daily-reco{--dr-ink:var(--ink,#e8f1ef);--dr-muted:var(--muted,#a9bdb9);--dr-pri:var(--pri,#3fb5ad);--dr-acc:var(--acc,#f0b73a);--dr-ok:var(--ok,#5cc38a);--dr-soft:rgba(240,183,58,.2);--dr-glass:rgba(24,40,39,.48);--dr-solid:rgba(22,35,34,.95);--dr-brd:rgba(255,255,255,.14);--dr-hi:rgba(255,255,255,.12);--dr-shadow:0 10px 30px rgba(0,0,0,.4);--dr-b1:#0b1f1e;--dr-b2:#1c1a10}

.daily-reco{position:relative;isolation:isolate;overflow:hidden;border-radius:22px;padding:16px 0 14px;color:var(--dr-ink);-webkit-tap-highlight-color:transparent;background-color:var(--dr-b1);
background-image:radial-gradient(55% 70% at 8% 0%,rgba(15,140,130,.45),transparent 70%),radial-gradient(50% 70% at 100% 10%,rgba(232,163,23,.4),transparent 70%),radial-gradient(60% 80% at 50% 120%,rgba(130,100,220,.3),transparent 70%),linear-gradient(135deg,var(--dr-b1),var(--dr-b2))}

/* Glass surfaces */
.dr-slide,.dr-arrow,.dr-count,.dr-empty{background:var(--dr-glass);-webkit-backdrop-filter:blur(18px) saturate(170%);backdrop-filter:blur(18px) saturate(170%);border:1px solid var(--dr-brd);box-shadow:var(--dr-shadow),inset 0 1px 0 var(--dr-hi)}
@supports not ((-webkit-backdrop-filter:blur(1px)) or (backdrop-filter:blur(1px))){.daily-reco{--dr-glass:var(--dr-solid)}}
@media (prefers-reduced-transparency:reduce){.daily-reco{--dr-glass:var(--dr-solid)}.dr-slide,.dr-arrow,.dr-count,.dr-empty,.dr-match,.dr-badge{-webkit-backdrop-filter:none;backdrop-filter:none}}

.dr-head{display:flex;align-items:flex-end;justify-content:space-between;flex-wrap:wrap;gap:10px;padding:0 16px 12px}
.dr-title{margin:0;font-size:1.15rem;line-height:1.25}
.dr-refresh{font-size:.8rem;color:var(--dr-muted)}
.dr-nav{display:flex;align-items:center;gap:8px}
.dr-count{font-size:.78rem;font-weight:600;padding:5px 11px;border-radius:99px;font-variant-numeric:tabular-nums}
.dr-arrow{display:grid;place-items:center;width:44px;height:44px;padding:0;border-radius:50%;color:var(--dr-ink);cursor:pointer;touch-action:manipulation;transition:transform .15s,opacity .2s}
.dr-arrow:active{transform:scale(.92)}
.dr-arrow:disabled{opacity:.35;cursor:default;transform:none}
.dr-arrow:focus-visible,.dr-track:focus-visible,.dr-name a:focus-visible{outline:3px solid var(--dr-acc);outline-offset:2px}

/* Track: native momentum swipe + snap */
.dr-track{display:flex;gap:12px;overflow-x:auto;overflow-y:hidden;padding:6px 16px 14px;scroll-snap-type:x mandatory;scroll-padding:0 16px;scroll-behavior:smooth;-webkit-overflow-scrolling:touch;overscroll-behavior-x:contain;touch-action:pan-x pan-y pinch-zoom;scrollbar-width:none;-ms-overflow-style:none}
.dr-track::-webkit-scrollbar{display:none}
.dr-track.is-dragging{scroll-snap-type:none;scroll-behavior:auto;cursor:grabbing;-webkit-user-select:none;user-select:none}
.dr-track.is-dragging a{pointer-events:none}

.dr-slide{flex:0 0 76%;max-width:290px;scroll-snap-align:start;border-radius:20px;overflow:hidden;display:flex;flex-direction:column;color:var(--dr-ink);animation:drIn .5s ease-out backwards;transition:transform .25s,box-shadow .25s}
.dr-slide:nth-child(2){animation-delay:.05s}.dr-slide:nth-child(3){animation-delay:.1s}.dr-slide:nth-child(4){animation-delay:.15s}.dr-slide:nth-child(5){animation-delay:.2s}.dr-slide:nth-child(n+6){animation-delay:.25s}
@keyframes drIn{from{opacity:0;transform:translateY(14px) scale(.98)}to{opacity:1;transform:none}}
.dr-photo{position:relative;display:grid;place-items:center;aspect-ratio:4/5;overflow:hidden;background:linear-gradient(160deg,var(--dr-pri),var(--dr-acc));color:#fff;font-size:2.6rem;font-weight:700;text-decoration:none}
@supports not (aspect-ratio:4/5){.dr-photo{height:300px}}
.dr-photo img{position:absolute;top:0;left:0;width:100%;height:100%;object-fit:cover;transition:transform .5s}
.dr-match{position:absolute;top:10px;right:10px;background:rgba(10,20,20,.45);-webkit-backdrop-filter:blur(8px);backdrop-filter:blur(8px);border:1px solid rgba(255,255,255,.3);color:#fff;font-size:.75rem;font-weight:600;padding:3px 10px;border-radius:99px}
.dr-body{padding:12px 14px 14px;display:flex;flex-direction:column;gap:3px;flex:1}
.dr-name{margin:0;font-size:1.05rem;line-height:1.3}
.dr-name a{color:inherit;text-decoration:none}
.dr-meta{margin:0;font-size:.85rem;color:var(--dr-muted)}
.dr-badges{display:flex;flex-wrap:wrap;gap:5px;margin-top:5px;min-height:22px}
.dr-badge{font-size:.7rem;font-weight:600;padding:2px 9px;border-radius:99px;background:var(--dr-soft);color:var(--dr-ink);border:1px solid var(--dr-brd)}
.dr-badge--ok{background:var(--dr-ok);border-color:transparent;color:#fff}
.dr-actions{margin-top:auto;padding-top:10px}
.dr-actions .btn{min-height:40px;border-radius:12px;touch-action:manipulation}

.dr-progress{height:4px;margin:2px 16px 0;border-radius:4px;background:rgba(127,127,127,.25);overflow:hidden}
.dr-bar{display:block;width:100%;height:100%;border-radius:4px;background:linear-gradient(90deg,var(--dr-pri),var(--dr-acc));-webkit-transform-origin:left center;transform-origin:left center;-webkit-transform:scaleX(.1);transform:scaleX(.1);transition:transform .15s}
.dr-static .dr-nav,.dr-static .dr-progress{display:none}

.dr-emptywrap{padding:16px}
.dr-empty{padding:20px;border-radius:20px;color:var(--dr-muted)}
.dr-empty .dr-title{color:var(--dr-ink);margin-bottom:6px}

@media (min-width:600px){.daily-reco{padding-top:20px}.dr-head{padding:0 22px 14px}.dr-track{padding-left:22px;padding-right:22px;scroll-padding:0 22px;gap:16px}.dr-progress{margin:2px 22px 0}.dr-title{font-size:1.3rem}.dr-slide{flex-basis:240px}.dr-arrow{width:40px;height:40px}}
@media (min-width:1024px){.dr-slide{flex-basis:264px}.dr-track{cursor:grab}}
@media (hover:hover){.dr-slide:hover{transform:translateY(-4px);box-shadow:0 16px 40px rgba(15,60,60,.24),inset 0 1px 0 var(--dr-hi)}.dr-slide:hover .dr-photo img{transform:scale(1.05)}.dr-arrow:hover:not(:disabled){transform:scale(1.08)}}
@media (pointer:coarse){.dr-arrow{width:48px;height:48px}.dr-actions .btn{min-height:44px}}
@media (prefers-reduced-motion:reduce){.dr-track{scroll-behavior:auto}.dr-slide,.dr-photo img,.dr-arrow,.dr-bar{animation:none;transition:none}}
@media (forced-colors:active){.dr-slide,.dr-arrow,.dr-count,.dr-empty{border:1px solid CanvasText;-webkit-backdrop-filter:none;backdrop-filter:none}}
'''

  // ------------------------------------------------------------------
  // Embedded JS: arrows, keyboard, mouse drag, autoplay, progress, countdown
  // ------------------------------------------------------------------
  private static final String JS = '''
(function () {
  'use strict';
  if (window.DailyReco) return;
  var reduce = !!(window.matchMedia && window.matchMedia('(prefers-reduced-motion: reduce)').matches);

  function throttle(fn) {
    var queued = false;
    return function () {
      if (queued) return;
      queued = true;
      (window.requestAnimationFrame || window.setTimeout)(function () { queued = false; fn(); });
    };
  }

  function init(root) {
    if (root.dataset.drReady) return;
    var track = root.querySelector('.dr-track');
    var prev = root.querySelector('.dr-prev');
    var next = root.querySelector('.dr-next');
    if (!track || !prev || !next) return;
    root.dataset.drReady = '1';

    var bar = root.querySelector('.dr-bar');
    var count = root.querySelector('.dr-count');
    var slides = track.querySelectorAll('.dr-slide');

    function step() {
      var a = slides[0], b = slides[1];
      if (!a) return track.clientWidth * 0.8;
      return b ? b.offsetLeft - a.offsetLeft : a.offsetWidth + 12;
    }
    function maxScroll() { return track.scrollWidth - track.clientWidth; }
    function atEnd() { return track.scrollLeft >= maxScroll() - 2; }
    function go(dir) {
      var left = dir * step();
      if (track.scrollBy) track.scrollBy({ left: left, behavior: reduce ? 'auto' : 'smooth' });
      else track.scrollLeft += left;
    }

    var update = throttle(function () {
      var max = maxScroll(), x = track.scrollLeft;
      if (max <= 2) root.classList.add('dr-static'); else root.classList.remove('dr-static');
      prev.disabled = x <= 2;
      next.disabled = x >= max - 2;
      if (bar) {
        var ratio = max > 0 ? Math.min(1, Math.max(0.08, x / max)) : 1;
        bar.style.transform = 'scaleX(' + ratio + ')';
        bar.style.webkitTransform = 'scaleX(' + ratio + ')';
      }
      if (count) {
        var i = x >= max - 2 ? slides.length : Math.round(x / step()) + 1;
        count.textContent = Math.min(slides.length, i) + ' / ' + slides.length;
      }
    });

    prev.addEventListener('click', function () { go(-1); });
    next.addEventListener('click', function () { go(1); });
    track.addEventListener('scroll', update, { passive: true });
    window.addEventListener('resize', update);
    if (window.ResizeObserver) new ResizeObserver(update).observe(track);
    track.addEventListener('keydown', function (e) {
      if (e.key === 'ArrowRight') { e.preventDefault(); go(1); }
      else if (e.key === 'ArrowLeft') { e.preventDefault(); go(-1); }
      else if (e.key === 'Home') { e.preventDefault(); track.scrollLeft = 0; }
      else if (e.key === 'End') { e.preventDefault(); track.scrollLeft = maxScroll(); }
    });

    // Mouse drag-to-scroll (touch and pen use native scrolling)
    var down = false, moved = false, startX = 0, startLeft = 0;
    track.addEventListener('pointerdown', function (e) {
      if (e.pointerType !== 'mouse' || e.button !== 0) return;
      down = true; moved = false; startX = e.clientX; startLeft = track.scrollLeft;
    });
    track.addEventListener('pointermove', function (e) {
      if (!down) return;
      var dx = e.clientX - startX;
      if (!moved && Math.abs(dx) > 6) { moved = true; track.classList.add('is-dragging'); }
      if (moved) track.scrollLeft = startLeft - dx;
    });
    function endDrag() {
      if (!down) return;
      down = false;
      track.classList.remove('is-dragging');
    }
    track.addEventListener('pointerup', endDrag);
    track.addEventListener('pointercancel', endDrag);
    track.addEventListener('pointerleave', endDrag);
    track.addEventListener('click', function (e) {
      if (moved) { e.preventDefault(); e.stopPropagation(); moved = false; }
    }, true);

    // Autoplay: pauses on hover, focus, touch, hidden tab and off-screen
    if (root.dataset.autoplay === 'true' && !reduce) {
      var ms = parseInt(root.dataset.interval, 10) || 5000;
      var timer = null, resume = null, visible = true;
      var stop = function () { clearInterval(timer); timer = null; };
      var start = function () {
        stop();
        timer = setInterval(function () {
          if (!visible || document.hidden) return;
          if (atEnd()) track.scrollTo({ left: 0, behavior: 'smooth' }); else go(1);
        }, ms);
      };
      var pause = function (delay) { stop(); clearTimeout(resume); resume = setTimeout(start, delay); };
      root.addEventListener('mouseenter', stop);
      root.addEventListener('mouseleave', start);
      root.addEventListener('focusin', stop);
      root.addEventListener('focusout', start);
      root.addEventListener('pointerdown', function (e) { if (e.pointerType !== 'mouse') pause(8000); });
      if (window.IntersectionObserver) {
        new IntersectionObserver(function (en) { visible = en[0].isIntersecting; }).observe(root);
      }
      start();
    }

    // "New picks in 5h 12m"
    var label = root.querySelector('.dr-countdown');
    var at = parseInt(root.dataset.refreshAt, 10);
    if (label && at) {
      var tick = function () {
        var m = Math.max(0, Math.floor((at - Date.now()) / 60000));
        label.textContent = Math.floor(m / 60) + 'h ' + (m % 60) + 'm';
      };
      tick();
      setInterval(tick, 30000);
    }

    update();
  }

  function initAll() {
    Array.prototype.forEach.call(document.querySelectorAll('.daily-reco'), init);
  }

  window.DailyReco = { init: init, initAll: initAll };
  if (document.readyState === 'loading') document.addEventListener('DOMContentLoaded', initAll);
  else initAll();
})();
'''
}
