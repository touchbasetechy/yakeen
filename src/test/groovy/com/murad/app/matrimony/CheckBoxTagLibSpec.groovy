package com.murad.app.matrimony

import com.murad.app.matrimony.toggle.CheckBoxTagLib
import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class CheckBoxTagLibSpec extends Specification implements TagLibUnitTest<CheckBoxTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
