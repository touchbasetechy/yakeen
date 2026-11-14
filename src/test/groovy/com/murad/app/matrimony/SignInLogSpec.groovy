package com.murad.app.matrimony

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class SignInLogSpec extends Specification implements DomainUnitTest<SignInLog> {

     void "test domain constraints"() {
        when:
        SignInLog domain = new SignInLog()
        //TODO: Set domain props here

        then:
        domain.validate()
     }
}
