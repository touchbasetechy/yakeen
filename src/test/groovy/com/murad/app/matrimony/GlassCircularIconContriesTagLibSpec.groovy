package com.murad.app.matrimony

import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class GlassCircularIconContriesTagLibSpec extends Specification implements TagLibUnitTest<GlassCircularIconContriesTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
