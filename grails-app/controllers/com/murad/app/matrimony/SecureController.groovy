package com.murad.app.matrimony


import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured

class SecureController {

  def springSecurityService

  @Secured(['IS_AUTHENTICATED_FULLY'])
  def profile() {
    def currentUser = springSecurityService.getCurrentUser()
    render([
      success: true,
      user   : [
        id       : currentUser.id,
        firstName: currentUser.firstName,
        lastName : currentUser.lastName,
        email    : currentUser.eMail
      ]
    ] as JSON)
  }

  @Secured(['ROLE_ADMIN'])
  def adminOnly() {
    render([success: true, message: "Admin access granted"] as JSON)
  }

  @Secured(['IS_AUTHENTICATED_FULLY'])
  def authenticatedUsers() {
    def principal = springSecurityService.getPrincipal()
    render([
      success    : true,
      username   : principal.username,
      authorities: principal.authorities*.authority
    ] as JSON)
  }
}
