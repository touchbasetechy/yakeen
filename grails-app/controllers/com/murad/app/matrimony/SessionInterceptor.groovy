package com.murad.app.matrimony

import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.annotation.Secured
import groovy.util.logging.Slf4j

@Slf4j
class SessionInterceptor {
  int order = HIGHEST_PRECEDENCE

  def authService // Injected transactional service

  SpringSecurityService springSecurityService

  SessionInterceptor() {
    matchAll()
      .excludes(uri: '/user/activate')          // <-- raw path match, works regardless of UrlMappings
      .excludes(controller: 'home', action: ['index'])
      .excludes(controller: 'auth', action: ['login', 'register'])
      .excludes(controller: 'login', action: ['authenticate', 'register'])
      .excludes(controller: 'logout', action: ['index'])
      .excludes(controller: 'OAuth2', action: ['google', 'gitHub'])
  }

  @Secured(['ROLE_ANONYMOUS', 'ROLE_USER', 'ROLE_ADMIN', 'ROLE_SUPER'])
  boolean before() {
    String uri = request.requestURI

    if (uri == '/user/activate') {
      return true
    }

    if (controllerName == 'home' && actionName in ['index']) {
      return true
    }

    if (controllerName == 'auth' && actionName in ['login', 'register']) {
      return true
    }

    if (controllerName == 'login' && actionName in ['authenticate', 'register', 'addAccount']) {
      return true
    }

    if (controllerName == 'passwordReset' && actionName in ['forgotPassword', 'requestReset', 'reset', 'updatePassword']) {
      return true
    }

    if (controllerName == 'OAuth2' && actionName in ['google', 'callback', 'gitHub', 'gitHubCallback']) {
      return true
    }

    try {
      def personId = session?.personId
      if (!personId) {
        flash.message = "Please log in"
//        redirect(controller: "logout", action: "index")
        return false
      }

      Person person = authService.getAuthenticatedUser(personId)

      if (!person) {
        flash.error = "Session expired"
//        redirect(controller: "logout", action: "index")
        return false
      }

      request.person = person
      return true

    } catch (Exception e) {
      log.error("Session interceptor error", e)
      if (controllerName != 'logIn') {
        redirect(controller: "logIn", action: "index")
      }

      if (controllerName != 'home') {
        redirect(controller: "home", action: "index")
      }
      return false
    }
  }
}

@Transactional
class AuthService {
  Person getAuthenticatedUser(Long id) {
    Person.get(id) // Runs in service transaction
  }
}
