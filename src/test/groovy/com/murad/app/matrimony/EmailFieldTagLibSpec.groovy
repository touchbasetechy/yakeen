package com.murad.app.matrimony

import com.murad.app.matrimony.textinput.EmailFieldTagLib
import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class EmailFieldTagLibSpec extends Specification implements TagLibUnitTest<EmailFieldTagLib> {

  void "test simple tag as method"() {
    expect:
    tagLib.simple()
  }
}
