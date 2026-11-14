package com.murad.app.matrimony

import org.grails.datastore.gorm.GormEntity

class Policy implements GormEntity<Policy> {

  boolean isActive
  boolean isBlocked
  boolean isOnHold

  static belongsTo = [person: Person]

  static constraints = {
    isActive nullable: true
    isBlocked nullable: true
    isOnHold nullable: true
  }
}
