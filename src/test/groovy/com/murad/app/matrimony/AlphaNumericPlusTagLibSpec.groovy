package com.murad.app.matrimony

import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class AlphaNumericPlusTagLibSpec extends Specification implements TagLibUnitTest<AlphaNumericPlusTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
