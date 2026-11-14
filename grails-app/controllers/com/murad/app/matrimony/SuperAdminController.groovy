package com.murad.app.matrimony

import grails.gorm.transactions.Transactional
import grails.validation.ValidationException

import java.time.LocalDateTime


class SuperAdminController {

  def edit() {
    def personToEdit = Person.findById(params?.p?.toLong())
    def oPTId = personToEdit?.personType?.id

    if (oPTId != params?.pt?.toLong()) {
      def personType = PersonType.findById(params?.pt?.toLong())

      personToEdit.personType = personType
      session.SAPersonIdToEdit = personToEdit?.id
      redirect(action: "updateGeneralDetails", params: [p: personToEdit.id, con: params?.con, opt: oPTId, pt: personType?.id])
    } else {
      session.SAPersonIdToEdit = personToEdit?.id
      redirect(action: "updateGeneralDetails", params: [p: personToEdit.id, con: params?.con, opt: oPTId])
    }
  }

  def updateGeneralDetails() {
    if (params?.opt) {
      def oPT = PersonType.findById(params?.opt?.toLong())
      [oPT: oPT]
    }
  }

  @Transactional
  def saveGeneralDetails() {
    Person personToUpdate = request.person

    personToUpdate.firstName = params?.firstName
    personToUpdate.lastName = params?.lastName

    Person updatedPerson = personToUpdate?.save(flush: true)

    if (updatedPerson) {
      session.SAPersonIdToEdit = updatedPerson?.id
      response.status = 200
      flash.message = "Successfully updated user general details!"
      redirect(action: "updatePersonalDetails")
    } else {
      flash.error = "Failed updating user general details!"
      redirect(action: "updateGeneralDetails")
    }
  }

  def updatePersonalDetails() {}

  @Transactional
  def savePersonalDetails() {
    Person personToUpdate = request.person

    if (!personToUpdate?.personDetails) {
      personToUpdate.personDetails = new PersonDetails()
    }

    personToUpdate.personDetails.properties = params
    personToUpdate.personDetails.dob = LocalDateTime.of(params?.year?.toInteger(), params?.month[0].toInteger(), params?.day?.toInteger(), 0, 0, 0)

//    personToUpdate.personDetails.religion = params?.Religion
//    personToUpdate.personDetails.caste = params?.Caste
//
//    if (params?.NonSmoker == "on") {
//      personToUpdate.personDetails.smokingHabit = 0
//    } else if (params?.SocialSmoker == "on") {
//      personToUpdate.personDetails.smokingHabit = 1
//    } else if (params?.NormalSmoker == "on") {
//      personToUpdate.personDetails.smokingHabit = 2
//    } else if (params?.ChainSmoker == "on") {
//      personToUpdate.personDetails.smokingHabit = 3
//    }
//
//    if (params?.NonDrinker == "on") {
//      personToUpdate.personDetails.drinkingHabit = 0
//    } else if (params?.SocialDrinker == "on") {
//      personToUpdate.personDetails.drinkingHabit = 1
//    } else if (params?.NormalDrinker == "on") {
//      personToUpdate.personDetails.drinkingHabit = 2
//    } else if (params?.HeavyDrinker == "on") {
//      personToUpdate.personDetails.drinkingHabit = 3
//    }

    try {
      if (personToUpdate.validate() && personToUpdate.save(flush: true)) {
        session.SAPersonIdToEdit = personToUpdate?.id
        response.status = 200
        flash.message = "Successfully updated personal details!"
        redirect(action: "updateSpouseDetails")
      } else {
        flash.error = "Failed updating personal details!"
        redirect(action: "updatePersonalDetails")
      }
    } catch (ValidationException e) {
      flash.error = "Failed updating personal details!"
      redirect(action: "updatePersonalDetails")
    }
  }

  def updateSpouseDetails() {}

