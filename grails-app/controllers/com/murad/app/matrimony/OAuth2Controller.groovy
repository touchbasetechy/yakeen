package com.murad.app.matrimony

import grails.core.GrailsApplication
import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.annotation.Secured
import grails.plugin.springsecurity.userdetails.GrailsUser
import groovy.json.JsonSlurper
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.context.HttpSessionSecurityContextRepository
import org.springframework.web.client.RestTemplate

import java.security.SecureRandom

@Secured(['permitAll'])
class OAuth2Controller {

  def springSecurityService
  GrailsApplication grailsApplication
  OAuth2Service oAuth2Service
  SignedInLogService signedInLogService

  GitHubAuthService gitHubAuthService


  @Secured(['permitAll'])
  def google() {
    try {
      // Get configuration with null checks
      def clientId = grailsApplication.config.getProperty('google.oauth2.client.clientId')
      def redirectUri = grailsApplication.config.getProperty('google.oauth2.client.redirectUri')

      if (!clientId) {
        log.error("Google OAuth2 client ID not configured")
        flash.message = "Google Sign-In is not properly configured"
        redirect(controller: 'login', action: 'auth', params: [oauth2_error: 'config'])
        return
      }

      if (!redirectUri) {
        log.error("Google OAuth2 redirect URI not configured")
        flash.message = "Google Sign-In is not properly configured"
        redirect(controller: 'login', action: 'auth', params: [oauth2_error: 'config'])
        return
      }

      def scope = 'openid profile email'
      def state = generateStateParameter()

      // Store state in session for verification
      session.oauth2State = state

      def authUrl = "https://accounts.google.com/o/oauth2/v2/auth?" +
        "client_id=${URLEncoder.encode(clientId, 'UTF-8')}&" +
        "redirect_uri=${URLEncoder.encode(redirectUri, 'UTF-8')}&" +
        "scope=${URLEncoder.encode(scope, 'UTF-8')}&" +
        "response_type=code&" +
        "state=${URLEncoder.encode(state, 'UTF-8')}"
      
      redirect(url: authUrl)

    } catch (Exception e) {
      log.error("Error initiating Google OAuth2: ${e.message}", e)
      flash.message = "Failed to initiate Google Sign-In: ${e.message}"
      redirect(controller: 'login', action: 'auth', params: [oauth2_error: 'init'])
    }
  }

  @Secured(['permitAll'])
  def callback() {
    def code = params.code
    def state = params.state
    def error = params.error

    if (error) {
      log.error("OAuth2 error: ${error}")
      flash.message = "Authentication failed: ${error}"
      redirect(controller: 'login', action: 'auth', params: [oauth2_error: error])
      return
    }

    if (!code) {
      log.error("No authorization code received")
      flash.message = "Authentication failed: No authorization code received"
      redirect(controller: 'login', action: 'auth', params: [oauth2_error: 'no_code'])
      return
    }

    // Verify state parameter
    if (!state || state != session.oauth2State) {
      log.error("Invalid state parameter. Expected: ${session.oauth2State}, Received: ${state}")
      flash.message = "Authentication failed: Invalid state parameter"
      redirect(controller: 'login', action: 'auth', params: [oauth2_error: 'invalid_state'])
      return
    }

    try {
      // Exchange code for access token
      def tokenResponse = exchangeCodeForToken(code)
      if (!tokenResponse.access_token) {
        throw new Exception("No access token received")
      }

      // Get user info from Google
      def userInfo = getUserInfo(tokenResponse.access_token)
      if (!userInfo.email) {
        throw new Exception("No email received from Google")
      }

      // Find or create user
      def user = findOrCreateUser(userInfo)
      if (!user) {
        throw new Exception("Failed to create or find user")
      }

      // Authenticate user
      authenticateUser(user)

      // Clear state from session
      session.removeAttribute('oauth2State')
      session.personId = user?.id

      // Redirect to success URL
      def targetUrl = session.removeAttribute('SPRING_SECURITY_SAVED_REQUEST')?.requestURL ?: '/DBoard/index'
      redirect(url: targetUrl)

    } catch (Exception e) {
      log.error("OAuth2 callback error: ${e.message}", e)
      flash.message = "Authentication failed: ${e.message}"
      redirect(controller: 'login', action: 'auth', params: [oauth2_error: 'callback'])
    }
  }


