package com.murad.app.matrimony

import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.annotation.Secured
import groovy.util.logging.Slf4j
import org.springframework.web.servlet.support.RequestContextUtils

import java.time.Duration
import java.time.LocalDateTime
import java.time.Period

@Slf4j
@Transactional
class DBoardController {
  def countryCodeService
  def religionService
  def additionalSpouseDetailsService
  def messageSource

  @Secured(['ROLE_USER'])
  def index() {
    Person person = request.getAttribute('currentPerson')

    def oldSignedInTime = SignInLog.where {
      user == person && status == "Offline" && loggedOutAt != null
    }.list(sort: "loggedOutAt", order: "desc", max: 1).find()

    def currentSignInLog = SignInLog.where {
      user == person && status == "Online" && loggedInAt != null && loggedOutAt == null
    }.list(sort: "loggedInAt", order: "desc", max: 1).first()

    Long daysBetween = null

    if (oldSignedInTime && oldSignedInTime.loggedOutAt && currentSignInLog && currentSignInLog.loggedInAt) {
      Duration duration = Duration.between(oldSignedInTime.loggedOutAt, currentSignInLog.loggedInAt)
      daysBetween = Math.abs(duration.toDays())
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
      user != null && user?.id != person?.id && status == "Online" && user?.personType?.personTypeName == "User"
    }.list(sort: 'loggedInAt', order: 'desc')

    def signedInUsersCount = signedInUsers.size()

    [person: person, signedInUsers: signedInUsers, userCount: signedInUsersCount, popperMsg: popper]
  }

  // ── VIEW ACTIONS ──────────────────────────────────────────────────

  @Secured(['ROLE_USER'])
  def personDetails() {
    [religions: religionService.getReligionsWithCaste()]
  }

  @Secured(['ROLE_USER'])
  def spouseDetails() {
    Person person = request.getAttribute('currentPerson')
    [personalDetails: person?.personDetails, religions: religionService.getReligionsWithCaste()]
  }

  @Secured(['ROLE_USER'])
  def familyDetails() {}

  @Secured(['ROLE_USER'])
  def spouseFamilyDetails() {
    Person person = request.getAttribute('currentPerson')
    [familyDetails: person.familyDetails]
  }

  @Secured(['ROLE_USER'])
  def contactDetails() {
    [countries: countryCodeService.getCountryCodes()]
  }

  @Secured(['ROLE_USER'])
  def educationDetails() {}

  @Secured(['ROLE_USER'])
  def professionDetails() {
    [currencies: countryCodeService.getWorldCurrencies()]
  }

  @Secured(['ROLE_USER'])
  def address() {}

  @Secured(['ROLE_USER'])
  def editPersonDetails() {
    Person person = request.getAttribute('currentPerson')
    [personalDetails: person?.personDetails]
  }

  @Secured(['ROLE_USER'])
  def editSpouseDetails() {
    Person person = request.getAttribute('currentPerson')
    [spouseDetails: person?.spouseDetails, personalDetails: person?.personDetails, religions: religionService.getReligionsWithCaste()]
  }

  @Secured(['ROLE_USER'])
  def editFamilyDetails() {
    Person person = request.getAttribute('currentPerson')
    [familyDetails: person?.familyDetails]
  }

  @Secured(['ROLE_USER'])
  def editSpouseFamilyDetails() {
    Person person = request.getAttribute('currentPerson')
    [spouseFamilyDetails: person?.spouseFamilyDetails, familyDetails: person?.familyDetails]
  }

  @Secured(['ROLE_USER'])
  def editContactDetails() {
    Person person = request.getAttribute('currentPerson')
    [contactDetails: person?.contactDetails, countries: countryCodeService.getAllCountryCodes()]
  }

  @Secured(['ROLE_USER'])
  def addContactDetailsForEdit() {
    [countries: countryCodeService.getCountryCodes()]
  }

  @Secured(['ROLE_USER'])
  def editEducationDetails() {
    Person person = request.getAttribute('currentPerson')
    [educationDetails: person?.educationDetails]
  }

  @Secured(['ROLE_USER'])
  def addEducationDetailsForEdit() {
  }

  @Secured(['ROLE_USER'])
  def editProfessionDetails() {
    Person person = request.getAttribute('currentPerson')
    [professions: person?.professionDetails, currencies: countryCodeService.getWorldCurrencies()]
  }

  @Secured(['ROLE_USER'])
  def addProfessionDetailsForEdit() {
    [currencies: countryCodeService.getWorldCurrencies()]
  }

  @Secured(['ROLE_USER'])
  def editAddressDetails() {
    Person person = request.getAttribute('currentPerson')
    [addresses: person?.address]
  }

  @Secured(['ROLE_USER'])
  def addAddressDetailsForEdit() {
  }

  @Secured(['ROLE_USER'])
  def addAdditionalSpouseDetails() {
    [educations: additionalSpouseDetailsService.getEducations(), countries: countryCodeService.getAllCountryCodes(), currencies: countryCodeService.getWorldCurrencies()]
  }

  @Secured(['ROLE_USER'])
  def editAdditionalSpouseDetails() {
    [educations: additionalSpouseDetailsService.getEducations(), countries: countryCodeService.getAllCountryCodes(), currencies: countryCodeService.getWorldCurrencies()]
  }

  @Secured(['ROLE_USER'])
  def addTicket() {}

  @Secured(['ROLE_USER'])
  def addDummyTicket() {}

  @Secured(['ROLE_USER'])
  def addTicketForAddTicket() {}

  @Secured(['ROLE_USER'])
  def listHobbiesToAdd() {
    params.offset = params?.offset ? params.int('offset') : 0
    params.max = params?.max ? params.int('max') : 10

    Person person = request.getAttribute('currentPerson')
    List<Long> associatedHobbyIds = person?.hobbies?.collect { it.id } ?: []

    // ids of categories that still have at least one ACTIVE hobby the person has not been assigned
    List<Long> categoryIds = Hobby.withCriteria {
      eq('active', true)
      if (associatedHobbyIds) {
        not { 'in'('id', associatedHobbyIds) }
      }
      projections { groupProperty('category.id') }
    } as List<Long>

    // those categories, in display order (empty list when none qualify)
    List<HobbyCategory> hobbyCategories = categoryIds ? HobbyCategory.withCriteria {
      'in'('id', categoryIds)
      order('displayOrder', 'asc')
      order('name', 'asc')
      order('description', 'asc')
    } as List<HobbyCategory> : []

    // Category to show: an explicit ?categoryId= (only if it is in the list above),
    // otherwise the first category in the list.
    Long requestedCategoryId = categoryIds.get(0)
    HobbyCategory selectedCategory = (requestedCategoryId ? hobbyCategories.find { it.id == requestedCategoryId } : null) ?: hobbyCategories.first()

//    def hobbies = []
//    long totalCount = 0

//    if (selectedCategory) {
//      // one query gives both the page of rows and the total (PagedResultList.totalCount)
//      def page = Hobby.createCriteria().list(max: params.max, offset: params.offset) {
//        eq('category', selectedCategory)
//        eq('active', true)
//        if (associatedHobbyIds) {
//          not { 'in'('id', associatedHobbyIds) }
//        }
//        order('name', 'asc')
//      }
//      hobbies = page
//      totalCount = page.totalCount
//    }
    response.status = 200
    [hobbyCategories : hobbyCategories,
     selectedCategory: selectedCategory]
//     hobbies         : hobbies,
//     totalCount      : totalCount]
  }

  @Secured('ROLE_USER')
  def listTicketTypeForAdd() {
    params.offset = params?.offset ? params.int('offset') : 0
    params.max = params?.max ? params.int('max') : 10
    [ticketTypes: TicketType.list(params)]
  }

  @Secured('ROLE_USER')
  def listTicketTypeForAddTicket() {
    params.offset = params?.offset ? params.int('offset') : 0
    params.max = params?.max ? params.int('max') : 10
    [ticketTypes: TicketType.list(params)]
  }

  @Secured(['ROLE_USER'])
  def listMoreTicketTypes() {
    params.offset = params?.offset ? params.int('offset') : 0
    params.max = params?.max ? params.int('max') : 10
    respond([ticketTypes: TicketType.list(params)], formats: ['json'])
  }

  def listHobbiesToEdit() {}

  // ── SAVE / UPDATE ACTIONS ─────────────────────────────────────────

  @Secured(['ROLE_USER'])
  def savePersonDetails() {
    try {
      Person person = request.getAttribute('currentPerson')

      PersonDetails personDetails = person?.personDetails
      if (personDetails) {
        response.status = 409
        respond([success: false, message: "Personal details already exists!"], formats: ['json'])
        return
      }

      def body = request.JSON
      personDetails = new PersonDetails(body)
      personDetails.person = person

      personDetails.dob = LocalDateTime.parse(body?.birthday)
      personDetails.age = getCurrentAge(personDetails.dob.toString())

      person?.personDetails = personDetails

      if (person.validate() && person.save(flush: true)) {
        respond([success: true, message: "Personal details added successfully!", redirectUrl: createLink(action: 'spouseDetails', params: [slot: "${request.getAttribute('currentSlot')}"])], formats: ['json'])
      } else {
        response.status = 400
        respond([success: false, message: resolveErrors(person.errors)], formats: ['json'])
      }

    } catch (Exception e) {
      log.error("Error saving personal details", e)
      response.status = 500
      respond([success: false, message: "Error saving personal details: ${e.message}"], formats: ['json'])
    }
  }

  @Secured(['ROLE_USER'])
  @Transactional
  def saveSpouseDetails() {
    try {
      Person person = request.getAttribute('currentPerson')

      SpouseDetails spouseDetails = person?.spouseDetails
      if (spouseDetails) {
        response.status = 409
        respond([success: false, message: "Spouse details already exists!"], formats: ['json'])
        return
      }

      def body = request.JSON
      spouseDetails = new SpouseDetails(body)
      spouseDetails.person = person
      spouseDetails.gender = (person?.personDetails?.gender == 'Female') ? 'Male' : 'Female'

      person.spouseDetails = spouseDetails

      if (person.validate() && person.save(flush: true)) {
        respond([success: true, message: "Spouse details added successfully!", redirectUrl: createLink(action: 'familyDetails', params: [slot: "${request.getAttribute('currentSlot')}"])], formats: ['json'])
      } else {
        response.status = 400
        respond([success: false, message: resolveErrors(person.errors)], formats: ['json'])
      }

    } catch (Exception e) {
      log.error("Error saving spouse details", e)
      response.status = 500
      respond([success: false, message: "Error saving spouse details: ${e.message}"], formats: ['json'])
    }
  }

  @Secured(['ROLE_USER'])
  @Transactional
  def saveFamilyDetails() {
    try {
      Person person = request.getAttribute('currentPerson')

      if (person.familyDetails) {
        response.status = 409
        respond([success: false, message: "Family details already exists!"], formats: ['json'])
        return
      }

      def body = request.JSON
      FamilyDetails familyDetails = new FamilyDetails(body)
      familyDetails.person = person
      person.familyDetails = familyDetails

      if (person.validate() && person.save(flush: true)) {
        respond([success: true, message: "Family details saved successfully!", redirectUrl: createLink(action: 'spouseFamilyDetails', params: [slot: "${request.getAttribute('currentSlot')}"])], formats: ['json'])
      } else {
        response.status = 400
        respond([success: false, message: resolveErrors(person.errors)], formats: ['json'])
      }

    } catch (Exception e) {
      log.error("Error saving family details", e)
      response.status = 500
      respond([success: false, message: "Error saving family details: ${e.message}"], formats: ['json'])
    }
  }

