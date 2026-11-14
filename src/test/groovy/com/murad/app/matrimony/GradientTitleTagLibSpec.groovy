package com.murad.app.matrimony

import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class GradientTitleTagLibSpec extends Specification implements TagLibUnitTest<GradientTitleTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
