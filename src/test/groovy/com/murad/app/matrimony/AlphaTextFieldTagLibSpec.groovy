package com.murad.app.matrimony

import com.murad.app.matrimony.textinput.AlphaTextFieldTagLib
import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class AlphaTextFieldTagLibSpec extends Specification implements TagLibUnitTest<AlphaTextFieldTagLib> {

  void "test simple tag as method"() {
    expect:
    tagLib.simple()
  }
}
