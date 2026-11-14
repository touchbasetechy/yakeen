package com.murad.app.matrimony

import grails.plugin.springsecurity.annotation.Secured


class ProfileController {
  @Secured(['ROLE_USER'])
  def index() {
    [person: request.person]
  }
}
