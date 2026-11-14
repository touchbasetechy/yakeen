package com.murad.app.matrimony

import com.murad.app.matrimony.textinput.SimplePasswordFieldTagLib
import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class SimplePasswordFieldTagLibSpec extends Specification implements TagLibUnitTest<SimplePasswordFieldTagLib> {

  void "test simple tag as method"() {
    expect:
    tagLib.simple()
  }
}
