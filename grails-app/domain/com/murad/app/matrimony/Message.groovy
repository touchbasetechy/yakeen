package com.murad.app.matrimony

import grails.gorm.annotation.Entity
import groovy.transform.ToString
import org.grails.datastore.gorm.GormEntity

@Entity
@ToString(includeNames = true, includeFields = true)
class Message implements GormEntity<Message> {
  String content
  String messageType // 'TEXT', 'IMAGE', 'FILE'
  String attachmentUrl
  Person sender
  ChatRoom chatRoom
  Person recipient // For private messages
  Date timestamp
  boolean read = false
  Date readAt

  static constraints = {
    content nullable: false, maxSize: 10000
    messageType inList: ['TEXT', 'IMAGE', 'FILE']
    attachmentUrl nullable: true, maxSize: 500
    sender nullable: false
    chatRoom nullable: true
    recipient nullable: true
    readAt nullable: true
  }

  static mapping = {
    content type: 'text'
    timestamp index: true
    chatRoom index: true
    sender index: true
    recipient index: true
    read index: true
    sort timestamp: 'asc'
  }

  String getFormattedContent() {
    if (content.length() > 100) {
      return content.substring(0, 97) + "..."
    }
    return content
  }
}
