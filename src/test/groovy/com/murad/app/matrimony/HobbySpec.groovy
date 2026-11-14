package com.murad.app.matrimony

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class HobbySpec extends Specification implements DomainUnitTest<Hobby> {

     void "test domain constraints"() {
        when:
        Hobby domain = new Hobby()
        //TODO: Set domain props here

        then:
        domain.validate()
     }
}
