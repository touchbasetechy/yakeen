package com.app.matrimony

import com.murad.app.matrimony.Person
import grails.plugin.springsecurity.SpringSecurityService
import grails.util.Holders
import org.springframework.security.core.context.SecurityContextHolder

class SecurityUtils {

  static SpringSecurityService getSpringSecurityService() {
    return Holders.grailsApplication.mainContext.getBean('springSecurityService')
  }

  static Person getCurrentUser() {
    return springSecurityService.currentUser as Person
  }

  static boolean isLoggedIn() {
    return springSecurityService.isLoggedIn()
  }

  static boolean hasRole(String role) {
    def authorities = SecurityContextHolder.context.authentication?.authorities
    return authorities?.any { it.authority == role }
  }

  static boolean hasAnyRole(String... roles) {
    def authorities = SecurityContextHolder.context.authentication?.authorities
    return authorities?.any { authority ->
      roles.any { role -> authority.authority == role }
    }
  }

  static List<String> getCurrentUserRoles() {
    def authorities = SecurityContextHolder.context.authentication?.authorities
    return authorities?.collect { it.authority } ?: []
  }
}
