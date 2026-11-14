package com.murad.app.matrimony

import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification

class InterestControllerSpec extends Specification implements ControllerUnitTest<InterestController> {

     void "test index action"() {
        when:
        controller.index()

        then:
        status == 200

     }
}
