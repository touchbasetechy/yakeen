package com.murad.app.matrimony

import org.grails.datastore.gorm.GormEntity

import java.time.LocalDateTime


class SignInLog implements GormEntity<SignInLog> {

  Person user
  LocalDateTime loggedInAt
  LocalDateTime loggedOutAt
  String status

  static mapping = {
    user fetch: 'select', cascade: 'save-update'
  }

  static constraints = {
    user nullable: true
    loggedInAt nullable: true
    loggedOutAt nullable: true
    status inList: ["Online", "Offline"]
  }
}
