package com.murad.app.matrimony

import com.murad.app.matrimony.PersonDetails
import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class PersonDetailsSpec extends Specification implements DomainUnitTest<PersonDetails> {

  void "test domain constraints"() {
    when:
    PersonDetails domain = new PersonDetails()
    //TODO: Set domain props here

    then:
    domain.validate()
  }
}
