package com.murad.app.matrimony

import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class GlassmorphicPopOverTagLibSpec extends Specification implements TagLibUnitTest<GlassmorphicPopOverTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
