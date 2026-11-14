package com.murad.app.matrimony

import org.grails.datastore.gorm.GormEntity

class EducationDetails implements GormEntity<EducationDetails> {

  String name
  String institution
  String university

  String institutionLocation
  String universityLocation
  Boolean isHighest

  static belongsTo = [person: Person]

  static constraints = {
//    institution nullable: true
    university nullable: true
    institutionLocation nullable: true
    universityLocation nullable: true
    isHighest nullable: true
  }
}
