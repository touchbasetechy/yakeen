package com.murad.app.matrimony

import grails.converters.JSON
import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.annotation.Secured
import grails.validation.ValidationException
import org.hibernate.Criteria

import java.time.Duration
import java.time.LocalDateTime

class PersonController {
  SpringSecurityService springSecurityService
  SecurityService securityService
  def countryCodeService
  def religionService
  def heightService

  @Secured(['ROLE_ADMIN'])
  def index() {
    def signedInTime = SignInLog.findByUser(request.person)

    Long daysBetween = null

    if (signedInTime && signedInTime.loggedOutAt) {
      Duration duration = Duration.between(signedInTime.loggedOutAt, signedInTime.loggedInAt)
      daysBetween = duration.toDays() // 24-hour periods

      daysBetween = Math.abs(daysBetween)
    }

    String popper

    if (daysBetween == null) {
      popper = "🎉 Welcome, new user! This is your first login."
    } else if (daysBetween == 0) {
      popper = "👋 Welcome back! You logged in today."
    } else if (daysBetween == 1) {
      popper = "🌟 Nice to see you again! It's been 1 day since your last login."
    } else if (daysBetween <= 7) {
      popper = "🚀 Welcome back! You were away for ${daysBetween} days."
    } else {
      popper = "🌍 Long time no see! It's been ${daysBetween} days since your last login."
    }

    def signedInUsers = SignInLog.where {
      user != null && user != request.person && status == "Online" && user?.personType?.personTypeName != "Super"
    }.list(sort: 'loggedInAt', order: 'desc') // Optional sorting

    [person: request.person, signedInUsers: signedInUsers, popperMsg: popper]
  }


  @Secured(['ROLE_ADMIN'])
  def listUsers() {
    params.offset = params?.offset ? params.int('offset') : 0
    params.max = params?.max ? params.int('max') : 10

    // Use HQL to return proper domain objects with associations
    def hql = """
        SELECT DISTINCT p
        FROM Person p
        JOIN FETCH p.personTypes pt
        LEFT JOIN FETCH p.personDetails pd
        LEFT JOIN FETCH p.spouseDetails sd
        WHERE pt.personTypeName = :personTypeName
        ORDER BY p.id
    """

    def persons = Person.executeQuery(
      hql,
      [personTypeName: 'ROLE_USER'],
      [offset: params.offset, max: params.max]
    )

    [persons: persons]
  }


  def listMorePersons() {
    params.offset = params?.offset ? params.int('offset') : 0
    params.max = params?.max ? params.int('max') : 10

    // HQL query with joins to fetch associated entities
    def hqlQuery = """
        SELECT DISTINCT p
        FROM Person p
        JOIN FETCH p.personDetails
        JOIN FETCH p.spouseDetails
        JOIN p.personType pt
        WHERE pt.personTypeName = :personTypeName
    """

    def persons = Person.executeQuery(
      hqlQuery,
      [personTypeName: 'User'],
      [offset: params.offset, max: params.max]
    )

    def jsonData = persons as JSON

    response.contentType = 'application/json'

    render jsonData
  }

  def addPerson() {}

  @Transactional
  def save() {
    def personType = PersonType.findByPersonTypeName("User")

    if (!personType) {
      flash.error = "Attempt to create user failed!"
      redirect(controller: "person", action: "index")
    } else {
      def person = new Person(params)

      try {
        if (person.validate() && person.save(flush: true)) {
          session.person = personSaved
          flash.message = "User created successfully!"
          redirect(action: "index")
        } else {
          flash.error = "Failed creating user!"
          redirect(action: "addPerson")
        }
      } catch (ValidationException e) {
        flash.error = "User not created ${e.errors}"
        redirect(action: "addPerson")
      }
    }
  }

  def addTicket() {}

