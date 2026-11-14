package com.murad.app.matrimony

import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class SlidInDownFromTopTagLibSpec extends Specification implements TagLibUnitTest<SlidInDownFromTopTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
