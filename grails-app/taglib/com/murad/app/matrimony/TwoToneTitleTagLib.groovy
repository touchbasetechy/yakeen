package com.murad.app.matrimony

/**
 * <title:twotone/> renders style 15 - Two-tone split title.
 *
 * The first word is muted, the rest is full-weight. Reads like a hero intro
 * line - useful when the title has a "soft lead-in, strong payoff" structure.
 *
 * Attributes:
 *   muted  (required) the muted lead-in word(s)
 *   strong (required, unless a body is supplied) the full-weight payoff text
 *   tag    (optional) wrapping heading element, defaults to 'h2'
 *
 * Usage:
 *   <title:twotone muted="Real">Success Stories</title:twotone>
 *   <title:twotone muted="Real" strong="Success Stories"/>
 *
 * CSS is injected automatically the first time this tag is used on a given
 * request/page - no manual stylesheet entry required.
 */
class TwoToneTitleTagLib {

  static namespace = 'title'

  // Guard flag stored on the request so the <style> block is only
  // emitted once per request, no matter how many times the tag is used.
  private static final String CSS_INJECTED_ATTR = 'title_twotone_css_injected'

  private static final String CSS = '''
<style data-taglib="title:twotone">
.title-twotone { margin: 0; font-size: 1.9rem; font-weight: 800; color: #f4f5f7; }
.title-twotone .muted { color: rgba(244,245,247,0.35); font-weight: 700; }
</style>
'''

  def twotone = { attrs, body ->
    injectCssOnce()

    def tagName = attrs.remove('tag') ?: 'h2'
    def muted = attrs.remove('muted')
    def strongText = attrs.remove('strong')
    if (strongText == null || strongText == '') {
      def bodyContent = body()
      strongText = bodyContent?.toString()?.trim()
    }

    out << "<${tagName} class=\"title-twotone\">"
    if (muted) {
      out << "<span class=\"muted\">${muted.encodeAsHTML()}</span> "
    }
    out << "${strongText?.encodeAsHTML()}"
    out << "</${tagName}>"
  }

  /**
   * Writes the required CSS to the output stream the first time this
   * taglib is invoked during a request, then marks the request so it
   * won't be written again.
   */
  private void injectCssOnce() {
    def req = request
    if (req?.getAttribute(CSS_INJECTED_ATTR) == true) {
      return
    }
    out << CSS
    req?.setAttribute(CSS_INJECTED_ATTR, true)
  }
}
