package com.murad.app.matrimony.eyecandy

import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class SwLikeTagLibSpec extends Specification implements TagLibUnitTest<SwLikeTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
