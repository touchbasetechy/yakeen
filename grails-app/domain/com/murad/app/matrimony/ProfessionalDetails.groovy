package com.murad.app.matrimony

import org.grails.datastore.gorm.GormEntity

class ProfessionalDetails implements GormEntity<ProfessionalDetails> {

  String designation
  String organisation
  String organisationLocation
  Boolean isCurrent
  String jobCategory
  String currencyCode
  String denomination
  String country
  String salary
  String salaryFor


  static belongsTo = [person: Person]

  static mapping = {
  }

  static constraints = {
    organisationLocation nullable: true
    isCurrent nullable: true
    jobCategory inList: ["Government / PSU", "Private Sector", "Business", "Defence", "Self Employed", "Un Employed"]
    currencyCode nullable: true
    denomination nullable: true
    country nullable: true
    salary nullable: true
    salaryFor inList: ["Per Anum", "Per Month", "Per Week", "Per Day", "Per Hour"]
  }
}
