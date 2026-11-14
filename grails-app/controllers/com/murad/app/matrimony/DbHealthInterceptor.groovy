package com.murad.app.matrimony

class DbHealthInterceptor {

  DbConnectivityStartupChecker dbConnectivityStartupChecker

  private static volatile long lastRecheckAt = 0L
  private static final long RECHECK_INTERVAL_MS = 5000 // avoid hammering DB on every request while down

  DbHealthInterceptor() {
    matchAll()
      .excludes(uri: '/assets/**')
      .excludes(uri: '/favicon.ico')
    order = HIGHEST_PRECEDENCE + 10 // run before everything else
  }

  boolean before() {
    if (!DbHealthStatus.isUp()) {
      recheckIfStale()
    }
    if (!DbHealthStatus.isUp()) {
      redirect(uri: '/static/db-down.html')
      return false
    }

    true
  }

  private void recheckIfStale() {
    long now = System.currentTimeMillis()
    if (now - lastRecheckAt > RECHECK_INTERVAL_MS) {
      lastRecheckAt = now
      dbConnectivityStartupChecker.checkNow()
    }
  }
}
