package com.murad.app.matrimony

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class EducationDetailsSpec extends Specification implements DomainUnitTest<EducationDetails> {

     void "test domain constraints"() {
        when:
        EducationDetails domain = new EducationDetails()
        //TODO: Set domain props here

        then:
        domain.validate()
     }
}
