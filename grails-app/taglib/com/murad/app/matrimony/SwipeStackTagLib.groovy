package com.murad.app.matrimony

import groovy.util.logging.Slf4j

/**
 * Swipe Stack — Tinder-style match cards — GSP TagLib
 *
 * Usage in GSP:
 *   <glass:swipeStack id="matchStack" onSwipeLeft="handlePass" onSwipeRight="handleLike"
 *                      onSwipeUp="handleSuperLike" emptyMessage="No more profiles right now">
 *     <glass:swipeCard cardId="42" name="Aisha, 26" subtitle="Chennai · Software Engineer"
 *                       imageUrl="/images/u42.jpg" distance="3 km away" verified="true"/>
 *     <glass:swipeCard cardId="17" name="Rahul, 29" subtitle="Bengaluru · Product Designer"
 *                       imageUrl="/images/u17.jpg" distance="9 km away"/>
 *   </glass:swipeStack>
 *
 *   <!-- drive it from elsewhere on the page (e.g. a "Skip" link) -->
 *   <script>
 *     document.getElementById('skipBtn').addEventListener('click', function () {
 *       glassSwipeStack.pass('matchStack');
 *     });
 *   </script>
 *
 * What it renders
 * ─────────────────────────────────────────────────────────────────────────
 * A depth-scaled stack of cards where the top card can be:
 *   1. dragged with a finger, pen or mouse (unified via the Pointer Events
 *      API, with a touch/mouse fallback for older browsers) — the card
 *      rotates as it's dragged, and LIKE / NOPE / SUPER LIKE stamps fade
 *      in based on drag direction and distance,
 *   2. released past a distance threshold OR flicked past a velocity
 *      threshold to commit the swipe — it flies off-screen and the next
 *      card animates into place,
 *   3. released short of both thresholds to spring back to center,
 *   4. dismissed without touch at all — via the on-screen pass/super/like
 *      buttons, or ArrowLeft / ArrowUp / ArrowRight on a focused stack.
 * When the stack is exhausted, an empty state fades in. A visually-hidden
 * live region announces each outcome for screen reader users.
 *
 * Global JS API (mirrors window.glassProgress's registry pattern):
 *   glassSwipeStack.like('matchStack')
 *   glassSwipeStack.pass('matchStack')
 *   glassSwipeStack.superLike('matchStack')
 *   glassSwipeStack.reset('matchStack')          // restore all swiped cards
 *   glassSwipeStack.remaining('matchStack')      // -> count of cards left
 *
 * <glass:swipeStack> attributes
 * ─────────────────────────────────────────────────────────────────────────
 *   id             (String, optional)  — unique id; auto-generated when omitted.
 *   onSwipeLeft    (String, optional)  — global JS function name, called as
 *                                        fn(cardId, cardEl) when a card is passed.
 *   onSwipeRight   (String, optional)  — same, called when a card is liked.
 *   onSwipeUp      (String, optional)  — same, called when a card is super-liked.
 *   threshold      (px, optional)      — drag distance to commit a swipe
 *                                        (default: 110, clamped 40..300).
 *   rotationFactor (double, optional)  — degrees of tilt per px dragged
 *                                        (default: 0.06, clamped 0.01..0.25).
 *   maxVisible     (int, optional)     — cards rendered with visible depth
 *                                        behind the top card (default: 3, clamped 1..6).
 *   buttons        (Boolean, optional) — show tap-to-swipe action buttons
 *                                        below the stack (default: true).
 *   indicators     (Boolean, optional) — show LIKE/NOPE/SUPER stamps while
 *                                        dragging (default: true).
 *   haptics        (Boolean, optional) — navigator.vibrate() on commit, when
 *                                        supported (default: true).
 *   accentLike     (String, optional)  — Bootstrap variant or raw CSS color
 *                                        for the like stamp/button (default: "success").
 *   accentPass     (String, optional)  — for the pass stamp/button (default: "danger").
 *   accentSuper    (String, optional)  — for the super-like stamp/button (default: "info").
 *   emptyMessage   (String, optional)  — text shown once the stack is empty
 *                                        (default: "You're all caught up").
 *   class          (String, optional)  — extra CSS classes on the wrapper.
 *
 * <glass:swipeCard> attributes (used inside the body of <glass:swipeStack>)
 * ─────────────────────────────────────────────────────────────────────────
 *   cardId    (String, optional)  — identifier passed back to callbacks (auto-generated if omitted).
 *   name      (String, required)  — headline, e.g. "Aisha, 26".
 *   subtitle  (String, optional)  — secondary line, e.g. "Chennai · Software Engineer".
 *   imageUrl  (String, required)  — profile photo.
 *   distance  (String, optional)  — small chip, e.g. "3 km away".
 *   badge     (String, optional)  — small corner badge, e.g. "New".
 *   verified  (Boolean, optional) — shows a verified checkmark next to the name.
 *   class     (String, optional)  — extra CSS classes on the card.
 *   Body content (optional) is appended below the subtitle — e.g. interest chips.
 */
