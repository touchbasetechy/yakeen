package com.murad.app.matrimony


import grails.plugin.springsecurity.annotation.Secured

@Secured(['ROLE_ADMIN'])
class AdminController {

  UserService userService

  def index() {
    List<Person> users = Person.list()
    [users: users]
  }

  def userDetails() {
    Long userId = params.long('id')
    Person user = Person.get(userId)
    if (!user) {
      flash.error = "User not found"
      redirect action: 'index'
      return
    }

    List<String> roles = userService.getUserRoles(user)
    [user: user, roles: roles]
  }

  def addRole() {
    Long userId = params.long('userId')
    String roleName = params.roleName

    Person user = Person.get(userId)
    if (user && roleName) {
      if (userService.addRoleToUser(user, roleName)) {
        flash.message = "Role added successfully"
      } else {
        flash.error = "Failed to add role or role already exists"
      }
    }

    redirect action: 'userDetails', id: userId
  }
}
