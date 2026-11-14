package com.murad.app.matrimony

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class OAuthIDSpec extends Specification implements DomainUnitTest<OAuthID> {

     void "test domain constraints"() {
        when:
        OAuthID domain = new OAuthID()
        //TODO: Set domain props here

        then:
        domain.validate()
     }
}