  @Secured(['ROLE_USER'])
  @Transactional
  def saveSpouseFamilyDetails() {
    try {
      Person person = request.getAttribute('currentPerson')

      if (person.spouseFamilyDetails) {
        response.status = 409
        respond([success: false, message: "Spouse family details already exists!"], formats: ['json'])
        return
      }

      def body = request.JSON
      SpouseFamilyDetails spouseFamilyDetails = new SpouseFamilyDetails(body)
      spouseFamilyDetails.person = person
      person.spouseFamilyDetails = spouseFamilyDetails

      if (person.validate() && person.save(flush: true)) {
        respond([success: true, message: "Spouse family details saved successfully!", redirectUrl: createLink(action: 'contactDetails', params: [slot: "${request.getAttribute('currentSlot')}"])], formats: ['json'])
      } else {
        response.status = 400
        respond([success: false, message: resolveErrors(person.errors)], formats: ['json'])
      }

    } catch (Exception e) {
      log.error("Error saving spouse family details", e)
      response.status = 500
      respond([success: false, message: "Error saving spouse family details: ${e.message}"], formats: ['json'])
    }
  }

  @Transactional
  @Secured(['ROLE_USER'])
  def saveContactDetails() {
    try {
      Person person = request.getAttribute('currentPerson')

      def body = request.JSON

      String isdCode = body?.isdCode?.trim()
      String country = body?.country?.trim()
      String flag = body?.flag?.trim()
      String mobileNumber = body?.mobileNumber?.trim()
      String eMail = body?.eMail?.trim()
      Boolean isPrimary = body?.isPrimary

      ContactDetails newContactDetails = new ContactDetails(
        isdCode: isdCode,
        country: country,
        flag: flag,
        mobileNumber: mobileNumber,
        eMail: eMail,
        isPrimary: isPrimary,
        person: person
      )

      // ── 1. Reject if caller wants primary but one already exists ──────
      ContactDetails primaryContactDetails = ContactDetails.findByIsPrimaryAndPerson(true, person)
      if (isPrimary && primaryContactDetails) {
        response.status = 409
        respond([success: false, message: "A primary contact already exists.", existingContact: primaryContactDetails, newContact: newContactDetails], formats: ['json'])
        return
      }

      // ── 2. Reject if this exact combination already exists ────────────
      ContactDetails exactDuplicate = ContactDetails.findByIsdCodeAndMobileNumberAndEMailAndPerson(isdCode, mobileNumber, eMail, person)
      if (exactDuplicate) {
        response.status = 409
        respond([success: false, message: "A contact with these details already exists.", existingContact: exactDuplicate, newContact: newContactDetails], formats: ['json'])
        return
      }

      // ── 3. Save ─────────────────────────────────────────────
      if (newContactDetails.validate() && newContactDetails.save(flush: true)) {
        respond([
          success    : true,
          message    : "New contact details saved successfully!",
          redirectUrl: createLink(action: 'index', params: [slot: "${request.getAttribute('currentSlot')}"])
        ], formats: ['json'])
      } else {
        response.status = 400
        respond([success: false, message: resolveErrors(contactDetails.errors)], formats: ['json'])
      }
    } catch (Exception e) {
      log.error("Error saving contact details", e)
      response.status = 500
      respond([success: false, message: "Error saving contact details: ${e.message}"], formats: ['json'])
    }
  }

//  @Transactional
//  @Secured(['ROLE_USER'])
//  def saveContactDetailsForEditAdd() {
//    try {
//      Person person = springSecurityService.currentUser as Person
//      def body = request.JSON
//
//      String isdCode = body?.isdCode?.trim()
//      String mobileNumber = body?.mobileNumber?.trim()
//      String eMail = body?.eMail?.trim()
//      Boolean isPrimary = body?.isPrimary
//
//      // ── 1. Reject if this exact combination already exists ────────────
//      if (ContactDetails.findByIsdCodeAndMobileNumberAndEMail(isdCode, mobileNumber, eMail)) {
//        response.status = 409
//        respond([success: false, message: "A contact with these details already exists."], formats: ['json'])
//        return
//      }
//
//      // ── 2. Reject if caller wants primary but one already exists ──────
//      if (isPrimary && ContactDetails.findByIsPrimaryAndPerson(true, person)) {
//        response.status = 409
//        respond([success: false, message: "A primary contact already exists."], formats: ['json'])
//        return
//      }
//
//      // ── 3. Build and save ─────────────────────────────────────────────
//      ContactDetails contactDetails = new ContactDetails(
//        isdCode: isdCode,
//        mobileNumber: mobileNumber,
//        eMail: eMail,
//        isPrimary: isPrimary,
//        person: person
//      )
//
//      if (contactDetails.validate() && contactDetails.save(flush: true)) {
//        respond([
//          success    : true,
//          message    : "Contact details saved successfully!",
//          redirectUrl: createLink(action: 'editContactDetails', params: [slot: "${request.getAttribute('currentSlot')}"])
//        ], formats: ['json'])
//      } else {
//        response.status = 400
//        respond([success: false, message: resolveErrors(contactDetails.errors)], formats: ['json'])
//      }
//    } catch (Exception e) {
//      log.error("Error saving contact details", e)
//      response.status = 500
//      respond([success: false, message: "Error saving contact details: ${e.message}"], formats: ['json'])
//    }
//  }

  @Transactional
  @Secured(['ROLE_USER'])
  def saveEducationDetails() {
    try {
      Person person = request.getAttribute('currentPerson')

      def body = request.JSON

      String name = body?.name?.trim()
      String institution = body?.institution?.trim()
      String institutionLocation = body?.institutionLocation?.trim()
      String university = body?.university?.trim()
      String universityLocation = body?.universityLocation?.trim()
      Boolean isHighest = body?.isHighest

      EducationDetails newEducation = new EducationDetails(
        name: name,
        institution: institution,
        institutionLocation: institutionLocation,
        university: university,
        universityLocation: universityLocation,
        isHighest: isHighest,
        person: person
      )


      // ── 1. Reject if caller wants highest but one already exists ──────
      if (isHighest) {
        EducationDetails existingHighest = EducationDetails.findByIsHighestAndPerson(true, person)
        if (existingHighest) {
          response.status = 409
          respond([
            success          : false,
            message          : "A highest education record already exists.",
            existingEducation: existingHighest,
            newEducation     : newEducation
          ], formats: ['json'])
          return
        }
      }

      // ── 2. Reject if this exact combination already exists ────────────
      EducationDetails exactDuplicate = EducationDetails.findByNameAndInstitutionAndUniversityAndPerson(
        name, institution, university, person
      )
      if (exactDuplicate) {
        response.status = 409
        respond([
          success          : false,
          message          : "Education details already exist.",
          existingEducation: exactDuplicate,
          newEducation     : newEducation
        ], formats: ['json'])
        return
      }

      // ── 3. Save ───────────────────────────────────────────────────────
      if (newEducation.validate() && newEducation.save(flush: true)) {
        respond([
          success    : true,
          message    : "Education details saved successfully!",
          redirectUrl: createLink(action: 'index', params: [slot: "${request.getAttribute('currentSlot')}"])
        ], formats: ['json'])
      } else {
        response.status = 400
        respond([
          success: false,
          message: resolveErrors(newEducation.errors)
        ], formats: ['json'])
      }

    } catch (Exception e) {
      log.error("Error saving education details", e)
      response.status = 500
      respond([
        success: false,
        message: "Error saving education details: ${e.message}"
      ], formats: ['json'])
    }
  }

