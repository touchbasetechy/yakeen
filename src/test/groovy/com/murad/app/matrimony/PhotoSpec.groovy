package com.murad.app.matrimony


import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class PhotoSpec extends Specification implements DomainUnitTest<Photo> {

  void "test domain constraints"() {
    when:
    Photo domain = new Photo()
    //TODO: Set domain props here

    then:
    domain.validate()
  }
}
