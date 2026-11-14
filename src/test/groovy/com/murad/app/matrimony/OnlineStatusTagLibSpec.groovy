package com.murad.app.matrimony

import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class OnlineStatusTagLibSpec extends Specification implements TagLibUnitTest<OnlineStatusTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
