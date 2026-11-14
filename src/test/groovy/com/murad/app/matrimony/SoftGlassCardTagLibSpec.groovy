package com.murad.app.matrimony

import com.murad.app.matrimony.card.SoftGlassCardTagLib
import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class SoftGlassCardTagLibSpec extends Specification implements TagLibUnitTest<SoftGlassCardTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
