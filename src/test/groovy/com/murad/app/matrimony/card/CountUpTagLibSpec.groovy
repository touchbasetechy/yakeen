package com.murad.app.matrimony.card

import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class CountUpTagLibSpec extends Specification implements TagLibUnitTest<CountUpTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
