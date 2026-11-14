package com.murad.app.matrimony

import groovy.util.logging.Slf4j

@Slf4j
class ConnectivityService {

  def grailsApplication

  boolean isOnline() {
    def config = grailsApplication.config.connectivity.check
    String testUrl = config.url ?: 'https://www.google.com'
    int timeout = config.timeout ?: 3000

    try {
      URL url = new URL(testUrl)
      HttpURLConnection connection = (HttpURLConnection) url.openConnection()
      connection.setRequestMethod("HEAD")
      connection.setConnectTimeout(timeout)
      connection.setReadTimeout(timeout)
      connection.setInstanceFollowRedirects(false)
      connection.connect()

      int code = connection.responseCode
      connection.disconnect()

      return (code >= 200 && code < 400)
    } catch (Exception e) {
      log.warn("Internet connectivity check failed: ${e.message}")
      return false
    }
  }
}
