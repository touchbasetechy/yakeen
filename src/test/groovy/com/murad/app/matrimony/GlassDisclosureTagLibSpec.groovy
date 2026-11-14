package com.murad.app.matrimony

import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class GlassDisclosureTagLibSpec extends Specification implements TagLibUnitTest<GlassDisclosureTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
