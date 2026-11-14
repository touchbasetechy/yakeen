package com.murad.app.matrimony

import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class GlassModalTagLibSpec extends Specification implements TagLibUnitTest<GlassModalTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
