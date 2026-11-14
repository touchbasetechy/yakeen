package com.murad.app.matrimony

import com.murad.app.matrimony.identity.LoggedInIdentity
import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.annotation.Secured
import grails.validation.ValidationException
import groovy.util.logging.Slf4j
import org.springframework.security.authentication.*
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.context.HttpSessionSecurityContextRepository

import java.time.LocalDateTime

@Slf4j
class LoginController {

    //  static responseFormats = ['json']

    SpringSecurityService springSecurityService
    AuthenticationManager authenticationManager
    JwtTokenService jwtTokenService
    RedirectService redirectService
    SignedInLogService signedInLogService
    def emailService

    @Secured(['permitAll'])
    @Transactional
    def authenticate() {
        def username = params.username ?: request.JSON?.username
        def password = params.password ?: request.JSON?.password

        username = username?.trim()
        password = password?.trim()

        if (!username && !password) {
            response.status = 400
            respond([success: false, message: "Username and password are required"], formats: ['json'])
            return
        }

        if (!username) {
            response.status = 400
            respond([success: false, message: "Username is required"], formats: ['json'])
            return
        }

        if (!password) {
            response.status = 400
            respond([success: false, message: "Password is required"], formats: ['json'])
            return
        }

        //    Person user = Person.findByEMail(username)

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            )

