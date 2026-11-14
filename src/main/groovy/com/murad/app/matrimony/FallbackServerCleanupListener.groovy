// src/main/groovy/com/murad/app/matrimony/FallbackServerCleanupListener.groovy
package com.murad.app.matrimony

import groovy.util.logging.Slf4j
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener

@Slf4j
class FallbackServerCleanupListener implements ApplicationListener<ApplicationReadyEvent> {

  @Override
  void onApplicationEvent(ApplicationReadyEvent event) {
    // Safety net: guarantee the fallback server is down now that Grails is confirmed live.
    // No-op if it was already stopped in scheduleRetries().
    Application.stopFallbackServerIfRunning()
    log.info("Grails application fully started — fallback server confirmed stopped.")
  }
}