  @Transactional
  @Secured(['ROLE_USER'])
  def saveEducationDetailsForEditAdd() {
    try {
      Person person = request.getAttribute('currentPerson')

      def body = request.JSON
      String name = body?.name?.trim() ?: ''
      String institution = body?.institution?.trim() ?: ''
      String university = body?.university?.trim() ?: ''
      String institutionLocation = body?.institutionLocation?.trim() ?: ''
      String universityLocation = body?.universityLocation?.trim() ?: ''
      Boolean isHighest = body?.isHighest as Boolean ?: false
      String ac = body?.ac?.trim() ?: ''
      def bodyId = body?.id

      // ── 0. Basic field validation ─────────────────────────────────
      // isHighest intentionally excluded — Boolean false is falsy and
      // would always trigger a 400 for unchecked submissions
      if ([name, institution, university, institutionLocation, universityLocation].any { !it }) {
        response.status = 400
        respond([
          success: false,
          message: "All required fields (name, institution, university, institutionLocation, universityLocation) must be filled in."
        ], formats: ['json'])
        return
      }

      // ── Build new education map once — reused in all 409 responses ──
      Map newEducationMap = [
        name               : name,
        institution        : institution,
        university         : university,
        institutionLocation: institutionLocation,
        universityLocation : universityLocation,
        isHighest          : isHighest
      ]

      // ── 1. Exact duplicate check ──────────────────────────────────────
// findWhere accepts a named-parameter map — safe for all field counts
// and handles nullable fields without dynamic finder method name issues
      EducationDetails exactDuplicate = EducationDetails.findWhere(
        name: name,
        institution: institution,
        university: university ?: null,
        institutionLocation: institutionLocation ?: null,
        universityLocation: universityLocation ?: null,
        person: person
      )

      if (exactDuplicate) {
        response.status = 409
        respond([
          success          : false,
          message          : "This education record already exists!",
          existingEducation: exactDuplicate,
          newEducation     : newEducationMap
        ], formats: ['json'])
        return
      }

      // ── 2. Highest qualification uniqueness check ─────────────────
      // Only one record per person can be flagged isHighest = true
      if (isHighest) {
        EducationDetails existingHighest = EducationDetails.findByIsHighestAndPerson(true, person)
        if (existingHighest) {
          response.status = 409
          respond([
            success          : false,
            message          : "A highest qualification already exists. Replace it or uncheck 'Is Highest'.",
            existingEducation: existingHighest,
            newEducation     : newEducationMap
          ], formats: ['json'])
          return
        }
      }

      // ── 3. Save ───────────────────────────────────────────────────
      EducationDetails education = new EducationDetails(
        name: name,
        institution: institution,
        university: university ?: null,
        institutionLocation: institutionLocation ?: null,
        universityLocation: universityLocation ?: null,
        isHighest: isHighest,
        person: person
      )

      if (education.validate() && education.save(flush: true)) {
        String redirectUrl = ac == 'an'
          ? createLink(controller: 'AdminDBoard', action: 'addPolicy',
          params: [id: bodyId, ac: ac, slot: "${request.getAttribute('currentSlot')}"])
          : createLink(action: 'editEducationDetails', params: [slot: "${request.getAttribute('currentSlot')}"])
        respond([
          success    : true,
          message    : "Education detail saved successfully!",
          redirectUrl: redirectUrl
        ], formats: ['json'])
      } else {
        response.status = 400
        respond([
          success: false,
          message: resolveErrors(education.errors)
        ], formats: ['json'])
      }

    } catch (Exception e) {
      log.error("Error saving education detail", e)
      response.status = 500
      respond([
        success: false,
        message: "Error saving education detail: ${e.message}"
      ], formats: ['json'])
    }
  }

/**
 * POST  /person/replaceEducationDetailsForEditAdd
 *
 * Demotes an existing EducationDetails record (clears isHighest) and saves a
 * new one in its place. The caller sends this after the user confirms the
 * 409 conflict dialog.
 *
 * Expected JSON body:
 * {
 *   "existingId": 5,
 *   "newEducation": {
 *     "name"               : "Master of Science",
 *     "institution"        : "IIT Madras",
 *     "university"         : "IIT Madras",
 *     "institutionLocation": "Chennai",
 *     "universityLocation" : "Chennai",
 *     "isHighest"          : true
 *   }
 * }
 *
 * HTTP responses:
 *   200  — replaced; body contains { success, message, oldEducation, newEducation, redirectUrl }
 *   400  — validation failure or missing required fields
 *   403  — caller does not own the existing record
 *   404  — existing record not found
 *   500  — unexpected server error
 */
  @Transactional
  @Secured(['ROLE_USER'])
  def replaceEducationDetailsForEditAdd() {
    Boolean existingWasHighest = null

    try {
      Person person = request.getAttribute('currentPerson')
      def body = request.JSON

      // ── 1. Load the existing education record to be demoted ───────
      EducationDetails existingEducation = EducationDetails.get(body?.existingId as Long)
      if (!existingEducation) {
        response.status = 404
        respond([
          success: false,
          message: "Existing education detail not found!"
        ], formats: ['json'])
        return
      }

      // ── 2. Verify ownership ───────────────────────────────────────
      if (existingEducation.person?.id != person.id) {
        response.status = 403
        respond([
          success: false,
          message: "You are not authorised to replace this education detail."
        ], formats: ['json'])
        return
      }

      // ── 3. Extract new education fields from the nested map ───────
      def n = body?.newEducation
      if (!n) {
        response.status = 400
        respond([
          success: false,
          message: "New education data is missing."
        ], formats: ['json'])
        return
      }

      String newName = n?.name?.trim() ?: ''
      String newInstitution = n?.institution?.trim() ?: ''
      String newUniversity = n?.university?.trim() ?: ''
      String newInstitutionLocation = n?.institutionLocation?.trim() ?: ''
      String newUniversityLocation = n?.universityLocation?.trim() ?: ''
      Boolean newIsHighest = n?.isHighest as Boolean ?: false

      if ([newName, newInstitution].any { !it }) {
        response.status = 400
        respond([
          success: false,
          message: "New education detail is missing required fields (name, institution)."
        ], formats: ['json'])
        return
      }

      // ── 4. Capture existing state before mutation ─────────────────
      existingWasHighest = existingEducation.isHighest

      // ── 5. Snapshot old education record for the response ─────────
      // Taken now so the response reflects pre-mutation values.
      Map oldEducationMap = [
        id                 : existingEducation.id,
        name               : existingEducation.name,
        institution        : existingEducation.institution,
        university         : existingEducation.university,
        institutionLocation: existingEducation.institutionLocation,
        universityLocation : existingEducation.universityLocation,
        isHighest          : existingEducation.isHighest
      ]

      // ── 6. Demote the existing record — clear isHighest ───────────
      // The qualification name and institution are intentionally preserved
      // so the record remains identifiable and auditable after demotion.
      existingEducation.isHighest = false

      if (!existingEducation.validate()) {
        response.status = 400
        respond([
          success: false,
          message: resolveErrors(existingEducation.errors)
        ], formats: ['json'])
        return
      }

      existingEducation.save(flush: false, failOnError: true)

      // ── 7. Save the new education record ──────────────────────────
      EducationDetails newEducation = new EducationDetails(
        name: newName,
        institution: newInstitution,
        university: newUniversity ?: null,
        institutionLocation: newInstitutionLocation ?: null,
        universityLocation: newUniversityLocation ?: null,
        isHighest: newIsHighest,
        person: person
      )

      if (!newEducation.validate()) {
        response.status = 400
        respond([
          success: false,
          message: resolveErrors(newEducation.errors)
        ], formats: ['json'])
        return
      }

      newEducation.save(flush: true, failOnError: true)

      // ── 8. Respond ────────────────────────────────────────────────
      String label = newIsHighest ? 'Highest' : 'New'
      respond([
        success     : true,
        message     : "${label} education detail saved successfully!",
        oldEducation: oldEducationMap,
        newEducation: newEducation,
        redirectUrl : createLink(action: 'editEducationDetails', params: [slot: "${request.getAttribute('currentSlot')}"])
      ], formats: ['json'])

    } catch (Exception e) {
      String ctx = existingWasHighest != null
        ? (existingWasHighest ? 'highest' : 'non-highest')
        : 'unknown'
      log.error("Error replacing existing ${ctx} education detail", e)
      response.status = 500
      respond([
        success: false,
        message: "Error replacing existing ${ctx} education detail: ${e.message}"
      ], formats: ['json'])
    }
  }

  @Transactional
  @Secured(['ROLE_USER'])
  def saveProfessionDetails() {
    try {
      Person person = request.getAttribute('currentPerson')

      def body = request.JSON

      String designation = body?.designation?.trim()
      String organisation = body?.organisation?.trim()
      String organisationLocation = body?.organisationLocation?.trim()
      String jobCategory = body?.jobCategory?.trim()
      String currencyCode = body?.currencyCode?.trim()
      String denomination = body?.denomination?.trim()
      String salary = body?.salary?.trim()
      String salaryFor = body?.salaryFor?.trim()
      Boolean isCurrent = body?.isCurrent

      ProfessionalDetails newProfession = new ProfessionalDetails(
        designation: designation,
        organisation: organisation,
        organisationLocation: organisationLocation,
        jobCategory: jobCategory,
        currencyCode: currencyCode,
        denomination: denomination,
        salary: salary,
        salaryFor: salaryFor,
        isCurrent: isCurrent,
        person: person
      )

      // ── 1. Reject if caller marks as current but one already exists ───
      if (isCurrent) {
        ProfessionalDetails existingCurrent = ProfessionalDetails.findByIsCurrentAndPerson(true, person)
        if (existingCurrent) {
          response.status = 409
          respond([
            success           : false,
            message           : "A current profession already exists.",
            existingProfession: existingCurrent,
            newProfession     : newProfession
          ], formats: ['json'])
          return
        }
      }

      // ── 2. Reject if this exact combination already exists ────────────
      ProfessionalDetails exactDuplicate = ProfessionalDetails.findByDesignationAndOrganisationAndOrganisationLocationAndPerson(
        designation, organisation, organisationLocation, person
      )
      if (exactDuplicate) {
        response.status = 409
        respond([
          success           : false,
          message           : "Profession details already exist.",
          existingProfession: exactDuplicate,
          newProfession     : newProfession
        ], formats: ['json'])
        return
      }

      // ── 3. Save ───────────────────────────────────────────────────────
      if (newProfession.validate() && newProfession.save(flush: true)) {
        respond([
          success    : true,
          message    : "Profession details saved successfully!",
          redirectUrl: createLink(action: 'index', params: [slot: "${request.getAttribute('currentSlot')}"])
        ], formats: ['json'])
      } else {
        response.status = 400
        respond([
          success: false,
          message: resolveErrors(newProfession.errors)
        ], formats: ['json'])
      }

    } catch (Exception e) {
      log.error("Error saving profession details", e)
      response.status = 500
      respond([
        success: false,
        message: "Error saving profession details: ${e.message}"
      ], formats: ['json'])
    }
  }

