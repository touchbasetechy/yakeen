package com.murad.app.matrimony

class GlassmorphicPopOverTagLib {

  static namespace = "glass"

  // ── Global JS: injected once per page ────────────────────────────────────
  // Handles: click/touch outside, keyboard Escape, focus-outside (keyboard nav),
  //          cross-browser getInstance, double-fire guard for mobile.
  private static final String GLOBAL_POPOVER_JS = '''
    if (!window._glassPopoverGlobalInit) {
        window._glassPopoverGlobalInit = true;

        function _glassHideAllPopovers(exceptEl) {
            document.querySelectorAll('[data-bs-toggle="popover"]').forEach(function(el) {
                if (el === exceptEl) return;
                try {
                    var inst = bootstrap.Popover.getInstance(el);
                    if (inst) { inst.hide(); }
                } catch(e) { /* swallow stale-instance errors */ }
            });
        }

        // ── Touch / click outside ─────────────────────────────────────────
        // 'touchend' fires before 'click' on mobile; flag guards double-fire.
        var _glassTouchFired = false;

        document.addEventListener('touchend', function(e) {
            _glassTouchFired = true;
            if (!e.target.closest('[data-bs-toggle="popover"]') &&
                !e.target.closest('.popover')) {
                _glassHideAllPopovers(null);
            }
            setTimeout(function() { _glassTouchFired = false; }, 400);
        }, { passive: true });

        document.addEventListener('click', function(e) {
            if (_glassTouchFired) return;
            if (!e.target.closest('[data-bs-toggle="popover"]') &&
                !e.target.closest('.popover')) {
                _glassHideAllPopovers(null);
            }
        });

        // ── Focus outside (keyboard navigation) ──────────────────────────
        document.addEventListener('focusin', function(e) {
            if (!e.target.closest('[data-bs-toggle="popover"]') &&
                !e.target.closest('.popover')) {
                _glassHideAllPopovers(null);
            }
        });

        // ── Escape key ────────────────────────────────────────────────────
        document.addEventListener('keydown', function(e) {
            var key = e.key || e.keyCode;
            if (key === 'Escape' || key === 'Esc' || key === 27) {
                _glassHideAllPopovers(null);
            }
        });
    }
  '''

  // ── Per-instance JS builder ───────────────────────────────────────────────
  private String buildInstanceJs(Map p) {

    def lsWriteFnJs = (p.lsName && p.lsValue) ? """
        function writeToLocalStorage() {
            try { localStorage.setItem('${p.lsName}', '${p.lsValue}'); }
            catch(e) { console.warn('GlassPopover [${p.id}]: localStorage.setItem failed', e); }
        }
    """ : ''

    def autoHideJs = (p.autoHideDelay != '0') ? """
        (function(instance) {
            var origShow = instance.show.bind(instance);
            var timer    = null;
            instance.show = function() {
                origShow();
                clearTimeout(timer);
                timer = setTimeout(function() { instance.hide(); }, ${p.autoHideDelay});
            };
        })(popoverInstance);
    """ : ''

    // aria-expanded sync + optional LS write on show; child-reset on hidden.
    def lsShowWrite = (p.lsName && p.lsValue) ? 'writeToLocalStorage();' : ''

    def lifecycleJs = """
        popoverEl.addEventListener('show.bs.popover', function() {
            popoverEl.setAttribute('aria-expanded', 'true');
            ${lsShowWrite}
        });
        popoverEl.addEventListener('hide.bs.popover', function() {
            popoverEl.setAttribute('aria-expanded', 'false');
        });
        popoverEl.addEventListener('hidden.bs.popover', function() {
            [].forEach.call(popoverEl.children, function(child) {
                child.style.display = 'none';
            });
        });
    """

    def autoShowJs = p.autoShow ? """
        requestAnimationFrame(function() {
            requestAnimationFrame(function() { if (popoverInstance) { popoverInstance.show(); } });
        });
    """ : ''

    def lsConditionalJs = ''
    if (p.lsConditional) {
      lsConditionalJs = """
        try {
            var _storedVal = localStorage.getItem('${p.lsName}');
            if (_storedVal === '${p.showWhenValue}') {
                requestAnimationFrame(function() {
                    requestAnimationFrame(function() { if (popoverInstance) { popoverInstance.show(); } });
                });
            } else {
                [].forEach.call(popoverEl.children, function(c) { c.style.display = 'none'; });
            }
        } catch(e) {
            console.warn('GlassPopover [${p.id}]: localStorage.getItem failed', e);
        }
      """
    }

    def setLSVJs = p.lsConditional ? """
        popoverInstance.setLocalStorageValue = function(value) {
            try {
                localStorage.setItem('${p.lsName}', value);
                if (value === '${p.showWhenValue}') { popoverInstance.show(); }
                else { popoverInstance.hide(); }
            } catch(e) {
                console.warn('GlassPopover [${p.id}]: setLocalStorageValue failed', e);
            }
        };
    """ : ''

    return """
        ${GLOBAL_POPOVER_JS}

        var popoverEl = document.getElementById('${p.id}');
        if (popoverEl && typeof bootstrap !== 'undefined') {
            ${lsWriteFnJs}

            var popoverInstance = new bootstrap.Popover(popoverEl, {
                sanitize: false,
                animation: ${p.animation},
                // ── Mobile: generous offset for fat-finger accuracy ───────
                offset: [0, 8]
            });

            ${autoHideJs}
            ${lifecycleJs}
            ${autoShowJs}
            ${lsConditionalJs}
            ${setLSVJs}

            popoverEl._glasspopover = popoverInstance;
        }
    """
  }

