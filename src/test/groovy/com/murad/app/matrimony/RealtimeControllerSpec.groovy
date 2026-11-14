package com.murad.app.matrimony

import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification

class RealtimeControllerSpec extends Specification implements ControllerUnitTest<RealtimeController11111> {

  void "test index action"() {
    when:
    controller.index()

    then:
    status == 200

  }
}
