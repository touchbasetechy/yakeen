package com.murad.app.matrimony

import org.grails.datastore.gorm.GormEntity

class OAuthID implements GormEntity<OAuthID> {
  String provider
  String accessToken
  String refreshToken
  Date tokenExpires
  String uid

  static belongsTo = [user: Person]

  static constraints = {
    accessToken nullable: true
    refreshToken nullable: true
    tokenExpires nullable: true
    uid nullable: false
    provider nullable: false
  }
}
