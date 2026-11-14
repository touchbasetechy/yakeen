package com.murad.app.matrimony

import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class GlassTabsTagLibSpec extends Specification implements TagLibUnitTest<GlassTabsTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
