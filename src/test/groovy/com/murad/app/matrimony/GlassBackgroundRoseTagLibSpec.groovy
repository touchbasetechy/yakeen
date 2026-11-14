package com.murad.app.matrimony

import com.murad.app.matrimony.background.GlassBackgroundRoseTagLib
import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class GlassBackgroundRoseTagLibSpec extends Specification implements TagLibUnitTest<GlassBackgroundRoseTagLib> {

  void "test simple tag as method"() {
    expect:
    tagLib.simple()
  }
}
