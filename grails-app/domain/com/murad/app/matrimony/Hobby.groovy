package com.murad.app.matrimony

import org.grails.datastore.gorm.GormEntity

class Hobby implements GormEntity<Hobby> {
  String code            // stable key from JSON, e.g. "painting"
  String name            // display label, e.g. "Painting"
  String description
  boolean active = true
  HobbyCategory category // plain many-to-one (HobbyCategory.hasMany pairs with this)
  Person createdBy
  Date dateCreated = new Date()
  Date lastUpdated

  static hasMany = [persons: Person]
  static belongsTo = Person   // Person owns the many-to-many

  static mapping = {
    table name: "hobby"
    category column: 'category_id'
    code index: 'idx_hobby_code'
    persons joinTable: [name: 'person_hobbies', key: 'hobby_id', column: 'person_id']
  }

  static constraints = {
    code blank: false, unique: true, maxSize: 80
    name blank: false, maxSize: 120
    description blank: true, nullable: true, maxSize: 255
    category nullable: false
    createdBy nullable: true
    dateCreated nullable: true
    lastUpdated nullable: true
    persons nullable: true
  }

  String toString() { name }
}