  @Secured('ROLE_ADMIN')
  def listTicketTypeForAdd() {
    params.offset = params?.offset ? params.int('offset') : 0
    params.max = params?.max ? params.int('max') : 10

    def ticketTypes = TicketType.list(params)

    [ticketTypes: ticketTypes]
  }

  def listMoreTicketTypes() {
    params.offset = params?.offset ? params.int('offset') : 0
    params.max = params?.max ? params.int('max') : 10

    def ticketTypes = TicketType.list(params)
    def jsonData = ticketTypes as JSON
    response.contentType = 'application/json'
    render jsonData
  }

  @Transactional
  def saveTicket() {
    TicketType ticketType = TicketType.findById(params?.id?.toLong())

    if (!ticketType) {
      flash.error = "Attempt to create ticket failed!"
      redirect(action: "addTicket")
    } else {
      def date = new Date()
      def currentTimeMillis = date.getTime().longValue()
      def ticketId = "Tkt" + currentTimeMillis

      def ticket = new Ticket(ticketId: ticketId, subject: params?.subject, message: params?.message, ticketType: ticketType)

      try {
        if (ticket.validate() && ticket.save(flush: true)) {
          flash.message = "Ticket of type ' ${ticketType.name} ' created successfully!"
          redirect(controller: 'person', action: 'index', params: [id: params?.id, ac: params?.ac])
        } else {
          flash.error = "Failed creating ticket!"
          redirect(controller: 'person', action: 'addTicket', params: [id: params?.id, ac: params?.ac])
        }
      } catch (Exception e) {
        flash.error = "Ticket not created ${e.errors}"
        redirect(controller: 'person', action: 'addTicket', params: [id: params?.id, ac: params?.ac])
      }
    }
  }

  @Secured('ROLE_ADMIN')
  def addTicketWithoutTT() {}

  @Secured('ROLE_ADMIN')
  @Transactional
  def saveTicketWithoutTT() {
    def date = new Date()
    def currentTimeMillis = date.getTime().longValue()
    def ticketId = "Tkt" + currentTimeMillis

    def ticket = new Ticket(ticketId: ticketId, subject: params?.subject, message: params?.message, ticketType: null)

    try {
      if (ticket.validate() && ticket.save(flush: true)) {
        flash.message = "Ticket created successfully!"
        redirect(controller: 'person', action: 'index', params: [id: params?.id, ac: params?.ac])
      } else {
        flash.error = "Failed creating ticket!"
        redirect(controller: 'person', action: 'addTicket', params: [id: params?.id, ac: params?.ac])
      }
    } catch (Exception e) {
      flash.error = "Ticket not created ${e.errors}"
      redirect(controller: 'person', action: 'addTicket', params: [id: params?.id, ac: params?.ac])
    }
  }

  def edit() {
    def personToEdit = Person.findById(params?.id?.toLong())
    def oPTId = personToEdit?.personType?.id

    if (params?.pt && (oPTId != params?.pt?.toLong())) {
      def personType = PersonType.findById(params?.pt?.toLong())

      personToEdit.personType = personType
      session.personIdToEdit = personToEdit?.id
      redirect(action: "updateGeneralDetails", params: [p: personToEdit.id, con: params?.con, op: oPTId, pt: personType?.id])
    } else {
      session.personIdToEdit = personToEdit?.id
      redirect(action: "updateGeneralDetails", params: [p: personToEdit.id, con: params?.con, op: oPTId])
    }
  }

  def updateGeneralDetails() {
    Person personToEdit = Person.get(session?.personIdToEdit)
    // if (params?.op) {
    def oPT = PersonType.findById(params?.op?.toLong())
    [personToEdit: personToEdit, oPT: oPT]
    //  }
  }

