package com.murad.app.matrimony

import com.murad.app.matrimony.identity.LoggedInIdentity
import grails.plugin.springsecurity.annotation.Secured
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder

class IdentityResolvingInterceptor {

  IdentityResolvingInterceptor() {
    match(uri: '/u/**').excludes(controller: 'login')
    matchAll().excludes(uri: '/assets/**')
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


    Integer slot = params.int('slot')
    if (slot == null) {
//      redirect(controller: 'auth', action: 'login')
      return false
    }

    List<LoggedInIdentity> identities = session.loggedInIdentities as List<LoggedInIdentity>
    LoggedInIdentity identity = identities?.find { it.slot == slot }

    if (!identity) {
      //    redirect(controller: 'auth', action: 'login')
      return false
    }

    Person person = Person.get(identity.personId)
    if (!person) {
      //  redirect(controller: 'auth', action: 'login')
      return false
    }

    def auth = new UsernamePasswordAuthenticationToken(person, null, person.authorities)
    SecurityContextHolder.context.authentication = auth

    // Interceptors have no implicit `model` map like the old Filters did
    // (that's what was throwing the NPE). Expose everything the views need
    // via request attributes instead - they're readable from GSPs as
    // ${request.currentPerson}, ${request.currentSlot}, ${request.allIdentities}.
    request.setAttribute('currentSlot', slot)
    request.setAttribute('currentPerson', person)

    true
  }

  @Secured(['ROLE_ANONYMOUS', 'ROLE_USER', 'ROLE_ADMIN', 'ROLE_SUPER'])
  boolean after() { true }

  void afterView() {
    SecurityContextHolder.clearContext()
  }
}
