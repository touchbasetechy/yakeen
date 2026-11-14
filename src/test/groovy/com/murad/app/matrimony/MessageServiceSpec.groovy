package com.murad.app.matrimony

import grails.testing.services.ServiceUnitTest
import spock.lang.Specification

class MessageServiceSpec extends Specification implements ServiceUnitTest<MessageService> {

     void "test something"() {
        expect:
        service.doSomething()
     }
}