  // ── Shared CSS builder ────────────────────────────────────────────────────
  // fallbackBg — opaque background used when backdrop-filter is unsupported
  //              (Firefox < 103, older Android WebView).
  // headerBg / headerBorder — retained in signature for call-site compat but
  //                           superseded by transparent + border:none rules.
  private String buildSharedCss(String customClass, String animKeyframe,
                                String popoverBg, String blurValue,
                                String border, String boxShadow,
                                String animation, float opacity,
                                String headerBg, String headerBorder,
                                String headerColor, String bodyColor,
                                String arrowBorderColor, String arrowFillColor,
                                String fallbackBg) {
    return """
        /* ═══════════════════════════════════════════════════════════════
           ${customClass}  ·  Glassmorphic Popover
           Support matrix: Chrome 76+  Firefox 103+  Safari 14+
                           Edge 79+    iOS Safari 14+  Android Chrome 96+
           ═══════════════════════════════════════════════════════════════ */

        /* ── Reduced-motion: kill all animation and transition ────────── */
        @media (prefers-reduced-motion: reduce) {
            .${customClass}.popover,
            .${customClass}.popover * {
                animation:  none !important;
                transition: none !important;
            }
        }

        /* ── @supports fallback: backdrop-filter unavailable ─────────── */
        /* Covers Firefox < 103 and older Android WebView. Targets the
           ::before glass overlay below, not .popover itself — see that
           rule's comment for why the background lives there now.       */
        @supports not ((-webkit-backdrop-filter: blur(1px)) or (backdrop-filter: blur(1px))) {
            .${customClass}.popover::before {
                background: ${fallbackBg} !important;
            }
        }

        /* ── Mobile-first: full-width on xs screens ───────────────────── */
        @media (max-width: 575.98px) {
            .${customClass}.popover {
                max-width: calc(100vw - 2rem) !important;
                min-width: 200px !important;
            }
        }

        ${animKeyframe}

        /* ── Popover shell ────────────────────────────────────────────── */
        .${customClass}.popover {
            /* Scoped design tokens */
            --_glass-bg:     ${popoverBg};
            --_glass-blur:   ${blurValue};
            --_glass-border: ${border};
            --_glass-shadow: ${boxShadow};

            /* IMPORTANT — root cause of the missing arrow tip:
               .popover-arrow (below) is positioned with a NEGATIVE
               offset so it pokes outside this element's own box.
               Chrome/Safari force an element with backdrop-filter onto
               its own composited layer, and a child positioned outside
               that layer's box gets visually clipped to the rounded
               box — even with overflow: visible. That's why the panel
               itself (background, border, shadow — everything that
               stays INSIDE the box) rendered fine while only the
               protruding arrow vanished.

               Fix: this element carries NO background/backdrop-filter/
               border of its own anymore. The glass surface is painted
               on an ::before overlay sized exactly inset:0 instead (see
               below), so backdrop-filter never has anything to clip.
               The arrow stays a plain sibling <div>, never a descendant
               of the blurred pseudo, so it's structurally immune to
               this bug rather than depending on overflow being honored. */
            background:    transparent !important;
            border:        none !important;
            border-radius: 12px !important;
            box-shadow:    var(--_glass-shadow, ${boxShadow}) !important;
            overflow:      visible !important;
            z-index:       1080 !important;
            position:      relative; /* fallback containing block; Bootstrap/Popper's own inline position:absolute always wins once it runs */

            /* Own stacking context — prevents z-index bleed from children */
            isolation: isolate !important;

            /* GPU-composite hint for smooth animation (widest-compat form) */
            -webkit-transform: translateZ(0);
                    transform: translateZ(0);
            -webkit-backface-visibility: hidden;
                    backface-visibility: hidden;

            ${animation}

            /* Touch: suppress tap highlight and text selection on the shell */
            -webkit-tap-highlight-color: transparent;
            -webkit-user-select: none;
               -moz-user-select: none;
                -ms-user-select: none;
                    user-select: none;
        }

        /* ── Glass surface (background + blur + border) ─────────────────
           Sized exactly to .popover's own box (inset:0) and painted
           BEHIND its real content (z-index:-1, safe inside the
           isolate'd stacking context above). This is the ONLY element
           carrying backdrop-filter now, and it never needs to paint
           outside its own bounds — nothing left for the browser to
           clip, which is what makes the arrow reliably visible.        */
        .${customClass}.popover::before {
            content:  "" !important;
            position: absolute !important;
            inset:    0 !important;
            display:  block !important;
            border-radius: inherit !important;
            background: var(--_glass-bg, ${popoverBg}) !important;

            -webkit-backdrop-filter: blur(var(--_glass-blur, ${blurValue})) saturate(180%) !important;
                    backdrop-filter: blur(var(--_glass-blur, ${blurValue})) saturate(180%) !important;

            border:   var(--_glass-border, ${border}) !important;
            z-index:  -1 !important;
            pointer-events: none !important;
        }

        /* ── Arrows ───────────────────────────────────────────────────── */
        .${customClass} .popover-arrow {
            position: absolute !important;
            display:  block !important;
            width:  1rem;
            height: 0.5rem;
            z-index: 1;   /* explicit: paint above the ::before glass surface, not left to chance */
        }

        /* Base pseudo-element properties for the triangle itself. Kept
           explicit (rather than assumed from Bootstrap's own popover.css)
           since this stylesheet already overrides everything else about
           the popover with !important — no reason for just this one part
           to silently depend on Bootstrap's CSS being present/unmodified. */
        .${customClass} .popover-arrow::before,
        .${customClass} .popover-arrow::after {
            content:      "" !important;
            display:      block !important;
            position:     absolute !important;
            border-color: transparent !important;
            border-style: solid !important;
            border-width: 0.5rem !important;
        }

        /* top */
        .${customClass}.bs-popover-top > .popover-arrow,
        .${customClass}.bs-popover-auto[data-popper-placement^="top"] > .popover-arrow
            { bottom: calc(-0.5rem - 1px); }
        .${customClass}.bs-popover-top > .popover-arrow::before,
        .${customClass}.bs-popover-auto[data-popper-placement^="top"] > .popover-arrow::before
            { bottom: 0; border-width: 0.5rem 0.5rem 0; border-top-color: ${arrowBorderColor}; }
        .${customClass}.bs-popover-top > .popover-arrow::after,
        .${customClass}.bs-popover-auto[data-popper-placement^="top"] > .popover-arrow::after
            { bottom: 1px; border-width: 0.5rem 0.5rem 0; border-top-color: ${arrowFillColor}; }

        /* bottom */
        .${customClass}.bs-popover-bottom > .popover-arrow,
        .${customClass}.bs-popover-auto[data-popper-placement^="bottom"] > .popover-arrow
            { top: calc(-0.5rem - 1px); }
        .${customClass}.bs-popover-bottom > .popover-arrow::before,
        .${customClass}.bs-popover-auto[data-popper-placement^="bottom"] > .popover-arrow::before
            { top: 0; border-width: 0 0.5rem 0.5rem; border-bottom-color: ${arrowBorderColor}; }
        .${customClass}.bs-popover-bottom > .popover-arrow::after,
        .${customClass}.bs-popover-auto[data-popper-placement^="bottom"] > .popover-arrow::after
            { top: 1px; border-width: 0 0.5rem 0.5rem; border-bottom-color: ${arrowFillColor}; }

        /* end / right */
        .${customClass}.bs-popover-end > .popover-arrow,
        .${customClass}.bs-popover-auto[data-popper-placement^="right"] > .popover-arrow
            { left: calc(-0.5rem - 1px); width: 0.5rem; height: 1rem; }
        .${customClass}.bs-popover-end > .popover-arrow::before,
        .${customClass}.bs-popover-auto[data-popper-placement^="right"] > .popover-arrow::before
            { left: 0; border-width: 0.5rem 0.5rem 0.5rem 0; border-right-color: ${arrowBorderColor}; }
        .${customClass}.bs-popover-end > .popover-arrow::after,
        .${customClass}.bs-popover-auto[data-popper-placement^="right"] > .popover-arrow::after
            { left: 1px; border-width: 0.5rem 0.5rem 0.5rem 0; border-right-color: ${arrowFillColor}; }

        /* start / left */
        .${customClass}.bs-popover-start > .popover-arrow,
        .${customClass}.bs-popover-auto[data-popper-placement^="left"] > .popover-arrow
            { right: calc(-0.5rem - 1px); width: 0.5rem; height: 1rem; }
        .${customClass}.bs-popover-start > .popover-arrow::before,
        .${customClass}.bs-popover-auto[data-popper-placement^="left"] > .popover-arrow::before
            { right: 0; border-width: 0.5rem 0 0.5rem 0.5rem; border-left-color: ${arrowBorderColor}; }
        .${customClass}.bs-popover-start > .popover-arrow::after,
        .${customClass}.bs-popover-auto[data-popper-placement^="left"] > .popover-arrow::after
            { right: 1px; border-width: 0.5rem 0 0.5rem 0.5rem; border-left-color: ${arrowFillColor}; }

        /* ── Header ───────────────────────────────────────────────────── */
        .${customClass} .popover-header {
            background:    transparent !important;
            border:        none !important;           /* ← all header borders removed */
            border-radius: 11px 11px 0 0 !important;
            color:         ${headerColor} !important;
            font-size:     13px !important;
            font-weight:   600 !important;
            letter-spacing: 0.01em !important;
            /* Sub-pixel font rendering — Chrome/Safari + Firefox */
            -webkit-font-smoothing:  antialiased;
            -moz-osx-font-smoothing: grayscale;
            /* Balance short title lines (Chrome 114+, silent no-op elsewhere) */
            text-wrap: balance;
            margin-top:    -4px !important;
            margin-bottom:  0   !important;
            padding:       12px !important;
            text-transform: none !important;
        }

        /* ── Body ────────────────────────────────────────────────────── */
        .${customClass} .popover-body {
            background:  transparent !important;
            color:       ${bodyColor} !important;
            font-size:   13px !important;
            line-height: 1.6 !important;
            padding:     12px !important;
            -webkit-font-smoothing:  antialiased;
            -moz-osx-font-smoothing: grayscale;
            /* Allow text selection inside the body only */
            -webkit-user-select: text;
               -moz-user-select: text;
                -ms-user-select: text;
                    user-select: text;
        }

        /* ── Trigger element ─────────────────────────────────────────── */
        /* WCAG 2.5.5 — minimum 44 × 44 px touch target                   */
        .glassmorphic-popover-trigger {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            min-width:  44px;
            min-height: 44px;
            /* Suppress iOS tap flash and double-tap zoom */
            -webkit-tap-highlight-color: transparent;
            touch-action: manipulation;
        }
        /* Mouse/touch: hide default outline */
        .glassmorphic-popover-trigger:focus { outline: none; }
        /* Keyboard-only focus ring (Chrome 86+, FF 85+, Safari 15.4+) */
        .glassmorphic-popover-trigger:focus-visible {
            outline:        2px solid ${headerColor};
            outline-offset: 3px;
            border-radius:  6px;
        }
    """
  }

