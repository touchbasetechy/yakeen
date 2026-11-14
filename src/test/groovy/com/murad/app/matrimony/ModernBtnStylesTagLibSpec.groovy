package com.murad.app.matrimony

import com.murad.app.matrimony.button.ModernBtnStylesTagLib
import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class ModernBtnStylesTagLibSpec extends Specification implements TagLibUnitTest<ModernBtnStylesTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
