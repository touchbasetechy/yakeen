package com.app.matrimony

import groovy.util.logging.Slf4j
import org.springframework.web.filter.OncePerRequestFilter

import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Slf4j
class CustomSecurityDebugFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {

    if (log.isDebugEnabled()) {
      debugRequest(request)
    }

    try {
      filterChain.doFilter(request, response)
    } catch (Exception e) {
      log.error("Error in security filter chain", e)
      throw e
    }

    if (log.isDebugEnabled()) {
      debugResponse(response)
    }
  }

  private void debugRequest(HttpServletRequest request) {
    try {
      log.debug("=== Security Debug - Request ===")
      log.debug("URL: ${request.requestURL}")
      log.debug("Method: ${request.method}")
      log.debug("Remote Address: ${request.remoteAddr}")

      // Safe session access
      def session = safeGetSession(request, false)
      if (session) {
        log.debug("Session ID: ${session.id}")
        log.debug("Session Creation Time: ${new Date(session.creationTime)}")
      } else {
        log.debug("No session found")
      }

      // Log security context if available
      try {
        def securityContext = org.springframework.security.core.context.SecurityContextHolder.context
        if (securityContext?.authentication) {
          log.debug("Principal: ${securityContext.authentication.principal}")
          log.debug("Authorities: ${securityContext.authentication.authorities}")
        } else {
          log.debug("No authentication found")
        }
      } catch (Exception e) {
        log.debug("Could not access security context: ${e.message}")
      }

    } catch (Exception e) {
      log.warn("Error in debug logging: ${e.message}")
    }
  }

  private void debugResponse(HttpServletResponse response) {
    try {
      log.debug("=== Security Debug - Response ===")
      log.debug("Status: ${response.status}")
    } catch (Exception e) {
      log.warn("Error in response debug logging: ${e.message}")
    }
  }

  private def safeGetSession(HttpServletRequest request, boolean create) {
    try {
      return request.getSession(create)
    } catch (Exception e) {
      log.warn("Could not access session: ${e.message}")
      return null
    }
  }
}
