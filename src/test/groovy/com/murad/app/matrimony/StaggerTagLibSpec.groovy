package com.murad.app.matrimony

import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class StaggerTagLibSpec extends Specification implements TagLibUnitTest<StaggerTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
