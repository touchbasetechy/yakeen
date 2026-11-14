package com.murad.app.matrimony

import com.murad.app.matrimony.page.SimpleFooterTagLib
import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class SimpleFooterTagLibSpec extends Specification implements TagLibUnitTest<SimpleFooterTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
