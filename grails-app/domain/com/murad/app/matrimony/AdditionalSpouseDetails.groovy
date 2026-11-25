package com.murad.app.matrimony

import org.grails.datastore.gorm.GormEntity

class AdditionalSpouseDetails implements GormEntity<AdditionalSpouseDetails> {
  String education
  String profession
  String salary
  String salaryFor
  String workLocation
  String workingCountry
  String currencyCode
  String currencyName
  String currencySymbol
  String currencyFlag

  static belongsTo = [person: Person]

  static constraints = {
    education nullable: true, blank: true
//inList: ["Doesn't Matter", "School Education", "Intermediate", "Graduate", "Post Graduate", "Doctorate"]
    profession nullable: true, blank: true
    salary nullable: true, blank: true
    salaryFor nullable: true, blank: true//inList: ["Per Anum", "Per Month", "Per Week", "Per Day", "Per Hour"]
    workLocation nullable: true, blank: true
    workingCountry nullable: true, blank: true
    currencyCode nullable: true, blank: true
    currencyName nullable: true, blank: true
    currencySymbol nullable: true, blank: true
    currencyFlag nullable: true, blank: true
  }
  static mapping = {
  }
}