  // ── light variant ──────────────────────────────────────────────────────────
  def popOver = { attrs, body ->
    def title = attrs.title ?: ''
    def content = attrs.content ?: ''
    def placement = attrs.placement ?: 'top'
    def html = attrs.html ? 'true' : 'false'
    def animBool = attrs.animation?.toString() != 'false'
    def borderGlow = attrs.borderGlow?.toString() != 'false'
    def autoShow = attrs.autoShow?.toString() == 'true'
    def customClass = attrs.customClass ?: ''
    def blurStr = attrs.blurStrength ?: 'medium'
    def opacity = attrs.opacity ?: 0.15
    def enableDefaultTrigger = attrs.enableDefaultTrigger?.toString() == 'true'
    def trigger = attrs.trigger ?: (enableDefaultTrigger ? 'click' : 'manual')
    def autoHideDelay = attrs.autoHideDelay != null ? attrs.autoHideDelay.toString() : '7000'
    def popoverId = attrs.id ?: "glasspopover-${UUID.randomUUID().toString().take(8)}"
    def lsName = attrs.localStorageName ?: ''
    def lsValue = attrs.localStorageValue ?: ''
    def showWhenValue = attrs.showWhenValue
    def lsConditional = (lsName && lsValue && showWhenValue != null)
    def blurValues = [low: '5px', medium: '10px', high: '15px']
    def blurValue = blurValues[blurStr] ?: blurValues['medium']

    def animKeyframe = borderGlow ? """
        @keyframes glasspopover-glow-breathe {
            0%, 100% { box-shadow: 0 4px 24px rgba(99,179,237,0.15), 0 0 0 1px rgba(255,255,255,0.5) inset; }
            50%       { box-shadow: 0 8px 40px rgba(99,179,237,0.35), 0 0 0 1px rgba(255,255,255,0.8) inset; }
        }
    """ : ''

    def css = buildSharedCss(
      'glassmorphic-popover',
      animKeyframe,
      "rgba(255,255,255,${opacity})",
      blurValue,
      '1px solid rgba(255,255,255,0.4)',
      '0 8px 32px rgba(31,38,135,0.18)',
      borderGlow ? 'animation: glasspopover-glow-breathe 3s ease-in-out infinite;' : '',
      opacity as float,
      /* headerBg   (unused) */ 'rgba(255,255,255,0.15)',
      /* headerBorder(unused)*/ 'none',
      '#1a2b3c',
      '#2c3e50',
      /* arrowBorderColor */ "rgba(255,255,255,${opacity})",
      /* arrowFillColor  */ "rgba(255,255,255,${opacity})",
      /* fallbackBg      */ 'rgba(255,255,255,0.92)'
    )

    def js = buildInstanceJs([
      id           : popoverId, animation: animBool, autoHideDelay: autoHideDelay,
      autoShow     : autoShow, lsName: lsName, lsValue: lsValue,
      showWhenValue: showWhenValue, lsConditional: lsConditional
    ])

    out << """
<span id="${popoverId}"
      class="glassmorphic-popover-trigger ${customClass}"
      data-bs-toggle="popover"
      data-bs-placement="${placement}"
      data-bs-trigger="${trigger}"
      data-bs-html="${html}"
      data-bs-title="${title.encodeAsHTML()}"
      data-bs-content="${content.encodeAsHTML()}"
      data-bs-custom-class="glassmorphic-popover"
      role="button"
      tabindex="0"
      aria-haspopup="dialog"
      aria-expanded="false"
      aria-label="${title.encodeAsHTML()}"
      style="cursor: pointer; touch-action: manipulation;">
    ${body()}
</span>
<style>${css}</style>
<script>document.addEventListener('DOMContentLoaded', function() { ${js} });</script>
"""
  }

