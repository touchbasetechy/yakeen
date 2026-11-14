package com.murad.app.matrimony

import com.murad.app.matrimony.AdminDBoardController
import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification

class AdminDBoardControllerSpec extends Specification implements ControllerUnitTest<AdminDBoardController> {

  void "test index action"() {
    when:
    controller.index()

    then:
    status == 200

  }
}
