package com.murad.app.matrimony.textinput

import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class AlphaNumericSpaceTextFieldTagLibSpec extends Specification implements TagLibUnitTest<AlphaNumericSpaceTextFieldTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
