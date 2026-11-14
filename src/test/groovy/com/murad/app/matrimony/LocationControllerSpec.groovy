package com.murad.app.matrimony

import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification

class LocationControllerSpec extends Specification implements ControllerUnitTest<LocationController> {

     void "test index action"() {
        when:
        controller.index()

        then:
        status == 200

     }
}
