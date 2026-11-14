package com.murad.app.matrimony

import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class GlassNumberFieldTagLibSpec extends Specification implements TagLibUnitTest<GlassNumberFieldTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
