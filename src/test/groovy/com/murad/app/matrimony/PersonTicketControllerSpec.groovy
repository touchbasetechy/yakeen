package com.murad.app.matrimony

import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification

class PersonTicketControllerSpec extends Specification implements ControllerUnitTest<PersonTicketController> {

     void "test index action"() {
        when:
        controller.index()

        then:
        status == 200

     }
}
