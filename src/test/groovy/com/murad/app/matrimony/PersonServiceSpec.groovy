package com.murad.app.matrimony

import grails.testing.services.ServiceUnitTest
import spock.lang.Specification

class PersonServiceSpec extends Specification implements ServiceUnitTest<PersonService> {

     void "test something"() {
        expect:
        service.doSomething()
     }
}
