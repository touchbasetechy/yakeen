package com.murad.app.matrimony

class ErrorController {

  def serverError() {
    def exception = request.exception ?: request.getAttribute('javax.servlet.error.exception')
    def statusCode = request.getAttribute('javax.servlet.error.status_code') ?: 500
    def requestUri = request.getAttribute('javax.servlet.error.request_uri') ?: request.forwardURI

    log.error("Server error occurred: ${exception?.message}", exception)

    respond([
      status   : statusCode,
      exception: exception,
      path     : requestUri
    ], view: "/error/serverError")
  }

  def notFound() {
    def requestUri = request.getAttribute('javax.servlet.error.request_uri') ?: request.forwardURI

    log.warn("404 Not Found: ${requestUri}")

    respond([
      path: requestUri
    ], view: "/error/notFound")
  }

  def forbidden() {
    def requestUri = request.getAttribute('javax.servlet.error.request_uri') ?: request.forwardURI

    log.warn("403 Forbidden: ${requestUri}")

    respond([
      path: requestUri
    ], view: "/error/forbidden")
  }

  def unauthorized() {
    def requestUri = request.getAttribute('javax.servlet.error.request_uri') ?: request.forwardURI

    log.warn("401 Unauthorized: ${requestUri}")

    respond([
      path: requestUri
    ], view: "/error/unauthorized")
  }

  def dbDown(Exception exception) {
    def requestUri = request.getAttribute('javax.servlet.error.request_uri') ?: request.forwardURI
  
    log.error("Unhandled exception", exception)

    respond([
      path: requestUri
    ], view: "/error/dbDown")
  }
}
