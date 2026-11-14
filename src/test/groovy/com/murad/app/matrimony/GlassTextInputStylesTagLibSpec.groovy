package com.murad.app.matrimony


import com.murad.app.matrimony.textinput.TextInputStylesTagLib
import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class GlassTextInputStylesTagLibSpec extends Specification implements TagLibUnitTest<TextInputStylesTagLib> {

  void "test simple tag as method"() {
    expect:
    tagLib.simple()
  }
}
