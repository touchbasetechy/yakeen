package com.murad.app.matrimony

import org.grails.datastore.gorm.GormEntity

class FamilyDetails implements GormEntity<FamilyDetails> {
  String maritalStatus
  String children
  String childrenStatus
  String height
  String weight
  String familyStatus
  String familyType
  String familyValues
  String disabilities

  static belongsTo = [person: Person]
  static constraints = {
    maritalStatus nullable: true
    children nullable: true
    childrenStatus nullable: true
    height nullable: true
    weight nullable: true
    familyStatus nullable: true
    familyType nullable: true
    familyValues nullable: true
    disabilities nullable: true
  }
}
