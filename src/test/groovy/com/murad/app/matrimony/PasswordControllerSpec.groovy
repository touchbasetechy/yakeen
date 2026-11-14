package com.murad.app.matrimony

import com.murad.app.matrimony.PasswordController
import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification

class PasswordControllerSpec extends Specification implements ControllerUnitTest<PasswordController> {

  void "test index action"() {
    when:
    controller.index()

    then:
    status == 200

  }
}
