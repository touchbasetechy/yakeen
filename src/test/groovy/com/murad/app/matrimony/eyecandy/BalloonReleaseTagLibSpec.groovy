package com.murad.app.matrimony.eyecandy

import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class BalloonReleaseTagLibSpec extends Specification implements TagLibUnitTest<BalloonReleaseTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
