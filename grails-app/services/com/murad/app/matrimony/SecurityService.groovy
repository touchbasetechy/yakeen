package com.murad.app.matrimony

import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.SpringSecurityService
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.LockedException

@Transactional
class SecurityService {

  SpringSecurityService springSecurityService

  /**
   * Authenticate user with email and password
   */
  boolean authenticateUser(String email, String password) {
    try {
      Person person = Person.findByEMail(email)
      if (!person) {
        return false
      }

      if (!person.enabled) {
        throw new DisabledException("Account is disabled")
      }

      if (person.accountLocked) {
        throw new LockedException("Account is locked")
      }

      return springSecurityService.passwordEncoder.matches(password, person.password)
    } catch (Exception e) {
      log.error("Authentication failed for user: ${email}", e)
      return false
    }
  }

  /**
   * Change user password
   */
  boolean changePassword(Person person, String oldPassword, String newPassword) {
    if (!springSecurityService.passwordEncoder.matches(oldPassword, person.password)) {
      return false
    }

    person.password = newPassword
    return person.save(flush: true)
  }

  /**
   * Reset password with token
   */
  boolean resetPassword(String token, String newPassword) {
    Person person = Person.findByPasswordResetToken(token)
    if (!person) {
      return false
    }

    person.password = newPassword
    person.passwordResetToken = null
    return person.save(flush: true)
  }

  /**
   * Generate password reset token
   */
  String generatePasswordResetToken(String email) {
    Person person = Person.findByEMail(email)
    if (!person) {
      return null
    }

    String token = UUID.randomUUID().toString()
    person.passwordResetToken = token
    person.save(flush: true)

    return token
  }

  /**
   * Enable/Disable user account
   */
  boolean toggleUserAccount(Long userId, boolean enabled) {
    Person person = Person.get(userId)
    if (!person) {
      return false
    }

    person.enabled = enabled
    return person.save(flush: true)
  }

  /**
   * Lock/Unlock user account
   */
  boolean toggleAccountLock(Long userId, boolean locked) {
    Person person = Person.get(userId)
    if (!person) {
      return false
    }

    person.accountLocked = locked
    return person.save(flush: true)
  }

  /**
   * Get user statistics
   */
  Map getUserStatistics() {
    return [
      totalUsers : Person.count(),
      activeUsers: Person.countByEnabled(true),
      lockedUsers: Person.countByAccountLocked(true),
      adminUsers : PersonPersonType.countByPersonType(PersonType.findByPersonTypeName('ROLE_ADMIN'))
    ]
  }
}
