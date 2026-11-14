package com.murad.app.matrimony

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class InterestSpec extends Specification implements DomainUnitTest<Interest> {

     void "test domain constraints"() {
        when:
        Interest domain = new Interest()
        //TODO: Set domain props here

        then:
        domain.validate()
     }
}
