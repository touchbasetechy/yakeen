package com.murad.app.matrimony.slider

/**
 * <glass:sliderAssets/> — shared CSS + JS engine for every glass "slider"
 * component. GlassRangeSliderTagLib and GlassWeightRangeSliderTagLib used
 * to each carry a full, nearly-identical copy of this: same card/track/
 * thumb/bubble CSS (just different class prefixes), and the same drag /
 * keyboard / ARIA / resize / render JS (just different value-formatting).
 * That's all been pulled out into this one file. Nothing in here knows
 * what a "range" or "weight" slider even means — it only knows how to
 * drag a thumb along a track and report the result. Kind-specific
 * behavior (plain numeric formatting vs. kg/lb conversion + unit toggle)
 * stays in each kind's own, now much smaller, taglib.
 *
 * CSS: one class set — `.gs-slider`, `.gs-slider-header`,
 * `.gs-slider-label-group`, `.gs-slider-label`, `.gs-slider-readout`,
 * `.gs-slider-unit-toggle` / `.gs-slider-unit-btn` (rendered by the
 * "weight" kind only, but defined once here rather than forking a second
 * copy), `.gs-slider-body`, `.gs-slider-track`, `.gs-slider-fill`,
 * `.gs-slider-thumb`, `.gs-bubble`. Every color/opacity value is still
 * copied 1:1 from GlassmorphicRadioTagLib's `.radio-glass` /
 * `.container-glass` rules, same as before — see that taglib for the
 * derivation table; it isn't repeated per-file anymore either.
 *
 * JS: `window.GlassSlider`, exposing:
 *   - clamp / roundToStep / makeFormatter / supportsHaptics — small utils
 *   - KG_TO_LB / CM_PER_INCH  — kept here so they aren't magic numbers
 *                                duplicated in the weight/height kinds' JS
 *   - Engine                  — the actual drag/keyboard/ARIA/resize/
 *                                render state machine. Takes a root
 *                                element plus `{ min, max, step, dual,
 *                                disabled, haptics, formatText(which,
 *                                value), eventName, buildDetail(engine) }`
 *                                — those four callbacks/strings are the
 *                                ENTIRE surface a kind has to supply.
 *   - registerKind(name, factory) — a kind registers `root -> Engine`
 *   - init(scope)              — wires up every not-yet-initialized
 *                                `[data-gs-slider]` element in `scope` by
 *                                dispatching to its `data-gs-kind`'s
 *                                registered factory
 *
 * BROWSER SUPPORT — every visual/interaction feature here has an explicit
 * fallback, not a silent degradation:
 *   - backdrop-filter blur      -> @supports not(...) swaps in a solid
 *                                  translucent card background
 *   - prefers-reduced-transparency (Safari 16.4+, growing elsewhere)
 *                               -> forces the same solid-background
 *                                  fallback for users who've asked their
 *                                  OS to reduce transparency, independent
 *                                  of whether the browser *can* blur
 *   - :has() drag-glow selector (universal since ~2023, but not in every
 *                                  browser still in the field) -> Engine
 *                                  also toggles a `.gs-active` class on
 *                                  the root while a thumb is mid-drag, so
 *                                  the same glow renders even without
 *                                  :has() support
 *   - container queries          -> paired with an equivalent min-width
 *                                  media query fallback
 *   - Pointer Events             -> unifies mouse/touch/pen with no
 *                                  separate touch/mouse code paths;
 *                                  supported by every evergreen browser
 *   - forced-colors / prefers-contrast / prefers-reduced-motion — all
 *     have dedicated rules, not just "hope the defaults are fine"
 * Touch targets: the visible thumb can be as small as 26px, but
 * `.gs-slider-thumb::before` pads its actual hit area out to 44px in
 * every direction — meets WCAG 2.5.5 (AAA) target size, not just the
 * 24px AA minimum.
 *
 * NOTE for dynamically-inserted sliders (an AJAX-fetched GSP fragment
 * spliced in via innerHTML after initial page load): browsers never
 * execute <script> tags that arrive via innerHTML, so call
 * `GlassSlider.init(containerEl)` manually once the fragment is attached.
 * One call now initializes every kind found in that container — before
 * this refactor you'd have needed `GsRangeSlider.init(...)` AND
 * `GsWeightRangeSlider.init(...)` separately.
 *
 * Both kind taglibs call this one's methods directly as static calls —
 * `GlassSliderCommonTagLib.injectAssets(request, out)`,
 * `GlassSliderCommonTagLib.toBigDecimal(...)`, etc. — rather than being
 * injected as a Spring-wired property. See injectAssets()'s doc comment
 * below for why: taglib-to-taglib property injection isn't reliable in
 * Grails, and static calls sidestep the problem entirely by needing no
 * bean lookup at all.
 */
