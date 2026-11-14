package com.murad.app.matrimony

import com.murad.app.matrimony.slider.GlassSliderCommonTagLib
import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class GlassSliderCommonTagLibSpec extends Specification implements TagLibUnitTest<GlassSliderCommonTagLib> {

  void "test simple tag as method"() {
    expect:
    tagLib.simple()
  }
}
