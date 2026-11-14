package com.murad.app.matrimony.button

/**
 * <glass:diagonalButton/>
 *
 * A Bootstrap 5 "pill" rendered as two SEPARATE buttons that together read
 * as one pill diagonally sliced in half, with a visible gap between the two
 * cut edges (like the pill was split and the halves pulled slightly apart).
 * Each half is its own click target — its own href/onclick/label/icon.
 *
 * Shape per half: pill-rounded on the outer end (border-radius), diagonal
 * on the inner end (clip-path). Both inner cuts slant the SAME direction so
 * they stay a constant perpendicular distance apart top-to-bottom, reading
 * as one pill sliced by a single diagonal line and pulled apart — not two
 * unrelated angled edges. The direction + steepness of that shared cut is
 * driven by `slantAngle`.
 *
 * Attrs:
 *   id            - optional, unique id prefix (auto-generated if omitted)
 *   leftLabel     - text for the left half (default: "")
 *   rightLabel    - text for the right half (default: "")
 *   leftIcon      - icon markup for the left half, e.g. <i class="..."></i>
 *   rightIcon     - icon markup for the right half
 *   leftHref / rightHref       - if present, renders that half as <a>
 *   leftOnclick / rightOnclick - JS handler, used when the matching href is absent
 *   leftAriaLabel / rightAriaLabel - accessible labels (fall back to the label text)
 *   leftId / rightId     - override the DOM id of each half
 *                   (default: "{id}_left" / "{id}_right"). All CSS
 *                   selectors for that half are generated from this value,
 *                   so overriding it re-scopes the emitted <style> block
 *                   too — no separate wiring needed.
 *   leftName / rightName - sets the HTML `name` attribute on
 *                   each half (useful for <button name="action"
 *                   value="...">-style form submission). Omitted entirely
 *                   when not provided.
 *   color1        - left half color. Bootstrap variant name (primary,
 *                   secondary, success, danger, warning, info, dark, light)
 *                   resolves to var(--bs-{name}); any raw CSS color also
 *                   works. Default: "primary"
 *   color2        - right half color, same rules. Default: "danger"
 *   textColor     - label/icon color for both halves. Default: "#fff"
 *   size          - sm | md | lg (default: md)
 *   gap           - px perpendicular distance between the two diagonal cut
 *                   edges, i.e. the actual visible space between the halves
 *                   (default: 8, clamped to >= 0). This is independent of
 *                   `slant` — internally the box-level spacing is
 *                   compensated (slant subtracted back out, possibly going
 *                   negative so the rectangular boxes overlap) so the
 *                   rendered gap always equals exactly this value, however
 *                   large or small `slant` is.
 *   slant         - px MAXIMUM horizontal offset of the diagonal cut, i.e.
 *                   the offset used when slantAngle is 0 or 180
 *                   (default: 22, clamped to >= 0)
 *   slantAngle    - degrees, clamped to 0..180 (default: 45). Controls
 *                   direction AND steepness of the shared diagonal cut,
 *                   measured off the vertical:
 *                     0   -> full-strength cut leaning one way   ("\")
 *                     90  -> perfectly vertical cut, no slant    ("|")
 *                     180 -> full-strength cut leaning the other way ("/")
 *                   Values between 0 and 90 ease from full "\" down to
 *                   vertical; values between 90 and 180 ease from vertical
 *                   out to full "/". Internally this is
 *                   offset = slant * cos(slantAngle), so it's a smooth,
 *                   continuous replacement for the old two-value direction
 *                   switch.
 *   glass         - true | false, glassmorphic frosted-glass style
 *                   (default: true). Set false to get the flat/solid look.
 *   glassOpacity  - 0..1, how much of color1/color2 tints the glass
 *                   (default: 0.1, clamped to 0..1, matching
 *                   GlassmorphicRadioTagLib's base label opacity — hover
 *                   bumps it by +0.05, same delta the radio uses). Only
 *                   relevant when glass=true.
 *   glassBlur     - px, backdrop blur strength (default: 10, matching
 *                   GlassmorphicRadioTagLib; clamped to >= 0). Only
 *                   relevant when glass=true.
 *   borderWidth   - px thickness of the border ring around each half,
 *                   including the diagonal edge (default: 1.5 when
 *                   glass=true, 0 when glass=false; clamped to 0..12). 0
 *                   disables the border entirely.
 *   borderColor   - CSS color for the border ring (default:
 *                   "rgba(255,255,255,0.2)" when glass=true, matching
 *                   GlassmorphicRadioTagLib's border opacity;
 *                   "rgba(0,0,0,0.18)" when glass=false).
 *
 * Interaction states:
 *   Hover and keyboard focus (:focus-visible) apply the SAME lift-and-glow
 *   treatment (translateY up + scale up + brightness/saturate boost +
 *   drop-shadow) — tabbing to a half looks identical to hovering it with a
 *   mouse. :focus-visible additionally keeps a visible outline ring on top,
 *   so keyboard users retain a clear focus indicator.
 *   Active/press (mouse click, tap, or keyboard activation) is intentionally
 *   a DIFFERENT, distinct treatment — pressed down and shrunk (translateY
 *   down + scale down) and dimmed rather than brightened — giving real
 *   tactile "button pushed in" feedback rather than reusing the hover look.
 *
 * Browser support notes:
 *   - Glass backgrounds use color-mix(), which lacks support pre-2023
 *     Safari/Firefox/Chrome. A @supports fallback renders a solid color1/
 *     color2 fill instead of silently failing to paint.
 *   - backdrop-filter has a separate @supports fallback that bumps the
 *     glass tint toward opaque, since without blur a barely-tinted
 *     see-through button reads as broken rather than styled.
 *   - Outer shadows use filter: drop-shadow(), not box-shadow — box-shadow
 *     paints against the rectangular border-box and ignores clip-path, so
 *     on a diagonally-cut shape it bleeds out as a square behind the cut
 *     corners. drop-shadow() is computed from the element's actual
 *     rendered (post-clip-path) alpha shape, so it hugs the pill +
 *     diagonal outline correctly.
 *   - Same problem, same fix, for the border: a plain CSS `border` only
 *     paints a box's four straight sides and has no way to follow a
 *     clip-path edge, so it used to appear only on the rounded pill-cap
 *     side and vanish on the diagonal. Replaced with a ::before layer that
 *     shares the exact same border-radius + clip-path as the button, sized
 *     slightly larger and stacked behind it — the sliver that peeks out
 *     around the entire outline (rounded end and diagonal alike) is what
 *     LOOKS like a border. See borderWidth/borderColor above.
 *   - Spacing between the halves is applied via `margin-left` on the right
 *     half rather than flexbox `gap`, since the box-level spacing needs to
 *     go negative (see `gap` attr above) to compensate for the diagonal's
 *     own offset — `gap` cannot go negative, `margin` can. This also
 *     avoids relying on flexbox `gap` support at all, which is missing
 *     before Safari 14.1 (April 2021).
 */
