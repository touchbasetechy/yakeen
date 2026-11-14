package com.murad.app.matrimony

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class TicketSpec extends Specification implements DomainUnitTest<Ticket> {

     void "test domain constraints"() {
        when:
        Ticket domain = new Ticket()
        //TODO: Set domain props here

        then:
        domain.validate()
     }
}
