package com.murad.app.matrimony

import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.SpringSecurityService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException

@Transactional
class UserService implements UserDetailsService {

  SpringSecurityService springSecurityService

  @Override
  UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Person person = Person.findByEMail(username)
    if (!person) {
      throw new UsernameNotFoundException("User not found: ${username}")
    }
    return person
  }

  Person createUser(String firstName, String lastName, String email, String password, String mobileNumber = null) {
    Person person = new Person(
      firstName: firstName,
      lastName: lastName,
      eMail: email,
      password: password,
      mobileNumber: mobileNumber,
      enabled: true
    )

    if (person.save(flush: true)) {
      // Assign default role (e.g., USER)
      PersonType userRole = PersonType.findByPersonTypeName('ROLE_USER') ?:
        new PersonType(personTypeName: 'ROLE_USER').save(flush: true)

      new PersonPersonType(person: person, personType: userRole).save(flush: true)
      return person
    }
    return null
  }

  boolean addRoleToUser(Person person, String roleName) {
    PersonType role = PersonType.findByPersonTypeName(roleName)
    if (!role) {
      role = new PersonType(personTypeName: roleName).save(flush: true)
    }

    // Check if user already has this role
    PersonPersonType existingRole = PersonPersonType.findByPersonAndPersonType(person, role)
    if (!existingRole) {
      new PersonPersonType(person: person, personType: role).save(flush: true)
      return true
    }
    return false
  }

  boolean removeRoleFromUser(Person person, String roleName) {
    PersonType role = PersonType.findByPersonTypeName(roleName)
    if (role) {
      PersonPersonType userRole = PersonPersonType.findByPersonAndPersonType(person, role)
      if (userRole) {
        userRole.delete(flush: true)
        return true
      }
    }
    return false
  }

  List<String> getUserRoles(Person person) {
    return PersonPersonType.findAllByPerson(person).collect { it.personType.personTypeName }
  }


  UserDetails loadUserByOAuth2(String provider, String uid, Map attributes) {
    // Find or create user based on OAuth provider and UID
    def oauthId = OAuthID.findByProviderAndUid(provider, uid)
    Person user

    if (oauthId) {
      user = oauthId.user
    } else {
      // Create new user
      user = new Person(
        username: attributes.email ?: "${provider}-${uid}",
        password: UUID.randomUUID().toString() // Random password for OAuth users
      )

      if (!user.save(flush: true)) {
        log.error("Couldn't save user: ${user.errors}")
        return null
      }

      oauthId = new OAuthID(
        provider: provider,
        uid: uid,
        user: user
      )

      if (!oauthId.save(flush: true)) {
        log.error("Couldn't save OAuthID: ${oauthId.errors}")
        return null
      }
    }

    // Grant default roles if needed
    if (!user.authorities) {
      def role = // assign default role
        UserRole.create(user, role, true)
    }

    return loadUserByUsername(user.username)
  }
}
