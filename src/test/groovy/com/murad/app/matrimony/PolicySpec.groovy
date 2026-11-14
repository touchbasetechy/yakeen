package com.murad.app.matrimony

import com.murad.app.matrimony.Policy
import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class PolicySpec extends Specification implements DomainUnitTest<Policy> {

  void "test domain constraints"() {
    when:
    Policy domain = new Policy()
    //TODO: Set domain props here

    then:
    domain.validate()
  }
}
