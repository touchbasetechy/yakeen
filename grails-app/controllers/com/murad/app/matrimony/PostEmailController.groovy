package com.murad.app.matrimony

import com.murad.app.matrimony.identity.LoggedInIdentity
import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.SpringSecurityService
import grails.validation.ValidationException
import groovy.util.logging.Slf4j
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.context.HttpSessionSecurityContextRepository

@Slf4j
class PostEmailController {
  SpringSecurityService springSecurityService
  AuthenticationManager authenticationManager
  JwtTokenService jwtTokenService
  RedirectService redirectService
  SignedInLogService signedInLogService

  def emailService

  @Transactional
  def activate() {
    def user = Person.findByActivationToken(params?.token)

    if (user) {
      user.activationToken = ""
      user.activationTokenExpiry = null
      user.enabled = true
      try {
        if (user?.validate() && user?.save(flush: true)) {
          Person updatedUser = Person.findByEMail(user?.eMail)

          def authToken = new UsernamePasswordAuthenticationToken(updatedUser,
            null,
            updatedUser.authorities)
          def securityContext = SecurityContextHolder.getContext()
          securityContext.setAuthentication(authToken)
          session.removeAttribute('SPRING_SECURITY_SAVED_REQUEST')

          session.setAttribute(
            HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
            securityContext
          )

          if (!updatedUser) {
            flash.message = "User not found!"
            //TODO: check the controller and action in below line and correct it.
            redirect(controller: 'login', action: 'index')
            return
          }

          def roles = updatedUser.personTypes.collect { it.personTypeName }
//          String jwtToken = jwtTokenService.generateToken(updatedUser?.eMail, roles)


          List<LoggedInIdentity> identities = session.loggedInIdentities as List<LoggedInIdentity>
          if (identities == null) {
            identities = []
            session.loggedInIdentities = identities
          }

          LoggedInIdentity existing = identities.find { it.personId == updatedUser.id }
          int slot
          def redirectUrl
          if (existing) {
            slot = existing.slot
            // Existing identity: still need a redirect URL for the response,
            // and this call also (re)populates the "controller"/"action"
            // request attributes for consistency, even though we don't need
            // them for a LoggedInIdentity here since one already exists.
            redirectUrl = redirectService.getRedirectUriForRoles(roles, slot, grailsLinkGenerator)
          } else {
            slot = identities.size()

            // IMPORTANT: call this BEFORE reading request.getAttribute("controller"/"action")
            // below. getRedirectUrlForRoles() is what sets those attributes in the first
            // place - reading them before this call always returns null.
            redirectUrl = redirectService.getRedirectUriForRoles(roles, slot, grailsLinkGenerator)

            identities << new LoggedInIdentity(
              slot: slot,
              personId: updatedUser.id,
              email: updatedUser.eMail,
              displayName: "${updatedUser.firstName} ${updatedUser.lastName}",
              controller: request.getAttribute("controller"),
              action: request.getAttribute("action")
            )
          }


          signedInLogService.addSignInLog(updatedUser, session)

          //        session.jwtToken = jwtToken
          session.user = updatedUser
          session.personId = updatedUser?.id

          //     def redirectConfig = redirectService.getRedirectForRoles(roles)

          checkAuth()
          flash.message = "User account successfully activated for user " + updatedUser?.firstName + " " + updatedUser?.lastName
          emailService.sendWelcomeEmail(updatedUser)

          redirect(uri: redirectUrl)
          // redirect(controller: redirectConfig.controller, action: redirectConfig.action)
        } else {
          flash.error = "failed to activate user account for user " + updatedUser?.firstName + " " + updatedUser?.lastName

          redirect(controller: "home", action: "index")
        }
      } catch (ValidationException e) {
        flash.error = "failed to validate user.  ${e.errors}"
        redirect(controller: "PostAdmin", action: "activate")
      }
    } else {
      flash.error = "User account is already activated or it doesn't exists !!. Couldn't activate user account."
      redirect(controller: 'home', action: 'index')
    }
  }


  def checkAuth() {
    log.info("Is logged in: ${springSecurityService.isLoggedIn()}")
    log.info("Current user: ${springSecurityService.currentUser}")
    log.info("Principal: ${springSecurityService.principal}")
    log.info("Authorities: ${springSecurityService.principal?.authorities}")
  }
}
