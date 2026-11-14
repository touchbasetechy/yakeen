package com.murad.app.matrimony

import com.murad.app.matrimony.slideinsheet.GlassBottomSheetBaseTagLib
import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class GlassCountryPickerBaseTagLibSpec extends Specification implements TagLibUnitTest<GlassBottomSheetBaseTagLib> {

  void "test simple tag as method"() {
    expect:
    tagLib.simple()
  }
}