  @Transactional
  def saveGeneralDetails() {
    Person personToUpdate = Person.get(session?.personIdToEdit)

    personToUpdate.properties = params

    try {
      if (personToUpdate.validate() && personToUpdate?.save(flush: true)) {
        session.personIdToEdit = personToUpdate?.id
        response.status = 200
        flash.message = "Successfully updated user general details!"
        redirect(action: "updatePersonalDetails", params: [id: personToUpdate?.id, op: params?.op, con: params?.con])
      } else {
        flash.error = "Failed updating user general details!"
        redirect(action: "updateGeneralDetails", params: [id: params?.id, op: params?.op, con: params?.con])
      }
    } catch (ValidationException e) {
      flash.error = "Failed updating user general details!"
      redirect(action: "updateGeneralDetails", params: [id: params?.id, op: params?.op, con: params?.con])
    }
  }

  def updatePersonalDetails() {
    Person person = Person.get(session?.personIdToEdit)

    [personDetails: person?.personDetails, religions: religionService.getReligionsWithCaste()]
  }

  @Transactional
  def savePersonalDetails() {
    Person personToUpdate = Person.get(session?.personIdToEdit)

    if (!personToUpdate?.personDetails) {
      personToUpdate.personDetails = new PersonDetails()
      personToUpdate.personDetails.person = personToUpdate
    }

    personToUpdate.personDetails.properties = params

    personToUpdate.personDetails.dob = LocalDateTime.of(params?.year?.toInteger(), params?.month[0].toInteger(), params?.day?.toInteger(), 0, 0, 0)

    try {
      if (personToUpdate.validate() && personToUpdate?.save(flush: true)) {
        session.personIdToEdit = personToUpdate?.id
        response.status = 200
        flash.message = "Successfully updated personal details!"
        redirect(action: "updateSpouseDetails", params: [id: personToUpdate?.id, op: params?.op, con: params?.con])
      } else {
        flash.error = "Failed updating personal details!"
        redirect(action: "updatePersonalDetails", params: [id: params?.id, op: params?.op, con: params?.con])
      }
    } catch (ValidationException e) {
      flash.error = "Personal details update failed!"
      redirect(action: "updatePersonalDetails", params: [id: params?.id, op: params?.op, con: params?.con])
    }
  }

  def updateSpouseDetails() {
    Person personToEdit = Person.get(session?.personIdToEdit)

    [spouseDetails: personToEdit?.spouseDetails, personalDetails: personToEdit?.personDetails, religions: religionService.getReligionsWithCaste()]
  }

  @Transactional
  def saveSpouseDetails() {
    Person personToUpdate = Person.get(session?.personIdToEdit)
    if (!personToUpdate?.spouseDetails) {
      personToUpdate.spouseDetails = new SpouseDetails()
      personToUpdate.spouseDetails.person = personToUpdate
    }

    personToUpdate.spouseDetails.properties = params

    if (!params?.gender) {
      if (personToUpdate?.personDetails?.gender == "Male") {
        personToUpdate.spouseDetails.gender = "Female"
      } else {
        personToUpdate.spouseDetails.gender = "Male"
      }
    }

    try {
      if (personToUpdate.validate() && personToUpdate.save(flush: true)) {
        session.personIdToEdit = personToUpdate?.id
        response.status = 200
        flash.message = "Successfully updated spouse details!"
        redirect(action: "updateFamilyDetails", params: [id: personToUpdate?.id, op: params?.op, con: params?.con])
      } else {
        flash.error = "Failed updating spouse details!"
        redirect(action: "updateSpouseDetails", params: [id: params?.id, op: params?.op, con: params?.con])
      }
    } catch (ValidationException e) {
      flash.error = "Spouse details update failed!"
      redirect(action: "updateSpouseDetails", params: [id: params?.id, op: params?.op, con: params?.con])
    }
  }

  def updateFamilyDetails() {
    Person personToUpdate = Person.get(session?.personIdToEdit)
    [familyDetails: personToUpdate?.familyDetails, heights: heightService.getHeights()]
  }

