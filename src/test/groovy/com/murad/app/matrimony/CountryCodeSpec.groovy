package com.murad.app.matrimony

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class CountryCodeSpec extends Specification implements DomainUnitTest<CountryCode> {

     void "test domain constraints"() {
        when:
        CountryCode domain = new CountryCode()
        //TODO: Set domain props here

        then:
        domain.validate()
     }
}
