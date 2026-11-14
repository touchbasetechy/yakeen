package com.murad.app.matrimony

import grails.gorm.annotation.Entity
import groovy.transform.ToString

@Entity
@ToString(includes = ['id', 'sender', 'roomId', 'content'])
class ChatMessage {

  String sender             // username of sender
  String senderDisplayName
  String roomId             // logical chat-room identifier
  String content            // message body
  String messageType = 'TEXT'   // FIX: default set here on the field, NOT in constraints
  Date dateCreated
  Date lastUpdated
  Boolean deleted = false

  static constraints = {
    sender blank: false, maxSize: 100
    senderDisplayName blank: true, nullable: true, maxSize: 150
    roomId blank: false, maxSize: 100
    content blank: false, maxSize: 4000
    messageType blank: false, inList: ['TEXT', 'IMAGE', 'SYSTEM']
    // FIX: removed defaultValue: 'TEXT' — 'defaultValue' is not a valid Grails
    //      constraint; GORM passes it raw to PostgreSQL as a DEFAULT expression,
    //      where 'TEXT' is interpreted as a column reference, not a string literal,
    //      causing: ERROR: cannot use column reference in DEFAULT expression
    deleted nullable: false
  }

  static mapping = {
    table 'chat_message'
    content type: 'text'
    dateCreated index: true
    roomId index: true
    sort 'dateCreated'
    order 'asc'
  }
}
