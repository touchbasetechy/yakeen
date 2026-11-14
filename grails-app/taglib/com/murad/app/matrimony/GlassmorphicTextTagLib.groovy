package com.murad.app.matrimony


class GlassmorphicTextTagLib {
  static namespace = "glass"

  /**
   * Renders text in a given color and transparency, with an optional icon.
   *
   * Example usage:
   * <glass:text color="#ff0000" opacity="0.5">Hello World</glass:text>
   * <glass:text color="white" opacity="0.8" size="24" weight="bold" icon="fa-star">Title</glass:text>
   * <glass:text color="#ffffff" opacity="1.0" tag="h2" icon="fa-lock" iconPosition="end">Locked</glass:text>
   * <glass:text color="gold" icon="fa-crown" iconColor="#ffd700">Premium</glass:text>
   * <glass:text color="white" icon="fa-star" position="top right">Badge</glass:text>
   * <glass:text color="white" position="bottom center">Footer note</glass:text>
   *
   * Attributes:
   * - color:        CSS color value (optional, default: "#ffffff")
   *                 Accepts: hex (#fff / #ffffff), rgb(), rgba(), named colors
   * - opacity:      Transparency 0.0 (invisible) → 1.0 (opaque) (optional, default: 0.6)
   * - size:         Font size in px (optional, default: 16)
   * - weight:       Font weight (optional, default: "normal")
   *                 Accepts: normal / bold / lighter / bolder / 100–900
   * - tag:          HTML element to wrap with (optional, default: "span")
   *                 Accepts: span / p / h1 / h2 / h3 / h4 / h5 / h6 / div / label
   * - icon:         Font Awesome icon class (optional, default: "fa-solid fa-circle-info")
   *                 Accepts short form "fa-star" (auto-prefixed with "fa")
   *                 or full form "fa-solid fa-circle-check"
   * - iconPosition: Where to place the icon relative to the text itself (optional, default: "start")
   *                 Accepts: start / end
   * - iconColor:    CSS color for the icon (optional, default: same as color)
   *                 Accepts same formats as color
   * - position:     Where to align the icon+text block within its container (optional, default: "center")
   *                 Accepts: "top left", "top center", "top right",
   *                          "left", "center", "right",
   *                          "bottom left", "bottom center", "bottom right"
   *                 The rendered element fills its parent (width:100%; height:100%)
   *                 and uses flexbox to place the icon+text at the requested spot,
   *                 so the parent container needs a defined size (or height) for
   *                 vertical positions to have visible effect.
   * - class:        Additional CSS classes (optional)
   * - id:           Element ID (optional)
   */
  def text = { attrs, body ->

    // ── Allowed values ───────────────────────────────────────────────────────
    final List<String> ALLOWED_TAGS = ['span', 'p', 'h1', 'h2', 'h3', 'h4', 'h5', 'h6', 'div', 'label']
    final List<String> ALLOWED_WEIGHTS = ['normal', 'bold', 'lighter', 'bolder',
                                          '100', '200', '300', '400', '500', '600', '700', '800', '900']
    final List<String> ALLOWED_ICON_POS = ['start', 'end']
    final Map<String, Map<String, String>> POSITION_MAP = [
      'top left'     : [justify: 'flex-start', align: 'flex-start'],
      'top center'   : [justify: 'center', align: 'flex-start'],
      'top right'    : [justify: 'flex-end', align: 'flex-start'],
      'left'         : [justify: 'flex-start', align: 'center'],
      'center'       : [justify: 'center', align: 'center'],
      'right'        : [justify: 'flex-end', align: 'center'],
      'bottom left'  : [justify: 'flex-start', align: 'flex-end'],
      'bottom center': [justify: 'center', align: 'flex-end'],
      'bottom right' : [justify: 'flex-end', align: 'flex-end']
    ]
    // ────────────────────────────────────────────────────────────────────────

    // ── Extract & validate color ─────────────────────────────────────────────
    String color = (attrs.color?.toString()?.trim()) ?: '#ffffff'
    if (!isValidColor(color)) {
      throwTagError("Attribute 'color' has an invalid value: '${color}'. " +
        "Accepted formats: hex (#fff / #ffffff), rgb(), rgba(), or a CSS named color.")
    }
    // ────────────────────────────────────────────────────────────────────────

    // ── Extract & validate opacity ───────────────────────────────────────────
    BigDecimal opacity = 0.6                                        // ← changed from 1.0
    if (attrs.opacity != null) {
      try {
        opacity = new BigDecimal(attrs.opacity.toString().trim())
      } catch (NumberFormatException e) {
        throwTagError("Attribute 'opacity' must be a number between 0.0 and 1.0. Got: '${attrs.opacity}'")
      }
      if (opacity < 0.0 || opacity > 1.0) {
        throwTagError("Attribute 'opacity' must be between 0.0 and 1.0. Got: ${opacity}")
      }
    }
    String opacityStr = opacity.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
    // ────────────────────────────────────────────────────────────────────────

    // ── Extract & validate font size ─────────────────────────────────────────
    int fontSize = 16
    if (attrs.size != null) {
      try {
        fontSize = Integer.parseInt(attrs.size.toString().trim())
      } catch (NumberFormatException e) {
        throwTagError("Attribute 'size' must be a positive integer (px). Got: '${attrs.size}'")
      }
      if (fontSize < 1 || fontSize > 999) {
        throwTagError("Attribute 'size' must be between 1 and 999. Got: ${fontSize}")
      }
    }
    // ────────────────────────────────────────────────────────────────────────

    // ── Extract & validate font weight ──────────────────────────────────────
    String weight = (attrs.weight?.toString()?.trim()?.toLowerCase()) ?: 'normal'
    if (!(weight in ALLOWED_WEIGHTS)) {
      throwTagError("Attribute 'weight' has an invalid value: '${weight}'. " +
        "Accepted: normal, bold, lighter, bolder, or 100–900 in steps of 100.")
    }
    // ────────────────────────────────────────────────────────────────────────

    // ── Extract & validate HTML tag ──────────────────────────────────────────
    String tag = (attrs.tag?.toString()?.trim()?.toLowerCase()) ?: 'span'
    if (!(tag in ALLOWED_TAGS)) {
      throwTagError("Attribute 'tag' has an invalid value: '${tag}'. " +
        "Accepted: ${ALLOWED_TAGS.join(', ')}.")
    }
    // ────────────────────────────────────────────────────────────────────────

    // ── Extract & validate block position (icon+text alignment) ─────────────
    String position = (attrs.position?.toString()?.trim()?.toLowerCase()) ?: 'center'
    if (!POSITION_MAP.containsKey(position)) {
      throwTagError("Attribute 'position' has an invalid value: '${position}'. " +
        "Accepted: ${POSITION_MAP.keySet().join(', ')}.")
    }
    String justifyContent = POSITION_MAP[position].justify
    String alignItems = POSITION_MAP[position].align
    // ────────────────────────────────────────────────────────────────────────

    // ── Extract & validate icon ──────────────────────────────────────────────
    // Default is the FA6 info-circle icon; pass icon="" to suppress it.
    String rawIcon = (attrs.icon?.toString()?.trim()) ?: 'fa-solid fa-circle-info'  // ← changed

    String iconHtml = ''
    if (rawIcon) {
      // Normalise short form: "fa-star" → "fa fa-star"
      // Full forms like "fa-solid fa-star" or "fas fa-star" are passed through
      // as-is since they already contain multiple classes.
      String iconClasses
      if (rawIcon.contains(' ')) {
        iconClasses = rawIcon
      } else {
        iconClasses = "fa ${rawIcon}"
      }

      // ── Extract & validate icon position (relative to the text) ────────────
      String iconPosition = (attrs.iconPosition?.toString()?.trim()?.toLowerCase()) ?: 'start'
      if (!(iconPosition in ALLOWED_ICON_POS)) {
        throwTagError("Attribute 'iconPosition' has an invalid value: '${iconPosition}'. " +
          "Accepted: start, end.")
      }

      // ── Extract & validate icon color ─────────────────────────────────────
      String iconColor = color
      if (attrs.iconColor) {
        iconColor = attrs.iconColor.toString().trim()
        if (!isValidColor(iconColor)) {
          throwTagError("Attribute 'iconColor' has an invalid value: '${iconColor}'. " +
            "Accepted formats: hex (#fff / #ffffff), rgb(), rgba(), or a CSS named color.")
        }
      }

      String iconStyle = "color:${iconColor};font-size:${fontSize}px;" +
        "vertical-align:middle;line-height:1;"

      String marginStyle = (iconPosition == 'start')
        ? "margin-right:${Math.max(4, fontSize / 4 as int)}px;"
        : "margin-left:${Math.max(4, fontSize / 4 as int)}px;"

      iconHtml = "<i class=\"${iconClasses}\" style=\"${iconStyle}${marginStyle}\" aria-hidden=\"true\"></i>"

      attrs._iconPosition = iconPosition
      attrs._iconHtml = iconHtml
    }
    // ────────────────────────────────────────────────────────────────────────

    // ── Optional pass-through attributes ────────────────────────────────────
    String cssClass = attrs.'class' ? " class=\"${attrs.'class'.toString().trim()}\"" : ''
    String idAttr = attrs.id ? " id=\"${attrs.id.toString().trim()}\"" : ''
    // ────────────────────────────────────────────────────────────────────────

    // ── Build inline style ───────────────────────────────────────────────────
    // The element is laid out as a flex container filling its parent, so the
    // icon+text group can be pinned to any of the 9 requested positions.
    String layoutStyle = "display:flex;width:100%;height:100%;box-sizing:border-box;" +
      "justify-content:${justifyContent};align-items:${alignItems};"
    String style = "${layoutStyle}color:${color};opacity:${opacityStr};" +
      "font-size:${fontSize}px;font-weight:${weight};"
    // ────────────────────────────────────────────────────────────────────────

    // ── Assemble inner content with icon in correct position ─────────────────
    String bodyContent = body()
    String innerContent
    if (iconHtml) {
      String pos = attrs._iconPosition as String
      innerContent = (pos == 'start')
        ? "${attrs._iconHtml}${bodyContent}"
        : "${bodyContent}${attrs._iconHtml}"
    } else {
      innerContent = bodyContent
    }
    // ────────────────────────────────────────────────────────────────────────

    out << "<${tag}${idAttr}${cssClass} style=\"${style}\">${innerContent}</${tag}>"
  }

