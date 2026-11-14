package com.murad.app.matrimony

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class ShortlistSpec extends Specification implements DomainUnitTest<Shortlist> {

     void "test domain constraints"() {
        when:
        Shortlist domain = new Shortlist()
        //TODO: Set domain props here

        then:
        domain.validate()
     }
}
