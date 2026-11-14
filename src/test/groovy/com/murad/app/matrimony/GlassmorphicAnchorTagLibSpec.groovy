package com.murad.app.matrimony

import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class GlassmorphicAnchorTagLibSpec extends Specification implements TagLibUnitTest<GlassmorphicAnchorTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
