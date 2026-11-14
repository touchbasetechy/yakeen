package com.murad.app.matrimony

import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class GlassBadgeTagLibSpec extends Specification implements TagLibUnitTest<GlassBadgeTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
