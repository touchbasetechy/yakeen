package com.murad.app.matrimony

class UserLocation {
  String ipAddress
  Double latitude
  Double longitude
  String city
  String country
  String source   // "browser", "maxmind", or "ip-api-fallback"
  Date dateCreated

  static constraints = {
    city nullable: true
    country nullable: true
  }
}
