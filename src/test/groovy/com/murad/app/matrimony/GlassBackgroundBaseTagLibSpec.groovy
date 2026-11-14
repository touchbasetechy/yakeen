package com.murad.app.matrimony

import com.murad.app.matrimony.background.GlassBackgroundBaseTagLib
import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class GlassBackgroundBaseTagLibSpec extends Specification implements TagLibUnitTest<GlassBackgroundBaseTagLib> {

  void "test simple tag as method"() {
    expect:
    tagLib.simple()
  }
}