class GlassDiagonalButtonTagLib {

  static namespace = 'glass'

  private static final Set<String> BS_VARIANTS = [
    'primary', 'secondary', 'success', 'danger', 'warning', 'info', 'dark', 'light'
  ] as Set

  private static final Map SIZES = [
    sm: [padY: '0.32rem', padX: '1.0rem', font: '0.82rem', gapText: '0.35rem'],
    md: [padY: '0.55rem', padX: '1.4rem', font: '0.95rem', gapText: '0.45rem'],
    lg: [padY: '0.75rem', padX: '1.9rem', font: '1.08rem', gapText: '0.55rem']
  ]

  private String resolveColor(String value) {
    return (value in BS_VARIANTS) ? "var(--bs-${value})".toString() : value
  }

  /** Parses a GSP attribute (always a String, or absent) into an int, falling
   *  back to a default on absence or malformed input rather than throwing
   *  and breaking the whole page render. */
  private int toIntOrDefault(Object v, int fallback) {
    if (v == null) return fallback
    try {
      return Integer.parseInt(v.toString().trim())
    } catch (NumberFormatException ignored) {
      return fallback
    }
  }

  private double toDoubleOrDefault(Object v, double fallback) {
    if (v == null) return fallback
    try {
      return Double.parseDouble(v.toString().trim())
    } catch (NumberFormatException ignored) {
      return fallback
    }
  }

