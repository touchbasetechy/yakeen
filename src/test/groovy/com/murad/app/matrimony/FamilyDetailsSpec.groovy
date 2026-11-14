package com.murad.app.matrimony

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class FamilyDetailsSpec extends Specification implements DomainUnitTest<FamilyDetails> {

     void "test domain constraints"() {
        when:
        FamilyDetails domain = new FamilyDetails()
        //TODO: Set domain props here

        then:
        domain.validate()
     }
}
