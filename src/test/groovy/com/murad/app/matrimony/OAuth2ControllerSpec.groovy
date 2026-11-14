package com.murad.app.matrimony

import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification

class OAuth2ControllerSpec extends Specification implements ControllerUnitTest<OAuth2Controller> {

     void "test index action"() {
        when:
        controller.index()

        then:
        status == 200

     }
}