class GlassSliderCommonTagLib {

  static namespace = 'glass'
  static encodeAsForTags = [sliderAssets: 'raw']

  static final String REQUEST_FLAG = 'glassSliderCommonAssetsInjected'

  /** 1 kg in lb. Lives here — not in the weight kind — since it's a
   *  plain unit-conversion fact, not slider behavior. The JS engine
   *  below exposes the same value as `GlassSlider.KG_TO_LB` so neither
   *  language has to duplicate the digits more than once. */
  static final BigDecimal KG_TO_LB = new BigDecimal('2.2046226218')

  /** cm per inch — exact by definition (international inch, 1959). Shared
   *  here for the same reason as KG_TO_LB: one definition, used by both
   *  the "height" kind's SSR formatting and its client-side JS. */
  static final BigDecimal CM_PER_INCH = new BigDecimal('2.54')

  private static final String CSS = '''
/*
 * gs-slider-core.css — shared by every glass slider kind.
 *
 * Every color/opacity value below is pulled directly from
 * GlassmorphicRadioTagLib's .radio-glass / .container-glass rules:
 *
 *   base glass background      rgba(255,255,255,0.10)   <- label background
 *   hover glass background      rgba(255,255,255,0.15)   <- label:hover background
 *   active/checked background   rgba(255,255,255,0.25)   <- input:checked + label
 *   base border          rgba(255,255,255,0.20)   <- label border
 *   hover border         rgba(255,255,255,0.30)   <- label:hover border
 *   active border         rgba(255,255,255,0.70), 2px  <- checked border
 *   base shadow          0 8px 32px rgba(0,0,0,0.10)
 *   hover shadow          0 12px 40px rgba(0,0,0,0.20)
 *   active shadow          0 12px 40px rgba(255,255,255,0.10),
 *                          inset 0 0 0 1px rgba(255,255,255,0.30)
 *   text color            white                     <- .radio-glass label color
 *   text shadow (headings) 0 2px 10px rgba(0,0,0,0.20)  <- .container-glass h2
 *   focus ring            2px solid rgba(255,255,255,0.50), offset 3px
 *   disabled              opacity .42, grayscale(35%),
 *                          border rgba(255,255,255,0.12),
 *                          background rgba(255,255,255,0.05)
 *   blur                  10px (matches backdrop-filter: blur(10px))
 *   card radius            20px (matches .container-glass)
 *
 * --gs-border, --gs-border-hover, --gs-border-active, --gs-text,
 * --gs-text-shadow, --gs-focus-ring and --gs-disabled-border are fixed to
 * the radio taglib's literal values and are NOT overridden by theme below
 * — that taglib has no light-mode palette, so this component doesn't
 * invent one for those properties either. Only background/shadow may
 * lighten per-theme for legibility on light pages.
 */

.gs-slider {
  container-type: inline-size;
  container-name: gs-slider;

  --gs-track-h: 6px;
  --gs-thumb-d: clamp(26px, 7vw, 32px);
  --gs-radius: 20px;

  --gs-background: rgba(255, 255, 255, 0.10);
  --gs-background-hover: rgba(255, 255, 255, 0.15);
  --gs-background-active: rgba(255, 255, 255, 0.25);

  --gs-border: rgba(255, 255, 255, 0.20);
  --gs-border-hover: rgba(255, 255, 255, 0.30);
  --gs-border-active: rgba(255, 255, 255, 0.70);
  --gs-text: #ffffff;
  --gs-text-shadow: rgba(0, 0, 0, 0.20);
  --gs-focus-ring: rgba(255, 255, 255, 0.50);
  --gs-disabled-border: rgba(255, 255, 255, 0.12);

  --gs-shadow: rgba(0, 0, 0, 0.10);
  --gs-shadow-hover: rgba(0, 0, 0, 0.20);
  --gs-shadow-active: rgba(255, 255, 255, 0.10);
  --gs-shadow-active-inset: rgba(255, 255, 255, 0.30);
  --gs-text-muted: rgba(255, 255, 255, 0.9);
  --gs-disabled-background: rgba(255, 255, 255, 0.05);
  --gs-bubble-shift: 0px;

  position: relative;
  width: 100%;
  max-width: 100%;
  box-sizing: border-box;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Inter, sans-serif;
  padding-block: clamp(16px, 5vw, 20px) clamp(20px, 6vw, 26px);
  padding-inline: clamp(14px, 4vw, 18px);
  border-radius: var(--gs-radius);
  color: var(--gs-text);
  background: var(--gs-background);
  border: 1px solid var(--gs-border);
  box-shadow: 0 8px 32px var(--gs-shadow);
  -webkit-backdrop-filter: blur(10px);
  backdrop-filter: blur(10px);
  -webkit-tap-highlight-color: transparent;
  transition: background 0.4s ease, border-color 0.4s ease, box-shadow 0.4s ease, transform 0.4s ease;
  isolation: isolate;
}

.gs-slider:hover:not([data-disabled="true"]) {
  background: var(--gs-background-hover);
  border-color: var(--gs-border-hover);
  box-shadow: 0 12px 40px var(--gs-shadow-hover);
}

/* "Active" reads here as: a thumb is currently being dragged. :has()
   covers this in every current browser; .gs-active is the same rule
   keyed off a plain class that Engine also toggles in JS, so browsers
   without :has() support still get the effect instead of losing it. */
.gs-slider:has(.gs-slider-thumb.gs-dragging),
.gs-slider.gs-active {
  background: var(--gs-background-active);
  border-color: var(--gs-border-active);
  border-width: 2px;
  box-shadow:
    0 12px 40px var(--gs-shadow-active),
    inset 0 0 0 1px var(--gs-shadow-active-inset);
}

.gs-slider[data-theme="light"] {
  --gs-background: rgba(15, 23, 42, 0.06);
  --gs-background-hover: rgba(15, 23, 42, 0.09);
  --gs-background-active: rgba(15, 23, 42, 0.14);
  --gs-shadow: rgba(15, 23, 42, 0.08);
  --gs-shadow-hover: rgba(15, 23, 42, 0.14);
  --gs-shadow-active: rgba(15, 23, 42, 0.08);
  --gs-shadow-active-inset: rgba(15, 23, 42, 0.22);
}

@media (prefers-color-scheme: light) {
  .gs-slider[data-theme="auto"] {
    --gs-background: rgba(15, 23, 42, 0.06);
    --gs-background-hover: rgba(15, 23, 42, 0.09);
    --gs-background-active: rgba(15, 23, 42, 0.14);
    --gs-shadow: rgba(15, 23, 42, 0.08);
    --gs-shadow-hover: rgba(15, 23, 42, 0.14);
    --gs-shadow-active: rgba(15, 23, 42, 0.08);
    --gs-shadow-active-inset: rgba(15, 23, 42, 0.22);
  }
}

@supports not ((backdrop-filter: blur(1px)) or (-webkit-backdrop-filter: blur(1px))) {
  .gs-slider { background: rgba(20, 20, 30, 0.82); }
}

/* Same solid fallback for users who've asked their OS to reduce
   transparency (Safari 16.4+; a no-op elsewhere) — a browser that CAN
   blur should still honor "please don't show me see-through UI". */
@media (prefers-reduced-transparency: reduce) {
  .gs-slider {
    background: rgba(20, 20, 30, 0.82);
    -webkit-backdrop-filter: none;
    backdrop-filter: none;
  }
}

.gs-slider[data-disabled="true"] {
  opacity: 0.42;
  cursor: not-allowed;
  pointer-events: none;
  -webkit-filter: grayscale(35%);
  filter: grayscale(35%);
  border-color: var(--gs-disabled-border) !important;
  background: var(--gs-disabled-background) !important;
  box-shadow: none !important;
  transform: none !important;
}

.gs-slider-header {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 10px;
  margin-block-end: clamp(18px, 5vw, 22px);
  flex-wrap: wrap;
}

.gs-slider-label-group {
  display: flex;
  align-items: baseline;
  gap: 8px;
  flex-wrap: wrap;
}

.gs-slider-label {
  font-size: clamp(12.5px, 3.4vw, 14px);
  font-weight: 600;
  letter-spacing: 0.01em;
  color: var(--gs-text);
  text-shadow: 0 2px 10px var(--gs-text-shadow);
}

.gs-slider-readout {
  font-size: clamp(12px, 3.2vw, 13px);
  font-weight: 700;
  color: var(--gs-text-muted);
  font-variant-numeric: tabular-nums;
  white-space: nowrap;
}

/* Unit toggle — only ever rendered by the "weight" kind, but defined once
   here so the "range" kind never has to know it exists. Same two-state
   look as a checked/unchecked radio: active segment gets the
   checked-state background/border, inactive segment sits at resting opacity. */
.gs-slider-unit-toggle {
  display: inline-flex;
  border-radius: 999px;
  padding: 2px;
  background: var(--gs-background-hover);
  border: 1px solid var(--gs-border);
  flex-shrink: 0;
}

.gs-slider-unit-btn {
  appearance: none;
  -webkit-appearance: none;
  border: 0;
  background: transparent;
  color: var(--gs-text);
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.03em;
  text-transform: uppercase;
  padding: 4px 10px;
  border-radius: 999px;
  cursor: pointer;
  transition: background 0.25s ease, box-shadow 0.25s ease, opacity 0.25s ease;
  opacity: 0.65;
  -webkit-tap-highlight-color: transparent;
  -webkit-user-select: none;
  user-select: none;
  touch-action: manipulation;
}

.gs-slider-unit-btn:hover { opacity: 0.9; }

.gs-slider-unit-btn[aria-pressed="true"] {
  background: var(--gs-background-active);
  box-shadow: inset 0 0 0 1px var(--gs-border-active);
  opacity: 1;
}

.gs-slider-unit-btn:focus-visible {
  outline: 2px solid var(--gs-focus-ring);
  outline-offset: 2px;
}

.gs-slider[data-disabled="true"] .gs-slider-unit-btn { pointer-events: none; }

.gs-slider-body {
  position: relative;
  z-index: 1;
  padding-inline: 2px;
}

.gs-slider-track {
  position: relative;
  width: 100%;
  height: var(--gs-track-h);
  border-radius: 999px;
  background: var(--gs-background-hover);
  box-shadow: inset 0 1px 2px var(--gs-shadow-hover);
  touch-action: none;
}

.gs-slider-fill {
  position: absolute;
  inset-block: 0;
  inset-inline-start: 0;
  width: 0%;
  border-radius: 999px;
  background: var(--gs-border-active);
  box-shadow: 0 0 12px 0 var(--gs-shadow-active);
  pointer-events: none;
}

.gs-slider-thumb {
  appearance: none;
  -webkit-appearance: none;
  position: absolute;
  top: 50%;
  inset-inline-start: 0%;
  width: var(--gs-thumb-d);
  height: var(--gs-thumb-d);
  margin-inline-start: calc(var(--gs-thumb-d) / -2);
  margin-block-start: calc(var(--gs-thumb-d) / -2);
  border: 0;
  border-radius: 50%;
  background: var(--gs-text);
  box-shadow: 0 8px 32px var(--gs-shadow);
  cursor: grab;
  touch-action: none;
  -webkit-user-select: none;
  user-select: none;
  -webkit-tap-highlight-color: transparent;
  transition: transform 0.4s ease, box-shadow 0.4s ease;
  z-index: 2;
}

.gs-slider-thumb::before {
  content: "";
  position: absolute;
  inset: calc((var(--gs-thumb-d) - 44px) / 2 * -1);
}

.gs-slider-thumb:hover {
  box-shadow: 0 12px 40px var(--gs-shadow-hover);
  transform: scale(1.08);
}

.gs-slider-thumb:active,
.gs-slider-thumb.gs-dragging {
  cursor: grabbing;
  transform: scale(1.18);
  box-shadow:
    0 12px 40px var(--gs-shadow-active),
    inset 0 0 0 1px var(--gs-shadow-active-inset);
}

.gs-slider-thumb:focus-visible {
  outline: 2px solid var(--gs-focus-ring);
  outline-offset: 3px;
}

.gs-bubble {
  position: absolute;
  bottom: calc(100% + 10px);
  left: 50%;
  transform: translateX(calc(-50% + var(--gs-bubble-shift))) translateY(4px) scale(0.85);
  background: rgba(0, 0, 0, 0.75);
  color: #ffffff;
  font-size: 11.5px;
  font-weight: 700;
  padding: 5px 9px;
  border-radius: 8px;
  white-space: nowrap;
  opacity: 0;
  pointer-events: none;
  transition: opacity 0.15s ease, transform 0.15s ease;
  font-variant-numeric: tabular-nums;
}

.gs-bubble::after {
  content: "";
  position: absolute;
  top: 100%;
  left: calc(50% - var(--gs-bubble-shift));
  transform: translateX(-50%);
  border: 5px solid transparent;
  border-top-color: rgba(0, 0, 0, 0.75);
}

.gs-slider-thumb.gs-dragging .gs-bubble,
.gs-slider-thumb:focus-visible .gs-bubble {
  opacity: 1;
  transform: translateX(calc(-50% + var(--gs-bubble-shift))) translateY(0) scale(1);
}

@media (prefers-reduced-motion: reduce) {
  .gs-slider,
  .gs-slider-thumb,
  .gs-bubble,
  .gs-slider-fill,
  .gs-slider-unit-btn {
    transition: none !important;
  }
}

@media (prefers-contrast: more) {
  .gs-slider { border-width: 1.5px; }
  .gs-slider-thumb { box-shadow: 0 0 0 2px var(--gs-text), 0 2px 8px var(--gs-shadow); }
}

@media (forced-colors: active) {
  .gs-slider { forced-color-adjust: none; background: Canvas; border: 1px solid CanvasText; }
  .gs-slider-track { background: Canvas; border: 1px solid CanvasText; }
  .gs-slider-fill { background: Highlight; }
  .gs-slider-thumb { background: ButtonFace; border: 2px solid ButtonText; box-shadow: none; }
  .gs-slider-thumb:focus-visible { outline: 2px solid Highlight; outline-offset: 2px; }
  .gs-slider-unit-btn[aria-pressed="true"] { background: Highlight; }
}

@container gs-slider (max-width: 260px) {
  .gs-slider { --gs-thumb-d: 24px; }
  .gs-slider-header { margin-block-end: 16px; }
}

@media (min-width: 768px) and (pointer: fine) {
  .gs-slider { --gs-thumb-d: 22px; }
}

'''

