package com.murad.app.matrimony

import com.murad.app.matrimony.slider.GlassRangeSliderTagLib
import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class GlassRangeSliderTagLibSpec extends Specification implements TagLibUnitTest<GlassRangeSliderTagLib> {

  void "test simple tag as method"() {
    expect:
    tagLib.simple()
  }
}