  @Transactional
  @Secured(['ROLE_USER'])
  def saveProfessionalDetailsForEditAdd() {
    try {
      Person person = request.getAttribute('currentPerson')

      def body = request.JSON
      String designation = body?.designation?.trim() ?: ''
      String organisation = body?.organisation?.trim() ?: ''
      String organisationLocation = body?.organisationLocation?.trim() ?: ''
      Boolean isCurrent = body?.isCurrent as Boolean ?: false
      String jobCategory = body?.jobCategory?.trim() ?: ''
      String currencyCode = body?.currencyCode?.trim() ?: ''
      String denomination = body?.denomination?.trim() ?: ''
      String country = body?.country?.trim() ?: ''
      String salary = body?.salary?.trim() ?: ''
      String salaryFor = body?.salaryFor?.trim() ?: ''
      String ac = body?.ac?.trim() ?: ''
      def bodyId = body?.id

      // ── 0. Basic field validation ──────────────────────────────────────
      // isCurrent intentionally excluded — Boolean false is falsy and
      // would always trigger a 400 for non-current positions
      if ([designation, organisation, jobCategory, salaryFor].any { !it }) {
        response.status = 400
        respond([
          success: false,
          message: "All required fields (designation, organisation, jobCategory, salaryFor) must be filled in."
        ], formats: ['json'])
        return
      }

      // ── Build new professional map once — reused in all 409 responses ──
      Map newProfessionalMap = [
        designation         : designation,
        organisation        : organisation,
        organisationLocation: organisationLocation,
        isCurrent           : isCurrent,
        jobCategory         : jobCategory,
        currencyCode        : currencyCode,
        denomination        : denomination,
        country             : country,
        salary              : salary,
        salaryFor           : salaryFor
      ]

      // ── 1. Exact duplicate check ───────────────────────────────────────
      ProfessionalDetails exactDuplicate = ProfessionalDetails.findWhere(
        designation: designation,
        organisation: organisation,
        organisationLocation: organisationLocation ?: null,
        jobCategory: jobCategory,
        salary: salary ?: null,
        salaryFor: salaryFor,
        person: person
      )

      if (exactDuplicate) {
        response.status = 409
        respond([
          success                   : false,
          message                   : "This professional record already exists!",
          existingProfessionalDetail: exactDuplicate,
          newProfessionalDetail     : newProfessionalMap
        ], formats: ['json'])
        return
      }

      // ── 2. Current position uniqueness check ──────────────────────────
      // Only one record per person can be flagged isCurrent = true
      if (isCurrent) {
        ProfessionalDetails existingCurrent = ProfessionalDetails.findByIsCurrentAndPerson(true, person)
        if (existingCurrent) {
          response.status = 409
          respond([
            success                   : false,
            message                   : "A current position already exists. Replace it or uncheck 'Is Current'.",
            existingProfessionalDetail: existingCurrent,
            newProfessionalDetail     : newProfessionalMap
          ], formats: ['json'])
          return
        }
      }

      // ── 3. Save ────────────────────────────────────────────────────────
      ProfessionalDetails professionalDetails = new ProfessionalDetails(
        designation: designation,
        organisation: organisation,
        organisationLocation: organisationLocation ?: null,
        isCurrent: isCurrent,
        jobCategory: jobCategory,
        currencyCode: currencyCode ?: null,
        denomination: denomination ?: null,
        country: country ?: null,
        salary: salary ?: null,
        salaryFor: salaryFor,
        person: person
      )

      if (professionalDetails.validate() && professionalDetails.save(flush: true)) {
        String redirectUrl = ac == 'an'
          ? createLink(controller: 'AdminDBoard', action: 'addPolicy',
          params: [id: bodyId, ac: ac, slot: "${request.getAttribute('currentSlot')}"])
          : createLink(action: 'editProfessionalDetails', params: [slot: "${request.getAttribute('currentSlot')}"])
        respond([
          success    : true,
          message    : "Professional detail saved successfully!",
          redirectUrl: redirectUrl
        ], formats: ['json'])
      } else {
        response.status = 400
        respond([
          success: false,
          message: resolveErrors(professionalDetails.errors)
        ], formats: ['json'])
      }

    } catch (Exception e) {
      log.error("Error saving professional detail", e)
      response.status = 500
      respond([
        success: false,
        message: "Error saving professional detail: ${e.message}"
      ], formats: ['json'])
    }
  }

/**
 * POST  /person/replaceProfessionalDetailsForEditAdd
 *
 * Demotes an existing ProfessionalDetails record (clears isCurrent) and saves a
 * new one in its place. The caller sends this after the user confirms the
 * 409 conflict dialog.
 *
 * Expected JSON body:
 * {
 *   "existingId": 5,
 *   "newProfessionalDetail": {
 *     "designation"         : "Senior Engineer",
 *     "organisation"        : "Acme Corp",
 *     "organisationLocation": "Chennai",
 *     "isCurrent"           : true,
 *     "jobCategory"         : "Private Sector",
 *     "currencyCode"        : "INR",
 *     "denomination"        : "Lakh",
 *     "country"             : "India",
 *     "salary"              : "10",
 *     "salaryFor"           : "Per Anum"
 *   }
 * }
 *
 * HTTP responses:
 *   200  — replaced; body contains { success, message, oldProfessionalDetail, newProfessionalDetail, redirectUrl }
 *   400  — validation failure or missing required fields
 *   403  — caller does not own the existing record
 *   404  — existing record not found
 *   500  — unexpected server error
 */
  @Transactional
  @Secured(['ROLE_USER'])
  def replaceProfessionalDetailsForEditAdd() {
    Boolean existingWasCurrent = null

    try {
      Person person = request.getAttribute('currentPerson')
      def body = request.JSON

      // ── 1. Load the existing professional record to be demoted ────────
      ProfessionalDetails existingProfessionalDetail = ProfessionalDetails.get(body?.existingId as Long)
      if (!existingProfessionalDetail) {
        response.status = 404
        respond([
          success: false,
          message: "Existing professional detail not found!"
        ], formats: ['json'])
        return
      }

      // ── 2. Verify ownership ───────────────────────────────────────────
      if (existingProfessionalDetail.person?.id != person.id) {
        response.status = 403
        respond([
          success: false,
          message: "You are not authorised to replace this professional detail."
        ], formats: ['json'])
        return
      }

      // ── 3. Extract new professional fields from the nested map ─────────
      def n = body?.newProfessionalDetail
      if (!n) {
        response.status = 400
        respond([
          success: false,
          message: "New professional data is missing."
        ], formats: ['json'])
        return
      }

      String newDesignation = n?.designation?.trim() ?: ''
      String newOrganisation = n?.organisation?.trim() ?: ''
      String newOrganisationLocation = n?.organisationLocation?.trim() ?: ''
      Boolean newIsCurrent = n?.isCurrent as Boolean ?: false
      String newJobCategory = n?.jobCategory?.trim() ?: ''
      String newCurrencyCode = n?.currencyCode?.trim() ?: ''
      String newDenomination = n?.denomination?.trim() ?: ''
      String newCountry = n?.country?.trim() ?: ''
      String newSalary = n?.salary?.trim() ?: ''
      String newSalaryFor = n?.salaryFor?.trim() ?: ''

      if ([newDesignation, newOrganisation, newJobCategory, newSalaryFor].any { !it }) {
        response.status = 400
        respond([
          success: false,
          message: "New professional detail is missing required fields (designation, organisation, jobCategory, salaryFor)."
        ], formats: ['json'])
        return
      }

      // ── 4. Capture existing state before mutation ──────────────────────
      existingWasCurrent = existingProfessionalDetail.isCurrent

      // ── 5. Snapshot old professional record for the response ───────────
      // Taken now so the response reflects pre-mutation values.
      Map oldProfessionalMap = [
        id                  : existingProfessionalDetail.id,
        designation         : existingProfessionalDetail.designation,
        organisation        : existingProfessionalDetail.organisation,
        organisationLocation: existingProfessionalDetail.organisationLocation,
        isCurrent           : false,
        jobCategory         : existingProfessionalDetail.jobCategory,
        currencyCode        : existingProfessionalDetail.currencyCode,
        denomination        : existingProfessionalDetail.denomination,
        country             : existingProfessionalDetail.country,
        salary              : existingProfessionalDetail.salary,
        salaryFor           : existingProfessionalDetail.salaryFor
      ]

      // ── 6. Demote the existing record — clear isCurrent ────────────────
      // The designation and organisation are intentionally preserved so the
      // record remains identifiable and auditable after demotion.
      existingProfessionalDetail.isCurrent = false

      if (!existingProfessionalDetail.validate()) {
        response.status = 400
        respond([
          success: false,
          message: resolveErrors(existingProfessionalDetail.errors)
        ], formats: ['json'])
        return
      }

      existingProfessionalDetail.save(flush: false, failOnError: true)

      // ── 7. Save the new professional record ───────────────────────────
      ProfessionalDetails newProfessionalDetail = new ProfessionalDetails(
        designation: newDesignation,
        organisation: newOrganisation,
        organisationLocation: newOrganisationLocation ?: null,
        isCurrent: newIsCurrent,
        jobCategory: newJobCategory,
        currencyCode: newCurrencyCode ?: null,
        denomination: newDenomination ?: null,
        country: newCountry ?: null,
        salary: newSalary ?: null,
        salaryFor: newSalaryFor,
        person: person
      )

      if (!newProfessionalDetail.validate()) {
        response.status = 400
        respond([
          success: false,
          message: resolveErrors(newProfessionalDetail.errors)
        ], formats: ['json'])
        return
      }

      newProfessionalDetail.save(flush: true, failOnError: true)

      // ── 8. Respond ─────────────────────────────────────────────────────
      String label = newIsCurrent ? 'Current' : 'New'
      respond([
        success              : true,
        message              : "${label} professional detail saved successfully!",
        oldProfessionalDetail: oldProfessionalMap,
        newProfessionalDetail: newProfessionalDetail,
        redirectUrl          : createLink(action: 'editProfessionalDetails', params: [slot: "${request.getAttribute('currentSlot')}"])
      ], formats: ['json'])

    } catch (Exception e) {
      String ctx = existingWasCurrent != null
        ? (existingWasCurrent ? 'current' : 'non-current')
        : 'unknown'
      log.error("Error replacing existing ${ctx} professional detail", e)
      response.status = 500
      respond([
        success: false,
        message: "Error replacing existing ${ctx} professional detail: ${e.message}"
      ], formats: ['json'])
    }
  }

//  @Transactional
//  @Secured(['ROLE_USER'])
//  def saveProfessionDetailsForEditAdd() {
//    try {
//      Person person = springSecurityService.currentUser as Person
//
//      def existing = ProfessionalDetails.findByDesignationAndOrganisationAndOrganisationLocation(params?.designation, params?.organisation, params?.organisationLocation)
//      if (existing) {
//        response.status = 409
//        respond([success: false, message: existing.isCurrent && params.boolean('isCurrent')
//          ? "Current job already exists!"
//          : "Job details already exists!"], formats: ['json'])
//        return
//      }
//
//      if (params.boolean('isCurrent')) {
//        def currentJob = ProfessionalDetails.findByIsCurrentAndPerson(true, person)
//        if (currentJob) {
//          response.status = 409
//          respond([success: false, message: "Current job already exists!"], formats: ['json'])
//          return
//        }
//      }
//
//      ProfessionalDetails professionalDetails = new ProfessionalDetails(params)
//      professionalDetails.person = person
//      professionalDetails.isCurrent = params.boolean('isCurrent')
//
//      if (professionalDetails.validate() && professionalDetails.save(flush: true)) {
//        respond([success: true, message: "Profession details saved successfully!", redirectUrl: createLink(action: 'editProfessionDetails', params: [slot: "${request.getAttribute('currentSlot')}"])], formats: ['json'])
//      } else {
//        response.status = 400
//        respond([success: false, message: resolveErrors(professionalDetails.errors)], formats: ['json'])
//      }
//
//    } catch (Exception e) {
//      log.error("Error saving profession details", e)
//      response.status = 500
//      respond([success: false, message: "Error saving profession details: ${e.message}"], formats: ['json'])
//    }
//  }


  @Transactional
  @Secured(['ROLE_USER'])
  def saveAddress() {
    try {
      Person person = request.getAttribute('currentPerson')
      def body = request.JSON

      String street1 = body?.street1?.trim()
      String street2 = body?.street2?.trim()
      String city = body?.city?.trim()
      String district = body?.district?.trim()
      String state = body?.state?.trim()
      String country = body?.country?.trim()
      String postalCode = body?.postalCode?.trim()
      String addressType = body?.addressType?.trim()
      String otherType = body?.otherType?.trim()

      Address newAddress = new Address(
        street1: street1,
        street2: street2,
        city: city,
        district: district,
        state: state,
        country: country,
        postalCode: postalCode,
        addressType: addressType,
        otherType: otherType,
        person: person
      )

      // ── 1. Reject if this exact combination already exists ────────────
      Address exactDuplicate = Address.findByStreet1AndStreet2AndCityAndDistrictAndStateAndCountryAndPostalCodeAndPerson(
        street1, street2, city, district, state, country, postalCode, person
      )
      if (exactDuplicate) {
        response.status = 409
        respond([
          success        : false,
          message        : "Address already exists.",
          existingAddress: exactDuplicate,
          newAddress     : newAddress
        ], formats: ['json'])
        return
      }

      // ── 2. Reject if an address of the same type already exists ───────
      if (addressType == 'Other') {
        Address otherAddress = Address.findByOtherTypeAndPerson(otherType, person)
        if (otherAddress) {
          response.status = 409
          respond([
            success        : false,
            message        : "An address of type '${otherType}' already exists.",
            existingAddress: otherAddress,
            newAddress     : newAddress
          ], formats: ['json'])
          return
        }
      } else {
        Address typeAddress = Address.findByAddressTypeAndPerson(addressType, person)
        if (typeAddress) {
          response.status = 409
          respond([
            success        : false,
            message        : "An address of type '${addressType}' already exists.",
            existingAddress: typeAddress,
            newAddress     : newAddress
          ], formats: ['json'])
          return
        }
      }

      // ── 3. Save ───────────────────────────────────────────────────────
      if (newAddress.validate() && newAddress.save(flush: true)) {
        String redirectUrl = body?.ac == 'an'
          ? createLink(controller: 'AdminDBoard', action: 'addPolicy', params: [id: body?.id, ac: body?.ac, slot: "${request.getAttribute('currentSlot')}"])
          : createLink(action: 'index', params: [slot: "${request.getAttribute('currentSlot')}"])
        respond([
          success    : true,
          message    : "Address saved successfully!",
          redirectUrl: redirectUrl
        ], formats: ['json'])
      } else {
        response.status = 400
        respond([
          success: false,
          message: resolveErrors(newAddress.errors)
        ], formats: ['json'])
      }

    } catch (Exception e) {
      log.error("Error saving address", e)
      response.status = 500
      respond([
        success: false,
        message: "Error saving address: ${e.message}"
      ], formats: ['json'])
    }
  }