  private static final String JS = '''

/*
 * gs-slider-core.js — shared by every glass slider kind. Zero dependencies.
 *
 * Engine owns everything that is identical across kinds: Pointer-Events
 * drag handling (mouse/touch/pen through one path), rAF-batched drag
 * updates, keyboard stepping (arrows/Home/End/PageUp/PageDown), RTL via
 * logical properties, ResizeObserver-driven re-render, hidden-input sync,
 * and keeping the value bubble on-screen. A "kind" (see
 * GlassRangeSliderTagLib / GlassWeightRangeSliderTagLib) supplies only:
 *   - formatText(which, rawValue) -> string shown in the readout/bubble
 *   - eventName                    -> CustomEvent name to dispatch
 *   - buildDetail(engine)          -> that event's detail payload
 * and registers itself once via GlassSlider.registerKind(name, factory).
 */
(function () {
  'use strict';

  function clamp(n, lo, hi) {
    return Math.min(hi, Math.max(lo, n));
  }

  function roundToStep(value, min, step) {
    var steps = Math.round((value - min) / step);
    var snapped = min + steps * step;
    var decimals = (String(step).split('.')[1] || '').length;
    return parseFloat(snapped.toFixed(decimals));
  }

  function makeFormatter(locale, maxDecimals) {
    if (typeof Intl === 'undefined' || typeof Intl.NumberFormat !== 'function') {
      return function (v) { return v.toFixed(maxDecimals); };
    }
    try {
      var nf = new Intl.NumberFormat(locale || undefined, { maximumFractionDigits: maxDecimals, minimumFractionDigits: 0 });
      return function (v) { return nf.format(v); };
    } catch (err) {
      return function (v) { return v.toFixed(maxDecimals); };
    }
  }

  function supportsHaptics() {
    return typeof navigator !== 'undefined' && typeof navigator.vibrate === 'function';
  }

  function Engine(root, opts) {
    this.root = root;
    this.opts = opts;

    this.track = root.querySelector('[data-gs-track]');
    this.fill = root.querySelector('[data-gs-fill]');
    this.readout = root.querySelector('[data-gs-readout]');
    this.minThumb = root.querySelector('[data-gs-thumb="min"]');
    this.maxThumb = root.querySelector('[data-gs-thumb="max"]');
    this.minInput = root.querySelector('input[data-gs-input="min"]');
    this.maxInput = root.querySelector('input[data-gs-input="max"]');

    this.min = opts.min;
    this.max = opts.max;
    this.step = opts.step;
    this.dual = opts.dual;
    this.disabled = opts.disabled;
    this.haptics = !!opts.haptics && supportsHaptics();

    this.valueMin = parseFloat(this.minInput.value);
    this.valueMax = this.dual ? parseFloat(this.maxInput.value) : this.max;

    this._pendingClientX = null;
    this._rafId = null;
    this._lastHapticValue = null;
    this._dragTarget = null;

    if (!this.disabled) this._bind();
    this.render();
  }

  Engine.prototype._isRTL = function () {
    return window.getComputedStyle(this.root).direction === 'rtl';
  };

  Engine.prototype._haptic = function (value) {
    if (!this.haptics || value === this._lastHapticValue) return;
    this._lastHapticValue = value;
    try { navigator.vibrate(4); } catch (err) { /* no-op */ }
  };

  Engine.prototype._bind = function () {
    var self = this;

    ['min', 'max'].forEach(function (which) {
      var thumb = which === 'min' ? self.minThumb : self.maxThumb;
      if (!thumb) return;

      thumb.addEventListener('pointerdown', function (e) {
        e.preventDefault();
        self._dragTarget = which;
        thumb.classList.add('gs-dragging');
        // .gs-active is the plain-class twin of the :has(.gs-dragging) CSS
        // selector — kept in sync here so the same "card glow while
        // dragging" effect still renders on browsers without :has().
        self.root.classList.add('gs-active');
        thumb.setPointerCapture(e.pointerId);
        thumb.focus();
        self._haptic(which === 'min' ? self.valueMin : self.valueMax);
      });

      thumb.addEventListener('pointermove', function (e) {
        if (self._dragTarget !== which) return;
        self._scheduleUpdate(which, e.clientX);
      });

      function release() {
        if (self._dragTarget !== which) return;
        thumb.classList.remove('gs-dragging');
        self.root.classList.remove('gs-active');
        self._dragTarget = null;
        self.emitChange();
      }
      thumb.addEventListener('pointerup', release);
      thumb.addEventListener('pointercancel', release);

      thumb.addEventListener('keydown', function (e) {
        var delta = 0;
        if (e.key === 'ArrowRight' || e.key === 'ArrowUp') delta = self.step;
        else if (e.key === 'ArrowLeft' || e.key === 'ArrowDown') delta = -self.step;
        else if (e.key === 'PageUp') delta = self.step * 10;
        else if (e.key === 'PageDown') delta = -self.step * 10;
        else if (e.key === 'Home') { self._setValue(which, self.min); self.emitChange(); e.preventDefault(); return; }
        else if (e.key === 'End') { self._setValue(which, self.max); self.emitChange(); e.preventDefault(); return; }
        else return;

        e.preventDefault();
        var current = which === 'min' ? self.valueMin : self.valueMax;
        self._setValue(which, current + delta);
        self._haptic(which === 'min' ? self.valueMin : self.valueMax);
        self.emitChange();
      });
    });

    this.track.addEventListener('pointerdown', function (e) {
      if (e.target.closest('[data-gs-thumb]')) return;
      var which = self.dual
        ? (self._distanceTo('min', e.clientX) <= self._distanceTo('max', e.clientX) ? 'min' : 'max')
        : 'min';
      self._updateFromClientX(which, e.clientX);
      self._haptic(which === 'min' ? self.valueMin : self.valueMax);
      self.emitChange();
    });

    if (typeof ResizeObserver === 'function') {
      this._resizeObserver = new ResizeObserver(function () { self.render(); });
      this._resizeObserver.observe(this.root);
    } else {
      window.addEventListener('resize', function () { self.render(); }, { passive: true });
    }
  };

  Engine.prototype._distanceTo = function (which, clientX) {
    var thumb = which === 'min' ? this.minThumb : this.maxThumb;
    var rect = thumb.getBoundingClientRect();
    return Math.abs(clientX - (rect.left + rect.width / 2));
  };

  Engine.prototype._scheduleUpdate = function (which, clientX) {
    var self = this;
    this._pendingClientX = clientX;
    if (this._rafId !== null) return;
    this._rafId = requestAnimationFrame(function () {
      self._rafId = null;
      self._updateFromClientX(which, self._pendingClientX);
    });
  };

  Engine.prototype._updateFromClientX = function (which, clientX) {
    var rect = this.track.getBoundingClientRect();
    var isRTL = this._isRTL();
    var pct = isRTL
      ? clamp((rect.right - clientX) / rect.width, 0, 1)
      : clamp((clientX - rect.left) / rect.width, 0, 1);
    var raw = this.min + pct * (this.max - this.min);
    var newValue = roundToStep(raw, this.min, this.step);
    var prev = which === 'min' ? this.valueMin : this.valueMax;
    this._setValue(which, newValue);
    if (newValue !== prev) this._haptic(newValue);
  };

  Engine.prototype._setValue = function (which, value) {
    value = clamp(value, this.min, this.max);

    if (this.dual) {
      if (which === 'min') {
        value = Math.min(value, this.valueMax);
        this.valueMin = value;
      } else {
        value = Math.max(value, this.valueMin);
        this.valueMax = value;
      }
    } else {
      this.valueMin = value;
    }

    this.render();
  };

  Engine.prototype._keepBubbleOnScreen = function (thumb) {
    var bubble = thumb.querySelector('[data-gs-bubble]');
    if (!bubble) return;
    thumb.style.setProperty('--gs-bubble-shift', '0px');
    var rect = bubble.getBoundingClientRect();
    var margin = 8;
    var shift = 0;
    if (rect.left < margin) {
      shift = margin - rect.left;
    } else if (rect.right > window.innerWidth - margin) {
      shift = (window.innerWidth - margin) - rect.right;
    }
    if (shift !== 0) {
      thumb.style.setProperty('--gs-bubble-shift', shift + 'px');
    }
  };

  Engine.prototype.render = function () {
    var range = this.max - this.min || 1;
    var pctMin = ((this.valueMin - this.min) / range) * 100;
    var pctMax = this.dual ? ((this.valueMax - this.min) / range) * 100 : pctMin;

    var minText = this.opts.formatText('min', this.valueMin);
    this.minThumb.style.insetInlineStart = pctMin + '%';
    this.minThumb.setAttribute('aria-valuenow', this.valueMin);
    this.minThumb.setAttribute('aria-valuetext', minText);
    var minBubble = this.minThumb.querySelector('[data-gs-bubble]');
    if (minBubble) minBubble.textContent = minText;
    this.minInput.value = this.valueMin;

    if (this.dual) {
      var maxText = this.opts.formatText('max', this.valueMax);
      this.maxThumb.style.insetInlineStart = pctMax + '%';
      this.maxThumb.setAttribute('aria-valuenow', this.valueMax);
      this.maxThumb.setAttribute('aria-valuetext', maxText);
      var maxBubble = this.maxThumb.querySelector('[data-gs-bubble]');
      if (maxBubble) maxBubble.textContent = maxText;
      this.maxInput.value = this.valueMax;

      this.fill.style.insetInlineStart = pctMin + '%';
      this.fill.style.width = (pctMax - pctMin) + '%';
    } else {
      this.fill.style.insetInlineStart = '0%';
      this.fill.style.width = pctMin + '%';
    }

    if (this.readout) {
      this.readout.innerHTML = this.dual
        ? minText + ' \u2013 ' + this.opts.formatText('max', this.valueMax)
        : minText;
    }

    var self = this;
    requestAnimationFrame(function () {
      self._keepBubbleOnScreen(self.minThumb);
      if (self.dual) self._keepBubbleOnScreen(self.maxThumb);
    });
  };

  Engine.prototype.emitChange = function () {
    var detail = this.opts.buildDetail(this);
    this.root.dispatchEvent(new CustomEvent(this.opts.eventName, { detail: detail, bubbles: true }));
  };

  // ---- kind registry -------------------------------------------------

  var kinds = {};

  function registerKind(name, factory) {
    kinds[name] = factory;
  }

  function init(scope) {
    var roots = (scope || document).querySelectorAll('[data-gs-slider]');
    roots.forEach(function (root) {
      if (root._gsSliderInstance) return;
      var kind = root.dataset.gsKind;
      var factory = kinds[kind];
      if (!factory) return; // that kind's small script hasn't registered yet
      root._gsSliderInstance = factory(root);
    });
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', function () { init(document); });
  } else {
    init(document);
  }

  window.GlassSlider = {
    clamp: clamp,
    roundToStep: roundToStep,
    makeFormatter: makeFormatter,
    supportsHaptics: supportsHaptics,
    KG_TO_LB: 2.2046226218,
    CM_PER_INCH: 2.54,
    Engine: Engine,
    registerKind: registerKind,
    init: init
  };
})();

'''

