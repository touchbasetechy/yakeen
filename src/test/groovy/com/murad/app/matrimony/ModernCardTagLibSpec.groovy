package com.murad.app.matrimony

import com.murad.app.matrimony.card.ModernCardTagLib
import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class ModernCardTagLibSpec extends Specification implements TagLibUnitTest<ModernCardTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
