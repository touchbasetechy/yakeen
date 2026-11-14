// grails-app/taglib/com/murad/app/matrimony/IdentityLinkTagLib.groovy
package com.murad.app.matrimony

class IdentityLinkTagLib {
  static namespace = 'u'

  def link = { attrs, body ->
    attrs.params = attrs.params ?: [:]
    if (!attrs.params.containsKey('slot')) {
      attrs.params.slot = request.getAttribute('currentSlot')
    }
    out << g.link(attrs, body)
  }

  def form = { attrs, body ->
    attrs.params = attrs.params ?: [:]
    if (!attrs.params.containsKey('slot')) {
      attrs.params.slot = request.getAttribute('currentSlot')
    }
    out << g.form(attrs, body)
  }
}
