package com.murad.app.matrimony

import grails.gorm.transactions.Transactional
import grails.validation.ValidationException

import java.time.LocalDateTime


class PersonEditDBoardController {
  def countryCodeService
  def religionService
  def heightService

  @Transactional
  def index() {
    def oldPT = PersonType.findById(params?.op)
    Person person = Person.get(session?.personIdToEdit)
    //def personType = PersonType.findById(person?.personTypeId)

    [oPT: oldPT, personType: person?.personType, personToEdit: person]
  }

  @Transactional
  def edit() {
    def personToEdit = Person.findById(params?.id?.toLong())
    def oPTId = personToEdit?.personType?.id

    session.personIdToEdit = personToEdit?.id
    redirect(action: "index", params: [p: personToEdit?.id, con: params?.con, op: oPTId])
  }

  def editGeneralDetails() {
    Person personToEdit = Person.get(session?.personIdToEdit)
    [personToEdit: personToEdit]
  }

  @Transactional
  def editAndSaveGeneralDetails() {
    Person personToUpdate = Person.get(session?.personIdToEdit)

    personToUpdate.firstName = params?.firstName
    personToUpdate.lastName = params?.lastName

    try {
      if (personToUpdate.validate() && personToUpdate?.save(flush: true)) {
        session.personIdToEdit = personToUpdate?.id
        response.status = 200
        flash.message = "Successfully updated user general details!"
        redirect(action: "index", params: [p: params?.p, con: params?.con, op: params?.op])

      } else {
        flash.error = "Failed updating user general details!"
        redirect(action: "editGeneralDetails", params: [p: params?.p, con: params?.con, op: params?.op])
      }
    } catch (ValidationException e) {
      flash.error = "Failed updating user general details!"
      redirect(action: "editGeneralDetails", params: [p: params?.p, con: params?.con, op: params?.op])
    }
  }

  def editPersonalDetails() {
    Person personToEdit = Person.get(session?.personIdToEdit)
    [personDetails: personToEdit?.personDetails, religions: religionService.getReligionsWithCaste()]
  }

  @Transactional
  def editAndSavePersonalDetails() {
    Person personToUpdate = Person.get(session?.personIdToEdit)

    if (!personToUpdate?.personDetails) {
      personToUpdate.personDetails = new PersonDetails()
      personToUpdate.personDetails.person = personToUpdate
    }

    personToUpdate.personDetails.properties = params

    personToUpdate.personDetails.dob = LocalDateTime.of(params?.year?.toInteger(), params?.month.toInteger(), params?.day?.toInteger(), 0, 0, 0)

    try {
      if (personToUpdate.validate() && personToUpdate.save(flush: true)) {
        session.personIdToEdit = personToUpdate?.id
        response.status = 200
        flash.message = "Successfully updated personal details!"
        redirect(action: "index", params: [p: params?.p, con: params?.con, op: params?.op])
      } else {
        flash.error = "Failed updating personal details!"
        redirect(action: "editPersonalDetails", params: [p: params?.p, con: params?.con, op: params?.op])
      }
    } catch (ValidationException e) {
      flash.error = "Failed updating personal details!"
      redirect(action: "editPersonalDetails", params: [p: params?.p, con: params?.con, op: params?.op])
    }
  }

  def editSpouseDetails() {
    Person personToEdit = Person.get(session?.personIdToEdit)
    [spouseDetails: personToEdit?.spouseDetails, personDetails: personToEdit?.personDetails, religions: religionService.getReligionsWithCaste()]
  }