  // ── Private helpers ────────────────────────────────────────────────────────

  /**
   * Returns true when the value is a plausible CSS color:
   *   - 3 or 6 digit hex  (#abc  #aabbcc)
   *   - 4 or 8 digit hex  (#abcd #aabbccdd)  — CSS Color Level 4
   *   - rgb() / rgba() / hsl() / hsla()
   *   - CSS named color (white, red, transparent, …)
   *   - "currentColor" / "inherit" / "initial" / "unset"
   */
  private static boolean isValidColor(String color) {
    if (!color) return false
    String c = color.trim()

    if (c ==~ /^#([0-9a-fA-F]{3}|[0-9a-fA-F]{4}|[0-9a-fA-F]{6}|[0-9a-fA-F]{8})$/) return true
    if (c ==~ /(?i)^(rgb|rgba|hsl|hsla)\(.*\)$/) return true
    if (c ==~ /(?i)^(currentColor|inherit|initial|unset|transparent)$/) return true

    final Set<String> NAMED = [
      'aliceblue', 'antiquewhite', 'aqua', 'aquamarine', 'azure', 'beige', 'bisque', 'black',
      'blanchedalmond', 'blue', 'blueviolet', 'brown', 'burlywood', 'cadetblue', 'chartreuse',
      'chocolate', 'coral', 'cornflowerblue', 'cornsilk', 'crimson', 'cyan', 'darkblue',
      'darkcyan', 'darkgoldenrod', 'darkgray', 'darkgreen', 'darkgrey', 'darkkhaki',
      'darkmagenta', 'darkolivegreen', 'darkorange', 'darkorchid', 'darkred', 'darksalmon',
      'darkseagreen', 'darkslateblue', 'darkslategray', 'darkslategrey', 'darkturquoise',
      'darkviolet', 'deeppink', 'deepskyblue', 'dimgray', 'dimgrey', 'dodgerblue', 'firebrick',
      'floralwhite', 'forestgreen', 'fuchsia', 'gainsboro', 'ghostwhite', 'gold', 'goldenrod',
      'gray', 'green', 'greenyellow', 'grey', 'honeydew', 'hotpink', 'indianred', 'indigo',
      'ivory', 'khaki', 'lavender', 'lavenderblush', 'lawngreen', 'lemonchiffon', 'lightblue',
      'lightcoral', 'lightcyan', 'lightgoldenrodyellow', 'lightgray', 'lightgreen', 'lightgrey',
      'lightpink', 'lightsalmon', 'lightseagreen', 'lightskyblue', 'lightslategray',
      'lightslategrey', 'lightsteelblue', 'lightyellow', 'lime', 'limegreen', 'linen',
      'magenta', 'maroon', 'mediumaquamarine', 'mediumblue', 'mediumorchid', 'mediumpurple',
      'mediumseagreen', 'mediumslateblue', 'mediumspringgreen', 'mediumturquoise',
      'mediumvioletred', 'midnightblue', 'mintcream', 'mistyrose', 'moccasin', 'navajowhite',
      'navy', 'oldlace', 'olive', 'olivedrab', 'orange', 'orangered', 'orchid', 'palegoldenrod',
      'palegreen', 'paleturquoise', 'palevioletred', 'papayawhip', 'peachpuff', 'peru', 'pink',
      'plum', 'powderblue', 'purple', 'rebeccapurple', 'red', 'rosybrown', 'royalblue',
      'saddlebrown', 'salmon', 'sandybrown', 'seagreen', 'seashell', 'sienna', 'silver',
      'skyblue', 'slateblue', 'slategray', 'slategrey', 'snow', 'springgreen', 'steelblue',
      'tan', 'teal', 'thistle', 'tomato', 'turquoise', 'violet', 'wheat', 'white', 'whitesmoke',
      'yellow', 'yellowgreen'
    ] as Set
    return c.toLowerCase() in NAMED
  }
}