  @Transactional
  def saveFamilyDetails() {
    Person personToUpdate = Person.get(session?.personIdToEdit)
    if (!personToUpdate?.familyDetails) {
      personToUpdate.familyDetails = new FamilyDetails()
      personToUpdate.familyDetails.person = personToUpdate
    }

    personToUpdate.familyDetails.properties = params

    try {
      if (personToUpdate.validate() && personToUpdate.save(flush: true)) {
        session.personIdToEdit = personToUpdate?.id
        response.status = 200
        flash.message = "Successfully updated family details!"
        redirect(action: "updateSpouseFamilyDetails", params: [id: personToUpdate?.id, op: params?.op, con: params?.con])
      } else {
        flash.error = "Failed updating family details!"
        redirect(action: "updateFamilyDetails", params: [id: params?.id, op: params?.op, con: params?.con])
      }
    } catch (ValidationException e) {
      flash.error = "Failed updating family details!"
      redirect(action: "updateFamilyDetails", params: [id: params?.id, op: params?.op, con: params?.con])
    }
  }

  def updateSpouseFamilyDetails() {
    Person personToUpdate = Person.get(session?.personIdToEdit)
    [spouseFamilyDetails: personToUpdate?.spouseFamilyDetails, familyDetails: personToUpdate?.familyDetails, heights: heightService.getHeights()]
  }

  @Transactional
  def saveSpouseFamilyDetails() {
    Person personToUpdate = Person.get(session?.personIdToEdit)
    if (!personToUpdate?.spouseFamilyDetails) {
      personToUpdate.spouseFamilyDetails = new SpouseFamilyDetails()
      personToUpdate.spouseFamilyDetails.person = personToUpdate
    }

    personToUpdate.spouseFamilyDetails.properties = params

    try {
      if (personToUpdate.validate() && personToUpdate.save(flush: true)) {
        session.personIdToEdit = personToUpdate?.id
        response.status = 200
        flash.message = "Successfully updated spouse family details!"
        redirect(action: "updateContactDetails", params: [id: personToUpdate?.id, op: params?.op, con: params?.con])
      } else {
        flash.error = "Failed updating spouse family details!"
        redirect(action: "updateSpouseFamilyDetails", params: [id: params?.id, op: params?.op, con: params?.con])
      }
    } catch (ValidationException e) {
      flash.error = "Failed updating spouse family details!"
      redirect(action: "updateSpouseFamilyDetails", params: [id: params?.id, op: params?.op, con: params?.con])
    }
  }

  def updateContactDetails() {
    Person personToUpdate = Person.get(session?.personIdToEdit)

    def countries = countryCodeService.getCountryCodes()
    [contacts: personToUpdate?.contactDetails, countries: countries]
  }

  @Transactional
  def saveContactDetails() {
    ContactDetails contactDetails = ContactDetails.get(params?.id)
    contactDetails.properties = params // Will bind street1, street2, etc.
    try {
      if (contactDetails.validate() && contactDetails.save(flush: true)) {
        response.status = 200
        flash.message = "Successfully updated contact details!"
        redirect(action: "updateContactDetails", params: [id: params?.id, op: params?.op, con: params?.con])
      } else {
        flash.error = "Failed updating contact details!"
        redirect(action: "updateContactDetails", params: [id: params?.id, op: params?.op, con: params?.con])
      }
    } catch (Exception e) { // Catch broader exceptions for unexpected errors
      flash.error = "An error occurred while saving contact details: ${e.message}"
      redirect(action: "updateContactDetails", params: [id: params?.id, op: params?.op, con: params?.con])
      log.error("Error saving contact details", e) // Log the error for debugging
    }
  }

  def addContactDetail() {
    def countries = countryCodeService.getCountryCodes()
    [countries: countries]
  }

