package com.murad.app.matrimony

import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.annotation.Secured
import grails.validation.ValidationException
import groovy.util.logging.Slf4j

import javax.servlet.http.HttpSession
import java.time.LocalDateTime


@Slf4j
@Secured(['permitAll'])
class SignedInLogService {

  @Transactional
  def addSignInLog(Person user, HttpSession session) {
    def signInLog = new SignInLog(user: user)
    signInLog.loggedInAt = LocalDateTime.now()
    signInLog.status = "Online"

    try {
      if (signInLog.validate() && signInLog.save(flush: true)) {
        session.personId = user?.id
        log.info("Successfully added sign in log!!")
      } else {
        log.error("Failed adding sign in log!!")
        flash.error = "Login failed. Please try again."
        redirect(controller: "auth", action: "login")
      }
    } catch (ValidationException e) {
      log.error("Validation failed for sign in log: ", e)
    }
  }
}
