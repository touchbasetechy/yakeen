package com.murad.app.matrimony

import org.grails.datastore.gorm.GormEntity

import java.time.LocalDateTime

class Hobby implements GormEntity<Hobby> {
  String name
  String description
  Person createdBy
  LocalDateTime createdAt

  // Many-to-many relationship with Person
  static hasMany = [persons: Person]
  static belongsTo = Person

  static mapping = {
    table name: "hobby"
    // Let Grails handle the join table automatically
    persons lazy: true, cache: true
  }

  static constraints = {
    name blank: false, size: 1..100
    description nullable: true, size: 0..500
    createdBy nullable: true
    createdAt nullable: true
    persons nullable: true
  }


}
