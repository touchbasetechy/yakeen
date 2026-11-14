package com.murad.app.matrimony

import com.murad.app.matrimony.button.GlassmorphicAdvancedButtonTagLib
import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class GlassmorphicAdvancedButtonTagLibSpec extends Specification implements TagLibUnitTest<GlassmorphicAdvancedButtonTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
