package com.murad.app.matrimony

import com.murad.app.matrimony.slideinsheet.GlassCountryBottomSheetTagLib
import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class GlassCountryBottomSheetTagLibSpec extends Specification implements TagLibUnitTest<GlassCountryBottomSheetTagLib> {

  void "test simple tag as method"() {
    expect:
    tagLib.simple()
  }
}
