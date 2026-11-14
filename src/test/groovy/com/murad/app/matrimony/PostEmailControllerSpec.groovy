package com.murad.app.matrimony

import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification

class PostEmailControllerSpec extends Specification implements ControllerUnitTest<PostEmailController> {

     void "test index action"() {
        when:
        controller.index()

        then:
        status == 200

     }
}
