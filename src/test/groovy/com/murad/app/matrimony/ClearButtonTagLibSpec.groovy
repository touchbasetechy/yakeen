package com.murad.app.matrimony

import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class ClearButtonTagLibSpec extends Specification implements TagLibUnitTest<ClearButtonTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
