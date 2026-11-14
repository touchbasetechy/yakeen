package com.murad.app.matrimony

import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class GlassmorphicPasswordFieldTagLibSpec extends Specification implements TagLibUnitTest<GlassmorphicPasswordFieldTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
