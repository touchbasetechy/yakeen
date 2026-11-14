package com.murad.app.matrimony

import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class TimeLineTagLibSpec extends Specification implements TagLibUnitTest<TimeLineTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
