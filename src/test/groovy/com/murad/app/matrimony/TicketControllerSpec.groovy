package com.murad.app.matrimony

import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification

class TicketControllerSpec extends Specification implements ControllerUnitTest<TicketController> {

     void "test index action"() {
        when:
        controller.index()

        then:
        status == 200

     }
}
