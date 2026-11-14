package com.murad.app.matrimony

import org.grails.datastore.gorm.GormEntity

class SpouseDetails implements GormEntity<SpouseDetails> {
  String gender
  Integer minAge
  Integer maxAge
  String religion
  String caste
  String smokingHabit
  String drinkingHabit

  static belongsTo = [person: Person]

  static mapping = {
    table name: "spouse_details"  // Fixed syntax
  }

  static constraints = {
    gender nullable: true, blank: true
    minAge nullable: true
    maxAge nullable: true
    religion nullable: true, blank: true
    caste nullable: true, blank: true
    smokingHabit nullable: true, blank: true
    drinkingHabit nullable: true, blank: true
  }
}
