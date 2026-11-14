package com.murad.app.matrimony

import com.murad.app.matrimony.card.ModernCardStylesTagLib
import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class ModernCardStylesTagLibSpec extends Specification implements TagLibUnitTest<ModernCardStylesTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