  def exchangeCodeForToken(String code) {
    def clientId = grailsApplication.config.getProperty('google.oauth2.client.clientId')
    def clientSecret = grailsApplication.config.getProperty('google.oauth2.client.clientSecret')
    def redirectUri = grailsApplication.config.getProperty('google.oauth2.client.redirectUri')

    if (!clientSecret) {
      throw new Exception("Google OAuth2 client secret not configured")
    }

    def tokenUrl = "https://oauth2.googleapis.com/token"
    def postData = [
      code         : code,
      client_id    : clientId,
      client_secret: clientSecret,
      redirect_uri : redirectUri,
      grant_type   : 'authorization_code'
    ]

    def connection = new URL(tokenUrl).openConnection()
    connection.setRequestMethod('POST')
    connection.setRequestProperty('Content-Type', 'application/x-www-form-urlencoded')
    connection.setDoOutput(true)
    connection.setConnectTimeout(10000) // 10 seconds
    connection.setReadTimeout(10000)    // 10 seconds

    def postDataString = postData.collect { k, v ->
      "${URLEncoder.encode(k.toString(), 'UTF-8')}=${URLEncoder.encode(v.toString(), 'UTF-8')}"
    }.join('&')

    connection.outputStream.withWriter('UTF-8') { writer ->
      writer.write(postDataString)
    }

    def responseCode = connection.responseCode
    if (responseCode != 200) {
      def errorStream = connection.errorStream?.text ?: "Unknown error"
      throw new Exception("Token exchange failed with code ${responseCode}: ${errorStream}")
    }

    def response = connection.inputStream.text
    return new JsonSlurper().parseText(response)
  }

  def getUserInfo(String accessToken) {
    def userInfoUrl = "https://www.googleapis.com/oauth2/v2/userinfo"

    def connection = new URL(userInfoUrl).openConnection()
    connection.setRequestProperty('Authorization', "Bearer ${accessToken}")
    connection.setConnectTimeout(10000) // 10 seconds
    connection.setReadTimeout(10000)    // 10 seconds

    def responseCode = connection.responseCode
    if (responseCode != 200) {
      def errorStream = connection.errorStream?.text ?: "Unknown error"
      throw new Exception("User info request failed with code ${responseCode}: ${errorStream}")
    }

    def response = connection.inputStream.text
    return new JsonSlurper().parseText(response)
  }

  @Transactional
  def findOrCreateUser(def userInfo) {
    def email = userInfo.email
    def user = Person.findByEMail(email)

    if (!user) {
      // Create new user
      user = new Person(
        eMail: email,
        firstName: userInfo.given_name ?: '',
        lastName: userInfo.family_name ?: '',
        password: springSecurityService.encodePassword(generateRandomPassword()),
        enabled: true,
        accountExpired: false,
        accountLocked: false,
        passwordExpired: false
      )

//      if (!user.save(flush: true)) {
//        log.error("Failed to save new user: ${user.errors}")
//        throw new Exception("Failed to create user account: ${user.errors}")
//      }

      // Assign default role
      def defaultRole = PersonType.findByPersonTypeName('ROLE_USER') ?:
        PersonType.findByPersonTypeName('USER') ?:
          PersonType.list()?.first()

      if (defaultRole) {
        user.addToPersonTypes(defaultRole)
        if (!user.save(flush: true)) {
          log.error("Failed to assign role to user: ${user.errors}")
        }
      } else {
        log.warn("No default role found for new user")
      }

      log.info("Created new user via Google OAuth2: ${email}")
    } else {
      log.info("Existing user logged in via Google OAuth2: ${email}")
    }

    return user
  }

  def authenticateUser(Person user) {
    try {
      def authorities = user.authorities?.collect { new SimpleGrantedAuthority(it.authority) } ?: ['ROLE_USER']
      def principal = new GrailsUser(user.eMail, 'N/A', true, true, true, true, authorities, user.id)
      def authentication = new UsernamePasswordAuthenticationToken(principal, null, authorities)

      SecurityContextHolder.context.authentication = authentication

      session.setAttribute(
        HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
        SecurityContextHolder.context
      )

      signedInLogService.addSignInLog(user, session)

      log.info("Successfully authenticated user: ${user.eMail}")
    } catch (Exception e) {
      log.error("Failed to authenticate user: ${e.message}", e)
      throw new Exception("Authentication setup failed")
    }
  }

  def generateStateParameter() {
    def random = new SecureRandom()
    def bytes = new byte[32]
    random.nextBytes(bytes)
    return bytes.encodeBase64().toString()
  }

  def generateRandomPassword() {
    def random = new SecureRandom()
    def bytes = new byte[16]
    random.nextBytes(bytes)
    return bytes.encodeBase64().toString()
  }

  @Secured(['permitAll'])
  def success() {
    if (!springSecurityService.isLoggedIn()) {
      redirect(controller: 'login', action: 'auth')
      return
    }

    render(view: 'success', model: [user: springSecurityService.currentUser])
  }

  @Secured(['permitAll'])
  def error() {
    def error = params.error ?: 'Unknown error occurred'
    render(view: 'error', model: [error: error])
  }

