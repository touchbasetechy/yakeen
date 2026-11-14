package com.murad.app.matrimony

class ContactDetails {

  String isdCode
  String country
  String flag
  String mobileNumber
  String eMail
  Boolean isPrimary = false

  static belongsTo = [person: Person]

  static constraints = {
    isdCode nullable: true
    country nullable: true
    flag nullable: true
    mobileNumber nullable: true
    eMail nullable: true
    person nullable: true
    isPrimary nullable: true
  }

  static mapping = {
    flag sqlType: 'text'
  }
}
