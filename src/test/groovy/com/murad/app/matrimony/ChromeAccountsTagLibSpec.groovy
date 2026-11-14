package com.murad.app.matrimony

import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class ChromeAccountsTagLibSpec extends Specification implements TagLibUnitTest<ChromeAccountTagLib> {

  void "test simple tag as method"() {
    expect:
    tagLib.simple()
  }
}
