package com.murad.app.matrimony

import com.murad.app.matrimony.SearchController
import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification

class SearchControllerSpec extends Specification implements ControllerUnitTest<SearchController> {

  void "test index action"() {
    when:
    controller.index()

    then:
    status == 200

  }
}