  private double clamp(double v, double min, double max) {
    return Math.max(min, Math.min(max, v))
  }

  def diagonalButton = { attrs, body ->
    String id = (attrs.id ?: "bsDiagPill_${System.currentTimeMillis()}_${(int) (Math.random() * 9999)}").toString()
    String leftId = (attrs.leftId ?: "${id}_left").toString()
    String rightId = (attrs.rightId ?: "${id}_right").toString()

    String leftLabel = (attrs.leftLabel ?: '').toString()
    String rightLabel = (attrs.rightLabel ?: '').toString()
    String leftIcon = attrs.leftIcon ?: ''
    String rightIcon = attrs.rightIcon ?: ''

    String leftHref = attrs.leftHref
    String rightHref = attrs.rightHref
    String leftOnclick = attrs.leftOnclick
    String rightOnclick = attrs.rightOnclick

    String leftAria = (attrs.leftAriaLabel ?: leftLabel ?: 'button').toString()
    String rightAria = (attrs.rightAriaLabel ?: rightLabel ?: 'button').toString()

    String leftNameAttr = attrs.leftName ? "name=\"${attrs.leftName}\"" : ''
    String rightNameAttr = attrs.rightName ? "name=\"${attrs.rightName}\"" : ''

    String color1 = resolveColor((attrs.color1 ?: 'primary').toString())
    String color2 = resolveColor((attrs.color2 ?: 'danger').toString())
    String textColor = (attrs.textColor ?: '#fff').toString()

    String sizeKey = (attrs.size ?: 'md').toString().toLowerCase()
    if (!SIZES.containsKey(sizeKey)) sizeKey = 'md'
    def sz = SIZES[sizeKey]

    int gapPx = Math.max(0, toIntOrDefault(attrs.gap, 8))

    // --- slant angle ---
    int slantPx = Math.max(0, toIntOrDefault(attrs.slant, 22))

    double slantAngleDeg = clamp(toDoubleOrDefault(attrs.slantAngle, 45d), 0d, 180d)

    double angleRad = Math.toRadians(slantAngleDeg)
    double cosVal = Math.cos(angleRad)              // +1 at 0deg, 0 at 90deg, -1 at 180deg
    int effectiveSlantPx = Math.round(Math.abs(slantPx * cosVal)) as int
    boolean leanPositive = cosVal >= 0               // true for 0-90deg range ("\" side)

    // The diagonal clip-path itself already eats effectiveSlantPx of visual
    // separation between the two halves (one corner of each half is cut in
    // by that amount while the opposite corner sits flush on the box edge),
    // so laying the boxes side-by-side with a plain `gap: ${gapPx}px` would
    // actually render a perpendicular distance of gapPx + effectiveSlantPx
    // between the cut edges — meaning gap values below the slant amount
    // (default slant=22) had no visible effect, since the slant's own
    // offset already exceeded them. Subtracting the slant back out of the
    // box-level spacing (allowing it to go negative, i.e. the boxes'
    // rectangular bounds overlap while their clipped, visible shapes do
    // not) makes the rendered perpendicular gap between the cut edges equal
    // exactly gapPx, independent of slant.
    int boxGapPx = gapPx - effectiveSlantPx

    String leftClip, rightClip
    if (leanPositive) {
      // cut runs top-left -> bottom-right, like "\"
      leftClip = 'polygon(0 0, calc(100% - var(--bdp-slant)) 0, 100% 100%, 0 100%)'
      rightClip = 'polygon(0 0, 100% 0, 100% 100%, var(--bdp-slant) 100%)'
    } else {
      // cut runs top-right -> bottom-left, like "/"
      leftClip = 'polygon(0 0, 100% 0, calc(100% - var(--bdp-slant)) 100%, 0 100%)'
      rightClip = 'polygon(var(--bdp-slant) 0, 100% 0, 100% 100%, 0 100%)'
    }

    // --- glassmorphism ---
    boolean glass = attrs.containsKey('glass') ? Boolean.parseBoolean(attrs.glass.toString()) : true

    // Shadow blur radius scales down with gap so a small gap doesn't get
    // visually bridged by the shadow bleeding across it — at gap=0 the
    // blur is minimal (2px), ramping up to its full design size by the
    // time gap reaches 8px, and staying capped there for larger gaps.
    int baseBlurCap = glass ? 10 : 7
    int shadowBlur = Math.max(2, Math.min(baseBlurCap, gapPx + 2))
    double glassOpacity = clamp(toDoubleOrDefault(attrs.glassOpacity, 0.1d), 0d, 1d)
    int glassBlur = Math.max(0, toIntOrDefault(attrs.glassBlur, 10))
    int glassOpacityPct = Math.round(glassOpacity * 100) as int
    // Matches GlassmorphicRadioTagLib's hover bump (0.1 -> 0.15, i.e. +0.05)
    int glassOpacityHoverPct = Math.min(100, glassOpacityPct + 5)

    String leftBg, rightBg, leftBgFallback, rightBgFallback, leftBgHover, rightBgHover
    String transitionCss, dsBase, dsHover, dsActive, glassOnlyCss

    if (glass) {
      leftBg = "color-mix(in srgb, ${color1} ${glassOpacityPct}%, transparent)"
      rightBg = "color-mix(in srgb, ${color2} ${glassOpacityPct}%, transparent)"
      leftBgHover = "color-mix(in srgb, ${color1} ${glassOpacityHoverPct}%, transparent)"
      rightBgHover = "color-mix(in srgb, ${color2} ${glassOpacityHoverPct}%, transparent)"
      // @supports fallback when color-mix() itself isn't supported.
      leftBgFallback = color1
      rightBgFallback = color2

      dsBase = "drop-shadow(0 4px ${shadowBlur}px rgba(0,0,0,0.18))"
      dsHover = "drop-shadow(0 9px ${Math.round(shadowBlur * 1.8)}px rgba(0,0,0,0.28))"
      dsActive = "drop-shadow(0 1px ${Math.max(2, Math.round(shadowBlur * 0.3))}px rgba(0,0,0,0.22))"

      transitionCss = 'transition: transform 0.12s ease-out, filter 0.12s ease-out, backdrop-filter 0.2s ease, background 0.2s ease;'
      glassOnlyCss = """
    backdrop-filter: blur(${glassBlur}px) saturate(180%);
    -webkit-backdrop-filter: blur(${glassBlur}px) saturate(180%);
    box-shadow: inset 0 1px 0 rgba(255,255,255,0.35), inset 0 -1px 6px rgba(255,255,255,0.06);
    filter: ${dsBase};
"""
    } else {
      leftBg = color1
      rightBg = color2
      leftBgHover = color1
      rightBgHover = color2
      leftBgFallback = color1
      rightBgFallback = color2

      dsBase = "drop-shadow(0 3px ${shadowBlur}px rgba(0,0,0,0.18))"
      dsHover = "drop-shadow(0 9px ${Math.round(shadowBlur * 2.3)}px rgba(0,0,0,0.32))"
      dsActive = "drop-shadow(0 1px ${Math.max(2, Math.round(shadowBlur * 0.3))}px rgba(0,0,0,0.26))"

      transitionCss = 'transition: transform 0.12s ease-out, filter 0.12s ease-out, background 0.2s ease;'
      glassOnlyCss = """
    filter: ${dsBase};
"""
    }

    // --- border ring ---
    // A plain CSS `border` only ever paints on a box's four straight sides —
    // it has no way to follow a clip-path-cut edge, so on these halves it
    // silently skipped the diagonal entirely and only showed on the rounded
    // (pill-cap) side. Fixed with the standard "clip-path border" trick
    // instead: a ::before pseudo-element sized slightly larger (inset by
    // -borderWidth), given the SAME border-radius + clip-path as the real
    // button, filled with the border color, and stacked behind it
    // (z-index -1). Since the real button's own clip-path leaves nothing
    // painted outside its exact shape, the slightly-larger pseudo shows
    // through only as a thin ring following the *entire* outline — rounded
    // end and diagonal cut alike.
    double borderWidthPx = clamp(toDoubleOrDefault(attrs.borderWidth, glass ? 1.5d : 0d), 0d, 12d)
    String borderColor = (attrs.borderColor ?: (glass ? 'rgba(255,255,255,0.2)' : 'rgba(0,0,0,0.18)')).toString()
    boolean showBorder = borderWidthPx > 0

    String leftTag = leftHref ? 'a' : 'button'
    String leftHrefAttr = leftHref ? "href=\"${leftHref}\"" : ''
    String leftTypeAttr = leftHref ? '' : 'type="button"'
    String leftOnclickAttr = (!leftHref && leftOnclick) ? "onclick=\"${leftOnclick}\"" : ''

    String rightTag = rightHref ? 'a' : 'button'
    String rightHrefAttr = rightHref ? "href=\"${rightHref}\"" : ''
    String rightTypeAttr = rightHref ? '' : 'type="button"'
    String rightOnclickAttr = (!rightHref && rightOnclick) ? "onclick=\"${rightOnclick}\"" : ''

    out << """
<style>
  #${id}.bdp-pair {
    display: inline-flex;
    align-items: stretch;
  }

  /* Spacing between the halves is applied as margin-left on the right half
     rather than flexbox `gap`, for two reasons: (1) `gap` can't go
     negative, but boxGapPx legitimately needs to when the slant offset
     exceeds the requested visual gap (see boxGapPx comment above) — the
     rectangular boxes overlap while the clipped, visible shapes still sit
     exactly gapPx apart; margin has no such restriction. (2) as a side
     benefit this also sidesteps flexbox `gap` support gaps in older
     engines (Safari < 14.1, April 2021) without needing a separate
     @supports fallback. */
  #${rightId} { margin-left: ${boxGapPx}px; }

  #${id} .bdp-half {
    --bdp-text: ${textColor};
    --bdp-slant: ${effectiveSlantPx}px;

    position: relative;
    display: inline-flex;
    align-items: center;
    isolation: isolate;
    border: 0;
    padding: ${sz.padY} ${sz.padX};
    font-size: ${sz.font};
    font-weight: 600;
    line-height: 1.2;
    color: var(--bdp-text) !important;
    text-decoration: none !important;
    cursor: pointer;
    -webkit-tap-highlight-color: transparent;
    -webkit-user-select: none;
    user-select: none;
    ${transitionCss}
${glassOnlyCss}  }

  #${leftId} {
    background: ${leftBg};
    border-top-left-radius: 999px;
    border-bottom-left-radius: 999px;
    padding-right: calc(${sz.padX} + var(--bdp-slant) * 0.6);
    justify-content: flex-start;
    clip-path: ${leftClip};
    -webkit-clip-path: ${leftClip};
  }

  #${rightId} {
    background: ${rightBg};
    border-top-right-radius: 999px;
    border-bottom-right-radius: 999px;
    padding-left: calc(${sz.padX} + var(--bdp-slant) * 0.6);
    justify-content: flex-end;
    clip-path: ${rightClip};
    -webkit-clip-path: ${rightClip};
  }

  ${showBorder ? """
  #${leftId}::before {
    content: "";
    position: absolute;
    inset: -${borderWidthPx}px;
    z-index: -1;
    background: ${borderColor};
    border-top-left-radius: 999px;
    border-bottom-left-radius: 999px;
    clip-path: ${leftClip};
    -webkit-clip-path: ${leftClip};
  }

