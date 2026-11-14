package com.murad.app.matrimony

import com.murad.app.matrimony.PasswordResetController
import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification

class PasswordResetControllerSpec extends Specification implements ControllerUnitTest<PasswordResetController> {

  void "test index action"() {
    when:
    controller.index()

    then:
    status == 200

  }
}