  /** Explicit/manual include for <head>, mirroring the kind taglibs'
   *  own *Assets tags — same idempotent-per-request behavior. */
  def sliderAssets = { attrs ->
    injectAssets(request, out)
  }

  /** Called by a kind taglib before it writes its own differential
   *  script. Idempotent per request.
   *
   *  STATIC on purpose: kind taglibs used to reach this via an injected
   *  `def glassSliderCommonTagLib` property, relying on Grails to
   *  autowire one taglib into another by property name. That autowiring
   *  is NOT reliable — it's a controller/service pattern, not a
   *  guaranteed taglib-to-taglib one — and in practice the property came
   *  through null, throwing NullPointerException at first use. Static
   *  methods need no bean lookup at all, so they can't be null: call
   *  GlassSliderCommonTagLib.injectAssets(request, out) directly. */
  static void injectAssets(request, out) {
    if (request.getAttribute(REQUEST_FLAG)) return
    request.setAttribute(REQUEST_FLAG, true)
    out << "<style>${CSS}</style>"
    out << "<script>${JS}</script>"
  }

  // ---- helpers shared by both kind taglibs — static for the same
  // reason as injectAssets above: no DI, so no way for these to be null.

  static BigDecimal toBigDecimal(def raw, def fallback) {
    if (raw == null || raw.toString().trim() == '') return new BigDecimal(fallback.toString())
    try {
      return new BigDecimal(raw.toString())
    } catch (NumberFormatException ignored) {
      return new BigDecimal(fallback.toString())
    }
  }

  static boolean toBoolean(def raw) {
    raw != null && Boolean.valueOf(raw.toString())
  }

  static String fmt(BigDecimal v) {
    v == null ? '' : (v.stripTrailingZeros().scale() <= 0 ? v.toBigInteger().toString() : v.toPlainString())
  }
}
