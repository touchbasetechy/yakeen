package com.murad.app.matrimony

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class HobbyCategorySpec extends Specification implements DomainUnitTest<HobbyCategory> {

     void "test domain constraints"() {
        when:
        HobbyCategory domain = new HobbyCategory()
        //TODO: Set domain props here

        then:
        domain.validate()
     }
}
