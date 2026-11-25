package com.murad.app.matrimony


import grails.plugin.springsecurity.SpringSecurityService
import groovy.transform.EqualsAndHashCode
import org.grails.datastore.gorm.GormEntity
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder

@EqualsAndHashCode(includes = 'eMail')
class Person implements UserDetails, Serializable, GormEntity<Person> {
  transient SpringSecurityService springSecurityService

  String firstName
  String lastName
  String eMail
  String mobileNumber
  String password
  String passwordResetToken
  String githubId
  String githubUsername
  String activationToken
  Date activationTokenExpiry
  Date dateCreated
  Date lastUpdated
  String profilePicture
  boolean enabled = true
  boolean accountExpired = false
  boolean accountLocked = false
  boolean passwordExpired = false

  static hasOne = [personDetails: PersonDetails, spouseDetails: SpouseDetails, familyDetails: FamilyDetails, spouseFamilyDetails: SpouseFamilyDetails, policies: Policy, additionalSpouseDetails: AdditionalSpouseDetails]
  static hasMany = [personTypes: PersonType, messages: Message, contactDetails: ContactDetails, educationDetails: EducationDetails, professionDetails: ProfessionalDetails, address: Address, hobbies: Hobby, sentInterests: Interest, receivedInterests: Interest, oAuthIDs: OAuthID]

  // TODO: sentInterests / receivedInterests both point to Interest, so they also need
  // entries here, e.g. sentInterests: "sender", receivedInterests: "receiver"
  // (use the actual property names from your Interest class).
  static mappedBy = [hobbies: "persons"]

  static mapping = {
    table name: "person"
    password column: '`password`'

    personTypes joinTable: [
      name  : 'person_person_type',
      key   : 'person_id',
      column: 'person_type_id'
    ]

    personDetails lazy: true, cascade: 'all'
    spouseDetails lazy: true, cascade: 'all'
    familyDetails lazy: true, cascade: 'all'
    spouseFamilyDetails lazy: true, cascade: 'all'
    policies lazy: true, cascade: 'all'
    additionalSpouseDetails lazy: true, cascade: 'all'

    contactDetails lazy: true, cascade: 'all', sort: 'id', order: 'asc'
    educationDetails lazy: true, cascade: 'all', sort: 'id', order: 'asc'
    professionDetails lazy: true, cascade: 'all', sort: 'id', order: 'asc'
    address lazy: true, cascade: 'all', sort: 'id', order: 'asc'
    messages lazy: true

    // Many-to-many with Hobby (Person is the owning side); no collection cache
    hobbies lazy: true, joinTable: [name: 'person_hobbies', key: 'person_id', column: 'hobby_id']
    sentInterests lazy: true, cascade: 'all', sort: 'id', order: 'asc'
    receivedInterests lazy: true, cascade: 'all', sort: 'id', order: 'asc'
    batchSize 25
  }

  static constraints = {
    firstName blank: false
    lastName blank: false
    eMail email: true, blank: false, unique: true
    mobileNumber nullable: true
    password blank: false, password: true
    passwordResetToken nullable: true
    personDetails nullable: true
    spouseDetails nullable: true
    familyDetails nullable: true
    spouseFamilyDetails nullable: true
    contactDetails nullable: true
    educationDetails nullable: true
    professionDetails nullable: true
    address nullable: true
    policies nullable: true
    additionalSpouseDetails nullable: true
    messages nullable: true
    hobbies nullable: true
    sentInterests nullable: true
    receivedInterests nullable: true
    githubId nullable: true, blank: true, unique: true
    githubUsername nullable: true, blank: true
    activationToken nullable: true, blank: true
    activationTokenExpiry nullable: true, blank: true
    profilePicture nullable: true, blank: true
  }

  Set<GrantedAuthority> getAuthorities() {
    personTypes.collect {
      new SimpleGrantedAuthority(it.getAuthority())
    } as Set
  }

  @Override
  String getUsername() {
    eMail
  }

  @Override
  boolean isAccountNonExpired() {
    !accountExpired
  }

  @Override
  boolean isAccountNonLocked() {
    !accountLocked
  }

  @Override
  boolean isCredentialsNonExpired() {
    !passwordExpired
  }

  def beforeInsert() {
    encodePassword()
  }

  def beforeUpdate() {
    if (isDirty('password')) {
      encodePassword()
    }
  }

  protected void encodePassword() {
    if (password) {
      if (springSecurityService) {
        password = springSecurityService.passwordEncoder.encode(password)
      } else {
        def ctx = grails.util.Holders.grailsApplication.mainContext
        PasswordEncoder passwordEncoder = ctx.getBean('passwordEncoder') as PasswordEncoder
        password = passwordEncoder.encode(password)
      }
    }
  }

  String toString() {
    "${firstName} ${lastName}"
  }
}
