package com.app.matrimony

import com.murad.app.matrimony.*
import grails.core.GrailsApplication
import grails.gorm.transactions.Transactional
import groovy.util.logging.Slf4j
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Slf4j
@Component
@Order(-2147483638)
class StartupDataListener implements ApplicationListener<ApplicationReadyEvent> {

  GrailsApplication grailsApplication
  ChatService chatService

  @Transactional
  @Override
  void onApplicationEvent(ApplicationReadyEvent event) {
    def springSecurityService  // Inject if in BootStrap

    if (PersonType.count() < 1) {
      def roles = ['ROLE_USER', 'ROLE_ADMIN', 'ROLE_SUPER']

      roles.each { role ->
        if (!PersonType.findByPersonTypeName(role)) {
          new PersonType(personTypeName: role).save(flush: true, failOnError: true)
          println "Inserted role: $role"
        }
      }
    }

    // Ensure ROLE_SUPER exists
    def superRole = PersonType.findByPersonTypeName('ROLE_SUPER')
    if (!superRole) {
      superRole = new PersonType(personTypeName: 'ROLE_SUPER').save(flush: true, failOnError: true)
    }

    // Ensure ROLE_ADMIN exists
    def adminRole = PersonType.findByPersonTypeName('ROLE_ADMIN')
    if (!adminRole) {
      adminRole = new PersonType(personTypeName: 'ROLE_ADMIN').save(flush: true, failOnError: true)
    }

    // Ensure ROLE_USER exists
    def userRole = PersonType.findByPersonTypeName('ROLE_USER')
    if (!userRole) {
      userRole = new PersonType(personTypeName: 'ROLE_USER').save(flush: true, failOnError: true)
    }

    Policy policy = new Policy(isActive: true, isBlocked: false, isOnHold: false)

    // Check if the super user already exists
    def existingSuperUser = Person.findByEMail("super@murad.com")
    if (!existingSuperUser) {
      def user = new Person(
        firstName: "Super",
        lastName: "User",
        eMail: "super@murad.com",
        password: "Fathiali34",
        enabled: true
      )

      // Inject SpringSecurityService if needed
      user.springSecurityService = springSecurityService

      // Associate role
      user.addToPersonTypes(superRole)

      user.save(flush: true, failOnError: true)
      println "Super user created successfully"
    } else {
      println "Super user already exists"
    }

    // Check if the admin user already exists
    def existingAdminUser = Person.findByEMail("admin@murad.com")
    if (!existingAdminUser) {
      def adminUser = new Person(
        firstName: "Admin",
        lastName: "User",
        eMail: "admin@murad.com",
        password: "Fathiali34",
        enabled: true
      )

      // Inject SpringSecurityService if needed
      adminUser.springSecurityService = springSecurityService

      // Associate role
      adminUser.addToPersonTypes(adminRole)

      adminUser.save(flush: true, failOnError: true)
      println "Admin user created successfully"
    } else {
      println "Admin user already exists"
    }

    // Check if the guest user already exists
    def existingGuestUser = Person.findByEMail("guest@murad.com")
    if (!existingGuestUser) {
      def guestUser = new Person(
        firstName: "Guest",
        lastName: "User",
        eMail: "guest@murad.com",
        password: "Fathiali34",
        enabled: true
      )

      // Inject SpringSecurityService if needed
      guestUser.springSecurityService = springSecurityService

      // Associate role
      guestUser.addToPersonTypes(userRole)

      guestUser.save(flush: true, failOnError: true)
      println "Guest user created successfully"
    } else {
      println "Guest user already exists"
    }

    // Seed default public chat rooms if none exist
    ChatRoom.withTransaction {
      if (ChatRoom.count() == 0) {
        [
          [roomId: 'general', name: 'General', description: 'Open conversation for everyone.'],
          [roomId: 'introductions', name: 'Introductions', description: 'Say hello and introduce yourself!'],
          [roomId: 'matrimony-talk', name: 'Matrimony Talk', description: 'Discuss matrimony topics and share experiences.'],
        ].each { Map attrs ->
          new ChatRoom(
            roomId: attrs.roomId,
            name: attrs.name,
            description: attrs.description,
            privateRoom: false
          ).save(flush: true, failOnError: true)
        }
      }
    }

    // Create sample data for development environment
    if (grails.util.Environment.isDevelopmentMode()) {
      createSampleShortlistData()
    }

    // Initialize shortlist-related configurations
    initializeShortlistConfig()
//
//    new com.murad.app.matrimony.PersonPersonType([person: adminUser, personType: adminRole, active: true]).save(flush: true)
//    new com.murad.app.matrimony.PersonPersonType([person: adminUser, personType: userRole, active: true]).save(flush: true)
  }

  private void createSampleShortlistData() {
    // Only create if no shortlist data exists
    if (com.murad.app.matrimony.Shortlist.count() == 0) {
      log.info("Creating sample shortlist data for development...")

      def users = com.murad.app.matrimony.Person.list(max: 5)
      if (users.size() >= 3) {
        try {
          // Create some sample shortlist entries
          new Shortlist(
            user: users[0],
            shortlistedUser: users[1],
            notes: "Interesting profile, similar educational background"
          ).save(failOnError: true)

          new Shortlist(
            user: users[0],
            shortlistedUser: users[2],
            notes: "Good family values and professional background"
          ).save(failOnError: true)

          new Shortlist(
            user: users[1],
            shortlistedUser: users[0],
            notes: "Mutual interests in music and travel"
          ).save(failOnError: true)

          log.info("Sample shortlist data created successfully")
        } catch (Exception e) {
          log.error("Error creating sample shortlist data: ${e.message}")
        }
      }
    }
  }

  private void initializeShortlistConfig() {
    // Set any application-wide shortlist configurations

    // Maximum shortlist size per user (optional constraint)
//    def maxShortlistSize = grailsApplication.config.matrimony.shortlist.maxSize ?: 100

    // Shortlist cleanup job interval (for removing old entries)
//    def cleanupInterval = grailsApplication.config.matrimony.shortlist.cleanupInterval ?: 30 // days

//    log.info("Shortlist configuration initialized - Max size: ${maxShortlistSize}, Cleanup interval: ${cleanupInterval} days")
  }
}
