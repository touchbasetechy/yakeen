package com.murad.app.matrimony

import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class GlassShimmerTitleTagLibSpec extends Specification implements TagLibUnitTest<GlassShimmerTitleTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
