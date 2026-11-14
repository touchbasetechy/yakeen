package com.murad.app.matrimony

import com.murad.app.matrimony.Message
import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class MessageSpec extends Specification implements DomainUnitTest<Message> {

  void "test domain constraints"() {
    when:
    Message domain = new Message()
    //TODO: Set domain props here

    then:
    domain.validate()
  }
}
