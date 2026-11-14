import grails.gsp.TagLib

@TagLib
class EmojiPickerTagLib {

  static namespace = 'emoji'

  /**
   * Renders a WhatsApp-style emoji picker triggered by a button that opens a Bootstrap popover.
   *
   * Attributes:
   *   buttonId     - id for the trigger button (default: auto-generated)
   *   targetInput  - CSS selector of the input/textarea to append emojis into (optional)
   *   placement    - popover placement: top | bottom | left | right | auto (default: top)
   *   accentColor  - active-tab underline / hover color (default: #00a884)
   *   buttonLabel  - accessible label on the trigger button (default: 'Open emoji picker')
   *   buttonClass  - extra CSS classes on the trigger button (default: '')
   *
   * Requirements:
   *   Bootstrap 5 JS + CSS must already be loaded on the page.
   *
   * Usage:
   *   <emoji:picker />
   *   <emoji:picker targetInput="#myTextarea" placement="bottom" accentColor="#075e54" />
   *   <emoji:picker buttonClass="btn btn-outline-secondary" buttonLabel="Emoji" />
   */
  def picker = { attrs ->

    def uniqueId = attrs.buttonId ?: "emoji-btn-${System.nanoTime()}"
    def targetInput = attrs.targetInput ?: ''
    def placement = attrs.placement ?: 'top'
    def accentColor = attrs.accentColor ?: '#00a884'
    def buttonLabel = attrs.buttonLabel ?: 'Open emoji picker'
    def buttonClass = attrs.buttonClass ?: ''

    out << render(
      template: '/taglib/emojiPicker',
      model: [
        uniqueId   : uniqueId,
        targetInput: targetInput,
        placement  : placement,
        accentColor: accentColor,
        buttonLabel: buttonLabel,
        buttonClass: buttonClass,
      ]
    )
  }
}
