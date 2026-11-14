package com.murad.app.matrimony

import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class GlassmorphicRadioTagLibSpec extends Specification implements TagLibUnitTest<GlassmorphicRadioTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