  @Transactional
  @Secured(['ROLE_USER'])
  def saveAddressDetailsForEditAdd() {
    try {
      Person person = request.getAttribute('currentPerson')

      def body = request.JSON
      String street1 = body?.street1?.trim() ?: ''
      String street2 = body?.street2?.trim() ?: ''
      String city = body?.city?.trim() ?: ''
      String district = body?.district?.trim() ?: ''
      String state = body?.state?.trim() ?: ''
      String country = body?.country?.trim() ?: ''
      String postalCode = body?.postalCode?.trim() ?: ''
      String addressType = body?.addressType?.trim() ?: ''
      String otherType = body?.otherType?.trim() ?: ''
      String ac = body?.ac?.trim() ?: ''
      def bodyId = body?.id

      // ── 0. Basic field validation ────────────────────────────────
      if ([street1, street2, city, district, state, country, postalCode, addressType].any { !it }) {
        response.status = 400
        respond([
          success: false,
          message: "All required fields must be filled in."
        ], formats: ['json'])
        return
      }

      // ── Build the new address map once — reused in all 409 responses ──
      // Using a plain map avoids Hibernate serialisation issues that occur
      // when an unsaved domain object is passed directly to respond().
      Map newAddressMap = [
        street1    : street1,
        street2    : street2,
        city       : city,
        district   : district,
        state      : state,
        country    : country,
        postalCode : postalCode,
        addressType: addressType,
        otherType  : otherType
      ]

      // ── 1. Exact duplicate check (same fields, same person) ──────
      Address exactDuplicate = Address.findByStreet1AndStreet2AndCityAndDistrictAndStateAndCountryAndPostalCodeAndPerson(
        street1, street2, city, district, state, country, postalCode, person)

      if (exactDuplicate) {
        response.status = 409
        respond([
          success        : false,
          message        : "This address already exists!",
          existingAddress: exactDuplicate,
          newAddress     : newAddressMap
        ], formats: ['json'])
        return
      }

      // ── 2. Address-type duplicate check (same type, same person) ─
      if (addressType == 'Other') {
        Address otherAddress = Address.findByOtherTypeAndPerson(otherType, person)
        if (otherAddress) {
          response.status = 409
          respond([
            success        : false,
            message        : "An address of type '${otherType}' already exists!",
            existingAddress: otherAddress,
            newAddress     : newAddressMap
          ], formats: ['json'])
          return
        }
      } else {
        Address typeAddress = Address.findByAddressTypeAndPerson(addressType, person)
        if (typeAddress) {
          response.status = 409
          respond([
            success        : false,
            message        : "An address of type '${addressType}' already exists!",
            existingAddress: typeAddress,
            newAddress     : newAddressMap
          ], formats: ['json'])
          return
        }
      }

      // ── 3. Save ──────────────────────────────────────────────────
      Address address = new Address(
        street1: street1,
        street2: street2,
        city: city,
        district: district,
        state: state,
        country: country,
        postalCode: postalCode,
        addressType: addressType,
        otherType: otherType,
        person: person
      )

      if (address.validate() && address.save(flush: true)) {
        String redirectUrl = ac == 'an'
          ? createLink(controller: 'AdminDBoard', action: 'addPolicy',
          params: [id: bodyId, ac: ac, slot: "${request.getAttribute('currentSlot')}"])
          : createLink(action: 'editAddressDetails', params: [slot: "${request.getAttribute('currentSlot')}"])
        respond([
          success    : true,
          message    : "Address saved successfully!",
          redirectUrl: redirectUrl
        ], formats: ['json'])
      } else {
        response.status = 400
        respond([
          success: false,
          message: resolveErrors(address.errors)
        ], formats: ['json'])
      }

    } catch (Exception e) {
      log.error("Error saving address", e)
      response.status = 500
      respond([
        success: false,
        message: "Error saving address: ${e.message}"
      ], formats: ['json'])
    }
  }

  @Transactional
  @Secured(['ROLE_USER'])
  def replaceAddressForEditAdd() {
    def body = request.JSON
    def addressType = null
    def otherType = null

    try {
      Person person = request.getAttribute('currentPerson')

      // ── 1. Load the existing address to be demoted ───────────────
      Address existingAddress = Address.get(body?.existingId as Long)
      if (!existingAddress) {
        response.status = 404
        respond([
          success: false,
          message: "Existing address not found!"
        ], formats: ['json'])
        return
      }

      // ── 2. Verify ownership ──────────────────────────────────────
      if (existingAddress.person?.id != person.id) {
        response.status = 403
        respond([
          success: false,
          message: "You are not authorised to replace this address."
        ], formats: ['json'])
        return
      }

      // ── 3. Extract new address fields from the nested map ────────
      def n = body?.newAddress
      if (!n) {
        response.status = 400
        respond([
          success: false,
          message: "New address data is missing."
        ], formats: ['json'])
        return
      }

      String street1 = n?.street1?.trim() ?: ''
      String street2 = n?.street2?.trim() ?: ''
      String city = n?.city?.trim() ?: ''
      String district = n?.district?.trim() ?: ''
      String state = n?.state?.trim() ?: ''
      String country = n?.country?.trim() ?: ''
      String postalCode = n?.postalCode?.trim() ?: ''
      String newAddrType = n?.addressType?.trim() ?: ''
      String newOtherType = n?.otherType?.trim() ?: ''

      if ([street1, street2, city, district, state, country, postalCode, newAddrType].any { !it }) {
        response.status = 400
        respond([
          success: false,
          message: "New address is missing required fields."
        ], formats: ['json'])
        return
      }

      // ── 4. Capture the existing type label before mutation ───────
      addressType = existingAddress.addressType
      otherType = existingAddress.otherType

      // ── 5. Snapshot of old address for the response ──────────────
      Map oldAddressMap = [
        id         : existingAddress.id,
        street1    : existingAddress.street1,
        street2    : existingAddress.street2,
        city       : existingAddress.city,
        district   : existingAddress.district,
        state      : existingAddress.state,
        country    : existingAddress.country,
        postalCode : existingAddress.postalCode,
        addressType: existingAddress.addressType,
        otherType  : existingAddress.otherType
      ]

      // ── 6. Demote existing address to type "Other" ───────────────
      //       Street/location fields are preserved as-is.
      //       otherType is set to the old addressType label so it is
      //       still identifiable (e.g. "Permanent", "Present", etc.)
      existingAddress.addressType = 'Other'
      existingAddress.otherType = 'Other'

      if (!existingAddress.validate()) {
        response.status = 400
        respond([
          success: false,
          message: resolveErrors(existingAddress.errors)
        ], formats: ['json'])
        return
      }

      existingAddress.save(flush: false, failOnError: true)

      // ── 7. Save new address record with the requested type ───────
      Address newAddress = new Address(
        street1: street1,
        street2: street2,
        city: city,
        district: district,
        state: state,
        country: country,
        postalCode: postalCode,
        addressType: newAddrType,
        otherType: (newAddrType == 'Other' ? newOtherType : null),
        person: person
      )

      if (!newAddress.validate()) {
        response.status = 400
        respond([
          success: false,
          message: resolveErrors(newAddress.errors)
        ], formats: ['json'])
        return
      }

      newAddress.save(flush: true, failOnError: true)

      def label = (newAddrType == 'Other' ? newOtherType : newAddrType) ?: 'Unknown'
      respond([
        success    : true,
        message    : "${label} address saved successfully!",
        oldAddress : existingAddress,
        newAddress : newAddress,
        redirectUrl: createLink(action: 'editAddressDetails', params: [slot: "${request.getAttribute('currentSlot')}"])
      ], formats: ['json'])

    } catch (Exception e) {
      def label = (addressType == 'Other' ? otherType : addressType) ?: 'Unknown'
      log.error("Error replacing existing ${label} address", e)
      response.status = 500
      respond([
        success: false,
        message: "Error replacing existing ${label} address: ${e.message}"
      ], formats: ['json'])
    }
  }

  /**
   * POST  /person/saveContactDetailsForEditAdd
   *
   * Saves a new ContactDetails record for the current user.
   *
   * Expected JSON body:
   * {
   *   "isdCode"      : "+91",
   *   "country"      : "India",
   *   "flag"         : "🇮🇳",
   *   "mobileNumber" : "9876543210",
   *   "eMail"        : "user@example.com",
   *   "isPrimary"    : true,
   *   "id"           : 42,         // optional — used when ac == 'an'
   *   "ac"           : "an"        // optional — controls redirect target
   * }
   *
   * HTTP responses:
   *   200  — saved; body contains { success, message, redirectUrl }
   *   400  — validation failure or missing required fields
   *   409  — exact duplicate or primary-contact conflict;
   *           body contains { success, message, existingContact, newContact }
   *   500  — unexpected server error
   */
  @Transactional
  @Secured(['ROLE_USER'])
  def saveContactDetailsForEditAdd() {
    try {
      Person person = request.getAttribute('currentPerson')

      def body = request.JSON
      String isdCode = body?.isdCode?.trim() ?: ''
      String country = body?.country?.trim() ?: ''
      String flag = body?.flag?.trim() ?: ''
      String mobileNumber = body?.mobileNumber?.trim() ?: ''
      String eMail = body?.eMail?.trim() ?: ''
      Boolean isPrimary = body?.isPrimary as Boolean ?: false
      String ac = body?.ac?.trim() ?: ''
      def bodyId = body?.id

      // ── 0. Basic field validation ─────────────────────────────────
      if ([isdCode, country, flag, mobileNumber, eMail].any { !it }) {
        response.status = 400
        respond([
          success: false,
          message: "All required fields (isdCode, country, flag, mobileNumber, eMail) must be filled in."
        ], formats: ['json'])
        return
      }

      // ── Build the new contact map once — reused in all 409 responses ──
      // Using a plain map avoids Hibernate serialisation issues that occur
      // when an unsaved domain object is passed directly to respond().
      Map newContactMap = [
        isdCode     : isdCode,
        country     : country,
        flag        : flag,
        mobileNumber: mobileNumber,
        eMail       : eMail,
        isPrimary   : isPrimary
      ]

      // ── 1. Exact duplicate check (same isdCode + number + email + person) ──
      ContactDetails exactDuplicate = ContactDetails.findByIsdCodeAndCountryAndFlagAndMobileNumberAndEMailAndPerson(
        isdCode, country, flag, mobileNumber, eMail, person)

      if (exactDuplicate) {
        response.status = 409
        respond([
          success        : false,
          message        : "This contact detail already exists!",
          existingContact: exactDuplicate,
          newContact     : newContactMap
        ], formats: ['json'])
        return
      }

      // ── 2. Primary-contact uniqueness check (only one primary per person) ──
      if (isPrimary) {
        ContactDetails existingPrimary = ContactDetails.findByIsPrimaryAndPerson(true, person)
        if (existingPrimary) {
          response.status = 409
          respond([
            success        : false,
            message        : "A primary contact already exists. Replace it or uncheck 'Primary'.",
            existingContact: existingPrimary,
            newContact     : newContactMap
          ], formats: ['json'])
          return
        }
      }

      // ── 3. Save ──────────────────────────────────────────────────
      ContactDetails contact = new ContactDetails(
        isdCode: isdCode,
        country: country,
        flag: flag,
        mobileNumber: mobileNumber,
        eMail: eMail,
        isPrimary: isPrimary,
        person: person
      )

      if (contact.validate() && contact.save(flush: true)) {
        String redirectUrl = ac == 'an'
          ? createLink(controller: 'AdminDBoard', action: 'addPolicy',
          params: [id: bodyId, ac: ac, slot: "${request.getAttribute('currentSlot')}"])
          : createLink(action: 'editContactDetails', params: [slot: "${request.getAttribute('currentSlot')}"])
        respond([
          success    : true,
          message    : "Contact detail saved successfully!",
          redirectUrl: redirectUrl
        ], formats: ['json'])
      } else {
        response.status = 400
        respond([
          success: false,
          message: resolveErrors(contact.errors)
        ], formats: ['json'])
      }

    } catch (Exception e) {
      log.error("Error saving contact detail", e)
      response.status = 500
      respond([
        success: false,
        message: "Error saving contact detail: ${e.message}"
      ], formats: ['json'])
    }
  }

