package com.murad.app.matrimony

import grails.plugin.springsecurity.annotation.Secured
import groovy.util.logging.Slf4j

import java.time.LocalDateTime

@Secured(['permitAll'])
@Slf4j
class PasswordResetController {

  PasswordResetService passwordResetService
  def emailService

  def forgotPassword() {
    log.info("Rendering forgot password page")
    [:]
  }

  def requestReset() {
    String email = params?.email?.trim()

    if (!email) {
      flash.message = "Please enter your email address"
      flash.type = "error"
      redirect action: 'forgotPassword'
      return
    }

    Person user = Person.findByEMail(email)

    if (!user) {
      // Security best practice: Don't reveal if email exists
      flash.message = "If an account exists with that email, password reset instructions have been sent"
      flash.type = "success"
      redirect action: 'forgotPassword'
      return
    }

    try {
      PasswordResetToken token = passwordResetService.createPasswordResetToken(user)
      String resetLink = createLink(
        controller: 'passwordReset',
        action: 'reset',
        params: [token: token.token],
        absolute: true
      )

      emailService.sendPasswordResetEmail(user, resetLink)

      flash.message = "Password reset instructions have been sent to your email"
      flash.type = "success"
    } catch (Exception e) {
      log.error("Error sending password reset email: ${e.message}", e)
      flash.message = "An error occurred while processing your request. Please try again."
      flash.type = "error"
    }

    redirect action: 'forgotPassword'
  }

  def reset(String token) {
    if (!token) {
      flash.message = "Invalid password reset link"
      flash.type = "error"
      redirect controller: 'auth', action: 'login'
      return
    }

    PasswordResetToken resetToken = PasswordResetToken.findByToken(token)

    if (!resetToken) {
      flash.message = "Invalid or expired password reset link"
      flash.type = "error"
      redirect controller: 'auth', action: 'login'
      return
    }

    // Check if token is expired
    if (!resetToken.expiryDate) {
      log.error("Token ${token} has null expiryDate")
      flash.message = "Invalid password reset link"
      flash.type = "error"
      redirect controller: 'auth', action: 'login'
      return
    }

    // Check if token has been used
    if (resetToken.used) {
      log.warn("Attempt to use already-used token: ${token}")
      flash.message = "This password reset link has already been used"
      flash.type = "error"
      redirect controller: 'auth', action: 'login'
      return
    }

    // Use LocalDateTime.isBefore() for comparison
    LocalDateTime now = LocalDateTime.now()
    if (resetToken.expiryDate.isBefore(now)) {
      flash.message = "This password reset link has expired"
      flash.type = "error"
      redirect controller: 'auth', action: 'login'
      return
    }

    // Return the token to the view
    [token: token]
  }

  def updatePassword() {
    String token = params.token
    String password = params.password
    String passwordConfirm = params.passwordConfirm

    if (!token) {
      flash.message = "Invalid request"
      flash.type = "error"
      redirect controller: 'auth', action: 'login'
      return
    }

    if (!password || !passwordConfirm) {
      flash.message = "Please fill in all fields"
      flash.type = "error"
      redirect action: 'reset', params: [token: token]
      return
    }

    if (password != passwordConfirm) {
      flash.message = "Passwords do not match"
      flash.type = "error"
      redirect action: 'reset', params: [token: token]
      return
    }

    if (password.length() < 8) {
      flash.message = "Password must be at least 8 characters long"
      flash.type = "error"
      redirect action: 'reset', params: [token: token]
      return
    }

    try {
      boolean success = passwordResetService.resetPassword(token, password)

      if (success) {
        flash.message = "Your password has been reset successfully. Please login with your new password."
        flash.type = "success"
        redirect controller: 'auth', action: 'login'
      } else {
        flash.message = "Invalid or expired password reset link"
        flash.type = "error"
        redirect controller: 'auth', action: 'login'
      }
    } catch (Exception e) {
      log.error("Error updating password: ${e.message}", e)
      flash.message = "An error occurred while resetting your password. Please try again."
      flash.type = "error"
      redirect action: 'reset', params: [token: token]
    }
  }
}
