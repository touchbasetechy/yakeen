package com.murad.app.matrimony

import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class GlassmorphicTextTagLibSpec extends Specification implements TagLibUnitTest<GlassmorphicTextTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
