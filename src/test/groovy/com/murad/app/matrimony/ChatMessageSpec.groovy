package com.murad.app.matrimony

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class ChatMessageSpec extends Specification implements DomainUnitTest<ChatMessage> {

     void "test domain constraints"() {
        when:
        ChatMessage domain = new ChatMessage()
        //TODO: Set domain props here

        then:
        domain.validate()
     }
}