  @Transactional
  def saveNewContactDetails() {
    Person personToUpdate = Person.get(session?.personIdToEdit)

    ContactDetails contactDetails = new ContactDetails(params)
    contactDetails.person = personToUpdate

    try {
      if (contactDetails.validate() && contactDetails.save(flush: true)) {
        response.status = 200
        flash.message = "Successfully added contact details!"
        redirect(action: "updateContactDetails", params: [id: params?.id, op: params?.op, con: params?.con])
      } else {
        flash.error = "Failed adding contact details!"
        redirect(action: "addContactDetail", params: [id: params?.id, op: params?.op, con: params?.con])
      }
    } catch (Exception e) { // Catch broader exceptions for unexpected errors
      flash.error = "An error occurred while saving contact details: ${e.message}"
      redirect(action: "addContactDetail", params: [id: params?.id, op: params?.op, con: params?.con])
      log.error("Error saving contact details", e) // Log the error for debugging
    }
  }

  def updateEducationDetails() {
    Person personToUpdate = Person.get(session?.personIdToEdit)

    [educations: personToUpdate?.educationDetails]
  }

  @Transactional
  def saveEducationDetails() {
    EducationDetails educationDetails = EducationDetails.get(params?.id)
    educationDetails.properties = params // Will bind street1, street2, etc.

    try {
      if (educationDetails.validate() && educationDetails.save(flush: true)) {
        response.status = 200
        flash.message = "Successfully updated education details!"
        redirect(action: "updateEducationDetails", params: [id: params?.id, op: params?.op, con: params?.con])
      } else {
        flash.error = "Failed updating education details!"
        redirect(action: "updateEducationDetails", params: [id: params?.id, op: params?.op, con: params?.con])
      }
    } catch (Exception e) { // Catch broader exceptions for unexpected errors
      flash.error = "An error occurred while saving education details: ${e.message}"
      redirect(action: "updateEducationDetails", params: [id: params?.id, op: params?.op, con: params?.con])
      log.error("Error saving education details", e) // Log the error for debugging
    }
  }

  def addEducationDetail() {}

  @Transactional
  def saveNewEducationDetails() {
    Person personToUpdate = Person.get(session?.personIdToEdit)

    EducationDetails educationDetails = new EducationDetails(params)
    educationDetails.person = personToUpdate

    try {
      if (educationDetails.validate() && educationDetails.save(flush: true)) {
        response.status = 200
        flash.message = "Successfully added education details!"
        redirect(action: "updateEducationDetails", params: [id: params?.id, op: params?.op, con: params?.con])
      } else {
        flash.error = "Failed adding education details!"
        redirect(action: "addEducationDetail", params: [id: params?.id, op: params?.op, con: params?.con])
      }
    } catch (Exception e) { // Catch broader exceptions for unexpected errors
      flash.error = "An error occurred while saving education details: ${e.message}"
      redirect(action: "addEducationDetail", params: [id: params?.id, op: params?.op, con: params?.con])
      log.error("Error saving education details", e) // Log the error for debugging
    }
  }

  def updateProfessionDetails() {
    Person personToUpdate = Person.get(session?.personIdToEdit)

    [professions: personToUpdate?.professionDetails, currencies: countryCodeService.getWorldCurrencies()]
  }

  @Transactional
  def saveProfessionDetails() {
    Person personToUpdate = Person.get(session?.personIdToEdit)

    ProfessionalDetails professionalDetails = ProfessionalDetails.get(params?.id)

    // Check for existing current job (user-specific)
    if (params.boolean('isCurrent')) {
      def existingCurrent = ProfessionalDetails.findByPersonAndIsCurrent(personToUpdate, true)
      if (existingCurrent && (existingCurrent?.id != professionalDetails?.id)) {
        flash.error = "You already have a current job!"
        flash.profession = existingCurrent
        redirect(action: "updateProfessionDetails", params: [id: params?.id, op: params?.op, con: params?.con])
        return
      }
    }


    professionalDetails.properties = params

    if (!params?.boolean('isCurrent')) {
      professionalDetails.isCurrent = false
    }

    try {
      if (professionalDetails.validate() && professionalDetails.save(flush: true)) {
        response.status = 200
        flash.message = "Successfully updated profession details!"
        redirect(action: "updateProfessionDetails", params: [id: params?.id, op: params?.op, con: params?.con])
      } else {
        flash.error = "Failed updating profession details!"
        redirect(action: "updateProfessionDetails", params: [id: params?.id, op: params?.op, con: params?.con])
      }
    } catch (Exception e) { // Catch broader exceptions for unexpected errors
      flash.error = "An error occurred while saving profession details: ${e.message}"
      redirect(action: "updateProfessionDetails", params: [id: params?.id, op: params?.op, con: params?.con])
      log.error("Error saving profession details", e) // Log the error for debugging
    }
  }

