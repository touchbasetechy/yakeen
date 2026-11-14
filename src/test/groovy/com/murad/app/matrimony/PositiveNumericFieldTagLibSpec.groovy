package com.murad.app.matrimony

import com.murad.app.matrimony.textinput.PositiveNumericFieldTagLib
import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class PositiveNumericFieldTagLibSpec extends Specification implements TagLibUnitTest<PositiveNumericFieldTagLib> {

  void "test simple tag as method"() {
    expect:
    tagLib.simple()
  }
}
