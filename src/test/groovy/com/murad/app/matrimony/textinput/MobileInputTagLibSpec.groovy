package com.murad.app.matrimony.textinput

import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class MobileInputTagLibSpec extends Specification implements TagLibUnitTest<MobileInputTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