  def addProfessionDetail() {
    [currencies: countryCodeService.getWorldCurrencies()]
  }

  @Transactional
  def saveNewProfessionDetails() {
    Person personToUpdate = Person.get(session?.personIdToEdit)

    if (params.boolean('isCurrent')) {
      def existingCurrent = ProfessionalDetails.findByPersonAndIsCurrent(personToUpdate, true)
      if (existingCurrent) {
        flash.error = "You already have a current job!"
        flash.existingProfession = existingCurrent
        redirect(action: "updateProfessionDetails", params: [id: params?.id, op: params?.op, con: params?.con])
        return
      }
    }

    ProfessionalDetails professionalDetails = new ProfessionalDetails(params)
    professionalDetails.person = personToUpdate

    try {
      if (professionalDetails.validate() && professionalDetails.save(flush: true)) {
        response.status = 200
        flash.message = "Successfully added profession details!"
        redirect(action: "updateProfessionDetails", params: [id: params?.id, op: params?.op, con: params?.con])
      } else {
        flash.error = "Failed adding profession details!"
        redirect(action: "addProfessionDetail", params: [id: params?.id, op: params?.op, con: params?.con])
      }
    } catch (Exception e) { // Catch broader exceptions for unexpected errors
      flash.error = "An error occurred while saving profession details: ${e.message}"
      redirect(action: "addProfessionDetail", params: [id: params?.id, op: params?.op, con: params?.con])
      log.error("Error saving profession details", e) // Log the error for debugging
    }
  }

  def updateAddressDetails() {
    Person personToUpdate = Person.get(session?.personIdToEdit)
    [addresses: personToUpdate?.address]
  }

  @Transactional
  def saveAddressDetails() {
    Address address = Address.get(params?.id)
    address.properties = params // Will bind street1, street2, etc.

    try {
      if (address.validate() && address.save(flush: true)) {
        response.status = 200
        flash.message = "Successfully updated address details!"
        redirect(action: "updateAddressDetails", params: [id: params?.id, op: params?.op, con: params?.con])
      } else {
        flash.error = "Failed updating address details!"
        redirect(action: "updateAddressDetails", params: [id: params?.id, op: params?.op, con: params?.con])
      }
    } catch (Exception e) { // Catch broader exceptions for unexpected errors
      flash.error = "An error occurred while saving address details: ${e.message}"
      redirect(action: "updateAddressDetails", params: [id: params?.id, op: params?.op, con: params?.con])
      log.error("Error saving address details", e) // Log the error for debugging
    }
  }

  def addAddress() {}

  @Transactional
  def saveNewAddress() {
    Person personToUpdate = Person.get(session?.personIdToEdit)
    Address address = new Address(params)
    address.person = personToUpdate

    try {
      if (address.validate() && address.save(flush: true)) {
        response.status = 200
        flash.message = "Successfully added address details!"
        redirect(action: "updateAddressDetails", params: [id: params?.id, op: params?.op, con: params?.con])
      } else {
        flash.error = "Failed adding address details!"
        redirect(action: "addAddress", params: [id: params?.id, op: params?.op, con: params?.con])
      }
    } catch (Exception e) { // Catch broader exceptions for unexpected errors
      flash.error = "An error occurred while saving address details: ${e.message}"
      redirect(action: "addAddress", params: [id: params?.id, op: params?.op, con: params?.con])
      log.error("Error saving address details", e) // Log the error for debugging
    }
  }

