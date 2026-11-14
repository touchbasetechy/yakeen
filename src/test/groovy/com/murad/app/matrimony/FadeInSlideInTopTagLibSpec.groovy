package com.murad.app.matrimony

import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class FadeInSlideInTopTagLibSpec extends Specification implements TagLibUnitTest<FadeInSlideInTopTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