  /**
   * POST  /person/replaceContactDetailsForEditAdd
   *
   * Demotes an existing ContactDetails record (clears isPrimary) and saves a
   * new one in its place.  Mirrors replaceAddressForEditAdd() — the caller
   * sends this request after the user confirms the 409 conflict dialog.
   *
   * Expected JSON body:
   * {
   *   "existingId": 7,
   *   "newContact": {
   *     "isdCode"     : "+91",
   *     "country"     : "India",
   *     "flag"        : "🇮🇳",
   *     "mobileNumber": "9876543210",
   *     "eMail"       : "new@example.com",
   *     "isPrimary"   : true
   *   }
   * }
   *
   * HTTP responses:
   *   200  — replaced; body contains { success, message, oldContact, newContact, redirectUrl }
   *   400  — validation failure or missing required fields
   *   403  — caller does not own the existing record
   *   404  — existing record not found
   *   500  — unexpected server error
   */
  @Transactional
  @Secured(['ROLE_USER'])
  def replaceContactDetailsForEditAdd() {
    // Captured before try so the catch block can build a meaningful log message
    Boolean existingWasPrimary = null

    try {
      Person person = request.getAttribute('currentPerson')
      def body = request.JSON

      // ── 1. Load the existing contact to be demoted ───────────────
      ContactDetails existingContact = ContactDetails.get(body?.existingId as Long)
      if (!existingContact) {
        response.status = 404
        respond([
          success: false,
          message: "Existing contact detail not found!"
        ], formats: ['json'])
        return
      }

      // ── 2. Verify ownership ──────────────────────────────────────
      if (existingContact.person?.id != person.id) {
        response.status = 403
        respond([
          success: false,
          message: "You are not authorised to replace this contact detail."
        ], formats: ['json'])
        return
      }

      // ── 3. Extract new contact fields from the nested map ────────
      def n = body?.newContact
      if (!n) {
        response.status = 400
        respond([
          success: false,
          message: "New contact data is missing."
        ], formats: ['json'])
        return
      }

      String newIsdCode = n?.isdCode?.trim() ?: ''
      String newCountry = n?.country?.trim() ?: ''
      String newFlag = n?.flag?.trim() ?: ''
      String newMobileNumber = n?.mobileNumber?.trim() ?: ''
      String newEMail = n?.eMail?.trim() ?: ''
      Boolean newIsPrimary = n?.isPrimary as Boolean ?: false

      if ([newIsdCode, newCountry, newFlag, newMobileNumber, newEMail].any { !it }) {
        response.status = 400
        respond([
          success: false,
          message: "New contact detail is missing required fields."
        ], formats: ['json'])
        return
      }

      // ── 4. Capture existing state before mutation ────────────────
      existingWasPrimary = existingContact.isPrimary

      // ── 5. Snapshot of old contact for the response ──────────────
      // Taken now so the response reflects pre-mutation values.
      Map oldContactMap = [
        id          : existingContact.id,
        isdCode     : existingContact.isdCode,
        country     : existingContact.country,
        flag        : existingContact.flag,
        mobileNumber: existingContact.mobileNumber,
        eMail       : existingContact.eMail,
        isPrimary   : existingContact.isPrimary
      ]

      // ── 6. Demote the existing record — clear isPrimary ──────────
      // The phone number and email are intentionally preserved so the
      // record remains identifiable and auditable after demotion.
      existingContact.isPrimary = false

      if (!existingContact.validate()) {
        response.status = 400
        respond([
          success: false,
          message: resolveErrors(existingContact.errors)
        ], formats: ['json'])
        return
      }

      existingContact.save(flush: false, failOnError: true)

      // ── 7. Save the new contact record ───────────────────────────
      ContactDetails newContact = new ContactDetails(
        isdCode: newIsdCode,
        country: newCountry,
        flag: newFlag,
        mobileNumber: newMobileNumber,
        eMail: newEMail,
        isPrimary: newIsPrimary,
        person: person
      )

      if (!newContact.validate()) {
        response.status = 400
        respond([
          success: false,
          message: resolveErrors(newContact.errors)
        ], formats: ['json'])
        return
      }

      newContact.save(flush: true, failOnError: true)

      // ── 8. Respond ───────────────────────────────────────────────
      String label = newIsPrimary ? 'Primary' : 'New'
      respond([
        success    : true,
        message    : "${label} contact detail saved successfully!",
        oldContact : oldContactMap,
        newContact : newContact,
        redirectUrl: createLink(action: 'editContactDetails', params: [slot: "${request.getAttribute('currentSlot')}"])
      ], formats: ['json'])

    } catch (Exception e) {
      String ctx = existingWasPrimary != null
        ? (existingWasPrimary ? 'primary' : 'non-primary')
        : 'unknown'
      log.error("Error replacing existing ${ctx} contact detail", e)
      response.status = 500
      respond([
        success: false,
        message: "Error replacing existing ${ctx} contact detail: ${e.message}"
      ], formats: ['json'])
    }
  }


  @Transactional
  @Secured(['ROLE_USER'])
  def saveTicket() {
    try {
      Person person = request.getAttribute('currentPerson')

      def body = request.JSON
      Ticket ticket = new Ticket(ticketId: "Tkt${new Date().getTime()}", subject: body?.subject, message: body?.message, ticketType: null)
      ticket.createdBy = person
      ticket.createdAt = LocalDateTime.now()

      if (ticket.validate() && ticket.save(flush: true)) {
        respond([success: true, message: "Ticket '${ticket?.subject}' created successfully!", redirectUrl: createLink(controller: 'DBoard', action: 'index', params: [slot: "${request.getAttribute('currentSlot')}"])], formats: ['json'])
      } else {
        response.status = 400
        respond([success: false, message: resolveErrors(ticket.errors)], formats: ['json'])
      }

    } catch (Exception e) {
      log.error("Error saving ticket", e)
      response.status = 500
      respond([success: false, message: "Error creating ticket: ${e.message}"], formats: ['json'])
    }
  }

  @Transactional
  @Secured(['ROLE_USER'])
  def saveDummyTicket() {
    try {
      Person person = request.getAttribute('currentPerson')

      def body = request.JSON
      Ticket ticket = new Ticket(ticketId: "Tkt${new Date().getTime()}", subject: body?.subject, message: body?.message, ticketType: null)
      ticket.createdBy = person
      ticket.createdAt = LocalDateTime.now()

      if (ticket.validate() && ticket.save(flush: true)) {
        respond([success: true, message: "Ticket '${ticket?.subject}' created successfully!", redirectUrl: createLink(controller: 'DBoard', action: 'index', params: [slot: "${request.getAttribute('currentSlot')}"])], formats: ['json'])
      } else {
        response.status = 400
        respond([success: false, message: resolveErrors(ticket.errors)], formats: ['json'])
      }

    } catch (Exception e) {
      log.error("Error saving dummy ticket", e)
      response.status = 500
      respond([success: false, message: "Error creating ticket: ${e.message}"], formats: ['json'])
    }
  }

  @Transactional
  @Secured(['ROLE_USER'])
  def saveTicketForAdd() {
    try {
      Person person = request.getAttribute('currentPerson')

      def body = request.JSON
      TicketType ticketType = TicketType.findById(body?.id?.toLong())
      Ticket ticket = new Ticket(ticketId: "Tkt${new Date().getTime()}", subject: body?.subject, message: body?.message, ticketType: ticketType)
      ticket.createdBy = person
      ticket.createdAt = LocalDateTime.now()

      if (ticket.validate() && ticket.save(flush: true)) {
        respond([success: true, message: "Ticket '${ticket?.subject}' created successfully!", redirectUrl: createLink(controller: 'DBoard', action: 'index', params: [slot: "${request.getAttribute('currentSlot')}"])], formats: ['json'])
      } else {
        response.status = 400
        respond([success: false, message: resolveErrors(ticket.errors)], formats: ['json'])
      }

    } catch (Exception e) {
      log.error("Error saving ticket for add", e)
      response.status = 500
      respond([success: false, message: "Error creating ticket: ${e.message}"], formats: ['json'])
    }
  }


  @Transactional
  @Secured(['ROLE_USER'])
  def updatePersonDetails() {
    try {
      Person person = request.getAttribute('currentPerson')

      if (!person?.personDetails) {
        transactionStatus.setRollbackOnly()
        respond([success: false, message: 'Personal details not found.'],
          status: 404, formats: ['json'])
        return
      }

      def body = request.JSON

      // Only the editable fields; read-only ones must not be client-writable
      bindData(person.personDetails, body, [include: ['smokingHabit', 'drinkingHabit']])

      if (person.validate() && person.save(flush: true)) {
        respond([
          success    : true,
          message    : 'Personal details updated successfully!',
          redirectUrl: createLink(
            action: person.spouseDetails ? 'editSpouseDetails' : 'index',
            params: [slot: request.getAttribute('currentSlot')]
          )
        ], status: 200, formats: ['json'])
      } else {
        transactionStatus.setRollbackOnly()
        respond([success: false, message: resolveErrors(person.errors)],
          status: 400, formats: ['json'])
      }

    } catch (Exception e) {
      log.error('Error updating personal details', e)
      transactionStatus.setRollbackOnly()
      respond([success: false, message: "Error updating personal details: ${e.message}"],
        status: 500, formats: ['json'])
    }
  }

