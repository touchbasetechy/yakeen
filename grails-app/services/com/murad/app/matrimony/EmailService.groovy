package com.murad.app.matrimony

import grails.gorm.transactions.Transactional

@Transactional
class EmailService {

  def mailService
  def grailsApplication

  /**
   * Send activation email to user
   * @param user User object
   * @param activationToken Generated activation token
   * @return boolean success status
   */
  boolean sendActivationEmail(Person user, String activationToken) {
    try {
      String activationLink = generateActivationLink(activationToken)

      mailService.sendMail {
        to user.eMail
//TODO:        from grailsApplication.config.grails.mail.from
        from "noreply@localhost"
        subject "Activate Your Account"
        html view: "/email/activation",
          model: [
            username      : user.firstName + ' ' + user.lastName,
            activationLink: activationLink,
            appName       : grailsApplication.config.info.app.name ?: "Yakeen Matrimony"
          ]
      }

      log.info("Activation email sent successfully to ${user.eMail}")
      return true

    } catch (Exception e) {
      log.error("Failed to send activation email to ${user.eMail}", e)
      return false
    }
  }

  /**
   * Generate activation link
   */
  private String generateActivationLink(String token) {
    String baseUrl = grailsApplication.config.grails.serverURL ?: "http://localhost:8080"
    return "${baseUrl}/user/activate?token=${token}"
  }

  /**
   * Send plain text activation email (alternative)
   */
  boolean sendActivationEmailPlainText(Person user, String activationToken) {
    try {
      String activationLink = generateActivationLink(activationToken)

      mailService.sendMail {
        to user.email
        from grailsApplication.config.grails.mail.from
        subject "Activate Your Account"
        body """
Hello ${user.username},

Thank you for registering! Please click the link below to activate your account:

${activationLink}

This link will expire in 24 hours.

If you did not register for this account, please ignore this email.

Best regards,
The Team
                """.trim()
      }

      log.info("Activation email sent successfully to ${user.email}")
      return true

    } catch (Exception e) {
      log.error("Failed to send activation email to ${user.email}", e)
      return false
    }
  }

  /**
   * Send welcome email to user after successful activation
   * @param user User object
   * @return boolean success status
   */
  boolean sendWelcomeEmail(Person user) {
    try {
      String dashboardUrl = grailsApplication.config.grails.serverURL ?: "http://localhost:8080"
      String appName = grailsApplication.config.info.app.name ?: "Yakeen Matrimony"

      mailService.sendMail {
        to user.eMail
        from "noreply@localhost"
        subject "Welcome to ${appName}!"
        html view: "/email/welcome",
          model: [
            username    : user.firstName + ' ' + user.lastName,
            firstName   : user.firstName,
            dashboardUrl: "${dashboardUrl}/dashboard",
            profileUrl  : "${dashboardUrl}/profile",
            supportUrl  : "${dashboardUrl}/support",
            appName     : appName
          ]
      }

      log.info("Welcome email sent successfully to ${user.eMail}")
      return true

    } catch (Exception e) {
      log.error("Failed to send welcome email to ${user.eMail}", e)
      return false
    }
  }

  /**
   * Send send Password Reset Request email to user after successful activation
   * @param user User object
   * @return boolean success status
   */
  boolean sendPasswordResetEmail(Person user, String resetUrl) {
    try {
      String dashboardUrl = grailsApplication.config.grails.serverURL ?: "http://localhost:8080"
      String appName = grailsApplication.config.info.app.name ?: "Yakeen Matrimony"

      mailService.sendMail {
        to user.eMail
        from "noreply@localhost"
        subject "Password Reset ${appName}!"
        html view: "/email/requestPwdReset",
          model: [
            username : user.firstName + ' ' + user.lastName,
            userEMail: user.eMail,
            resetUrl : resetUrl,
            appName  : appName
          ]
      }

      log.info("Password reset email sent successfully to ${user.eMail}")
      return true

    } catch (Exception e) {
      log.error("Failed to send password reset email to ${user.eMail}", e)
      return false
    }
  }
}
