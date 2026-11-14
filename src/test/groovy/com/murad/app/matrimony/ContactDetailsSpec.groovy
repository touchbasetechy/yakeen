package com.murad.app.matrimony

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class ContactDetailsSpec extends Specification implements DomainUnitTest<ContactDetails> {

     void "test domain constraints"() {
        when:
        ContactDetails domain = new ContactDetails()
        //TODO: Set domain props here

        then:
        domain.validate()
     }
}
