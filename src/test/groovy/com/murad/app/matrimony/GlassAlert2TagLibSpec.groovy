package com.murad.app.matrimony

import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class GlassAlert2TagLibSpec extends Specification implements TagLibUnitTest<GlassAlert2TagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
