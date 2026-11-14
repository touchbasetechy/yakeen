package com.murad.app.matrimony

import com.murad.app.matrimony.button.PrimaryButtonTagLib
import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class PrimaryButtonTagLibSpec extends Specification implements TagLibUnitTest<PrimaryButtonTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
