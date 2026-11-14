package com.murad.app.matrimony

import org.grails.datastore.gorm.GormEntity

class CountryCode implements GormEntity<CountryCode> {

  String code
  String name
  String flag

  static constraints = {
  }
}