  @Transactional
  def saveSpouseDetails() {
    Person personToUpdate = request.person

    if (!personToUpdate?.spouseDetails) {
      personToUpdate.spouseDetails = new SpouseDetails()
    }

    personToUpdate.spouseDetails.properties = params
    personToUpdate.spouseDetails.minAge = params?.StartingAge?.toInteger()
    personToUpdate.spouseDetails.maxAge = params?.EndingAge?.toInteger()

//    personToUpdate.spouseDetails.religion = params?.Religion
//    personToUpdate.spouseDetails.caste = params?.Caste

//    if (params?.NonSmoker == "on") {
//      personToUpdate.spouseDetails.smokingHabit = 0
//    } else if (params?.SocialSmoker == "on") {
//      personToUpdate.spouseDetails.smokingHabit = 1
//    } else if (params?.NormalSmoker == "on") {
//      personToUpdate.spouseDetails.smokingHabit = 2
//    } else if (params?.ChainSmoker == "on") {
//      personToUpdate.spouseDetails.smokingHabit = 3
//    }
//
//    if (params?.NonDrinker == "on") {
//      personToUpdate.spouseDetails.drinkingHabit = 0
//    } else if (params?.SocialDrinker == "on") {
//      personToUpdate.spouseDetails.drinkingHabit = 1
//    } else if (params?.NormalDrinker == "on") {
//      personToUpdate.spouseDetails.drinkingHabit = 2
//    } else if (params?.HeavyDrinker == "on") {
//      personToUpdate.spouseDetails.drinkingHabit = 3
//    }

    try {
      if (personToUpdate.validate() && personToUpdate.save(flush: true)) {
        response.status = 200
        flash.message = "Successfully updated spouse details!"
        redirect(action: "updateAddressDetails")
      } else {
        flash.error = "Failed updating spouse details!"
        redirect(action: "updateSpouseDetails")
      }
    } catch (ValidationException e) {
      flash.error = "Failed updating spouse details!"
      redirect(action: "updateSpouseDetails")
    }
  }

  def updateAddressDetails() {}

  @Transactional
  def saveAddressDetails() {
    Person personToUpdate = request.person

    if (!personToUpdate?.address) {
      personToUpdate.address = new Address()
    }

    personToUpdate.address.properties = params
//    personToUpdate.address.street1 = params?.street1
//    personToUpdate.address.street2 = params?.street2
//    personToUpdate.address.city = params?.city
//    personToUpdate.address.district = params?.district
//    personToUpdate.address.state = params?.state
//    personToUpdate.address.country = params?.country
//    personToUpdate.address.postalCode = params?.postalCode

    try {
      if (personToUpdate.validate() && personToUpdate.save(flush: true)) {
        session.SAPersonIdToEdit = personToUpdate?.id
        response.status = 200
        flash.message = "Successfully updated address details!"
        redirect(action: "updatePolicies")
      } else {
        flash.error = "Failed updating address details!"
        redirect(action: "updateAddressDetails")
      }
    } catch (ValidationException e) {
      flash.error = "Failed updating address details!"
      redirect(action: "updateAddressDetails")
    }
  }

  def updatePolicies() {}

  @Transactional
  def savePolicies() {
    Person personToUpdate = request.person

    if (!personToUpdate?.policies) {
      personToUpdate.policies = new Policy()
    }

    personToUpdate.policies.isActive = params?.isActive
    personToUpdate.policies.isBlocked = params?.isBlocked
    personToUpdate.policies.isOnHold = params?.isOnHold

    try {
      if (personToUpdate.validate() && personToUpdate.save(flush: true)) {
        session.removeAttribute("SAPersonIdToEdit")
        response.status = 200
        flash.message = "Successfully updated user policies!"
        redirect(controller: "AdminDBoard", action: "listPerson")
      } else {
        flash.error = "Failed updating user policies!"
        redirect(action: "updatePolicies", params: [edit: params?.edit])
      }
    } catch (ValidationException e) {
      flash.error = "Failed updating user policies!"
      redirect(action: "updatePolicies", params: [edit: params?.edit])
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
    redirect(controller: "AdminDBoard", action: "index")

  }
}

