package com.murad.app.matrimony

import com.murad.app.matrimony.button.GlassmorphicButtonTagLib
import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class GlassmorphicButtonTagLibSpec extends Specification implements TagLibUnitTest<GlassmorphicButtonTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
