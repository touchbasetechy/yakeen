package com.murad.app.matrimony

import com.murad.app.matrimony.page.SimpleNavbarTagLib
import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class SimpleNavbarTagLibSpec extends Specification implements TagLibUnitTest<SimpleNavbarTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
