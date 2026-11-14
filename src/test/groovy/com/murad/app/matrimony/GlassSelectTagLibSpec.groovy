package com.murad.app.matrimony

import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class GlassSelectTagLibSpec extends Specification implements TagLibUnitTest<GlassSelectTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
