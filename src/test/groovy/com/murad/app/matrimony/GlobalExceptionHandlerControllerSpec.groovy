package com.murad.app.matrimony

import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification

class GlobalExceptionHandlerControllerSpec extends Specification implements ControllerUnitTest<GlobalExceptionHandlerController> {

     void "test index action"() {
        when:
        controller.index()

        then:
        status == 200

     }
}
