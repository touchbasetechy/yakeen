package com.murad.app.matrimony

import groovy.util.logging.Slf4j
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.logout.LogoutHandler
import org.springframework.stereotype.Component

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.time.LocalDateTime

@Slf4j
@Component
class SignInLogLogoutHandler implements LogoutHandler {

  @Override
  void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
    if (!authentication) {
      log.warn("Logout called with null authentication — skipping SignInLog update")
      return
    }

    try {
      def principal = authentication.principal
      String email = (principal instanceof UserDetails) ? principal.username : principal.toString()


      // withTransaction opens a Hibernate session manually —
      // required because security filters run outside the normal
      // Grails request/transaction lifecycle
      SignInLog.withTransaction {
        Person person = Person.findByEMail(email)
        if (!person) {
          log.warn("No Person found for email: ${email}")
          return
        }

        SignInLog signInLog = SignInLog.findByUserAndStatus(person, "Online")
        if (!signInLog) {
          log.warn("No active SignInLog found for user: ${email}")
          return
        }

        signInLog.loggedOutAt = LocalDateTime.now()
        signInLog.status = "Offline"
        signInLog.save(flush: true)

        log.info("SignInLog updated on logout for user: ${email}")
      }

    } catch (Exception e) {
      log.error("Error updating SignInLog on logout for user", e)
      // Never rethrow — a failing logout handler must not block
      // Spring Security from completing the logout process
    }
  }
}
