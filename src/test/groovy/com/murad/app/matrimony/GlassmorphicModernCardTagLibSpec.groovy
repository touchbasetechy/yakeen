package com.murad.app.matrimony

import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class GlassmorphicModernCardTagLibSpec extends Specification implements TagLibUnitTest<GlassmorphicModernCardTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
