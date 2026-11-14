package com.murad.app.matrimony

import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class DateTimePickerTagLibSpec extends Specification implements TagLibUnitTest<DateTimePickerTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
