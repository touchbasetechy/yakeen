package com.murad.app.matrimony

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class PersonHobbySpec extends Specification implements DomainUnitTest<PersonHobby> {

     void "test domain constraints"() {
        when:
        PersonHobby domain = new PersonHobby()
        //TODO: Set domain props here

        then:
        domain.validate()
     }
}