  @Transactional
  def editAndSaveSpouseDetails() {
    Person personToUpdate = Person.get(session?.personIdToEdit)

    if (!personToUpdate?.spouseDetails) {
      personToUpdate.spouseDetails = new SpouseDetails()
    }
    personToUpdate.spouseDetails.properties = params

    try {
      if (personToUpdate.validate() && personToUpdate.save(flush: true)) {
        session.personIdToEdit = personToUpdate?.id
        response.status = 200
        flash.message = "Successfully updated spouse details!"
        redirect(action: "index", params: [p: params?.p, con: params?.con, op: params?.op])
      } else {
        flash.error = "Failed updating spouse details!"
        redirect(action: "editSpouseDetails", params: [p: params?.p, con: params?.con, op: params?.op])
      }
    } catch (ValidationException e) {
      flash.error = "Failed updating spouse details!"
      redirect(action: "editSpouseDetails", params: [p: params?.p, con: params?.con, op: params?.op])
    }
  }

  def editFamilyDetails() {
    Person personToEdit = Person.get(session?.personIdToEdit)
    [familyDetails: personToEdit?.familyDetails]
  }

  @Transactional
  def editAndSaveFamilyDetails() {
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
        redirect(action: "index", params: [p: personToUpdate?.id, op: params?.op, con: params?.con])
      } else {
        flash.error = "Failed updating family details!"
        redirect(action: "editFamilyDetails", params: [p: params?.p, op: params?.op, con: params?.con])
      }
    } catch (ValidationException e) {
      flash.error = "Failed updating family details!"
      redirect(action: "editFamilyDetails", params: [p: params?.p, op: params?.op, con: params?.con])
    }
  }

  def editSpouseFamilyPreferences() {
    Person personToEdit = Person.get(session?.personIdToEdit)
    [spouseFamilyDetails: personToEdit?.spouseFamilyDetails, familyDetails: personToEdit?.familyDetails, heights: heightService.getHeights()]
  }

  @Transactional
  def editAndSaveSpouseFamilyPreferences() {
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
        flash.message = "Successfully updated spouse family preferences!"
        redirect(action: "index", params: [p: personToUpdate?.id, op: params?.op, con: params?.con])
      } else {
        flash.error = "Failed updating spouse family preferences!"
        redirect(action: "editSpouseFamilyPreferences", params: [id: params?.id, op: params?.op, con: params?.con])
      }
    } catch (ValidationException e) {
      flash.error = "Failed updating spouse family preferences!"
      redirect(action: "editSpouseFamilyPreferences", params: [id: params?.id, op: params?.op, con: params?.con])
    }
  }

  def editContactDetails() {
    Person personToEdit = Person.get(session?.personIdToEdit)
    def countries = countryCodeService.getCountryCodes()
    [contacts: personToEdit?.contactDetails, countries: countries]
  }

  @Transactional
  def editAndSaveContactDetails() {
    ContactDetails contactDetails = ContactDetails.get(params?.id)
    contactDetails.properties = params

    if (!params?.isPrimary) {
      contactDetails.isPrimary = false
    }

    try {
      if (contactDetails.validate() && contactDetails.save(flush: true)) {
        response.status = 200
        flash.message = "Successfully updated contact details!"
        redirect(action: "index", params: [p: params?.p, op: params?.op, con: params?.con])
      } else {
        flash.error = "Failed updating contact details!"
        redirect(action: "editContactDetails", params: [p: params?.p, op: params?.op, con: params?.con])
      }
    } catch (Exception e) { // Catch broader exceptions for unexpected errors
      flash.error = "An error occurred while saving contact details: ${e.message}"
      redirect(action: "editContactDetails", params: [p: params?.p, op: params?.op, con: params?.con])
      log.error("Error saving contact details", e) // Log the error for debugging
    }
  }

  def addContactDetail() {
    [countries: countryCodeService.getCountryCodes()]
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
        redirect(action: "editContactDetails", params: [p: params?.p, op: params?.op, con: params?.con])
      } else {
        flash.error = "Failed adding contact details!"
        redirect(action: "addContactDetail", params: [p: params?.p, op: params?.op, con: params?.con])
      }
    } catch (Exception e) { // Catch broader exceptions for unexpected errors
      flash.error = "An error occurred while saving contact details: ${e.message}"
      redirect(action: "addContactDetail", params: [p: params?.p, op: params?.op, con: params?.con])
      log.error("Error saving contact details", e) // Log the error for debugging
    }
  }

  def editEducationDetails() {
    Person personToEdit = Person.get(session?.personIdToEdit)
    [educations: personToEdit?.educationDetails]
  }

  @Transactional
  def editAndSaveEducationDetails() {
    EducationDetails educationDetails = EducationDetails.get(params?.id)
    educationDetails.properties = params

    if (!params?.isHighest) {
      educationDetails.isHighest = false
    }

    try {
      if (educationDetails.validate() && educationDetails.save(flush: true)) {
        response.status = 200
        flash.message = "Successfully updated education details!"
        redirect(action: "index", params: [p: params?.p, op: params?.op, con: params?.con])
      } else {
        flash.error = "Failed updating education details!"
        redirect(action: "editEducationDetails", params: [p: params?.p, op: params?.op, con: params?.con])
      }
    } catch (Exception e) { // Catch broader exceptions for unexpected errors
      flash.error = "An error occurred while saving education details: ${e.message}"
      redirect(action: "editEducationDetails", params: [p: params?.p, op: params?.op, con: params?.con])
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
        redirect(action: "editEducationDetails", params: [p: params?.p, op: params?.op, con: params?.con])
      } else {
        flash.error = "Failed adding education details!"
        redirect(action: "addEducationDetail", params: [p: params?.p, op: params?.op, con: params?.con])
      }
    } catch (Exception e) {
      flash.error = "An error occurred while saving education details: ${e.message}"
      redirect(action: "addEducationDetail", params: [p: params?.p, op: params?.op, con: params?.con])
      log.error("Error saving education details", e) // Log the error for debugging
    }
  }

  def editProfessionDetails() {
    Person personToEdit = Person.get(session?.personIdToEdit)
    [professions: personToEdit?.professionDetails, currencies: countryCodeService.getWorldCurrencies()]
  }

  @Transactional
  def editAndSaveProfessionDetails() {
    Person personToUpdate = Person.get(session?.personIdToEdit)

    ProfessionalDetails professionalDetails = ProfessionalDetails.get(params?.id)

    // Check for existing current job (user-specific)
    if (params.boolean('isCurrent')) {
      def existingCurrent = ProfessionalDetails.findByPersonAndIsCurrent(personToUpdate, true)
      if (existingCurrent && (existingCurrent?.id != professionalDetails?.id)) {
        flash.error = "You already have a current job!"
        flash.profession = existingCurrent
        redirect(action: "editProfessionDetails", params: [p: params?.p, op: params?.op, con: params?.con])
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
        redirect(action: "index", params: [p: params?.p, op: params?.op, con: params?.con])
      } else {
        flash.error = "Failed updating profession details!"
        redirect(action: "editProfessionDetails", params: [p: params?.p, op: params?.op, con: params?.con])
      }
    } catch (Exception e) { // Catch broader exceptions for unexpected errors
      flash.error = "An error occurred while saving profession details: ${e.message}"
      redirect(action: "editProfessionDetails", params: [p: params?.p, op: params?.op, con: params?.con])
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
        flash.profession = existingCurrent
        redirect(action: "addProfessionDetail", params: [p: params?.p, op: params?.op, con: params?.con])
        return
      }
    }

    ProfessionalDetails professionalDetails = new ProfessionalDetails(params)
    professionalDetails.person = personToUpdate

    try {
      if (professionalDetails.validate() && professionalDetails.save(flush: true)) {
        response.status = 200
        flash.message = "Successfully added profession details!"
        redirect(action: "editProfessionDetails", params: [p: params?.p, op: params?.op, con: params?.con])
      } else {
        flash.error = "Failed adding profession details!"
        redirect(action: "addProfessionDetail", params: [p: params?.p, op: params?.op, con: params?.con])
      }
    } catch (Exception e) { // Catch broader exceptions for unexpected errors
      flash.error = "An error occurred while saving profession details: ${e.message}"
      redirect(action: "addProfessionDetail", params: [p: params?.p, op: params?.op, con: params?.con])
      log.error("Error saving profession details", e) // Log the error for debugging
    }
  }

  def editAddressDetails() {
    Person personToEdit = Person.get(session?.personIdToEdit)
    [addresses: personToEdit?.address]
  }

  @Transactional
  def editAndSaveAddressDetails() {
    Person personToUpdate = Person.get(session?.personIdToEdit)

    def address = new Address(params)

    personToUpdate.addToAddress(address)

    try {
      if (personToUpdate.validate() && personToUpdate.save(flush: true)) {
        session.personIdToEdit = personToUpdate?.id
        response.status = 200
        flash.message = "Successfully updated address details!"
        redirect(action: "index", params: [p: params?.p, con: params?.con, op: params?.op])
      } else {
        flash.error = "Failed updating address details!"
        redirect(action: "editAddressDetails", params: [p: params?.p, con: params?.con, op: params?.op])
      }
    } catch (ValidationException e) {
      flash.error = "Failed updating address details!"
      redirect(action: "editAddressDetails", params: [p: params?.p, con: params?.con, op: params?.op])
    }
  }

  def addAddress() {}

  @Transactional
  def saveAddress() {
    try {
      Address addressDetails = Address.findByStreet1AndStreet2AndCityAndDistrictAndStateAndCountryAndPostalCode(params?.street1, params?.street2, params?.city, params?.district, params?.state, params?.country, params?.postalCode)
      if (addressDetails) {
        flash.error = "Address already exists!"
        flash.address = addressDetails
        redirect(action: "addAddress")
        return
      } else if (params?.addressType == "Other") {
        addressDetails = Address.findByOtherTypeAndPerson(params?.otherType, request.person)

        if (addressDetails) {
          flash.error = "Address of type ${params?.otherType} already exists!"
          flash.address = addressDetails
          redirect(action: "addAddress")
          return
        }

      } else {
        addressDetails = Address.findByAddressTypeAndPerson(params?.addressType, request.person)
        if (addressDetails) {
          flash.error = "Address of type ${params?.addressType} already exists!"
          flash.address = addressDetails
          redirect(action: "addAddress")
          return
        }
      }
      Address address = new Address(params)
      address.person = Person.get(session?.personIdToEdit)


      if (address.validate() && address.save(flush: true)) {
        flash.message = "Successfully added address to user!"
        redirect(action: 'editAddressDetails', params: [p: params?.p, con: params?.con, op: params?.op])
      } else {
        flash.error = "Failed adding address to user!"
        redirect(action: "addAddress", params: [p: params?.p, con: params?.con, op: params?.op])
      }

    } catch (ValidationException e) { // Catch broader exceptions for unexpected errors
      flash.error = "An error occurred while saving address details: ${e.message}"
      redirect(action: "addAddress", params: [p: params?.p, con: params?.con, op: params?.op])
      log.error("Error saving address details", e) // Log the error for debugging
    }
  }

//  def editPolicies() {
//    Person personToEdit = Person.get(session?.personIdToEdit)
//    [policies: personToEdit?.policies]
//  }
//
//  @Transactional
//  def editAndSavePolicies() {
//    Person personToUpdate = Person.get(session?.personIdToEdit)
//
//    if (!personToUpdate?.policies) {
//      personToUpdate.policies = new Policy()
//    }
//
//    personToUpdate.policies.isActive = params?.isActive
//    personToUpdate.policies.isBlocked = params?.isBlocked
//    personToUpdate.policies.isOnHold = params?.isOnHold
//
//    Person updatedPerson = personToUpdate.save(flush: true)
//
//    if (updatedPerson) {
//      session.personIdToEdit = updatedPerson?.id
//      response.status = 200
//      flash.message = "Successfully updated user policies!"
//      redirect(action: "index", params: [p: params?.p, con: params?.con, op: params?.op])
//    } else {
//      flash.error = "Failed updating user policies!"
//      redirect(action: "editPolicies", params: [p: params?.p, con: params?.con, op: params?.op])
//    }
//  }
}
