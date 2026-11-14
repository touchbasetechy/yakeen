package com.murad.app.matrimony

import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class UsercardTagLibSpec extends Specification implements TagLibUnitTest<UsercardTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
