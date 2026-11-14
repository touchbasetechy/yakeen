package com.murad.app.matrimony

import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification

class SuperAdminControllerControllerSpec extends Specification implements ControllerUnitTest<SuperAdminController> {

  void "test index action"() {
    when:
    controller.index()

    then:
    status == 200

  }
}
