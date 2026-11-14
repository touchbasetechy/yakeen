package com.murad.app.matrimony.card

import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class UserCardTagLibSpec extends Specification implements TagLibUnitTest<UserCardTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
