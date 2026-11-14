package com.murad.app.matrimony

import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.annotation.Secured
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

@Transactional
class RedirectService {

  // Configuration map for role-based redirects
  private static final Map<String, Map<String, String>> ROLE_REDIRECTS = [
    'ROLE_SUPER': [
      controller: 'AdminDBoard',
      action    : 'index'
    ],
    'ROLE_ADMIN': [
      controller: 'person',
      action    : 'index'
    ],
//    'ROLE_MODERATOR'   : [
//      controller: 'moderator',
//      action    : 'dashboard'
//    ],
//    'ROLE_PREMIUM_USER': [
//      controller: 'premium',
//      action    : 'dashboard'
//    ],
    'ROLE_USER' : [
      controller: 'DBoard',
      action    : 'index'
    ]
  ]

  /**
   * Get redirect configuration based on user roles
   * Returns the highest priority role's redirect
   */
  @Secured(['permitAll'])
  Map<String, String> getRedirectForRoles(List<String> roles) {
    // Define role priority (higher index = higher priority)
    def rolePriority = ['ROLE_GUEST', 'ROLE_USER', 'ROLE_PREMIUM_USER', 'ROLE_MODERATOR', 'ROLE_ADMIN']

    // Find the highest priority role
    String highestRole = roles.findAll { ROLE_REDIRECTS.containsKey(it) }
      .max { rolePriority.indexOf(it) } ?: 'ROLE_GUEST'

    return ROLE_REDIRECTS[highestRole]
  }

  /**
   * Get redirect URL based on user roles.
   *
   * IMPORTANT: this must land on the slot-scoped "/u/$slot/..." path, since
   * that's the only URL pattern your Spring Security config (permitAll +
   * filterChain exclusions for securityContextPersistenceFilter /
   * filterInvocationInterceptor) and IdentityResolvingInterceptor are set
   * up to handle. We build the URI directly with `uri:` rather than
   * `controller:`/`action:` + a `slot` param - the latter lets Grails'
   * reverse URL mapping pick ANY mapping that resolves to that
   * controller/action (e.g. the literal "/home" alias in UrlMappings.groovy),
   * which has no `slot` token to absorb the value, silently degrading it
   * to a query string ("/home?slot=1") that matches no permitAll rule and
   * gets rejected by the filter chain before any controller/interceptor runs.
   *
   * As a side effect, this also stashes the resolved controller/action onto
   * the current request as attributes ("controller" / "action") so that
   * callers later in the same request (e.g. LoginController.authenticate())
   * can read them back via request.getAttribute(...) without having to
   * recompute the redirect config themselves. Callers that rely on those
   * attributes MUST invoke this method before reading them.
   */
  @Secured(['permitAll'])
  String getRedirectUrlForRoles(List<String> roles, Integer slot, def linkGenerator) {
    def redirectConfig = getRedirectForRoles(roles)

    def attrs = RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes
    def request = attrs.request

    request.setAttribute("controller", redirectConfig.controller)
    request.setAttribute("action", redirectConfig.action)
//    request.setAttribute('currentSlot', $ { slot })


    return linkGenerator.link(
      uri: "/u/${slot}/${redirectConfig.controller}/${redirectConfig.action}",
      absolute: true
    )
  }

  @Secured(['permitAll'])
  String getRedirectUriForRoles(List<String> roles, Integer slot, def linkGenerator) {
    def redirectConfig = getRedirectForRoles(roles)

    def attrs = RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes
    def request = attrs.request

    request.setAttribute("controller", redirectConfig.controller)
    request.setAttribute("action", redirectConfig.action)
//    request.setAttribute('currentSlot', $ { slot })


    return linkGenerator.link(
      uri: "/u/${slot}/${redirectConfig.controller}/${redirectConfig.action}",
      absolute: false
    )
  }

  /**
   * Check if user has access to a specific controller/action
   */
  @Secured(['permitAll'])
  boolean hasAccessToController(List<String> userRoles, String controller) {
    switch (controller.toLowerCase()) {
      case 'admin':
        return userRoles.contains('ROLE_ADMIN')
      case 'moderator':
        return userRoles.contains('ROLE_ADMIN') || userRoles.contains('ROLE_MODERATOR')
      case 'premium':
        return userRoles.contains('ROLE_ADMIN') || userRoles.contains('ROLE_MODERATOR') ||
          userRoles.contains('ROLE_PREMIUM_USER')
      case 'user':
        return userRoles.any { it in ['ROLE_ADMIN', 'ROLE_MODERATOR', 'ROLE_PREMIUM_USER', 'ROLE_USER'] }
      default:
        return true // Public controllers
    }
  }

  /**
   * Get available menu items based on user roles
   */
  @Secured(['permitAll'])
  List<Map<String, String>> getAvailableMenuItems(List<String> roles) {
    def menuItems = []

    if (roles.contains('ROLE_ADMIN')) {
      menuItems.addAll([
        [name: 'Admin Dashboard', controller: 'admin', action: 'dashboard'],
        [name: 'User Management', controller: 'admin', action: 'users'],
        [name: 'System Settings', controller: 'admin', action: 'settings']
      ])
    }

    if (roles.contains('ROLE_MODERATOR')) {
      menuItems.addAll([
        [name: 'Moderator Dashboard', controller: 'moderator', action: 'dashboard'],
        [name: 'Content Moderation', controller: 'moderator', action: 'content'],
        [name: 'Reports', controller: 'moderator', action: 'reports']
      ])
    }

    if (roles.contains('PREMIUM_USER')) {
      menuItems.addAll([
        [name: 'Premium Dashboard', controller: 'premium', action: 'dashboard'],
        [name: 'Advanced Search', controller: 'premium', action: 'search'],
        [name: 'Premium Features', controller: 'premium', action: 'features']
      ])
    }

    if (roles.contains('ROLE_USER')) {
      menuItems.addAll([
        [name: 'My Dashboard', controller: 'user', action: 'dashboard'],
        [name: 'My Profile', controller: 'user', action: 'profile'],
        [name: 'Messages', controller: 'user', action: 'messages']
      ])
    }

    // Common menu items for all users
    menuItems.addAll([
      [name: 'Home', controller: 'home', action: 'index'],
      [name: 'Search', controller: 'search', action: 'index']
    ])

    return menuItems
  }
}
