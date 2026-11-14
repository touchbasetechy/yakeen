package com.murad.app.matrimony

import com.murad.app.matrimony.card.ModernGlassCardTagLib
import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class ModernGlassCardTagLibSpec extends Specification implements TagLibUnitTest<ModernGlassCardTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
