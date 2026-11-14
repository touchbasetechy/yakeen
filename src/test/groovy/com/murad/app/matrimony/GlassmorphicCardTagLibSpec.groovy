package com.murad.app.matrimony

import com.murad.app.matrimony.card.GlassmorphicCardTagLib
import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class GlassmorphicCardTagLibSpec extends Specification implements TagLibUnitTest<GlassmorphicCardTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
