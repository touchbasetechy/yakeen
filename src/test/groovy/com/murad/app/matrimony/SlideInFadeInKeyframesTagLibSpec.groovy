package com.murad.app.matrimony

import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class SlideInFadeInKeyframesTagLibSpec extends Specification implements TagLibUnitTest<SlideInFadeInKeyframesTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
