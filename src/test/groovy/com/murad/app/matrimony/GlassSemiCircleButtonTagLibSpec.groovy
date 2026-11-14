package com.murad.app.matrimony

import com.murad.app.matrimony.button.GlassSemiCircleButtonTagLib
import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class GlassSemiCircleButtonTagLibSpec extends Specification implements TagLibUnitTest<GlassSemiCircleButtonTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
