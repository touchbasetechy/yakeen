package com.murad.app.matrimony

import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification

class PersonEditDBoardControllerSpec extends Specification implements ControllerUnitTest<PersonEditDBoardController> {

     void "test index action"() {
        when:
        controller.index()

        then:
        status == 200

     }
}
