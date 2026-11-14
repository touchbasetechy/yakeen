package com.murad.app.matrimony

import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification

class ChatWebSocketControllerSpec extends Specification implements ControllerUnitTest<ChatWebSocketController> {

     void "test index action"() {
        when:
        controller.index()

        then:
        status == 200

     }
}
