package com.murad.app.matrimony

import grails.gorm.transactions.Transactional
import grails.validation.ValidationException

import java.time.LocalDateTime


class EditDBoardController {
  @Transactional
  def index() {
    def oldPT = PersonType.findById(params?.op)
    Person personToEdit = Person.get(session?.personIdToEdit)
    def personType = PersonType.findById(personToEdit?.personTypeId)

    [oPT: oldPT, personType: personType]
  }

  @Transactional
  def edit() {
    def personToEdit = null

    if (params?.id) {
      personToEdit = Person.findById(params?.id?.toLong())
    } else if (params?.p) {
      personToEdit = Person.findById(params?.p?.toLong())
    }

    def oPTId = personToEdit?.personType?.id

    if (params?.pt && (oPTId != params?.pt?.toLong())) {
      def personType = PersonType.findById(params?.pt?.toLong())

      personToEdit.personType = personType
      session.personIdToEdit = personToEdit?.id
      redirect(action: "index", params: [p: personToEdit.id, con: params?.con, op: oPTId, pt: personType?.id])
    } else {
      session.personIdToEdit = personToEdit?.id
      redirect(action: "index", params: [p: personToEdit.id, con: params?.con, op: oPTId])
    }
  }

  def editGeneralDetails() {}

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
        if (!params?.pt) {
          redirect(action: "index", params: [p: params?.p, con: params?.con, op: params?.op])
        } else {
          redirect(action: "index", params: [p: params?.p, con: params?.con, op: params?.op, pt: params?.pt])
        }
      } else {
        flash.error = "Failed updating user general details!"
        if (!params?.pt) {
          redirect(action: "editGeneralDetails", params: [p: params?.p, con: params?.con, op: params?.op])
        } else {
          redirect(action: "editGeneralDetails", params: [p: params?.p, con: params?.con, op: params?.op, pt: params?.pt])
        }
      }
    } catch (ValidationException e) {
      flash.error = "Failed updating user general details!"
      if (!params?.pt) {
        redirect(action: "editGeneralDetails", params: [p: params?.p, con: params?.con, op: params?.op])
      } else {
        redirect(action: "editGeneralDetails", params: [p: params?.p, con: params?.con, op: params?.op, pt: params?.pt])
      }
    }
  }

  def editPersonalDetails() {}

  @Transactional
  def editAndSavePersonalDetails() {
    Person personToUpdate = Person.get(session?.personIdToEdit)

    if (!personToUpdate?.personDetails) {
      personToUpdate.personDetails = new PersonDetails()
    }

    Map<String, Integer> monthMap = new HashMap<>();

    // Populate the map with months and their corresponding numbers
    monthMap.put("JANUARY", 1);
    monthMap.put("FEBRUARY", 2);
    monthMap.put("MARCH", 3);
    monthMap.put("APRIL", 4);
    monthMap.put("MAY", 5);
    monthMap.put("JUNE", 6);
    monthMap.put("JULY", 7);
    monthMap.put("AUGUST", 8);
    monthMap.put("SEPTEMBER", 9);
    monthMap.put("OCTOBER", 10);
    monthMap.put("NOVEMBER", 11);
    monthMap.put("DECEMBER", 12);

    personToUpdate.personDetails.properties = params
    personToUpdate.personDetails.dob = LocalDateTime.of(params?.year?.toInteger(), monthMap.get(params?.month[0]), params?.day?.toInteger(), 0, 0, 0)

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
        session.personIdToEdit = personToUpdate?.id
        response.status = 200
        flash.message = "Successfully updated personal details!"
        if (!params?.pt) {
          redirect(action: "index", params: [p: params?.p, con: params?.con, op: params?.op])
        } else {
          redirect(action: "index", params: [p: params?.p, con: params?.con, op: params?.op, pt: params?.pt])
        }
      } else {
        flash.error = "Failed updating personal details!"
        if (!params?.pt) {
          redirect(action: "editPersonalDetails", params: [p: params?.p, con: params?.con, op: params?.op])
        } else {
          redirect(action: "editPersonalDetails", params: [p: params?.p, con: params?.con, op: params?.op, pt: params?.pt])
        }
      }
    } catch (ValidationException e) {
      flash.error = "Failed updating personal details!"
      if (!params?.pt) {
        redirect(action: "editPersonalDetails", params: [p: params?.p, con: params?.con, op: params?.op])
      } else {
        redirect(action: "editPersonalDetails", params: [p: params?.p, con: params?.con, op: params?.op, pt: params?.pt])
      }
    }
  }

  def editSpouseDetails() {
    [spouseDetails: request.person?.spouseDetails]
  }

  @Transactional
  def editAndSaveSpouseDetails() {
    Person personToUpdate = Person.get(session?.personIdToEdit)

    if (!personToUpdate?.spouseDetails) {
      personToUpdate.spouseDetails = new SpouseDetails()
    }

    personToUpdate?.spouseDetails.properties = params
//    personToUpdate.spouseDetails.minAge = params?.StartingAge?.toInteger()
//    personToUpdate.spouseDetails.maxAge = params?.EndingAge?.toInteger()

//    personToUpdate.spouseDetails.religion = params?.Religion
//    personToUpdate.spouseDetails.caste = params?.Caste
//
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
        if (!params?.pt) {
          redirect(action: "index", params: [p: params?.p, con: params?.con, op: params?.op])
        } else {
          redirect(action: "index", params: [p: params?.p, con: params?.con, op: params?.op, pt: params?.pt])
        }
      } else {
        flash.error = "Failed updating spouse details!"
        if (!params?.pt) {
          redirect(action: "editSpouseDetails", params: [p: params?.p, con: params?.con, op: params?.op])
        } else {
          redirect(action: "editSpouseDetails", params: [p: params?.p, con: params?.con, op: params?.op, pt: params?.pt])
        }
      }
    } catch (ValidationException e) {
      flash.error = "Failed updating spouse details!"
      if (!params?.pt) {
        redirect(action: "editSpouseDetails", params: [p: params?.p, con: params?.con, op: params?.op])
      } else {
        redirect(action: "editSpouseDetails", params: [p: params?.p, con: params?.con, op: params?.op, pt: params?.pt])
      }
    }

  }

  def editAddressDetails() {}

  @Transactional
  def editAndSaveAddressDetails() {
    Person personToUpdate = Person.get(session?.personIdToEdit)

    if (!personToUpdate?.address) {
      personToUpdate.address = new Address()
    }

    personToUpdate?.address?.properties = params
//    personToUpdate.address.street1 = params?.street1
//    personToUpdate.address.street2 = params?.street2
//    personToUpdate.address.city = params?.city
//    personToUpdate.address.district = params?.district
//    personToUpdate.address.state = params?.state
//    personToUpdate.address.country = params?.country
//    personToUpdate.address.postalCode = params?.postalCode

    try {
      if (personToUpdate.validate()) {
        Person updatedPerson = personToUpdate.save(flush: true)

        if (updatedPerson) {
          session.personIdToEdit = updatedPerson?.id
          response.status = 200
          flash.message = "Successfully updated address details!"
          if (!params?.pt) {
            redirect(action: "index", params: [p: params?.p, con: params?.con, op: params?.op])
          } else {
            redirect(action: "index", params: [p: params?.p, con: params?.con, op: params?.op, pt: params?.pt])
          }
        } else {
          flash.error = "Failed updating address details!"
          if (!params?.pt) {
            redirect(action: "editAddressDetails", params: [p: params?.p, con: params?.con, op: params?.op])
          } else {
            redirect(action: "editAddressDetails", params: [p: params?.p, con: params?.con, op: params?.op, pt: params?.pt])
          }
        }
      }
    } catch (ValidationException e) {
      flash.error = "Failed updating address details!"
      if (!params?.pt) {
        redirect(action: "editAddressDetails", params: [p: params?.p, con: params?.con, op: params?.op])
      } else {
        redirect(action: "editAddressDetails", params: [p: params?.p, con: params?.con, op: params?.op, pt: params?.pt])
      }
    }
  }

  def editPolicies() {}

  @Transactional
  def editAndSavePolicies() {
    Person personToUpdate = Person.get(session?.personIdToEdit)

    if (!personToUpdate?.policies) {
      personToUpdate.policies = new Policy()
    }

    personToUpdate.policies.isActive = params?.isActive
    personToUpdate.policies.isBlocked = params?.isBlocked
    personToUpdate.policies.isOnHold = params?.isOnHold

    try {
      if (personToUpdate.validate()) {
        Person updatedPerson = personToUpdate.save(flush: true)

        if (updatedPerson) {
          session.personIdToEdit = updatedPerson?.id
          response.status = 200
          flash.message = "Successfully updated user policies!"
          if (!params?.pt) {
            redirect(action: "index", params: [p: params?.p, con: params?.con, op: params?.op])
          } else {
            redirect(action: "index", params: [p: params?.p, con: params?.con, op: params?.op, pt: params?.pt])
          }
        } else {
          flash.error = "Failed updating user policies!"
          if (!params?.pt) {
            redirect(action: "editPolicies", params: [p: params?.p, con: params?.con, op: params?.op])
          } else {
            redirect(action: "editPolicies", params: [p: params?.p, con: params?.con, op: params?.op, pt: params?.pt])
          }
        }
      }
    } catch (ValidationException e) {
      flash.error = "Failed updating user policies!"
      if (!params?.pt) {
        redirect(action: "editPolicies", params: [p: params?.p, con: params?.con, op: params?.op])
      } else {
        redirect(action: "editPolicies", params: [p: params?.p, con: params?.con, op: params?.op, pt: params?.pt])
      }
    }
  }
}
