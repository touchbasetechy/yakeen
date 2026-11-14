package com.murad.app.matrimony

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class GallerySpec extends Specification implements DomainUnitTest<Gallery> {

     void "test domain constraints"() {
        when:
        Gallery domain = new Gallery()
        //TODO: Set domain props here

        then:
        domain.validate()
     }
}
