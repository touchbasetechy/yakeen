package com.murad.app.matrimony

import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class PulseRingTagLibSpec extends Specification implements TagLibUnitTest<PulseRingTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
