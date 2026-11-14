package com.murad.app.matrimony

import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class SlideAnimationStylesTagLibSpec extends Specification implements TagLibUnitTest<SlideAnimationStylesTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