  @Secured(['permitAll'])
  def gitHub() {
    try {
      // Get configuration with null checks
      def clientId = grailsApplication.config.getProperty('github.oauth2.client.clientId')
      def redirectUri = grailsApplication.config.getProperty('github.oauth2.client.redirectUri')

      if (!clientId) {
        log.error("Github OAuth2 client ID not configured")
        flash.message = "Github Sign-In is not properly configured"
        redirect(controller: 'login', action: 'auth', params: [oauth2_error: 'config'])
        return
      }

      if (!redirectUri) {
        log.error("Github OAuth2 redirect URI not configured")
        flash.message = "Github Sign-In is not properly configured"
        redirect(controller: 'login', action: 'auth', params: [oauth2_error: 'config'])
        return
      }

      def scope = 'user:email'
      def state = generateState()

      // Store state in session for verification
      session.oauth2State = state

      def authUrl = grailsApplication.config.getProperty('github.oauth2.provider.authorizationUri') + "?" +
        "client_id=${URLEncoder.encode(clientId, 'UTF-8')}&" +
        "redirect_uri=${URLEncoder.encode(redirectUri, 'UTF-8')}&" +
        "scope=${URLEncoder.encode(scope, 'UTF-8')}&" +
        "state=${URLEncoder.encode(state, 'UTF-8')}"

      log.info("Redirecting to Github OAuth2: ${authUrl}")
      redirect(url: authUrl)

    } catch (Exception e) {
      log.error("Error initiating Github OAuth2: ${e.message}", e)
      flash.message = "Failed to initiate Github Sign-In: ${e.message}"
      redirect(controller: 'login', action: 'auth', params: [oauth2_error: 'init'])
    }
  }

  @Secured(['permitAll'])
  def index() {
    if (springSecurityService.isLoggedIn()) {
      redirect(controller: 'DBoard', action: 'index')
    } else {
      render(view: 'login')
    }
  }

  @Secured('permitAll')
  def gitHubCallback() {
    String code = params.code
    String state = params.state

    if (!code || !state) {
      flash.error = "Invalid GitHub authentication response"
      redirect(action: 'index')
      return
    }

    try {
      // Exchange code for access token
      String accessToken = gitHubExchangeCodeForToken(code)

      if (!accessToken) {
        flash.error = "Failed to get access token from GitHub"
        redirect(action: 'index')
        return
      }

      // Get user info from GitHub
      Map githubUserData = fetchGitHubUserData(accessToken)

      if (!githubUserData) {
        flash.error = "Failed to fetch user data from GitHub"
        redirect(action: 'index')
        return
      }

      // Create or update user
      Person user = gitHubAuthService.createOrUpdateUserFromGitHub(githubUserData, accessToken)

      if (user) {
        // Log in the user
        // Authenticate user
        authenticateUser(user)

        // Clear state from session
        session.removeAttribute('oauth2State')
        session.personId = user?.id
        // Redirect to success URL
        def targetUrl = session.removeAttribute('SPRING_SECURITY_SAVED_REQUEST')?.requestURL ?: '/DBoard/index'

        redirect(url: targetUrl)
      } else {
        flash.error = "Failed to create or update user account"
        redirect(action: 'index')
      }

    } catch (Exception e) {
      flash.error = "Authentication failed. Please try again."
      redirect(action: 'index')
    } finally {
      session.removeAttribute('oauthState')
    }
  }

  @Secured(['permitAll'])
  def gitHubExchangeCodeForToken(String code) {
    String clientId = grailsApplication.config.getProperty('github.oauth2.client.clientId')
    String clientSecret = grailsApplication.config.getProperty('github.oauth2.client.clientSecret')
    String redirectUri = grailsApplication.config.getProperty('github.oauth2.client.redirectUri')
    String encodedRedirectURI = URLEncoder.encode(redirectUri, 'UTF-8')

    RestTemplate restTemplate = new RestTemplate()
    HttpHeaders headers = new HttpHeaders()
    headers.set('Accept', 'application/json')

    String tokenUrl = "https://github.com/login/oauth/access_token?" +
      "client_id=" + clientId + "&" +
      "client_secret=" + clientSecret + "&" +
      "code=${code}&" +
      "redirect_uri=${redirectUri}"

    HttpEntity<String> entity = new HttpEntity<>(headers)
    ResponseEntity<String> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, entity, String.class)

    if (response.statusCode.is2xxSuccessful()) {
      JsonSlurper jsonSlurper = new JsonSlurper()
      Map tokenData = jsonSlurper.parseText(response.body) as Map
      return tokenData.access_token
    }
    return null
  }

  @Secured(['permitAll'])
  def fetchGitHubUserData(String accessToken) {
    RestTemplate restTemplate = new RestTemplate()
    HttpHeaders headers = new HttpHeaders()
    headers.set('Authorization', "Bearer ${accessToken}")
    headers.set('Accept', 'application/vnd.github.v3+json')

    HttpEntity<String> entity = new HttpEntity<>(headers)
    ResponseEntity<String> response = restTemplate.exchange(
      'https://api.github.com/user',
      HttpMethod.GET,
      entity,
      String.class
    )

    if (response.statusCode.is2xxSuccessful()) {
      JsonSlurper jsonSlurper = new JsonSlurper()
      return jsonSlurper.parseText(response.body) as Map
    }
    return null
  }

  private String generateState() {
    return UUID.randomUUID().toString()
  }

  def validateState(String receivedState) {
    // Compare with state stored in session
    def expectedState = session.remove('oauth_state')
    return receivedState && expectedState && receivedState == expectedState
  }
}
