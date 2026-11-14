package com.murad.app.matrimony

import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class IdentityLinkTagLibSpec extends Specification implements TagLibUnitTest<IdentityLinkTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
