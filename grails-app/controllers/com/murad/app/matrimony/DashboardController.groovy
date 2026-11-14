package com.murad.app.matrimony

import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.annotation.Secured

@Secured(['ROLE_USER'])
class DashboardController {

  SpringSecurityService springSecurityService

  def index() {
    Person currentUser = springSecurityService.currentUser as Person
    [user: currentUser]
  }
}
