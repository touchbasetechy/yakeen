package com.murad.app.matrimony.slideinsheet

import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class GlassCurrencyBottomSheetTagLibSpec extends Specification implements TagLibUnitTest<GlassCurrencyBottomSheetTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
