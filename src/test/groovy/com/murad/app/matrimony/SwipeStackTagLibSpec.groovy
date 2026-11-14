package com.murad.app.matrimony

import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class SwipeStackTagLibSpec extends Specification implements TagLibUnitTest<SwipeStackTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
