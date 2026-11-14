package com.murad.app.matrimony.eyecandy

import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class FireWorksTagLibSpec extends Specification implements TagLibUnitTest<FireWorksTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
