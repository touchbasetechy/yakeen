package com.murad.app.matrimony

import grails.gorm.services.Query
import grails.gorm.services.Service

@Service(Message)
interface MessageDataService {

  @Query("""
        select m from ${Message m}
        where (${m.sender} = $person1 and ${m.recipient} = $person2)
           or (${m.sender} = $person2 and ${m.recipient} = $person1)
        order by ${m.timestamp} desc
    """)
  List<Message> findConversation(Person person1, Person person2)
}
