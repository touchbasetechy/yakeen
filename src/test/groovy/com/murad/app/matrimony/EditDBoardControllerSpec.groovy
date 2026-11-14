package com.murad.app.matrimony

import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification

class EditDBoardControllerSpec extends Specification implements ControllerUnitTest<EditDBoardController> {

     void "test index action"() {
        when:
        controller.index()

        then:
        status == 200

     }
}
