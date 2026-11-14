package com.murad.app.matrimony

import com.murad.app.matrimony.button.GlassmorphicAdvancedCardButtonStylesTagLib
import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class GlassmorphicAdvancedCardButtonStylesTagLibSpec extends Specification implements TagLibUnitTest<GlassmorphicAdvancedCardButtonStylesTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
