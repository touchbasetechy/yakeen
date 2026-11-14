package com.murad.app.matrimony

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class AdditionalSpouseDetailsSpec extends Specification implements DomainUnitTest<AdditionalSpouseDetails> {

     void "test domain constraints"() {
        when:
        AdditionalSpouseDetails domain = new AdditionalSpouseDetails()
        //TODO: Set domain props here

        then:
        domain.validate()
     }
}
