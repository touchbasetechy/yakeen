package com.murad.app.matrimony

import grails.testing.services.ServiceUnitTest
import spock.lang.Specification

class JwtTokenServiceSpec extends Specification implements ServiceUnitTest<JwtTokenService> {

     void "test something"() {
        expect:
        service.doSomething()
     }
}
