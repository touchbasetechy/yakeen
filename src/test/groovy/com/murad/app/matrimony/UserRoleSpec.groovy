package com.murad.app.matrimony

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class UserRoleSpec extends Specification implements DomainUnitTest<PersonType> {

  void "test domain constraints"() {
    when:
    PersonType domain = new PersonType()
    //TODO: Set domain props here

    then:
    domain.validate()
  }
}
