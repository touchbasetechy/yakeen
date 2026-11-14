package com.murad.app.matrimony

import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class SlideInTopTagLibSpec extends Specification implements TagLibUnitTest<SlideInTopTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
