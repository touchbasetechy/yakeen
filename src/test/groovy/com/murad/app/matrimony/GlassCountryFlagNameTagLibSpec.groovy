package com.murad.app.matrimony

import com.murad.app.matrimony.slideinsheet.GlassCountryNameBottomSheetTagLib
import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class GlassCountryFlagNameTagLibSpec extends Specification implements TagLibUnitTest<GlassCountryNameBottomSheetTagLib> {

  void "test simple tag as method"() {
    expect:
    tagLib.simple()
  }
}
