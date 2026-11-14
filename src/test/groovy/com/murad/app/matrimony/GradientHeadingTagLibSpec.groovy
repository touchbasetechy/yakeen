package com.murad.app.matrimony

import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class GradientHeadingTagLibSpec extends Specification implements TagLibUnitTest<GradientHeadingTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
