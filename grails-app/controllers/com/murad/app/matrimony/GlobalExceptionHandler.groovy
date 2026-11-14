package com.murad.app.matrimony

import grails.web.Controller

@Controller
class GlobalExceptionHandler {

  def handleException(Exception e) {
    log.error("Request: ${request.forwardURI} failed", e)

    if (grails.util.Environment.current == grails.util.Environment.DEVELOPMENT) {
      request.stackTrace = e.stackTrace
    }

    withFormat {
      html {
        request.exception = e
        forward(controller: 'error', action: 'internalServerError')
      }
      json {
        render(status: 500, [
          timestamp: new Date(),
          status   : response.status,
          error    : "Internal Server Error",
          path     : request.forwardURI
        ])
      }
    }

  }
}
