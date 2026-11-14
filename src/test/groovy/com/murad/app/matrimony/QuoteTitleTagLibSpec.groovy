package com.murad.app.matrimony

import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class QuoteTitleTagLibSpec extends Specification implements TagLibUnitTest<QuoteTitleTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
