package com.murad.app.matrimony

import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class FadeOutSlideOutTopTagLibSpec extends Specification implements TagLibUnitTest<FadeOutSlideOutTopTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