@Slf4j
class SwipeStackTagLib {

  static namespace = 'glass'

  private static final Set<String> BS_VARIANTS = [
    'primary', 'secondary', 'success', 'danger', 'warning', 'info', 'dark', 'light'
  ] as Set

  private String resolveColor(String value) {
    return (value in BS_VARIANTS) ? "var(--bs-${value})".toString() : value
  }

  private int toIntOrDefault(Object v, int fallback) {
    if (v == null) return fallback
    try { return Integer.parseInt(v.toString().trim()) } catch (NumberFormatException ignored) { return fallback }
  }

  private double toDoubleOrDefault(Object v, double fallback) {
    if (v == null) return fallback
    try { return Double.parseDouble(v.toString().trim()) } catch (NumberFormatException ignored) { return fallback }
  }

  private int clampInt(int v, int min, int max) { Math.max(min, Math.min(max, v)) }

  private double clampDouble(double v, double min, double max) { Math.max(min, Math.min(max, v)) }

  private boolean toBoolOrDefault(Map attrs, String key, boolean fallback) {
    attrs.containsKey(key) ? Boolean.parseBoolean(attrs.remove(key).toString()) : fallback
  }

  // ── swipeCard ────────────────────────────────────────────────────────────

  /**
   * Renders a single card. Must be used inside the body of <glass:swipeStack>.
   */
  def swipeCard = { attrs, body ->
    String cardId = (attrs.remove('cardId') ?: "card_${System.currentTimeMillis()}_${(int) (Math.random() * 9999)}").toString()
    String name = (attrs.remove('name') ?: '').toString()
    String subtitle = (attrs.remove('subtitle') ?: '').toString()
    String imageUrl = (attrs.remove('imageUrl') ?: '').toString()
    String distance = (attrs.remove('distance') ?: '').toString()
    String badge = (attrs.remove('badge') ?: '').toString()
    boolean verified = attrs.remove('verified') in [true, 'true', '1', 'yes']
    String extra = (attrs.remove('class') ?: '').toString()

    String safeName = name.encodeAsHTML()
    String safeSubtitle = subtitle.encodeAsHTML()
    String ariaLabel = ([safeName, subtitle.encodeAsHTML()].findAll { it }).join(', ')
    String extraBody = body() ?: ''

    out << """
<article class="glass-swipe-card${extra ? ' ' + extra : ''}" data-glass-card data-card-id="${cardId.encodeAsHTML()}"
         role="group" aria-roledescription="slide" aria-label="${ariaLabel}" tabindex="-1">
  <img class="glass-swipe-card-media" src="${imageUrl.encodeAsHTML()}" alt="${safeName}" loading="lazy" decoding="async" draggable="false">
  <div class="glass-swipe-card-gradient" aria-hidden="true"></div>
  ${badge ? "<span class=\"glass-swipe-card-badge\">${badge.encodeAsHTML()}</span>" : ''}
  <div class="glass-swipe-card-info">
    <h3 class="glass-swipe-card-name">${safeName}${verified ? ' <span class="glass-swipe-verified" title="Verified" aria-hidden="true">&#10003;</span>' : ''}</h3>
    ${safeSubtitle ? "<p class=\"glass-swipe-card-subtitle\">${safeSubtitle}</p>" : ''}
    ${distance ? "<span class=\"glass-swipe-card-distance\">${distance.encodeAsHTML()}</span>" : ''}
    ${extraBody}
  </div>
  <div class="glass-swipe-stamp glass-swipe-stamp-like" aria-hidden="true">LIKE</div>
  <div class="glass-swipe-stamp glass-swipe-stamp-pass" aria-hidden="true">NOPE</div>
  <div class="glass-swipe-stamp glass-swipe-stamp-super" aria-hidden="true">SUPER LIKE</div>
</article>
"""
  }

