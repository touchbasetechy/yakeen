// grails-app/domain/com/murad/app/matrimony/PasswordResetToken.groovy
package com.murad.app.matrimony

import java.time.LocalDateTime

class PasswordResetToken {
  String token
  Person person
  LocalDateTime expiryDate
  boolean used = false

  static constraints = {
    token nullable: false, unique: true
    person nullable: false
    expiryDate nullable: false
  }
}
