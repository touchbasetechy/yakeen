package com.app.matrimony

import com.murad.app.matrimony.JwtTokenService
import grails.plugin.springsecurity.annotation.Secured
import grails.util.Holders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JwtAuthenticationFilter extends OncePerRequestFilter {

  JwtTokenService jwtTokenService

  @Secured(['permitAll'])
  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {

    String token = extractTokenFromRequest(request)

    if (token) {
      // Get jwtTokenService from Spring context if not injected
      if (!jwtTokenService) {
        jwtTokenService = Holders.grailsApplication.mainContext.getBean('jwtTokenService')
      }

      if (jwtTokenService.validateToken(token)) {
        String username = jwtTokenService.getUsernameFromToken(token)
        List<String> roles = jwtTokenService.getRolesFromToken(token)

        // Create authorities with proper ROLE_ prefix
        List<SimpleGrantedAuthority> authorities = roles.collect {
          new SimpleGrantedAuthority(it.startsWith("ROLE_") ? it : "ROLE_${it}")
        }

        // Create authentication token
        UsernamePasswordAuthenticationToken authentication =
          new UsernamePasswordAuthenticationToken(username, null, authorities)

        // Set in security context
        SecurityContextHolder.getContext().setAuthentication(authentication)
      }
    }

    filterChain.doFilter(request, response)
  }

  @Secured(['permitAll'])
  private String extractTokenFromRequest(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization")
    if (bearerToken && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7)
    }
    return null
  }

  @Secured(['permitAll'])
  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
    String path = request.getRequestURI()
    // Skip JWT filter for login endpoints
    return path.contains("/login/") || path.contains("/api/auth/")
  }
}
