package com.murad.app.matrimony

import com.murad.app.matrimony.SpouseprofileController
import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification

class SpouseprofileControllerSpec extends Specification implements ControllerUnitTest<SpouseprofileController> {

  void "test index action"() {
    when:
    controller.index()

    then:
    status == 200

  }
}
