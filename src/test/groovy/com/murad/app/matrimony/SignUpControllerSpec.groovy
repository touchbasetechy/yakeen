package com.murad.app.matrimony

import com.murad.app.matrimony.SignUpController
import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification

class SignUpControllerSpec extends Specification implements ControllerUnitTest<SignUpController> {

  void "test index action"() {
    when:
    controller.index()

    then:
    status == 200

  }
}
