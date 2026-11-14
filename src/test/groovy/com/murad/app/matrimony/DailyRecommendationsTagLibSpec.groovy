package com.murad.app.matrimony

import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class DailyRecommendationsTagLibSpec extends Specification implements TagLibUnitTest<DailyRecommendationsTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
