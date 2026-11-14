package com.murad.app.matrimony

import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class GreenAnchorTagLibSpec extends Specification implements TagLibUnitTest<GreenAnchorTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
