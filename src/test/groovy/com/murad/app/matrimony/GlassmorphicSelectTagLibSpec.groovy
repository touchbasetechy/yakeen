package com.murad.app.matrimony

import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class GlassmorphicSelectTagLibSpec extends Specification implements TagLibUnitTest<GlassmorphicSelectTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
