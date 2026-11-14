// grails-app/taglib/com/murad/app/matrimony/ListItemTagLib.groovy
//
// NOTE: This project already has a taglib providing glass:glassCard,
// glass:whiteAnchor and glass:whiteAnchorStyles under the "glass" namespace.
// If that file already exists, copy AVATAR_PALETTE, LIST_ITEM_STYLES, the
// emitListItemStylesOnce() helper and the `listItem` / `listItemStyles`
// closures into it instead of adding a second class with the same namespace.

package com.murad.app.matrimony

class ListItemTagLib {

  static namespace = "glass"

  private static final String STYLES_FLAG = 'glass.listItemStylesEmitted'

  // Gradient pairs the avatar background is picked from. Add/remove entries
  // freely - the palette size only affects how many distinct colors rotate.
  private static final List<List<String>> AVATAR_PALETTE = [
    ['#7f9cf5', '#f472b6'],
    ['#34d399', '#22d3ee'],
    ['#fbbf24', '#f97316'],
    ['#a78bfa', '#ec4899'],
    ['#60a5fa', '#818cf8'],
    ['#f87171', '#fb923c'],
    ['#2dd4bf', '#38bdf8'],
    ['#c084fc', '#e879f9']
  ]

  // Motion styles for glass:listItem. Emitted automatically (once per request)
  // by listItem, or explicitly via <glass:listItemStyles/> (e.g. in <head>).
  // Triple-single-quoted so Groovy does no interpolation.
  private static final String LIST_ITEM_STYLES = '''
@keyframes identity-slide-in {
  from { opacity: 0; transform: translateX(-24px); }
  to   { opacity: 1; transform: translateX(0); }
}
@keyframes identity-avatar-pop {
  0%   { opacity: 0; transform: scale(0.5); }
  65%  { opacity: 1; transform: scale(1.12); }
  100% { opacity: 1; transform: scale(1); }
}

/* `backwards` fill: hidden/offset during the delay, then back to normal styles,
   so the hover transforms below still work (`forwards` would pin the transform). */
.identity-list > li {
  animation: identity-slide-in 0.45s cubic-bezier(.2, .8, .2, 1) backwards;
  animation-delay: calc(min(var(--i, 0), 10) * 70ms);
}

.identity-list .avatar {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  animation: identity-avatar-pop 0.5s cubic-bezier(.34, 1.56, .64, 1) backwards;
  animation-delay: calc(min(var(--i, 0), 10) * 70ms + 180ms);
  transition: box-shadow 0.2s ease;
}

.identity-list .identity-link {
  display: flex;
  transition: transform 0.2s ease, background-color 0.2s ease;
}
.identity-list .identity-link:hover,
.identity-list .identity-link:focus-visible {
  transform: translateX(6px);
}
.identity-list .identity-link:hover .avatar,
.identity-list .identity-link:focus-visible .avatar {
  box-shadow: 0 4px 14px rgba(0, 0, 0, 0.25);
}

@media (prefers-reduced-motion: reduce) {
  .identity-list > li,
  .identity-list .avatar { animation: none; }
  .identity-list .identity-link,
  .identity-list .avatar { transition: none; }
  .identity-list .identity-link:hover,
  .identity-list .identity-link:focus-visible { transform: none; }
}
'''

  // Writes the <style> block the first time it is called in a request.
  private void emitListItemStylesOnce() {
    if (request.getAttribute(STYLES_FLAG)) {
      return
    }
    request.setAttribute(STYLES_FLAG, true)
    out << '<style id="glass-list-item-styles">' << LIST_ITEM_STYLES << '</style>'
  }

  /**
   * Optional: place <glass:listItemStyles/> in <head> to load the list item
   * styles up front. Safe to call more than once; styles are only emitted once.
   */
  def listItemStyles = { attrs ->
    emitListItemStylesOnce()
  }

  /**
   * Renders a single "signed in identity" row for the identity list.
   * The animation styles are included automatically the first time this tag
   * runs in a request, so no separate CSS file or include is needed.
   *
   * Attributes:
   *   uri     - (required) link target for this identity's dashboard, e.g. "/u/${idn.slot}/dashboard"
   *   name    - (optional) display name shown as the identity name; its first
   *             letter is used as the avatar initial when present
   *   initial - (optional) fallback avatar initial used only when `name` is blank
   *   email   - (required) email address shown as the identity email
   *   index   - (optional) 0-based position in the list. Exposed to CSS as
   *             the --i custom property to drive the staggered delays. Defaults to 0.
   *
   * Typical use (wrap the rows in an element with class "identity-list"):
   *   <ul class="identity-list">
   *     <g:each in="${identities}" var="idn" status="i">
   *       <glass:listItem index="${i}" uri="/u/${idn.slot}/dashboard"
   *                       name="${idn.name}" email="${idn.email}"/>
   *     </g:each>
   *   </ul>
   */
  def listItem = { attrs ->
    emitListItemStylesOnce()

    def displayName = attrs.name?.toString()
    def avatarLetter = displayName?.trim() ? displayName.trim().substring(0, 1).toUpperCase()
      : (attrs.initial?.toString() ?: '')

    // Pick a color for this row deterministically from the identity's own
    // data, so the same identity always gets the same color but different
    // identities in the list get visually distinct avatars.
    def colorSeed = (displayName?.trim() ?: attrs.email?.toString() ?: attrs.initial?.toString() ?: attrs.uri?.toString() ?: '')
    // Mask the sign bit instead of Math.abs(): abs(Integer.MIN_VALUE) is still negative.
    int paletteIndex = (colorSeed.hashCode() & 0x7fffffff) % AVATAR_PALETTE.size()
    def (colorStart, colorEnd) = AVATAR_PALETTE[paletteIndex]
    def avatarStyle = "background: linear-gradient(135deg, ${colorStart}, ${colorEnd});"

    // Stagger index -> CSS variable. Coerce defensively so a bad value can't
    // break out of the style attribute.
    int idx = 0
    try {
      idx = Math.max(0, (attrs.index ?: 0) as int)
    } catch (ignored) {
      idx = 0
    }

    // User-controlled text is HTML-encoded here. If you have set
    // `defaultEncodeAs` to 'html' for taglibs, remove the encodeAsHTML() calls
    // to avoid double-encoding.
    def safeLetter = avatarLetter?.encodeAsHTML()
    def safeName = attrs.name?.toString()?.encodeAsHTML() ?: ''
    def safeEmail = attrs.email?.toString()?.encodeAsHTML() ?: ''

    out << "<li style=\"--i: ${idx}\">"
    // Build the link body as a returned string rather than writing to `out`
    // inside the closure, so output order is predictable across Grails versions.
    out << g.link(uri: attrs.uri, class: 'identity-link') {
      """<span class="avatar" style="${avatarStyle}">${safeLetter}</span>""" +
        """<span class="identity-meta">""" +
        """<span class="identity-name">${safeName}</span>""" +
        """<span class="identity-email">${safeEmail}</span>""" +
        """</span>"""
    }
    out << '</li>'
  }
}
