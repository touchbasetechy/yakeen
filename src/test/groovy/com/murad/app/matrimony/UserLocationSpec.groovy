package com.murad.app.matrimony

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class UserLocationSpec extends Specification implements DomainUnitTest<UserLocation> {

     void "test domain constraints"() {
        when:
        UserLocation domain = new UserLocation()
        //TODO: Set domain props here

        then:
        domain.validate()
     }
}
