package com.murad.app.matrimony

import com.app.matrimony.SecurityUtils
import grails.plugin.springsecurity.SpringSecurityService

class SecurityTagLib {

  static namespace = "sec"

  SpringSecurityService springSecurityService

  /**
   * Custom tag to check if user has specific role
   * Usage: <sec:hasRole role="ROLE_ADMIN">...</sec:hasRole>
   */
  def hasRole = { attrs, body ->
    if (SecurityUtils.hasRole(attrs.role)) {
      out << body()
    }
  }

  /**
   * Custom tag to check if user has any of the specified roles
   * Usage: <sec:hasAnyRole roles="ROLE_ADMIN,ROLE_USER">...</sec:hasAnyRole>
   */
  def hasAnyRole = { attrs, body ->
    String[] roles = attrs.roles.split(',')
    if (SecurityUtils.hasAnyRole(roles)) {
      out << body()
    }
  }

  /**
   * Show content only if user is logged in
   * Usage: <sec:ifLoggedIn>...</sec:ifLoggedIn>
   */
  def ifLoggedIn = { attrs, body ->
    if (SecurityUtils.isLoggedIn()) {
      out << body()
    }
  }

  /**
   * Show content only if user is NOT logged in
   * Usage: <sec:ifNotLoggedIn>...</sec:ifNotLoggedIn>
   */
  def ifNotLoggedIn = { attrs, body ->
    if (!SecurityUtils.isLoggedIn()) {
      out << body()
    }
  }

  /**
   * Display current user's name
   * Usage: <sec:username/>
   */
  def username = { attrs ->
    if (SecurityUtils.isLoggedIn()) {
      Person user = SecurityUtils.currentUser
      out << "${user.firstName} ${user.lastName}"
    }
  }
}
