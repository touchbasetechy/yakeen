package com.murad.app.matrimony

import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification

class ShortlistControllerSpec extends Specification implements ControllerUnitTest<ShortlistController> {

     void "test index action"() {
        when:
        controller.index()

        then:
        status == 200

     }
}
