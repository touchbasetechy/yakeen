package com.murad.app.matrimony.eyecandy

import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class StarBurstTagLibSpec extends Specification implements TagLibUnitTest<StarBurstTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
