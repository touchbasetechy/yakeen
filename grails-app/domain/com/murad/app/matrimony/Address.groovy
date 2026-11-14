package com.murad.app.matrimony

import org.grails.datastore.gorm.GormEntity

class Address implements GormEntity<Address> {
  String street1
  String street2
  String city
  String district
  String state
  String country
  String postalCode
  String addressType
  String otherType
  static belongsTo = [person: Person]

  static mapping = {
    table(name "address")
    version(versioned: false)
    //  version false
  }

  static constraints = {
    addressType nullable: true
    otherType nullable: true
    addressType inList: ["Permanent", "Present", "Temporary", "Other"]
  }
}
