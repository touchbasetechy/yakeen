package com.murad.app.matrimony

import com.murad.app.matrimony.card.FeatureCardTagLib
import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class FeatureCardTagLibSpec extends Specification implements TagLibUnitTest<FeatureCardTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
