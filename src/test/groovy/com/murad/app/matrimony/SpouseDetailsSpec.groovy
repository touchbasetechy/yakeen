package com.murad.app.matrimony

import com.murad.app.matrimony.SpouseDetails
import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class SpouseDetailsSpec extends Specification implements DomainUnitTest<SpouseDetails> {

  void "test domain constraints"() {
    when:
    SpouseDetails domain = new SpouseDetails()
    //TODO: Set domain props here

    then:
    domain.validate()
  }
}
