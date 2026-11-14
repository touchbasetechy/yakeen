package com.murad.app.matrimony

import com.murad.app.matrimony.slider.GlassWeightRangeSliderTagLib
import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class GlassWeightRangeSliderTagLibSpec extends Specification implements TagLibUnitTest<GlassWeightRangeSliderTagLib> {

  void "test simple tag as method"() {
    expect:
    tagLib.simple()
  }
}
