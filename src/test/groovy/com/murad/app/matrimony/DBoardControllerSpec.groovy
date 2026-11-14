package com.murad.app.matrimony

import com.murad.app.matrimony.DBoardController
import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification

class DBoardControllerSpec extends Specification implements ControllerUnitTest<DBoardController> {

  void "test index action"() {
    when:
    controller.index()

    then:
    status == 200

  }
}
