package com.murad.app.matrimony

import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification

class SnackBarControllerSpec extends Specification implements ControllerUnitTest<SnackBarController> {

     void "test index action"() {
        when:
        controller.index()

        then:
        status == 200

     }
}
