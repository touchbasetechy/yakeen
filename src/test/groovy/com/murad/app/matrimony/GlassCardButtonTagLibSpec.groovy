package com.murad.app.matrimony

import com.murad.app.matrimony.button.GlassCardButtonTagLib
import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class GlassCardButtonTagLibSpec extends Specification implements TagLibUnitTest<GlassCardButtonTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
