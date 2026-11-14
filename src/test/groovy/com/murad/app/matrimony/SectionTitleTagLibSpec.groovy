package com.murad.app.matrimony

import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class SectionTitleTagLibSpec extends Specification implements TagLibUnitTest<SectionTitleTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
