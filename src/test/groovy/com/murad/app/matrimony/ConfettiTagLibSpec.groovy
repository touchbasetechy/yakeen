package com.murad.app.matrimony

import com.murad.app.matrimony.eyecandy.ConfettiTagLib
import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class ConfettiTagLibSpec extends Specification implements TagLibUnitTest<ConfettiTagLib> {

  void "test simple tag as method"() {
    expect:
    tagLib.simple()
  }
}
