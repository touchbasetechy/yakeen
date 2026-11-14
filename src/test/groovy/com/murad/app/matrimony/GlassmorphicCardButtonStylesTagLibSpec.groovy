package com.murad.app.matrimony

import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class GlassmorphicCardButtonStylesTagLibSpec extends Specification implements TagLibUnitTest<GlassmorphicCardButtonStylesTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
