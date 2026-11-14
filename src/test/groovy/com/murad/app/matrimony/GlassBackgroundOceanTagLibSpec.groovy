package com.murad.app.matrimony

import com.murad.app.matrimony.background.GlassBackgroundOceanTagLib
import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class GlassBackgroundOceanTagLibSpec extends Specification implements TagLibUnitTest<GlassBackgroundOceanTagLib> {

  void "test simple tag as method"() {
    expect:
    tagLib.simple()
  }
}
