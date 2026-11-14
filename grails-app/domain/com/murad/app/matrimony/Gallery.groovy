package com.murad.app.matrimony

import org.grails.datastore.gorm.GormEntity

class Gallery implements GormEntity<Gallery> {

  String title
  String description

  static hasMany = [photos: Photo]

  static constraints = {
    title nullable: false, blank: false
    description nullable: true, blank: true
  }
}
