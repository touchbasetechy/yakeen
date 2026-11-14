package com.murad.app.matrimony

import com.murad.app.matrimony.card.GlassCardTagLib
import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class GlassCardTagLibSpec extends Specification implements TagLibUnitTest<GlassCardTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
