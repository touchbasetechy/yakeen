package com.murad.app.matrimony

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class SpouseFamilyDetailsSpec extends Specification implements DomainUnitTest<SpouseFamilyDetails> {

     void "test domain constraints"() {
        when:
        SpouseFamilyDetails domain = new SpouseFamilyDetails()
        //TODO: Set domain props here

        then:
        domain.validate()
     }
}
