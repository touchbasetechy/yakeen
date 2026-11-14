package com.murad.app.matrimony

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class ChatRoomSpec extends Specification implements DomainUnitTest<ChatRoom> {

     void "test domain constraints"() {
        when:
        ChatRoom domain = new ChatRoom()
        //TODO: Set domain props here

        then:
        domain.validate()
     }
}
