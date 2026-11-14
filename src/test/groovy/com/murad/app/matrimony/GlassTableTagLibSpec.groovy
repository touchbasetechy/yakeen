package com.murad.app.matrimony

import com.murad.app.matrimony.table.GlassTableTagLib
import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class GlassTableTagLibSpec extends Specification implements TagLibUnitTest<GlassTableTagLib> {

  void "test simple tag as method"() {
    expect:
    tagLib.simple()
  }
}
