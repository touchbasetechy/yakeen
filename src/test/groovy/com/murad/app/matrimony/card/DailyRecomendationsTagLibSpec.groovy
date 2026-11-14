package com.murad.app.matrimony.card

import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class DailyRecomendationsTagLibSpec extends Specification implements TagLibUnitTest<DailyRecomendationsTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
