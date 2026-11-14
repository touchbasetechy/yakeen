package com.murad.app.matrimony

import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class GlassAlertTagLibSpec extends Specification implements TagLibUnitTest<GlassAlertTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
