package com.murad.app.matrimony

import grails.testing.services.ServiceUnitTest
import spock.lang.Specification

class MailerSendServiceSpec extends Specification implements ServiceUnitTest<MailerSendService> {

     void "test something"() {
        expect:
        service.doSomething()
     }
}
