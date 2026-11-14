package com.murad.app.matrimony

import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class SlideOutFadeOutKeyframesTagLibSpec extends Specification implements TagLibUnitTest<SlideOutFadeOutKeyframesTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
