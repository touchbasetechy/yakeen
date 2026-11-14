package com.murad.app.matrimony

import org.grails.datastore.gorm.GormEntity

import java.time.LocalDateTime

class PersonDetails implements GormEntity<PersonDetails> {
  LocalDateTime dob
  BigDecimal age
  String gender
  String religion
  String caste
  String smokingHabit
  String drinkingHabit
  static belongsTo = [person: Person]

  static mapping = {
    table(name "person_details")
    version(versioned: false)
  }


  static constraints = {
  }
}
