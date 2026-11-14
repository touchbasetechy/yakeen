package com.murad.app.matrimony

import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class SlideInBottomTagLibSpec extends Specification implements TagLibUnitTest<SlideInBottomTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
