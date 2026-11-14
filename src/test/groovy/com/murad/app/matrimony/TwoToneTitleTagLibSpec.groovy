package com.murad.app.matrimony

import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class TwoToneTitleTagLibSpec extends Specification implements TagLibUnitTest<TwoToneTitleTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
