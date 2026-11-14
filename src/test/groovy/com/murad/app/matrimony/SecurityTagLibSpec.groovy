package com.murad.app.matrimony

import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class SecurityTagLibSpec extends Specification implements TagLibUnitTest<SecurityTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
