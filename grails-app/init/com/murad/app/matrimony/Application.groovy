package com.murad.app.matrimony

import com.sun.net.httpserver.HttpServer
import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration

import java.sql.DriverManager
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class Application extends GrailsAutoConfiguration {

  static final int FALLBACK_PORT = 8081
  static final int GRAILS_PORT = 8082

  static HttpServer fallbackServer
  static ScheduledExecutorService retryExecutor

  static void main(String[] args) {
    if (isDbReachable()) {
      System.setProperty('server.port', GRAILS_PORT as String)
      GrailsApp.run(Application, args)
      switchNginxUpstream('grails') // tell nginx to point at Grails immediately
    } else {
      println "DB unreachable at startup — serving static fallback page."
      startFallbackServer()
      switchNginxUpstream('fallback')
      scheduleRetries(args)
    }
  }

  static boolean isDbReachable() {
    String url = System.getenv('DB_URL') ?: 'jdbc:postgresql://localhost:5432/yakeenmm'
    String user = System.getenv('DB_USER') ?: 'postgres'
    String pass = System.getenv('DB_PASS') ?: 'fathiali34'
    try {
      Class.forName('org.postgresql.Driver')
      Properties props = new Properties()
      props.setProperty('user', user)
      props.setProperty('password', pass)
      props.setProperty('connectTimeout', '3')
      DriverManager.getConnection(url, props).withCloseable { true }
    } catch (Exception e) {
      println "DB connection check failed: ${e.message}"
      false
    }
  }

  static void startFallbackServer() {
    fallbackServer = HttpServer.create(new InetSocketAddress(FALLBACK_PORT), 0)
    fallbackServer.createContext('/') { exchange ->
      try {
        byte[] body = Application.classLoader
          .getResourceAsStream('static/db-down.html')?.bytes
        if (body == null) {
          body = "<h1>Service temporarily unavailable</h1>".bytes
        }
        exchange.responseHeaders.set('Content-Type', 'text/html')
        exchange.sendResponseHeaders(503, body.length)
        exchange.responseBody.withCloseable { it.write(body) }
      } catch (Exception e) {
        e.printStackTrace()
        String msg = "Internal error: ${e.message}"
        exchange.sendResponseHeaders(500, msg.bytes.length)
        exchange.responseBody.withCloseable { it.write(msg.bytes) }
      } finally {
        exchange.close()
      }
    }
    fallbackServer.start()
    println "Fallback HttpServer listening on port ${FALLBACK_PORT}"
  }

  static synchronized void stopFallbackServerIfRunning() {
    if (fallbackServer != null) {
      try {
        fallbackServer.stop(0)
        println "Fallback HttpServer stopped."
      } catch (Exception e) {
        println "Error stopping fallback server: ${e.message}"
      } finally {
        fallbackServer = null
      }
    }
  }

  static void scheduleRetries(String[] args) {
    retryExecutor = Executors.newSingleThreadScheduledExecutor()
    retryExecutor.scheduleAtFixedRate({
      println "Retrying DB connection..."
      if (isDbReachable()) {
        println "DB is back — starting Grails on its own port, then switching nginx."
        retryExecutor.shutdown()

        System.setProperty('server.port', GRAILS_PORT as String)
        GrailsApp.run(Application, args) // starts on 8082 while fallback still serves 8081

        switchNginxUpstream('grails')     // instant, atomic — no dead window
        stopFallbackServerIfRunning()     // safe to stop now, nginx no longer routes to it
      }
    }, 8, 8, TimeUnit.SECONDS)
  }

  /**
   * Tells nginx which upstream to use, via a tiny config file nginx watches/reloads.
   * See nginx setup below for how this file is consumed.
   */
  static void switchNginxUpstream(String target) {
    File upstreamFile = new File('/usr/local/etc/nginx/upstream_target.conf')
    String upstreamName = (target == 'grails') ? 'grails_backend' : 'fallback_backend'
    upstreamFile.text = "set \$backend ${upstreamName};\n"
    ['/usr/local/bin/nginx', '-s', 'reload'].execute().waitFor()
    println "nginx switched to upstream: ${upstreamName}"
  }
}
