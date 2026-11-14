package com.murad.app.matrimony

import grails.gorm.transactions.Transactional

@Transactional
class MailTrapEmailService {

  def mailService

  /**
   * Send account activation email
   */
  def sendActivationEmail(String toEmail, String activationLink, String userName) {
  }

  /**
   * Send password reset email
   */
  def sendPasswordResetEmail(String toEmail, String resetLink, String userName) {

  }

  /**
   * Send welcome email
   */
  def sendWelcomeEmail(String toEmail, String userName) {

  }

  /**
   * Send notification email with attachments
   */
  def sendNotificationWithAttachment(String toEmail, String subject, String content, File attachment = null) {

  }
}
