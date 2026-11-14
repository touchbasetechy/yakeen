package com.murad.app.matrimony

class ConnectivityInterceptor {

  ConnectivityService connectivityService

  ConnectivityInterceptor() {
    matchAll() // Apply to all controllers
    // OR match specific controllers:
    // match(controller: ~/(home|dashboard|profile)/)
  }

  boolean before() {
    if (!connectivityService.isOnline()) {
      render(view: '/error/noInternet')
      return false // Stop further processing
    }
    return true // Continue to controller action
  }
}
