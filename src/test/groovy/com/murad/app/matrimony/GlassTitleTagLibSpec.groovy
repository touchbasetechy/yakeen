package com.murad.app.matrimony

import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class GlassTitleTagLibSpec extends Specification implements TagLibUnitTest<GlassTitleTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
