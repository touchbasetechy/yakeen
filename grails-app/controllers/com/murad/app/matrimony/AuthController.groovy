package com.murad.app.matrimony

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured

class AuthController {
  def springSecurityService

  @Secured(['ROLE_ANONYMOUS'])
  def login() {}

  @Secured(['ROLE_ANONYMOUS'])
  def register() {}

  @Secured(['IS_AUTHENTICATED_FULLY'])
  def profile() {
    def user = springSecurityService.getCurrentUser()
    render([
      username: user.username,
      roles   : user.authorities*.authority
    ] as JSON)
  }
}
