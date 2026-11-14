package com.murad.app.matrimony

import com.murad.app.matrimony.card.GlassmorphicAdvancedCardTagLib
import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class GlassmorphicAdvancedCardTagLibSpec extends Specification implements TagLibUnitTest<GlassmorphicAdvancedCardTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