  @Transactional
  @Secured(['ROLE_USER'])
  def updateSpouseDetails() {
    try {
      Person person = request.getAttribute('currentPerson')

      if (!person?.spouseDetails) {
        transactionStatus.setRollbackOnly()
        respond([success: false, message: 'Spouse details not found.'],
          status: 404, formats: ['json'])
        return
      }

      def body = request.JSON

      bindData(person?.spouseDetails, body, [include: ['minAge', 'maxAge', 'religion', 'caste', 'smokingHabit', 'drinkingHabit']])

      person?.spouseDetails?.gender = (person?.personDetails?.gender == 'Female') ? 'Male' : 'Female'

      if (person.validate() && person.save(flush: true)) {
        respond([success    : true, message: "Spouse details updated successfully!",
                 redirectUrl: createLink(action: person?.familyDetails ? 'editFamilyDetails' : 'index', params: [slot: "${request.getAttribute('currentSlot')}"])], formats: ['json'])
      } else {
        response.status = 400
        respond([success: false, message: resolveErrors(person.errors)], formats: ['json'])
      }

    } catch (Exception e) {
      log.error("Error updating spouse details", e)
      response.status = 500
      respond([success: false, message: "Error updating spouse details: ${e.message}"], formats: ['json'])
    }
  }

  @Transactional
  @Secured(['ROLE_USER'])
  def updateFamilyDetails() {
    try {
      Person person = request.getAttribute('currentPerson')

      if (!person?.familyDetails) {
        transactionStatus.setRollbackOnly()
        respond([success: false, message: 'Family details not found.'],
          status: 404, formats: ['json'])
        return
      }

      def body = request.JSON
      // Only the editable fields; read-only ones must not be client-writable
      bindData(person.familyDetails, body, [include: ['height', 'weight', 'familyStatus', 'familyType', 'familyValues', 'disabilities']])

      if (person.validate() && person.save(flush: true)) {
        respond([success    : true, message: "Family details updated successfully!",
                 redirectUrl: createLink(action: person?.spouseFamilyDetails ? 'editSpouseFamilyDetails' : 'index', params: [slot: "${request.getAttribute('currentSlot')}"])], formats: ['json'])
      } else {
        response.status = 400
        respond([success: false, message: resolveErrors(person.errors)], formats: ['json'])
      }

    } catch (Exception e) {
      log.error("Error updating family details", e)
      response.status = 500
      respond([success: false, message: "Error updating family details: ${e.message}"], formats: ['json'])
    }
  }

  @Transactional
  @Secured(['ROLE_USER'])
  def updateSpouseFamilyDetails() {
    try {
      Person person = request.getAttribute('currentPerson')

      if (!person?.spouseFamilyDetails) {
        transactionStatus.setRollbackOnly()
        respond([success: false, message: 'Spouse family details not found.'],
          status: 404, formats: ['json'])
        return
      }

      def body = request.JSON
      person?.spouseFamilyDetails?.properties = body

      if (person.validate() && person.save(flush: true)) {
        respond([success    : true, message: "Spouse family details updated successfully!",
                 redirectUrl: createLink(action: person?.contactDetails ? 'editContactDetails' : 'index', params: [slot: "${request.getAttribute('currentSlot')}"])], formats: ['json'])
      } else {
        response.status = 400
        respond([success: false, message: resolveErrors(person.errors)], formats: ['json'])
      }

    } catch (Exception e) {
      log.error("Error updating spouse family details", e)
      response.status = 500
      respond([success: false, message: "Error updating spouse family details: ${e.message}"], formats: ['json'])
    }
  }

  @Transactional
  @Secured(['ROLE_USER'])
  def updateContactDetails() {
    try {
      Person person = request.getAttribute('currentPerson')

      if (!person?.contactDetails) {
        transactionStatus.setRollbackOnly()
        respond([success: false, message: 'Contact details not found!'],
          status: 404, formats: ['json'])
        return
      }
      def body = request.JSON

      def contactId = body?.id.trim()

      ContactDetails contactDetails = ContactDetails.get(contactId)
      def primaryContact = ContactDetails.findByIsPrimaryAndPerson(true, person)

      if (body?.isPrimary && primaryContact != null && contactDetails?.id != primaryContact?.id) {
        ContactDetails returnContactDetails = new ContactDetails(body)
        returnContactDetails.id = contactDetails?.id
        response.status = 409
        respond([success: false, message: "Primary contact already exists.", newContact: returnContactDetails, contact: primaryContact], formats: ['json'])
        return
      }

      contactDetails.properties = body
      // contactDetails.isPrimary = (params?.isPrimary == "on")

      if (contactDetails.validate() && contactDetails.save(flush: true)) {
        respond([success: true, message: "Contact details updated successfully!", redirectUrl: createLink(action: 'editContactDetails', params: [slot: "${request.getAttribute('currentSlot')}"])], formats: ['json'])
      } else {
        response.status = 400
        respond([success: false, message: resolveErrors(contactDetails.errors)], formats: ['json'])
      }

    } catch (Exception e) {
      log.error("Error updating contact details", e)
      response.status = 500
      respond([success: false, message: "Error updating contact details: ${e.message}"], formats: ['json'])
    }
  }

  @Transactional
  @Secured(['ROLE_USER'])
  def replaceContact() {
    def body = request.JSON
    try {
      ContactDetails existingContactDetails = ContactDetails.get(body?.existingId)

      if (!existingContactDetails) {
        response.status = 404
        respond([success: false, message: "Old contact details not found!", redirectUrl: createLink(action: 'editContactDetails', params: [slot: "${request.getAttribute('currentSlot')}"])], formats: ['json'])
        return
      }


      ContactDetails newPrimaryContact = new ContactDetails(body.newContact)
      newPrimaryContact.person = request.getAttribute('currentPerson')

      // ── Optionally bind any extra fields sent in body.newContact ────
      // Only do this if you want the client to update fields beyond isPrimary.
      // Whitelist the fields you trust — never bind blindly.
//      if (body?.newContact) {
//        bindData(newPrimaryContact, body.newContact, [include: ['isdCode', 'country', 'flag', 'mobileNumber', 'eMail', 'isPrimary']])
//      }

      // ── Mutate existingContactDetails object ───────────────────────────────────────────
      existingContactDetails.isPrimary = false

      // ── Validate both before touching the DB ─────────────────────────
      boolean existingValid = existingContactDetails.validate()
      boolean newValid = newPrimaryContact.validate()

      if (!existingValid) {
        response.status = 400
        respond([success: false, message: resolveErrors(existingContactDetails.errors)], formats: ['json'])
        return
      }

      if (!newValid) {
        response.status = 400
        respond([success: false, message: resolveErrors(newPrimaryContact.errors)], formats: ['json'])
        return
      }

// ── Both valid — save together inside the same transaction ────────
      existingContactDetails.save(flush: false, failOnError: true)
      newPrimaryContact.save(flush: true, failOnError: true)

      respond([
        success    : true,
        message    : "Primary contact replaced successfully!",
        oldContact : existingContactDetails,
        newContact : newPrimaryContact,
        redirectUrl: createLink(action: 'editContactDetails', params: [slot: "${request.getAttribute('currentSlot')}"])
      ], formats: ['json'])

    } catch (Exception e) {
      log.error("Error replacing primary contact", e)
      response.status = 500
      respond([success: false, message: "Error replacing primary contact: ${e.message}"], formats: ['json'])
    }
  }

  @Transactional
  @Secured(['ROLE_USER'])
  def updateEducationDetails() {
    try {
      Person person = request.getAttribute('currentPerson')

      if (!person?.educationDetails) {
        transactionStatus.setRollbackOnly()
        respond([success: false, message: 'Education details not found!'],
          status: 404, formats: ['json'])
        return
      }

      def body = request.JSON

      EducationDetails educationDetails = EducationDetails.get(body?.id as Long)

      if (body?.isHighest) {
        def highestEducation = EducationDetails.findByIsHighestAndPerson(true, person)
        if (highestEducation && highestEducation?.id != educationDetails?.id) {
          EducationDetails newEducationDetails = new EducationDetails(body)
          newEducationDetails.id = educationDetails.id
          response.status = 409
          respond([success: false, message: "Highest education already exists!", newEducation: newEducationDetails, existingEducation: highestEducation], formats: ['json'])
          return
        }
      }

      educationDetails.properties = body

      if (educationDetails.validate() && educationDetails.save(flush: true)) {
        respond([success: true, message: "Education details updated successfully!", redirectUrl: createLink(action: 'editEducationDetails', params: [slot: "${request.getAttribute('currentSlot')}"])], formats: ['json'])
      } else {
        response.status = 400
        respond([success: false, message: resolveErrors(educationDetails.errors)], formats: ['json'])
      }

    } catch (Exception e) {
      log.error("Error updating education details", e)
      response.status = 500
      respond([success: false, message: "Error updating education details: ${e.message}"], formats: ['json'])
    }
  }

  @Transactional
  @Secured(['ROLE_USER'])
  def replaceEducation() {
    def body = request.JSON

    try {
      EducationDetails existingEducationDetails = EducationDetails.get(body?.existingId as Long)
      if (!existingEducationDetails) {
        response.status = 404
        respond([success    : false, message: "Old education details not found!",
                 redirectUrl: createLink(action: 'editEducationDetails', params: [slot: "${request.getAttribute('currentSlot')}"])], formats: ['json'])
        return
      }

      EducationDetails newEducation = EducationDetails.get(body?.newEducation?.id as Long)
      if (!newEducation) {
        response.status = 404
        respond([success    : false, message: "New education details not found!",
                 redirectUrl: createLink(action: 'editEducationDetails', params: [slot: "${request.getAttribute('currentSlot')}"])], formats: ['json'])
        return
      }

      if (body?.newEducation) {
        bindData(newEducation, body.newEducation, [include: ['name', 'institution', 'university', 'institutionLocation', 'universityLocation']])
      }


      // ── Validate before touching DB ────────────────────────────────
      if (!existingEducationDetails.validate()) {
        response.status = 400
        respond([success: false, message: resolveErrors(existingEducationDetails.errors)], formats: ['json'])
        return
      }
      if (!newEducation.validate()) {
        response.status = 400
        respond([success: false, message: resolveErrors(newEducation.errors)], formats: ['json'])
        return
      }

      existingEducationDetails.isHighest = false
      newEducation.isHighest = true

      // ── Save both in the same transaction ──────────────────────────
      existingEducationDetails.save(flush: false, failOnError: true)
      newEducation.save(flush: true, failOnError: true)

      respond([
        success     : true,
        message     : "Existing highest education detail replaced successfully!",
        oldEducation: existingEducationDetails,
        newEducation: newEducation,
        redirectUrl : createLink(action: 'editEducationDetails', params: [slot: "${request.getAttribute('currentSlot')}"])
      ], formats: ['json'])

    } catch (Exception e) {
      response.status = 500
      log.error("Error replacing existing highest education detail", e)
      respond([success: false, message: "Error replacing existing highest education detail: ${e.message}"],
        formats: ['json'])
    }
  }

