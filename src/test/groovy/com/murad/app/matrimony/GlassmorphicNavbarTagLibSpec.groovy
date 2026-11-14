package com.murad.app.matrimony

import com.murad.app.matrimony.page.GlassmorphicNavbarTagLib
import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class GlassmorphicNavbarTagLibSpec extends Specification implements TagLibUnitTest<GlassmorphicNavbarTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
