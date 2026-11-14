package com.murad.app.matrimony

import com.murad.app.matrimony.button.ModernButtonTagLib
import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class ModernButtonTagLibSpec extends Specification implements TagLibUnitTest<ModernButtonTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