  @Transactional
  @Secured(['ROLE_USER'])
  def updateProfessionDetails() {
    try {
      Person person = request.getAttribute('currentPerson')

      if (!person?.professionDetails) {
        transactionStatus.setRollbackOnly()
        respond([success: false, message: 'Profession details not found!'],
          status: 404, formats: ['json'])
        return
      }

      def body = request.JSON
      ProfessionalDetails profession = ProfessionalDetails.get(body?.id as Long)

      if (body?.isCurrent) {
        ProfessionalDetails currentJob = ProfessionalDetails.findByIsCurrentAndPerson(true, person)
        if (currentJob && profession?.id != currentJob?.id) {
          ProfessionalDetails newProfession = new ProfessionalDetails(body)
          newProfession.id = profession?.id

          response.status = 409
          respond([success: false, message: "Current job already exists!", newProfession: newProfession, existingProfession: currentJob], formats: ['json'])
          return
        }
      }

      profession.properties = body

      if (profession.validate() && profession.save(flush: true)) {
        respond([success: true, message: "Profession details updated successfully!", redirectUrl: createLink(action: 'editProfessionDetails', params: [slot: "${request.getAttribute('currentSlot')}"])], formats: ['json'])
      } else {
        response.status = 400
        respond([success: false, message: resolveErrors(profession.errors)], formats: ['json'])
      }

    } catch (Exception e) {
      log.error("Error updating profession details", e)
      response.status = 500
      respond([success: false, message: "Error updating profession details: ${e.message}"], formats: ['json'])
    }
  }

  @Transactional
  @Secured(['ROLE_USER'])
  def replaceProfession() {
    def body = request.JSON

    try {
      ProfessionalDetails existingProfessionDetails = ProfessionalDetails.get(body?.existingId as Long)

      if (!existingProfessionDetails) {
        response.status = 404
        respond([success: false, message: "Old profession details not found!", redirectUrl: createLink(action: 'editProfessionDetails', params: [slot: "${request.getAttribute('currentSlot')}"])], formats: ['json'])
        return
      }

      ProfessionalDetails newCurrentJob = ProfessionalDetails.get(body?.newCurrentJob?.id as Long)

      if (!newCurrentJob) {
        response.status = 404
        respond([success: false, message: "New profession details not found!", redirectUrl: createLink(action: 'editProfessionDetails', params: [slot: "${request.getAttribute('currentSlot')}"])], formats: ['json'])
        return
      }

      // ── Optionally bind any extra fields sent in body.newContact ────
      // Only do this if you want the client to update fields beyond isCurrent.
      // Whitelist the fields you trust — never bind blindly.
      if (body?.newCurrentJob) {
        bindData(newCurrentJob, body.newCurrentJob, [include: ['designation', 'organisation', 'organisationLocation', 'isCurrent', 'jobCategory', 'currencyCode', 'denomination', 'country', 'salary', 'salaryFor']])
      }

      // ── Mutate existingProfessionDetails object ───────────────────────────────────────────
      existingProfessionDetails.isCurrent = false

      // ── Validate both before touching the DB ─────────────────────────
      boolean existingValid = existingProfessionDetails.validate()
      boolean newValid = newCurrentJob.validate()

      if (!existingValid) {
        response.status = 400
        respond([success: false, message: resolveErrors(existingProfessionDetails.errors)], formats: ['json'])
        return
      }

      if (!newValid) {
        response.status = 400
        respond([success: false, message: resolveErrors(newCurrentJob.errors)], formats: ['json'])
        return
      }

// ── Both valid — save together inside the same transaction ────────
      existingProfessionDetails.save(flush: false, failOnError: true)
      newCurrentJob.save(flush: true, failOnError: true)

      respond([
        success      : true,
        message      : "Current job replaced successfully!",
        oldProfession: existingProfessionDetails,
        newProfession: newCurrentJob,
        redirectUrl  : createLink(action: 'editProfessionDetails', params: [slot: "${request.getAttribute('currentSlot')}"])
      ], formats: ['json'])

    } catch (Exception e) {
      log.error("Error replacing current job", e)
      response.status = 500
      respond([success: false, message: "Error replacing current job: ${e.message}"], formats: ['json'])
    }
  }


  @Transactional
  @Secured(['ROLE_USER'])
  def updateAddressDetails() {
    try {
      Person person = request.getAttribute('currentPerson')

      if (!person?.address) {
        transactionStatus.setRollbackOnly()
        respond([success: false, message: 'Address details not found!'],
          status: 404, formats: ['json'])
        return
      }

      def body = request?.JSON
      def addressType = body?.addressType.trim()
      def otherType = body?.otherType.trim()

      Address address = Address.get(body?.id.trim())

      if (addressType) {
        Address existing = Address.findByAddressTypeAndPerson(addressType, person)
        if (existing && address?.id != existing.id) {
          Address newAddress = new Address(body)
          newAddress.id = address?.id
          response.status = 409
          respond([success: false, message: "Address of type ${addressType} already exists!", newAddress: newAddress, address: existing], formats: ['json'])
          return
        }
      } else if (otherType) {
        Address existing = Address.findByOtherTypeAndPerson(otherType, person)
        if (existing) {
          Address newAddress = new Address(body)
          newAddress.id = address?.id
          response.status = 409
          respond([success: false, message: "Address of type ${otherType} already exists!", newAddress: newAddress, address: existing], formats: ['json'])
          return
        }
      }

      address.properties = body

      if (address.validate() && address.save(flush: true)) {
        respond([success: true, message: "Address updated successfully!", redirectUrl: createLink(action: 'editAddressDetails', params: [slot: "${request.getAttribute('currentSlot')}"])], formats: ['json'])
      } else {
        response.status = 400
        respond([success: false, message: resolveErrors(address.errors)], formats: ['json'])
      }

    } catch (Exception e) {
      log.error("Error updating address details", e)
      response.status = 500
      respond([success: false, message: "Error updating address: ${e.message}"], formats: ['json'])
    }
  }

  @Transactional
  @Secured(['ROLE_USER'])
  def replaceAddress() {
    def body = request.JSON

    // ── Declared before try so catch can reference them ──────────────
    def addressType = null
    def otherType = null

    try {
      Address existingAddressDetails = Address.get(body?.existingId as Long)
      if (!existingAddressDetails) {
        response.status = 404
        respond([success    : false, message: "Old address details not found!",
                 redirectUrl: createLink(action: 'editAddressDetails', params: [slot: "${request.getAttribute('currentSlot')}"])], formats: ['json'])
        return
      }

      Address newAddress = Address.get(body?.newAddress?.id as Long)
      if (!newAddress) {
        response.status = 404
        respond([success    : false, message: "New address details not found!",
                 redirectUrl: createLink(action: 'editAddressDetails', params: [slot: "${request.getAttribute('currentSlot')}"])], formats: ['json'])
        return
      }

      addressType = existingAddressDetails.addressType
      otherType = existingAddressDetails.otherType


      // ── Optionally bind any extra fields sent in body.newAddress ────
      // Only do this if you want the client to update fields beyond addressType, otherType.
      // Whitelist the fields you trust — never bind blindly.
      if (body?.newAddress) {
        bindData(newAddress, body.newAddress, [include: ['street1', 'street2', 'city', 'district', 'state', 'country', 'postalCode']])
      }

      // ── Mutate both objects ─────────────────────────────────────────
      newAddress.addressType = addressType
      newAddress.otherType = otherType
      existingAddressDetails.addressType = "Other"
      existingAddressDetails.otherType = "Other"

      // ── Validate before touching DB ────────────────────────────────
      if (!existingAddressDetails.validate()) {
        response.status = 400
        respond([success: false, message: resolveErrors(existingAddressDetails.errors)], formats: ['json'])
        return
      }
      if (!newAddress.validate()) {
        response.status = 400
        respond([success: false, message: resolveErrors(newAddress.errors)], formats: ['json'])
        return
      }

      // ── Save both in the same transaction ──────────────────────────
      existingAddressDetails.save(flush: false, failOnError: true)
      newAddress.save(flush: true, failOnError: true)

      // ── Single respond with correct label ──────────────────────────
      def label = (addressType == 'Other' ? otherType : addressType) ?: 'Unknown'
      respond([
        success    : true,
        message    : "Existing ${label} address replaced successfully!",
        oldAddress : existingAddressDetails,
        newAddress : newAddress,
        redirectUrl: createLink(action: 'editAddressDetails', params: [slot: "${request.getAttribute('currentSlot')}"])
      ], formats: ['json'])

    } catch (Exception e) {
      response.status = 500
      def label = (addressType == 'Other' ? otherType : addressType) ?: 'Unknown'
      log.error("Error replacing existing ${label} address", e)
      respond([success: false, message: "Error replacing existing ${label} address: ${e.message}"],
        formats: ['json'])
    }
  }

  @Transactional
  @Secured(['ROLE_USER'])
  def saveAdditionalSpouseDetails() {
    try {
      Person person = request.getAttribute('currentPerson')

      if (person.additionalSpouseDetails) {
        response.status = 409
        respond([success: false, message: "Additional spouse details already exists!"], formats: ['json'])
        return
      }

      def body = request.JSON
      AdditionalSpouseDetails additionalSpouseDetails = new AdditionalSpouseDetails(body)
      additionalSpouseDetails.person = person

      if (additionalSpouseDetails.validate() && additionalSpouseDetails.save(flush: true)) {
        respond([success: true, message: "Additional spouse details saved successfully!", redirectUrl: createLink(action: 'index', params: [slot: "${request.getAttribute('currentSlot')}"])], formats: ['json'])
      } else {
        response.status = 400
        respond([success: false, message: resolveErrors(additionalSpouseDetails.errors)], formats: ['json'])
      }

    } catch (Exception e) {
      log.error("Error saving additional spouse details", e)
      response.status = 500
      respond([success: false, message: "Error saving additional spouse details: ${e.message}"], formats: ['json'])
    }
  }

  @Transactional
  @Secured(['ROLE_USER'])
  def updateAdditionalSpouseDetails() {
    try {
      Person person = request.getAttribute('currentPerson')

      if (!person?.additionalSpouseDetails) {
        transactionStatus.setRollbackOnly()
        respond([success: false, message: 'Additional spouse details not found.'],
          status: 404, formats: ['json'])
        return
      }

      AdditionalSpouseDetails additionalSpouseDetails = person?.additionalSpouseDetails

      def body = request.JSON
      additionalSpouseDetails.properties = body

      if (additionalSpouseDetails.validate() && additionalSpouseDetails.save(flush: true)) {
        respond([success: true, message: "Additional spouse details updated successfully!", redirectUrl: createLink(action: 'index', params: [slot: "${request.getAttribute('currentSlot')}"])], formats: ['json'])
      } else {
        response.status = 400
        respond([success: false, message: resolveErrors(additionalSpouseDetails.errors)], formats: ['json'])
      }

    } catch (Exception e) {
      log.error("Error updating additional spouse details", e)
      response.status = 500
      respond([success: false, message: "Error updating additional spouse details: ${e.message}"], formats: ['json'])
    }
  }

  // ── Helpers ───────────────────────────────────────────────────────

  BigDecimal getCurrentAge(String dob) {
    if (!dob) return null
    LocalDateTime dobDate = LocalDateTime.parse(dob)
    def now = LocalDateTime.now()
    def period = Period.between(dobDate.toLocalDate(), now.toLocalDate())
    return (period.years + (period.months / 12.0) + (period.days / 365.25)).round(2)
  }

  private String resolveErrors(errors) {
    Locale locale = RequestContextUtils.getLocale(request)

    errors.allErrors
      .collect { messageSource.getMessage(it, locale) }
      .join(', ')
  }

}
