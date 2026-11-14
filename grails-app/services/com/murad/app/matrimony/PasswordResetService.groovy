// grails-app/services/com/murad/app/matrimony/PasswordResetService.groovy
package com.murad.app.matrimony

import grails.gorm.transactions.Transactional

import java.time.LocalDateTime

@Transactional
class PasswordResetService {

  def mailService
  def grailsApplication

  PasswordResetToken createPasswordResetToken(Person user) {
    // Delete any existing tokens for this user
    PasswordResetToken.where { person == user }.deleteAll()

    // Create new token
    String token = UUID.randomUUID().toString()
    LocalDateTime expiryDate = LocalDateTime.now().plusHours(24) // 24 hours validity

    PasswordResetToken resetToken = new PasswordResetToken(
      token: token,
      person: user,
      expiryDate: expiryDate,
      used: false
    )

    resetToken.save(failOnError: true)
    return resetToken
  }

  boolean resetPassword(String token, String newPassword) {
    PasswordResetToken resetToken = PasswordResetToken.findByToken(token)

    def currentDate = LocalDateTime.now()
    if (!resetToken || resetToken.expiryDate.isBefore(currentDate) || resetToken.used) {
      return false
    }

    // Update password
    Person user = resetToken.person
    user.password = newPassword
    user.save(failOnError: true)

    // Mark token as used
    resetToken.used = true
    resetToken.save(failOnError: true)

    return true
  }
}
