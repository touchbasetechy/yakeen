package com.murad.app.matrimony

import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class AlertTagLibSpec extends Specification implements TagLibUnitTest<AlertTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
