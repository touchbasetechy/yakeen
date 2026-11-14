package com.murad.app.matrimony

import com.murad.app.matrimony.textinput.FormattedNumericFieldTagLib
import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class FormattedNumericFieldTagLibSpec extends Specification implements TagLibUnitTest<FormattedNumericFieldTagLib> {

  void "test simple tag as method"() {
    expect:
    tagLib.simple()
  }
}
