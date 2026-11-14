package com.murad.app.matrimony

import java.util.concurrent.atomic.AtomicBoolean

class DbHealthStatus {

  private static final AtomicBoolean UP = new AtomicBoolean(true)
  private static volatile Throwable lastError = null
  private static volatile long lastCheckedAt = 0L

  static boolean isUp() {
    UP.get()
  }

  static void markUp() {
    UP.set(true)
    lastError = null
    lastCheckedAt = System.currentTimeMillis()
  }

  static void markDown(Throwable t) {
    UP.set(false)
    lastError = t
    lastCheckedAt = System.currentTimeMillis()
  }

  static Throwable getLastError() {
    lastError
  }

  static long getLastCheckedAt() {
    lastCheckedAt
  }
}
