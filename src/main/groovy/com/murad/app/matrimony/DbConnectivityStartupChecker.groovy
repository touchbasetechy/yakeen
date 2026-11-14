package com.murad.app.matrimony

import groovy.util.logging.Slf4j
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order

import javax.sql.DataSource
import java.sql.Connection
import java.sql.SQLException

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
class DbConnectivityStartupChecker implements ApplicationListener<ApplicationReadyEvent> {

  DataSource dataSource

  private static final int VALIDATION_TIMEOUT_SECS = 3

  @Override
  void onApplicationEvent(ApplicationReadyEvent event) {
    checkNow()
  }

  void checkNow() {
    Connection conn = null
    try {
      conn = dataSource.getConnection()
      boolean valid = conn.isValid(VALIDATION_TIMEOUT_SECS)
      if (valid) {
        DbHealthStatus.markUp()
        log.info("DB connectivity check OK at startup")
      } else {
        DbHealthStatus.markDown(new SQLException("Connection.isValid() returned false"))
        log.error("DB connectivity check FAILED at startup: connection not valid")
      }
    } catch (Throwable t) {
      DbHealthStatus.markDown(t)
      log.error("DB connectivity check FAILED at startup: {}", t.message)
    } finally {
      if (conn != null) {
        try {
          conn.close()
        } catch (SQLException ignored) {
        }
      }
    }
  }
}
