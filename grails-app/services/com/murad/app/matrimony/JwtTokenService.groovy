package com.murad.app.matrimony

import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.annotation.Secured
import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys

import javax.crypto.SecretKey
import java.util.concurrent.TimeUnit

@Transactional
class JwtTokenService {

  def grailsApplication

  @Secured(['permitAll'])
  private SecretKey getSigningKey() {
    String secret = grailsApplication.config.jwt.secret ?: "mySecretKeyThatIsAtLeast256BitsLongForHS256Algorithm"
    return Keys.hmacShaKeyFor(secret.bytes)
  }

  @Secured(['permitAll'])
  String generateToken(String username, Collection<String> roles = []) {
    Date now = new Date()
    Date expiration = new Date(now.time + TimeUnit.HOURS.toMillis(24)) // 24 hours

    return Jwts.builder()
      .setSubject(username)
      .claim("roles", roles)
      .setIssuedAt(now)
      .setExpiration(expiration)
      .signWith(getSigningKey(), SignatureAlgorithm.HS256)
      .compact()
  }

  @Secured(['permitAll'])
  Claims parseToken(String token) {
    return Jwts.parserBuilder()
      .setSigningKey(getSigningKey())
      .build()
      .parseClaimsJws(token)
      .getBody()
  }

  @Secured(['permitAll'])
  boolean validateToken(String token) {
    try {
      parseToken(token)
      return true
    } catch (JwtException | IllegalArgumentException e) {
      log.debug("Invalid JWT token: ${e.message}")
      return false
    }
  }

  @Secured(['permitAll'])
  String getUsernameFromToken(String token) {
    return parseToken(token).getSubject()
  }

  @Secured(['permitAll'])
  List<String> getRolesFromToken(String token) {
    Claims claims = parseToken(token)
    return claims.get("roles", List.class) ?: []
  }

  @Secured(['permitAll'])
  Date getExpirationDateFromToken(String token) {
    Claims claims = Jwts.parserBuilder()
      .setSigningKey(getSigningKey())
      .build()
      .parseClaimsJws(token)
      .getBody()

    return claims.getExpiration()
  }

  @Secured(['permitAll'])
  boolean isTokenExpired(String token) {
    Date expiration = getExpirationDateFromToken(token)
    return expiration.before(new Date())
  }
}
