package com.murad.app.matrimony

import com.murad.app.matrimony.textinput.PasswordFieldTagLib
import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class PasswordFieldTagLibSpec extends Specification implements TagLibUnitTest<PasswordFieldTagLib> {

  void "test simple tag as method"() {
    expect:
    tagLib.simple()
  }
}