            if (authentication.authenticated) {
                Person person = authentication.principal as Person

                if (!person) {
                    response.status = 404
                    respond([success: false, message: "User not found with email ${username}"], formats: ['json'])
                    return
                }

                if (!person.enabled) {
                    response.status = 403
                    respond([
                            success: false,
                            message: "Account is not activated. Please check your email [${username}] for activation link."
                    ], formats: ['json'])
                    return
                }

                // Resolve roles once, up front - needed both for the redirect lookup
                // below and for the response payload later.
                def roles = person.personTypes.collect { it.personTypeName }

                List<LoggedInIdentity> identities = session.loggedInIdentities as List<LoggedInIdentity>
                if (identities == null) {
                    identities = []
                    session.loggedInIdentities = identities
                }

                LoggedInIdentity existing = identities.find { it.personId == person.id }
                int slot
                def redirectUrl
                if (existing) {
                    slot = existing.slot
                    // Existing identity: still need a redirect URL for the response,
                    // and this call also (re)populates the "controller"/"action"
                    // request attributes for consistency, even though we don't need
                    // them for a LoggedInIdentity here since one already exists.
                    redirectUrl = redirectService.getRedirectUrlForRoles(roles, slot, grailsLinkGenerator)
                } else {
                    slot = identities.size()

                    // IMPORTANT: call this BEFORE reading request.getAttribute("controller"/"action")
                    // below. getRedirectUrlForRoles() is what sets those attributes in the first
                    // place - reading them before this call always returns null.
                    redirectUrl = redirectService.getRedirectUrlForRoles(roles, slot, grailsLinkGenerator)

                    identities << new LoggedInIdentity(
                            slot: slot,
                            personId: person.id,
                            email: person.eMail,
                            displayName: "${person.firstName} ${person.lastName}",
                            controller: request.getAttribute("controller"),
                            action: request.getAttribute("action")
                    )
                }

                def securityContext = SecurityContextHolder.getContext()
                securityContext.setAuthentication(authentication)
                session.removeAttribute('SPRING_SECURITY_SAVED_REQUEST')
                session.setAttribute(
                        HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                        securityContext
                )

                if (person?.personDetails?.dob) {
                    LocalDateTime dob = person?.personDetails?.dob
                    person.personDetails.age = getCurrentAge(dob.toString())
                    try {
                        if (person.validate()) person.save(flush: true)
                    } catch (ValidationException e) {
                        log.error("Error updating age for user: ${person?.eMail}", e)
                    }
                }

                //        String jwtToken = jwtTokenService.generateToken(username, roles)

                signedInLogService.addSignInLog(person, session)
                log.info("User authenticated successfully: ${username}")

                def menuItems = redirectService.getAvailableMenuItems(roles)

                respond([
                        success    : true,
                        message    : "Authentication successful",
                        //          token      : jwtToken,
                        redirectUrl: redirectUrl,
                        menuItems  : menuItems,
                        user       : [
                                id       : person.id,
                                firstName: person.firstName,
                                lastName : person.lastName,
                                email    : person.eMail,
                                roles    : roles
                        ]
                ], formats: ['json'])

            } else {
                response.status = 401
                respond([success: false, message: "Invalid credentials"], formats: ['json'])
            }

        } catch (BadCredentialsException e) {
            log.error("Bad credentials for user: ${username}")
            response.status = 401
            respond([success: false, message: "Invalid email or password"], formats: ['json'])

        } catch (DisabledException e) {
            log.error("Account disabled for user: ${username}")
            response.status = 403
            respond([success: false, message: "Account is disabled. Please contact support."], formats: ['json'])

        } catch (LockedException e) {
            log.error("Account locked for user: ${username}")
            response.status = 403
            respond([success: false, message: "Account is locked. Please contact support."], formats: ['json'])

        } catch (Exception e) {
            log.error("Authentication error for ${username}: ${e.message}", e)
            response.status = 500
            respond([success: false, message: "Invalid credentials"], formats: ['json'])
        }
    }

    @Secured(['permitAll'])
    def refresh() {
        def token = request.getHeader("Authorization")?.replace("Bearer ", "")

        if (!token || !jwtTokenService.validateToken(token)) {
            response.status = 401
            respond([success: false, message: "Invalid token"], formats: ['json'])
            return
        }

        def username = jwtTokenService.getUsernameFromToken(token)
        Person user = Person.findByEMail(username)

        if (user) {
            def roles = user.personTypes.collect { it.personTypeName }
            def newToken = jwtTokenService.generateToken(username, roles)
            respond([
                    success: true,
                    token  : newToken,
                    message: "Token refreshed successfully"
            ], formats: ['json'])
        } else {
            response.status = 404
            respond([success: false, message: "User not found"], formats: ['json'])
        }
    }

    @Secured(['permitAll'])
    def logout() {
        SecurityContextHolder.clearContext()
        request.getSession(false)?.invalidate()
        respond([success: true, message: "Logged out successfully"], formats: ['json'])
    }

    @Transactional
    def logoutSlot() {
        Integer slot = params.slot?.toInteger()

        List<LoggedInIdentity> identities = session.loggedInIdentities as List<LoggedInIdentity>

        // findAll here will only ever match at most one entry in practice
        // (slot is assigned uniquely per identity in authenticate()), but
        // we still iterate the whole list rather than assuming a single
        // match, so this stays correct if that invariant ever changes.
        List<LoggedInIdentity> currentIdentities = identities?.findAll { it.slot == slot }

        currentIdentities?.each { LoggedInIdentity currentIdentity ->
            Person person = Person.findById(currentIdentity.personId)

            if (!person) {
                log.warn("No Person found for slot: ${slot}")
                return // continue to the next identity in the .each loop
            }

            SignInLog signInLog = SignInLog.findByUserAndStatus(person, "Online")
            if (!signInLog) {
                log.warn("No active SignInLog found for user: ${person?.eMail}")
                return
            }

            signInLog.loggedOutAt = LocalDateTime.now()
            signInLog.status = "Offline"
            signInLog.save(flush: true)

            log.info("SignInLog updated on logout for user: ${person?.eMail}")
        }

        identities?.removeAll { it.slot == slot }

        if (identities) {
            redirect(uri: "/u/${identities[0].slot}/${identities[0].controller}/${identities[0].action}")
        } else {
            session.invalidate()
            redirect(uri: '/sign-in')
        }
    }

    // POST /logoutAll — nukes every account in this browser session at once
    def logoutAll() {
        session.invalidate()
        redirect(uri: '/sign-in')
    }

    @Secured(['permitAll'])
    @Transactional
    def register() {
        if (request.method != 'POST') {
            response.status = 405
            respond([success: false, message: "Registration format is wrong"], formats: ['json'])
            return
        }

        try {
            def jsonParams = request.JSON
            def email = jsonParams?.email?.trim()

            if (Person.findByEMail(email)) {
                response.status = 409
                respond([success: false, message: "An account with this email already exists"], formats: ['json'])
                return
            }

            Person user = new Person(
                    firstName: jsonParams?.firstName,
                    lastName: jsonParams?.lastName,
                    password: jsonParams?.password,
                    eMail: email,
                    mobileNumber: jsonParams?.mobileNumber,
                    enabled: false,
                    accountExpired: false,
                    accountLocked: false,
                    passwordExpired: false
            )

            user.addToPersonTypes(PersonType.findByPersonTypeName('ROLE_USER'))

            if (user.validate() && user.save(flush: true)) {
                String token = jwtTokenService.generateToken(user.eMail, ['ROLE_USER'])
                user.activationToken = UUID.randomUUID().toString()
                Person currentUser = user.save(flush: true)
                session.personId = currentUser?.id

                def userPayload = [
                        id       : currentUser.id,
                        email    : currentUser.eMail,
                        firstName: currentUser.firstName,
                        lastName : currentUser.lastName,
                        roles    : ['ROLE_USER']
                ]

                boolean emailSent = emailService.sendActivationEmail(user, user.activationToken)
                String msg = emailSent
                        ? "Registration successful! Please check your email [${user.eMail}] to activate your account."
                        : "Registration successful, but failed to send activation email. Please contact support."

                respond([
                        success: true,
                        message: msg,
                        token  : token,
                        user   : userPayload
                ], formats: ['json'])

            } else {
                response.status = 400
                log.error("Registration validation errors: ${user.errors.allErrors.collect { it.defaultMessage }}")
                respond([
                        success: false,
                        errors : user.errors.allErrors.collect { it.defaultMessage }
                ], formats: ['json'])
            }

        } catch (Exception e) {
            log.error("Registration error: ${e.message}", e)
            response.status = 500
            respond([success: false, message: "Registration failed: ${e.message}"], formats: ['json'])
        }
    }

    def checkAuth() {
        log.info("Is logged in: ${springSecurityService.isLoggedIn()}")
        log.info("Current user: ${springSecurityService.currentUser}")
        log.info("Principal: ${springSecurityService.principal}")
        log.info("Authorities: ${springSecurityService.principal?.authorities}")
    }

    def addAccount() {}

    // ── Helpers ───────────────────────────────────────────────────────

    BigDecimal getCurrentAge(String dob) {
        if (!dob) return null
        LocalDateTime dobDate = LocalDateTime.parse(dob)
        def now = LocalDateTime.now()
        def period = java.time.Period.between(dobDate.toLocalDate(), now.toLocalDate())
        return (period.years + (period.months / 12.0) + (period.days / 365.25)).round(2)
    }
}
