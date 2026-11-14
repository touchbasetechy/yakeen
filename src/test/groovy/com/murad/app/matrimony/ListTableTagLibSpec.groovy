package com.murad.app.matrimony

import com.murad.app.matrimony.table.ListTableTagLib
import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class ListTableTagLibSpec extends Specification implements TagLibUnitTest<ListTableTagLib> {

  void "test simple tag as method"() {
    expect:
    tagLib.simple()
  }
}
