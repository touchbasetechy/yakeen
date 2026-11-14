package com.murad.app.matrimony

import grails.gorm.annotation.Entity
import groovy.transform.ToString

@Entity
@ToString(includes = ['id', 'roomId', 'name'])
class ChatRoom {

  String roomId          // slug / URL-safe identifier  e.g. "general"
  String name            // display name
  String description
  Boolean privateRoom = false
  Date dateCreated
  Date lastUpdated

  static constraints = {
    roomId blank: false, unique: true, maxSize: 100, matches: '[a-z0-9_-]+'
    name blank: false, maxSize: 150
    description nullable: true, maxSize: 500
    privateRoom nullable: false
  }

  static mapping = {
    table 'chat_room'
    roomId index: true
  }
}
