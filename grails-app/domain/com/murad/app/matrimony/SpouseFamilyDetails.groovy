package com.murad.app.matrimony

import org.grails.datastore.gorm.GormEntity

class SpouseFamilyDetails implements GormEntity<SpouseFamilyDetails> {
  String maritalStatus
  String children
  String childrenStatus
  String minHeight
  String minWeight
  String maxHeight
  String maxWeight
  String heightUnit
  String weightUnit
  String familyStatus
  String familyType
  String familyValues
  String disabilities

  static belongsTo = [person: Person]
  static constraints = {
    maritalStatus nullable: true, blank: true
    children nullable: true, blank: true
    childrenStatus nullable: true, blank: true
    minHeight nullable: true, blank: true
    minWeight nullable: true, blank: true
    maxHeight nullable: true, blank: true
    maxWeight nullable: true, blank: true
    heightUnit nullable: true, blank: true
    weightUnit nullable: true, blank: true
    familyStatus nullable: true, blank: true
    familyType nullable: true, blank: true
    familyValues nullable: true, blank: true
    disabilities nullable: true, blank: true
  }
}