  // ── dark variant ───────────────────────────────────────────────────────────
  def popOverDark = { attrs, body ->
    def title = attrs.title ?: ''
    def content = attrs.content ?: ''
    def placement = attrs.placement ?: 'top'
    def html = attrs.html ? 'true' : 'false'
    def animBool = attrs.animation?.toString() != 'false'
    def borderGlow = attrs.borderGlow?.toString() != 'false'
    def autoShow = attrs.autoShow?.toString() == 'true'
    def customClass = attrs.customClass ?: ''
    def blurStr = attrs.blurStrength ?: 'medium'
    def opacity = attrs.opacity ?: 0.7
    def enableDefaultTrigger = attrs.enableDefaultTrigger?.toString() == 'true'
    def trigger = attrs.trigger ?: (enableDefaultTrigger ? 'click' : 'manual')
    def autoHideDelay = attrs.autoHideDelay != null ? attrs.autoHideDelay.toString() : '7000'
    def popoverId = attrs.id ?: "glasspopover-dark-${UUID.randomUUID().toString().take(8)}"
    def lsName = attrs.localStorageName ?: ''
    def lsValue = attrs.localStorageValue ?: ''
    def showWhenValue = attrs.showWhenValue
    def lsConditional = (lsName && lsValue && showWhenValue != null)
    def blurValues = [low: '5px', medium: '10px', high: '15px']
    def blurValue = blurValues[blurStr] ?: blurValues['medium']

    def animKeyframe = borderGlow ? """
        @keyframes glasspopover-dark-glow-breathe {
            0%, 100% { box-shadow: 0 8px 32px rgba(0,0,0,0.4),  0 0 0 1px rgba(255,255,255,0.08) inset; }
            50%       { box-shadow: 0 12px 48px rgba(0,0,0,0.6), 0 0 0 1px rgba(255,255,255,0.18) inset; }
        }
    """ : ''

    def css = buildSharedCss(
      'glassmorphic-popover-dark',
      animKeyframe,
      "rgba(20,25,35,${opacity})",
      blurValue,
      '1px solid rgba(255,255,255,0.12)',
      '0 8px 32px rgba(0,0,0,0.4)',
      borderGlow ? 'animation: glasspopover-dark-glow-breathe 3s ease-in-out infinite;' : '',
      opacity as float,
      /* headerBg   (unused) */ 'rgba(255,255,255,0.06)',
      /* headerBorder(unused)*/ 'none',
      '#dce8f4',
      '#a0aec0',
      /* arrowBorderColor */ "rgba(20,25,35,${opacity})",
      /* arrowFillColor  */ "rgba(20,25,35,${opacity})",
      /* fallbackBg      */ 'rgba(20,25,35,0.96)'
    )

    def js = buildInstanceJs([
      id           : popoverId, animation: animBool, autoHideDelay: autoHideDelay,
      autoShow     : autoShow, lsName: lsName, lsValue: lsValue,
      showWhenValue: showWhenValue, lsConditional: lsConditional
    ])

    out << """
<span id="${popoverId}"
      class="glassmorphic-popover-trigger ${customClass}"
      data-bs-toggle="popover"
      data-bs-placement="${placement}"
      data-bs-trigger="${trigger}"
      data-bs-html="${html}"
      data-bs-title="${title.encodeAsHTML()}"
      data-bs-content="${content.encodeAsHTML()}"
      data-bs-custom-class="glassmorphic-popover-dark"
      role="button"
      tabindex="0"
      aria-haspopup="dialog"
      aria-expanded="false"
      aria-label="${title.encodeAsHTML()}"
      style="cursor: pointer; touch-action: manipulation;">
    ${body()}
</span>
<style>${css}</style>
<script>document.addEventListener('DOMContentLoaded', function() { ${js} });</script>
"""
  }
}
