package com.murad.app.matrimony

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class TicketTypeSpec extends Specification implements DomainUnitTest<TicketType> {

     void "test domain constraints"() {
        when:
        TicketType domain = new TicketType()
        //TODO: Set domain props here

        then:
        domain.validate()
     }
}
