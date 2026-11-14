package com.murad.app.matrimony

import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class ModernGlassMorphismTagLibSpec extends Specification implements TagLibUnitTest<ModernGlassMorphismTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
