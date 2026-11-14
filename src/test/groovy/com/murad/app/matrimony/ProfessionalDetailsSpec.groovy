package com.murad.app.matrimony

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class ProfessionalDetailsSpec extends Specification implements DomainUnitTest<ProfessionalDetails> {

     void "test domain constraints"() {
        when:
        ProfessionalDetails domain = new ProfessionalDetails()
        //TODO: Set domain props here

        then:
        domain.validate()
     }
}
