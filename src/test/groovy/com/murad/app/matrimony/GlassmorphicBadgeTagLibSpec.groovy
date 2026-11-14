package com.murad.app.matrimony

import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class GlassmorphicBadgeTagLibSpec extends Specification implements TagLibUnitTest<GlassmorphicBadgeTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
