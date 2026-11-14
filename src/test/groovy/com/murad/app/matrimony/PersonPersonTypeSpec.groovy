package com.murad.app.matrimony

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class PersonPersonTypeSpec extends Specification implements DomainUnitTest<PersonPersonType> {

     void "test domain constraints"() {
        when:
        PersonPersonType domain = new PersonPersonType()
        //TODO: Set domain props here

        then:
        domain.validate()
     }
}
