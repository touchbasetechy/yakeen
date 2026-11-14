package com.murad.app.matrimony

import com.murad.app.matrimony.toggle.GlassToggleTagLib
import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class GlassToggleTagLibSpec extends Specification implements TagLibUnitTest<GlassToggleTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
