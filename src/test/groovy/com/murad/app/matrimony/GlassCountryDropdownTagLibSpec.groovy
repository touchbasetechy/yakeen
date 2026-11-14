package com.murad.app.matrimony

import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class GlassCountryDropdownTagLibSpec extends Specification implements TagLibUnitTest<GlassCountryDropdownTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