  def listUsersForPolicies() {
    params.offset = params?.offset ? params.int('offset') : 0
    params.max = params?.max ? params.int('max') : 10

    def persons = Person.withCriteria {
      // Filter by personTypeName
      personType {
        eq('personTypeName', 'User')
      }

      firstResult(params.offset)
      maxResults(params.max)

      resultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
    }

    [persons: persons]
  }

  def updatePolicies() {
    Person personToUpdate = Person.get(params?.id)
    [policies: personToUpdate?.policies, id: params?.id]
  }

  @Transactional
  def savePolicies() {
    Person personToUpdate = Person.get(params?.id)

    if (!personToUpdate?.policies) {
      personToUpdate.policies = new Policy()
      personToUpdate.policies.person = personToUpdate
    }

    personToUpdate.policies.isActive = params.boolean('isActive')
    personToUpdate.policies.isBlocked = params.boolean('isBlocked')
    personToUpdate.policies.isOnHold = params.boolean('isOnHold')

    try {
      if (personToUpdate.validate() && personToUpdate.save(flush: true)) {
        response.status = 200
        flash.message = "Successfully updated user policies!"
        redirect(action: "index")
      } else {
        flash.error = "Failed updating user policies!"
        redirect(action: "updatePolicies", params: [id: params?.id, op: params?.op, con: params?.con])
      }
    } catch (ValidationException e) {
      flash.error = "Updating user policies failed!"
      redirect(action: "updatePolicies", params: [id: params?.id, op: params?.op, con: params?.con])
    }
  }

  @Transactional
  def delete() {
    def personToDelete = Person.get(params?.id?.toLong())
    if (personToDelete) {
      personToDelete.delete(flush: true)
      flash.message = "User deleted successfully!"
    } else {
      flash.error = "User not found!!"
    }
    redirect(action: "index")
  }

  def listTickets() {
//    params.offset = params?.offset ? params.int('offset') : 0
//    params.max = params?.max ? params.int('max') : 10
//
//    def tickets = Ticket.withCriteria {
//      // Filter by personTypeName
//      personType {
//        eq('personTypeName', 'User')
//      }
//
//      // Use fetchMode to avoid fetching the persons association
//      fetchMode('persons', FetchMode.SELECT)
//
//      firstResult(params.offset)
//      maxResults(params.max)
//    }
//
//    [tickets: tickets]
  }

  @Secured(['IS_AUTHENTICATED_FULLY'])
  def profile() {
    Person currentUser = springSecurityService.currentUser as Person
    [user: currentUser]
  }

  def edit1() {
    Person currentUser = springSecurityService.currentUser as Person
    [user: currentUser]
  }

  def update() {
    Person currentUser = springSecurityService.currentUser as Person

    currentUser.firstName = params.firstName
    currentUser.lastName = params.lastName
    currentUser.mobileNumber = params.mobileNumber

    if (currentUser.save(flush: true)) {
      flash.message = "Profile updated successfully"
    } else {
      flash.error = "Failed to update profile"
    }

    redirect action: 'profile'
  }

  def changePassword() {
    if (request.method == 'POST') {
      Person currentUser = springSecurityService.currentUser as Person
      String oldPassword = params.oldPassword
      String newPassword = params.newPassword
      String confirmPassword = params.confirmPassword

      if (newPassword != confirmPassword) {
        flash.error = "New passwords do not match"
        render view: 'changePassword'
        return
      }

      if (securityService.changePassword(currentUser, oldPassword, newPassword)) {
        flash.message = "Password changed successfully"
        redirect action: 'profile'
      } else {
        flash.error = "Current password is incorrect"
        render view: 'changePassword'
      }
    }
  }

}
