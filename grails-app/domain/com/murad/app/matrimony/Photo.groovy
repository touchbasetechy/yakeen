package com.murad.app.matrimony

import org.grails.datastore.gorm.GormEntity

class Photo implements GormEntity<Photo> {
  String title
  String description
  String originalFilename
  String contentType
  Long fileSize
  byte[] fileData

  static constraints = {
    title blank: false
    description nullable: true
    originalFilename blank: false
    contentType blank: false
    fileSize min: 0L
    fileData maxSize: 1024 * 1024 * 10 // 10MB max for example
  }
}
