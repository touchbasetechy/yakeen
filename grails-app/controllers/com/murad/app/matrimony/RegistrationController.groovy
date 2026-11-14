package com.murad.app.matrimony


import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.annotation.Secured

class RegistrationController {

  SpringSecurityService springSecurityService

  @Secured(['permitAll'])
  def index() {
    [user: new Person()]
  }

  @Secured(['permitAll'])
  def register() {
    def user = new Person(params)
    user.enabled = true

    if (!user.save()) {
      render view: 'index', model: [user: user]
      return
    }

    // Assign default ROLE_USER
    Role userRole = Role.findByAuthority('ROLE_USER') ?: new Role(authority: 'ROLE_USER').save()
    UserRole.create(user, userRole, true)

    // Auto-login after registration
    springSecurityService.reauthenticate(user.username)

    redirect uri: '/'
  }
}