  #${rightId}::before {
    content: "";
    position: absolute;
    inset: -${borderWidthPx}px;
    z-index: -1;
    background: ${borderColor};
    border-top-right-radius: 999px;
    border-bottom-right-radius: 999px;
    clip-path: ${rightClip};
    -webkit-clip-path: ${rightClip};
  }
  """ : ''}

  /* Fallback for browsers without color-mix() support (pre-2023 Safari/
     Firefox/Chrome): fall back to a solid fill instead of an unstyled or
     unpainted background. */
  @supports not (background: color-mix(in srgb, red 50%, transparent)) {
    #${leftId} { background: ${leftBgFallback}; }
    #${rightId} { background: ${rightBgFallback}; }
  }

  ${glass ? """
  /* Fallback for browsers without backdrop-filter: without blur, a
     barely-tinted see-through button reads as broken, so bump toward
     a near-opaque fill instead. */
  @supports not ((backdrop-filter: blur(1px)) or (-webkit-backdrop-filter: blur(1px))) {
    #${leftId} { background: color-mix(in srgb, ${color1} 88%, transparent); }
    #${rightId} { background: color-mix(in srgb, ${color2} 88%, transparent); }
  }
  """ : ''}

  #${id} .bdp-content {
    position: relative;
    z-index: 1;
    display: inline-flex;
    align-items: center;
    gap: ${sz.gapText};
    white-space: nowrap;
    text-shadow: 0 1px 2px rgba(0,0,0,0.25);
  }

  #${id} .bdp-icon {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    line-height: 1;
  }

  /* Hover and keyboard focus share the exact same lift-and-glow treatment,
     so tabbing to a half looks identical to a mouse hover. Grouped into one
     selector so they can't drift out of sync. */
  #${id} .bdp-half:hover,
  #${id} .bdp-half:focus-visible {
    transform: translateY(-4px) scale(1.05);
    filter: brightness(1.18) saturate(1.35) ${dsHover};
  }

  #${leftId}:hover,
  #${leftId}:focus-visible  { background: ${leftBgHover}; }

  #${rightId}:hover,
  #${rightId}:focus-visible { background: ${rightBgHover}; }

  /* :focus-visible keeps its own outline ring on top of the shared hover
     look, so keyboard users still get an explicit focus indicator and not
     just the lift/glow alone. */
  #${leftId}:focus-visible { outline: 2px solid ${color1}; outline-offset: 3px; }
  #${rightId}:focus-visible { outline: 2px solid ${color2}; outline-offset: 3px; }

  /* Active/press (mouse click, tap, or keyboard activation) gets its own
     distinct "pushed in" look — pressed down and shrunk, dimmed rather
     than brightened — restoring the tactile press feedback that hover and
     focus-visible (a lifted, brightened state) can't provide on their own.
     Placed after the hover/focus rule so equal-specificity :active wins
     while a half is both hovered/focused AND pressed. */
  #${id} .bdp-half:active {
    transform: translateY(1px) scale(0.90);
    filter: brightness(0.78) saturate(0.85) ${dsActive};
  }

  #${leftId}:active  { background: ${leftBgHover}; }
  #${rightId}:active { background: ${rightBgHover}; }

  @media (prefers-reduced-motion: reduce) {
    #${id} .bdp-half { transition: none; }
  }
</style>

<span id="${id}" class="bdp-pair">
  <${leftTag} id="${leftId}" class="btn bdp-half" ${leftHrefAttr} ${leftTypeAttr} ${leftOnclickAttr} ${leftNameAttr}
      aria-label="${leftAria.replace('"', '&quot;')}">
    <span class="bdp-content">
      ${leftIcon ? "<span class=\"bdp-icon\" aria-hidden=\"true\">${leftIcon}</span>" : ''}
      ${leftLabel ? "<span class=\"bdp-label\">${leftLabel.encodeAsHTML()}</span>" : ''}
    </span>
  </${leftTag}>
  <${rightTag} id="${rightId}" class="btn bdp-half" ${rightHrefAttr} ${rightTypeAttr} ${rightOnclickAttr} ${rightNameAttr}
      aria-label="${rightAria.replace('"', '&quot;')}">
    <span class="bdp-content">
      ${rightIcon ? "<span class=\"bdp-icon\" aria-hidden=\"true\">${rightIcon}</span>" : ''}
      ${rightLabel ? "<span class=\"bdp-label\">${rightLabel.encodeAsHTML()}</span>" : ''}
    </span>
  </${rightTag}>
</span>
${body() ?: ''}
"""
  }
}
