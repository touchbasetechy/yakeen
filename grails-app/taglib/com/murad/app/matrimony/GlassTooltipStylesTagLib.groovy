package com.murad.app.matrimony

/**
 * Emits the shared glassmorphic tooltip CSS (design tokens + portal styles)
 * once per page.
 *
 * Owns every tooltip-specific CSS rule:
 *  • CSS custom properties (--tt-*)
 *  • @property --tt-scale  (Houdini animatable, Chrome 85+ / Safari 16.4+)
 *  • .glass-tooltip-portal  (body-appended bubble clone, positioned by JS)
 *  • .clear-btn .glass-tooltip  (source span — always hidden)
 *  • @starting-style entry animation (Chrome 117+, progressive enhancement)
 *
 * All other glass input styles are emitted by their respective field taglibs.
 *
 * Key design decisions
 * ─────────────────────
 *  • Mobile-first: base rules target narrow screens; @media (min-width: 480px)
 *    enables single-line nowrap as a progressive enhancement.
 *  • Safe-area insets on margin (not padding) — keeps all four padding sides
 *    equal (--tt-padding) on notched iPhones and Android cutout devices.
 *  • backdrop-filter in @supports block with solid opaque fallback for
 *    Firefox < 103 and other non-supporting browsers.
 *  • pointer-events: none — portal never blocks touch gestures.
 *
 * TOOLTIP_STYLES_EMITTED is static so field taglibs can reference it as
 * GlassTooltipStylesTagLib.TOOLTIP_STYLES_EMITTED and guarantee the block
 * is emitted exactly once per page.
 *
 * Usage (optional — field taglibs auto-inject if omitted):
 *   <glass:tooltipStyles />
 */
class GlassTooltipStylesTagLib {

  static namespace = "glass"

  // Static so field taglibs can read it as GlassTooltipStylesTagLib.TOOLTIP_STYLES_EMITTED.
  static final String TOOLTIP_STYLES_EMITTED = 'glass_tooltipStylesEmitted'

  def tooltipStyles = {
    emitTooltipStylesOnce()
  }

  void emitTooltipStylesOnce() {
    if (pageScope[TOOLTIP_STYLES_EMITTED]) return
    pageScope[TOOLTIP_STYLES_EMITTED] = true

    out << '''
<style>
/* — Design tokens ---------------------------------------------------------- */
:root {
  --tt-background          : rgba(8, 12, 28, 0.88);
  --tt-background-fallback : rgb(18, 24, 48);
  --tt-border      : rgba(126, 184, 247, 0.28);
  --tt-text        : #d6e8ff;
  --tt-radius      : 7px;
  --tt-blur        : blur(20px) saturate(200%);
  --tt-shadow      : 0 8px 28px rgba(0, 0, 0, 0.55),
                     0 0 0 1px rgba(126, 184, 247, 0.10),
                     inset 0 1px 0 rgba(255, 255, 255, 0.08);
  --tt-font-size   : 0.75rem;
  --tt-padding     : 0.80em;
  --tt-gap         : 8px;
  --tt-duration    : 170ms;
  --tt-ease        : cubic-bezier(0.22, 1, 0.36, 1);
  --tt-slide       : 5px;
}

/* Houdini animatable custom property — ignored on unsupported browsers */
@property --tt-scale {
  syntax        : "<number>";
  initial-value : 0.88;
  inherits      : false;
}

/* — Source span: always hidden; only the body-portal is shown -------------- */
.clear-btn .glass-tooltip {
  display        : none !important;
  pointer-events : none !important;
}

/* — Tooltip portal (mobile-first) ----------------------------------------- */
/* Appended to <body> by JS — outside any backdrop-filter stacking context.   */
/* margin absorbs safe-area insets; padding stays uniform on all four sides.  */
.glass-tooltip-portal {
  position            : absolute;
  z-index             : 2147483647;
  opacity             : 0;
  pointer-events      : none;
  -webkit-transform   : scale(0.88);
      -ms-transform   : scale(0.88);
          transform   : scale(0.88);
  white-space         : normal;
  word-break          : break-word;
  max-width           : min(280px, calc(100vw - 32px));
  padding             : var(--tt-padding);
  margin-left         : env(safe-area-inset-left,  0px);
  margin-right        : env(safe-area-inset-right, 0px);
  border-radius       : var(--tt-radius);
  font-size           : var(--tt-font-size);
  font-weight         : 500;
  letter-spacing      : 0.03em;
  color               : var(--tt-text);
  background          : var(--tt-background-fallback);
  border              : 1px solid var(--tt-border);
  box-shadow          : var(--tt-shadow);
  -webkit-transition  : opacity           var(--tt-duration) var(--tt-ease),
                        -webkit-transform var(--tt-duration) var(--tt-ease);
          transition  : opacity           var(--tt-duration) var(--tt-ease),
                        transform         var(--tt-duration) var(--tt-ease);
  -webkit-user-select : none;
     -moz-user-select : none;
      -ms-user-select : none;
          user-select : none;
}

/* Progressive enhancement: glassmorphic blur when supported */
@supports (backdrop-filter: blur(1px)) or (-webkit-backdrop-filter: blur(1px)) {
  .glass-tooltip-portal {
    background              : var(--tt-background);
    -webkit-backdrop-filter : var(--tt-blur);
            backdrop-filter : var(--tt-blur);
  }
}

/* Progressive enhancement: single-line on wider screens */
@media (min-width: 480px) {
  .glass-tooltip-portal {
    white-space : nowrap;
    max-width   : none;
  }
}

/* Progressive enhancement: smooth entry on first paint (Chrome 117+) */
@supports (selector(:has(*))) {
  @starting-style {
    .glass-tooltip-portal {
      opacity           : 0;
      -webkit-transform : scale(0.88);
              transform : scale(0.88);
    }
  }
}
</style>
'''
  }
}
