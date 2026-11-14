package com.murad.app.matrimony

import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class ProgressBarTagLibSpec extends Specification implements TagLibUnitTest<ProgressBarTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
