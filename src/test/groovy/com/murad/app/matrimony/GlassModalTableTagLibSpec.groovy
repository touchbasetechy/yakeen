package com.murad.app.matrimony

import com.murad.app.matrimony.table.GlassModalTableTagLib
import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class GlassModalTableTagLibSpec extends Specification implements TagLibUnitTest<GlassModalTableTagLib> {

  void "test simple tag as method"() {
    expect:
    tagLib.simple()
  }
}