  // ── swipeStack ───────────────────────────────────────────────────────────

  def swipeStack = { attrs, body ->
    String id = (attrs.remove('id') ?: "glassSwipe_${System.currentTimeMillis()}_${(int) (Math.random() * 9999)}").toString()
    String extra = (attrs.remove('class') ?: '').toString()

    String onSwipeLeft = (attrs.remove('onSwipeLeft') ?: '').toString()
    String onSwipeRight = (attrs.remove('onSwipeRight') ?: '').toString()
    String onSwipeUp = (attrs.remove('onSwipeUp') ?: '').toString()

    int threshold = clampInt(toIntOrDefault(attrs.remove('threshold'), 110), 40, 300)
    double rotationFactor = clampDouble(toDoubleOrDefault(attrs.remove('rotationFactor'), 0.06d), 0.01d, 0.25d)
    int maxVisible = clampInt(toIntOrDefault(attrs.remove('maxVisible'), 3), 1, 6)

    boolean buttons = toBoolOrDefault(attrs, 'buttons', true)
    boolean indicators = toBoolOrDefault(attrs, 'indicators', true)
    boolean haptics = toBoolOrDefault(attrs, 'haptics', true)

    String accentLike = resolveColor((attrs.remove('accentLike') ?: 'success').toString())
    String accentPass = resolveColor((attrs.remove('accentPass') ?: 'danger').toString())
    String accentSuper = resolveColor((attrs.remove('accentSuper') ?: 'info').toString())

    String emptyMessage = ((attrs.remove('emptyMessage') ?: "You're all caught up").toString()).encodeAsHTML()

    String bodyContent = body() ?: ''

    log.debug("swipeStack: id=${id}, threshold=${threshold}, rotationFactor=${rotationFactor}, maxVisible=${maxVisible}")

    out << """
<style>
  #${id}.glass-swipe-wrap {
    width          : 100%;
    max-width      : 400px;
    margin         : 0 auto;
    font-family    : 'Outfit', sans-serif;
  }

  #${id} .glass-swipe-viewport {
    position        : relative;
    width            : 100%;
    aspect-ratio     : 3 / 4;
    -webkit-perspective : 1200px;
            perspective : 1200px;
    outline          : none;
  }
  #${id} .glass-swipe-viewport:focus-visible {
    -webkit-box-shadow : 0 0 0 3px ${accentLike};
            box-shadow : 0 0 0 3px ${accentLike};
    -webkit-border-radius : 22px;
            border-radius : 22px;
  }

  #${id} .glass-swipe-card {
    position                : absolute;
    inset                   : 0;
    -webkit-border-radius   : 22px;
            border-radius   : 22px;
    overflow                : hidden;
    background               : rgba(20, 20, 30, 0.55);
    -webkit-backdrop-filter  : blur(14px) saturate(160%);
            backdrop-filter  : blur(14px) saturate(160%);
    border                    : 1px solid rgba(255,255,255,0.14);
    -webkit-box-shadow        : 0 12px 30px rgba(0,0,0,0.35);
            box-shadow        : 0 12px 30px rgba(0,0,0,0.35);
    touch-action              : none;
    -webkit-user-select       : none;
       -moz-user-select       : none;
            user-select       : none;
    -webkit-touch-callout     : none;
    will-change                : transform;
    -webkit-transform-origin   : center bottom;
            transform-origin   : center bottom;
    -webkit-transition          : -webkit-transform 0.35s cubic-bezier(0.22, 0.61, 0.36, 1), opacity 0.35s ease;
            transition          : transform 0.35s cubic-bezier(0.22, 0.61, 0.36, 1), opacity 0.35s ease;
    -webkit-transform : translateY(calc(var(--stack-index, 0) * 12px)) scale(calc(1 - var(--stack-index, 0) * 0.05));
            transform : translateY(calc(var(--stack-index, 0) * 12px)) scale(calc(1 - var(--stack-index, 0) * 0.05));
  }
  @supports not (backdrop-filter: blur(1px)) {
    #${id} .glass-swipe-card { background: rgba(24, 24, 34, 0.92); }
  }
  #${id} .glass-swipe-card.is-dragging {
    -webkit-transition : none;
            transition : none;
    cursor              : grabbing;
  }
  #${id} .glass-swipe-card.is-exiting {
    -webkit-transition : -webkit-transform 0.5s cubic-bezier(0.32, 0, 0.67, 0), opacity 0.5s ease;
            transition : transform 0.5s cubic-bezier(0.32, 0, 0.67, 0), opacity 0.5s ease;
    opacity              : 0;
  }

  #${id} .glass-swipe-card-media {
    position    : absolute;
    inset       : 0;
    width        : 100%;
    height       : 100%;
    object-fit   : cover;
    pointer-events: none;
  }
  #${id} .glass-swipe-card-gradient {
    position   : absolute;
    inset      : 0;
    background : -webkit-linear-gradient(180deg, rgba(0,0,0,0) 45%, rgba(0,0,0,0.82) 100%);
    background : linear-gradient(180deg, rgba(0,0,0,0) 45%, rgba(0,0,0,0.82) 100%);
  }
  #${id} .glass-swipe-card-badge {
    position                : absolute;
    top                     : 14px;
    left                    : 14px;
    padding                 : 4px 10px;
    font-size                : 0.7rem;
    font-weight               : 700;
    letter-spacing            : 0.03em;
    color                      : #fff;
    background                 : rgba(255,255,255,0.22);
    -webkit-backdrop-filter     : blur(6px);
            backdrop-filter     : blur(6px);
    -webkit-border-radius        : 999px;
            border-radius        : 999px;
    border                        : 1px solid rgba(255,255,255,0.3);
  }
  #${id} .glass-swipe-card-info {
    position   : absolute;
    left       : 18px;
    right      : 18px;
    bottom     : 18px;
    color      : #fff;
  }
  #${id} .glass-swipe-card-name {
    margin      : 0 0 2px;
    font-size    : 1.3rem;
    font-weight   : 700;
    text-shadow    : 0 1px 4px rgba(0,0,0,0.4);
  }
  #${id} .glass-swipe-verified {
    display           : inline-flex;
    align-items        : center;
    justify-content     : center;
    width                : 18px;
    height               : 18px;
    font-size             : 0.65rem;
    color                  : #fff;
    background              : ${accentLike};
    -webkit-border-radius     : 50%;
            border-radius     : 50%;
    vertical-align             : middle;
  }
  #${id} .glass-swipe-card-subtitle {
    margin       : 0 0 6px;
    font-size     : 0.9rem;
    opacity        : 0.9;
  }
  #${id} .glass-swipe-card-distance {
    display              : inline-block;
    padding               : 3px 9px;
    font-size              : 0.72rem;
    font-weight              : 600;
    background                : rgba(255,255,255,0.18);
    -webkit-backdrop-filter     : blur(6px);
            backdrop-filter     : blur(6px);
    -webkit-border-radius        : 999px;
            border-radius        : 999px;
  }

  #${id} .glass-swipe-stamp {
    position          : absolute;
    top               : 28px;
    padding            : 6px 14px;
    font-size           : 1.05rem;
    font-weight          : 800;
    letter-spacing        : 0.04em;
    -webkit-border-radius   : 10px;
            border-radius   : 10px;
    border                   : 3px solid;
    opacity                   : 0;
    pointer-events             : none;
  }
  #${id} .glass-swipe-stamp-like  { left: 22px;  color: ${accentLike};  border-color: ${accentLike};  -webkit-transform: rotate(-14deg); transform: rotate(-14deg); }
  #${id} .glass-swipe-stamp-pass  { right: 22px; color: ${accentPass};  border-color: ${accentPass};  -webkit-transform: rotate(14deg);  transform: rotate(14deg); }
  #${id} .glass-swipe-stamp-super { left: 50%; top: 20px; color: ${accentSuper}; border-color: ${accentSuper}; -webkit-transform: translateX(-50%); transform: translateX(-50%); }

  #${id} .glass-swipe-empty {
    position         : absolute;
    inset            : 0;
    display          : -webkit-box; display: -webkit-flex; display: -ms-flexbox; display: flex;
    -webkit-box-orient: vertical; -webkit-box-direction: normal;
    -webkit-flex-direction: column; -ms-flex-direction: column; flex-direction: column;
    -webkit-box-pack: center; -webkit-justify-content: center; -ms-flex-pack: center; justify-content: center;
    -webkit-box-align: center; -webkit-align-items: center; -ms-flex-align: center; align-items: center;
    text-align        : center;
    gap               : 10px;
    color             : rgba(255,255,255,0.85);
    background         : rgba(255,255,255,0.06);
    -webkit-backdrop-filter : blur(10px);
            backdrop-filter : blur(10px);
    border                   : 1px solid rgba(255,255,255,0.14);
    -webkit-border-radius     : 22px;
            border-radius     : 22px;
    padding                    : 24px;
    opacity                     : 0;
    -webkit-transition           : opacity 0.4s ease;
            transition           : opacity 0.4s ease;
  }
  #${id} .glass-swipe-empty.is-visible { opacity: 1; }
  #${id} .glass-swipe-empty-icon { font-size: 2rem; }

  #${id} .glass-swipe-actions {
    display              : -webkit-box; display: -webkit-flex; display: -ms-flexbox; display: flex;
    -webkit-box-pack     : center; -webkit-justify-content: center; -ms-flex-pack: center; justify-content: center;
    -webkit-box-align    : center; -webkit-align-items: center; -ms-flex-align: center; align-items: center;
    gap                   : 18px;
    margin-top             : 18px;
  }
  #${id} .glass-swipe-btn {
    display              : -webkit-box; display: -webkit-flex; display: -ms-flexbox; display: flex;
    -webkit-box-pack     : center; -webkit-justify-content: center; -ms-flex-pack: center; justify-content: center;
    -webkit-box-align    : center; -webkit-align-items: center; -ms-flex-align: center; align-items: center;
    width                 : 52px;
    height                : 52px;
    min-width              : 44px;
    min-height             : 44px;
    -webkit-border-radius    : 50%;
            border-radius    : 50%;
    border                    : 1px solid rgba(255,255,255,0.25);
    background                 : rgba(255,255,255,0.12);
    -webkit-backdrop-filter      : blur(10px);
            backdrop-filter      : blur(10px);
    cursor                        : pointer;
    font-size                     : 1.3rem;
    line-height                    : 1;
    touch-action                    : manipulation;
    -webkit-tap-highlight-color      : transparent;
    -webkit-transition                : -webkit-transform 0.15s ease, background 0.2s ease;
            transition                : transform 0.15s ease, background 0.2s ease;
  }
  #${id} .glass-swipe-btn:active { -webkit-transform: scale(0.92); transform: scale(0.92); }
  #${id} .glass-swipe-btn:focus-visible { outline: 2px solid #fff; outline-offset: 2px; }
  @media (hover: hover) and (pointer: fine) {
    #${id} .glass-swipe-btn:hover { -webkit-transform: translateY(-2px); transform: translateY(-2px); background: rgba(255,255,255,0.2); }
  }
  #${id} .glass-swipe-btn-pass  { color: ${accentPass};  }
  #${id} .glass-swipe-btn-super { color: ${accentSuper}; width: 44px; height: 44px; min-width: 40px; min-height: 40px; font-size: 1.05rem; }
  #${id} .glass-swipe-btn-like  { color: ${accentLike};  }

  @media (prefers-reduced-motion: reduce) {
    #${id} .glass-swipe-card,
    #${id} .glass-swipe-btn,
    #${id} .glass-swipe-empty { -webkit-transition: opacity 0.2s ease; transition: opacity 0.2s ease; }
    #${id} .glass-swipe-card.is-exiting { -webkit-transform: none; transform: none; }
  }
</style>

<div id="${id}" class="glass-swipe-wrap${extra ? ' ' + extra : ''}">
  <div class="glass-swipe-viewport" tabindex="0" data-glass-swipe-viewport role="group" aria-roledescription="card stack" aria-label="Match cards">
    ${bodyContent}
    <div class="glass-swipe-empty" data-glass-swipe-empty>
      <div class="glass-swipe-empty-icon" aria-hidden="true">&#10024;</div>
      <p>${emptyMessage}</p>
    </div>
  </div>
  ${buttons ? """
  <div class="glass-swipe-actions">
    <button type="button" class="glass-swipe-btn glass-swipe-btn-pass" data-glass-swipe-action="pass" aria-label="Pass">&#10005;</button>
    <button type="button" class="glass-swipe-btn glass-swipe-btn-super" data-glass-swipe-action="super" aria-label="Super like">&#9733;</button>
    <button type="button" class="glass-swipe-btn glass-swipe-btn-like" data-glass-swipe-action="like" aria-label="Like">&#10084;</button>
  </div>
  """ : ''}
  <span data-glass-swipe-status style="position:absolute;width:1px;height:1px;overflow:hidden;clip:rect(0 0 0 0);white-space:nowrap;" aria-live="polite"></span>
</div>

<script>
(function () {
  var THRESHOLD = ${threshold};
  var ROTATION_FACTOR = ${rotationFactor};
  var MAX_VISIBLE = ${maxVisible};
  var INDICATORS = ${indicators};
  var HAPTICS = ${haptics};
  var CB_LEFT = ${onSwipeLeft ? "'" + onSwipeLeft + "'" : 'null'};
  var CB_RIGHT = ${onSwipeRight ? "'" + onSwipeRight + "'" : 'null'};
  var CB_UP = ${onSwipeUp ? "'" + onSwipeUp + "'" : 'null'};
  var STACK_ID = '${id}';

  // ── Shared global API: window.glassSwipeStack — defined once, every
  // stack instance on the page just registers its own context. ─────────────
  window.glassSwipeStack = window.glassSwipeStack || (function () {
    var registry = {};

    function act(id, action) {
      var ctx = registry[id];
      if (!ctx) { if (window.console) console.warn('glassSwipeStack: no stack found for id "' + id + '"'); return; }
      ctx.triggerAction(action);
    }

    return {
      _register: function (id, ctx) { registry[id] = ctx; },
      like: function (id) { act(id, 'like'); },
      pass: function (id) { act(id, 'pass'); },
      superLike: function (id) { act(id, 'super'); },
      reset: function (id) { var ctx = registry[id]; if (ctx) ctx.reset(); },
      remaining: function (id) { var ctx = registry[id]; return ctx ? ctx.remaining() : 0; }
    };
  })();

  var wrap = document.getElementById(STACK_ID);
  if (!wrap) return;
  var viewport = wrap.querySelector('[data-glass-swipe-viewport]');
  var emptyEl = wrap.querySelector('[data-glass-swipe-empty]');
  var statusEl = wrap.querySelector('[data-glass-swipe-status]');
  var allCards = Array.prototype.slice.call(wrap.querySelectorAll('[data-glass-card]'));
  var activeCards = allCards.slice();
  var supportsPointer = !!window.PointerEvent;
  var supportsVibrate = HAPTICS && typeof navigator.vibrate === 'function';

  function announce(text) { if (statusEl) statusEl.textContent = text; }

  function vibrate(ms) { if (supportsVibrate) { try { navigator.vibrate(ms); } catch (err) {} } }

  function layout() {
    activeCards.forEach(function (card, i) {
      card.style.setProperty('--stack-index', String(i));
      card.style.zIndex = String(activeCards.length - i);
      card.style.display = i < MAX_VISIBLE ? '' : 'none';
      card.style.pointerEvents = i === 0 ? 'auto' : 'none';
    });
    var isEmpty = activeCards.length === 0;
    if (emptyEl) {
      if (isEmpty) { emptyEl.removeAttribute('hidden'); requestAnimationFrame(function () { emptyEl.classList.add('is-visible'); }); }
      else { emptyEl.classList.remove('is-visible'); emptyEl.setAttribute('hidden', ''); }
    }
  }

  function topCard() { return activeCards[0] || null; }

  function setStamps(card, dx, dy) {
    if (!INDICATORS) return;
    var likeStamp = card.querySelector('.glass-swipe-stamp-like');
    var passStamp = card.querySelector('.glass-swipe-stamp-pass');
    var superStamp = card.querySelector('.glass-swipe-stamp-super');
    var hOpacity = Math.min(1, Math.abs(dx) / THRESHOLD);
    var vOpacity = Math.min(1, Math.max(0, -dy) / THRESHOLD);
    if (likeStamp) likeStamp.style.opacity = dx > 0 ? String(hOpacity) : '0';
    if (passStamp) passStamp.style.opacity = dx < 0 ? String(hOpacity) : '0';
    if (superStamp) superStamp.style.opacity = (dy < -20 && Math.abs(dx) < THRESHOLD * 0.6) ? String(vOpacity) : '0';
  }

  function resetStamps(card) {
    ['like', 'pass', 'super'].forEach(function (kind) {
      var el = card.querySelector('.glass-swipe-stamp-' + kind);
      if (el) el.style.opacity = '0';
    });
  }

  function runCallback(name, cardId, cardEl) {
    if (name && typeof window[name] === 'function') {
      try { window[name](cardId, cardEl); } catch (err) { if (window.console) console.error('glassSwipeStack callback error:', err); }
    }
  }

  function commit(card, direction) {
    var cardId = card.getAttribute('data-card-id');
    var flyX = direction === 'left' ? -600 : direction === 'right' ? 600 : 0;
    var flyY = direction === 'up' ? -700 : direction === 'left' || direction === 'right' ? -80 : 0;
    var rotate = direction === 'left' ? -28 : direction === 'right' ? 28 : 0;

    card.classList.add('is-exiting');
    card.style.transform = 'translate(' + flyX + 'px,' + flyY + 'px) rotate(' + rotate + 'deg)';

    var idx = activeCards.indexOf(card);
    if (idx > -1) activeCards.splice(idx, 1);
    layout();

    vibrate(direction === 'super' || direction === 'up' ? [15, 40, 15] : 25);

    if (direction === 'left') { runCallback(CB_LEFT, cardId, card); announce('Passed'); }
    else if (direction === 'right') { runCallback(CB_RIGHT, cardId, card); announce('Liked'); }
    else if (direction === 'up') { runCallback(CB_UP, cardId, card); announce('Super liked'); }
  }

  function snapBack(card) {
    card.classList.remove('is-dragging');
    card.style.transform = '';
    resetStamps(card);
  }

  function triggerAction(action) {
    var card = topCard();
    if (!card) return;
    var direction = action === 'like' ? 'right' : action === 'pass' ? 'left' : 'up';
    commit(card, direction);
  }

  function reset() {
    activeCards = allCards.slice();
    allCards.forEach(function (card) {
      card.classList.remove('is-exiting', 'is-dragging');
      card.style.transform = '';
      card.style.display = '';
      resetStamps(card);
    });
    layout();
    announce('Stack reset');
  }

  function remaining() { return activeCards.length; }

  // ── Drag handling (Pointer Events primary, touch/mouse fallback) ────────
  function bindDrag(card) {
    var dragging = false, startX = 0, startY = 0, curX = 0, curY = 0, startTime = 0, pointerId = null;

    function xy(e) {
      if (e.touches && e.touches.length) return { x: e.touches[0].clientX, y: e.touches[0].clientY };
      if (e.changedTouches && e.changedTouches.length) return { x: e.changedTouches[0].clientX, y: e.changedTouches[0].clientY };
      return { x: e.clientX, y: e.clientY };
    }

    function onDown(e) {
      if (card !== topCard()) return;
      dragging = true;
      var p = xy(e);
      startX = p.x; startY = p.y; curX = startX; curY = startY; startTime = Date.now();
      card.classList.add('is-dragging');
      if (supportsPointer && e.pointerId != null && card.setPointerCapture) {
        pointerId = e.pointerId;
        try { card.setPointerCapture(pointerId); } catch (err) {}
      }
    }

    function onMove(e) {
      if (!dragging) return;
      var p = xy(e);
      curX = p.x; curY = p.y;
      var dx = curX - startX, dy = curY - startY;
      card.style.transform = 'translate(' + dx + 'px,' + dy + 'px) rotate(' + (dx * ROTATION_FACTOR) + 'deg)';
      setStamps(card, dx, dy);
      if (e.cancelable) e.preventDefault();
    }

    function onUp() {
      if (!dragging) return;
      dragging = false;
      var dx = curX - startX, dy = curY - startY;
      var dt = Math.max(1, Date.now() - startTime);
      var velocity = dx / dt;
      var flungHorizontal = Math.abs(velocity) > 0.55 && Math.abs(dx) > 24;

      if (dy < -THRESHOLD * 1.1 && Math.abs(dx) < THRESHOLD) {
        commit(card, 'up');
      } else if (Math.abs(dx) > THRESHOLD || flungHorizontal) {
        commit(card, dx > 0 ? 'right' : 'left');
      } else {
        snapBack(card);
      }
      if (pointerId != null && card.releasePointerCapture) {
        try { card.releasePointerCapture(pointerId); } catch (err) {}
        pointerId = null;
      }
    }

    if (supportsPointer) {
      card.addEventListener('pointerdown', onDown);
      card.addEventListener('pointermove', onMove);
      card.addEventListener('pointerup', onUp);
      card.addEventListener('pointercancel', onUp);
    } else {
      card.addEventListener('touchstart', onDown, { passive: true });
      card.addEventListener('touchmove', onMove, { passive: false });
      card.addEventListener('touchend', onUp, { passive: true });
      card.addEventListener('touchcancel', onUp, { passive: true });
      card.addEventListener('mousedown', onDown);
      document.addEventListener('mousemove', onMove);
      document.addEventListener('mouseup', onUp);
    }
  }

  allCards.forEach(bindDrag);
  layout();

  if (viewport) {
    viewport.addEventListener('keydown', function (e) {
      if (e.key === 'ArrowLeft') { e.preventDefault(); triggerAction('pass'); }
      else if (e.key === 'ArrowRight') { e.preventDefault(); triggerAction('like'); }
      else if (e.key === 'ArrowUp') { e.preventDefault(); triggerAction('super'); }
    });
  }

  wrap.querySelectorAll('[data-glass-swipe-action]').forEach(function (btn) {
    btn.addEventListener('click', function () { triggerAction(btn.getAttribute('data-glass-swipe-action')); });
  });

  window.glassSwipeStack._register(STACK_ID, {
    triggerAction: triggerAction,
    reset: reset,
    remaining: remaining
  });
})();
</script>
"""
  }
}
