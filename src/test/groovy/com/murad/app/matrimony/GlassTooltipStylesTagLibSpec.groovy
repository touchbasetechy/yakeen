package com.murad.app.matrimony

import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class GlassTooltipStylesTagLibSpec extends Specification implements TagLibUnitTest<GlassTooltipStylesTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
