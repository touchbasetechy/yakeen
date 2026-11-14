package com.murad.app.matrimony

import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class SlideInLeftTagLibSpec extends Specification implements TagLibUnitTest<SlideInLeftTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
