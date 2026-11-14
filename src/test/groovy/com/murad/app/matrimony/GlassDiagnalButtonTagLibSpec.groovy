package com.murad.app.matrimony

import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class GlassDiagnalButtonTagLibSpec extends Specification implements TagLibUnitTest<GlassDiagnalButtonTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
