package com.murad.app.matrimony

import com.murad.app.matrimony.SignInController
import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification

class SignInControllerSpec extends Specification implements ControllerUnitTest<SignInController> {

  void "test index action"() {
    when:
    controller.index()

    then:
    status == 200

  }
}
