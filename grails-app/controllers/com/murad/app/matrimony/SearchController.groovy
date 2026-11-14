package com.murad.app.matrimony

import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.annotation.Secured
import grails.validation.ValidationException

class SearchController {

  def searchService
  def religionService
  def heightService
  def weightService
  def additionalSpouseDetailsService
  def countriesService

  @Secured(['ROLE_USER'])
  def index() {
    Person person = Person.get(request.getAttribute("currentPerson") as Long)
    def religions = religionService.getReligionsWithCaste()

    // Ensure we have spouseDetails, create empty one if null
    def spouseDetails = person?.spouseDetails
    if (!spouseDetails) {
      // You might want to create a default SpouseDetails object
      // or handle this case based on your business logic
      log.warn("No spouse details found for person: ${person?.id}")
    }

    def spouseFamilyPreferences = person?.spouseFamilyDetails
    if (!spouseFamilyPreferences) {
      // You might want to create a default SpouseDetails object
      // or handle this case based on your business logic
      log.warn("No spouse family preferences found for person: ${person?.id}")
    }

    def countries = countriesService.getCountries()

    return [spouseDetails          : spouseDetails,
            spouseFamilyPreferences: spouseFamilyPreferences,
            additionalSpouseDetails: person?.additionalSpouseDetails,
            heights                : heightService.getHeights(),
            weights                : weightService.getHeights(),
            religions              : religions,
            educations             : additionalSpouseDetailsService.getEducations(),
            countries              : countries]
  }

  @Secured(['ROLE_USER'])
  def search() {
    def searchResult = searchService.search(params)

    // If you need to pass spouseDetails and religions to the search view as well
//    Person person = Person.get(session.personId)
    def religions = religionService.getReligionsWithCaste()
//    def spouseDetails = person?.spouseDetails

    render(view: 'search', model: [
      searchResult: searchResult,
//      spouseDetails: spouseDetails,
      religions   : religions
    ])
  }

  def searchResults() {
    // Implementation for search results if needed
  }

  @Secured(['ROLE_USER'])
  @Transactional
  def saveSpouseDetails() {
    try {
      Person person = request?.person

      // Find or create spouse details
      SpouseDetails spouseDetails = person.spouseDetails ?: new SpouseDetails()

      // Bind parameters efficiently
      bindData(spouseDetails, params, [include: ['minAge', 'maxAge', 'religion', 'caste', 'smokingHabit', 'drinkingHabit']])

      // Set gender based on person's gender
      spouseDetails.gender = person?.personDetails?.gender == 'Female' ? 'Male' : 'Female'

      // Set the bidirectional relationship
      spouseDetails.person = person
      person.spouseDetails = spouseDetails

      // Normalize null/empty values
      ['religion', 'caste', 'smokingHabit', 'drinkingHabit'].each { field ->
        if (!spouseDetails[field]) {
          spouseDetails[field] = ''
        }
      }

      // Save the person (cascade will save spouseDetails)
      if (!person.validate()) {
        def errorMessages = person.errors.allErrors.collect { message(error: it) }.join(', ')
        response.status = 400
        render(contentType: 'application/json') {
          [success: false, message: "Validation failed", errors: errorMessages]
        }
        return
      }

      person.save(flush: true, failOnError: true)

      flash.message = "Spouse details added successfully!"
      session.personId = person.id

      render(contentType: 'application/json') {
        [success: true, message: flash.message]
      }

    } catch (ValidationException e) {
      def errorMessages = e.errors.allErrors.collect { message(error: it) }.join(', ')
      response.status = 400
      render(contentType: 'application/json') {
        [success: false, message: "Failed to add spouse details", errors: errorMessages]
      }
    } catch (Exception e) {
      log.error("Error saving spouse details", e)
      response.status = 500
      render(contentType: 'application/json') {
        [success: false, message: "An unexpected error occurred: ${e.message}"]
      }
    }
  }

  @Secured(['ROLE_USER'])
  @Transactional
  def saveSpouseFamilyDetails() {
    try {
      Person person = request?.person

      // Find or create spouse family details
      SpouseFamilyDetails spouseFamilyDetails = person?.spouseFamilyDetails ?: new SpouseFamilyDetails()

      // List of fields to bind
      def fields = ['maritalStatus', 'children', 'childrenStatus', 'height', 'weight',
                    'familyStatus', 'familyType', 'familyValues', 'disabilities']

      // Bind parameters efficiently using bindData
      bindData(spouseFamilyDetails, params, [include: fields])

      // Normalize null/empty values to empty strings
      fields.each { field ->
        if (!spouseFamilyDetails[field]) {
          spouseFamilyDetails[field] = ''
        }
      }

      // Set bidirectional relationship
      spouseFamilyDetails.person = person
      person.spouseFamilyDetails = spouseFamilyDetails

      // Validate before saving
      if (!person.validate()) {
        def errorMessages = person.errors.allErrors.collect { message(error: it) }.join(', ')
        response.status = 400
        render(contentType: 'application/json') {
          [success: false, message: "Validation failed", errors: errorMessages]
        }
        return
      }

      // Save the person (cascade will save spouseFamilyDetails)
      person.save(flush: true, failOnError: true)

      flash.message = "Spouse family details added successfully!"
      session.personId = person.id

      render(contentType: 'application/json') {
        [success: true, message: flash.message]
      }
    } catch (ValidationException e) {
      def errorMessages = e.errors.allErrors.collect { message(error: it) }.join(', ')
      response.status = 400
      render(contentType: 'application/json') {
        [success: false, message: "Failed to add spouse family details", errors: errorMessages]
      }
    } catch (Exception e) {
      log.error("Error saving spouse family details", e)
      response.status = 500
      render(contentType: 'application/json') {
        [success: false, message: "An unexpected error occurred: ${e.message}"]
      }
    }
  }

  @Secured(['ROLE_USER'])
  @Transactional
  def saveAdditionalSpouseDetails() {
    try {
      Person person = request?.person

      // Find or create additional spouse details
      AdditionalSpouseDetails additionalSpouseDetails = person?.additionalSpouseDetails ?: new AdditionalSpouseDetails()

      // List of fields to bind
      def fields = ['education', 'profession', 'salary', 'salaryFor', 'workLocation', 'workingCountry']

      // Bind parameters efficiently using bindData
      bindData(additionalSpouseDetails, params, [include: fields])

      // Normalize null/empty values to empty strings
      fields.each { field ->
        if (!additionalSpouseDetails[field]) {
          additionalSpouseDetails[field] = ''
        }
      }

      // Set bidirectional relationship
      additionalSpouseDetails.person = person
      person.additionalSpouseDetails = additionalSpouseDetails

      // Validate before saving
      if (!person.validate()) {
        def errorMessages = person.errors.allErrors.collect { message(error: it) }.join(', ')
        response.status = 400
        render(contentType: 'application/json') {
          [success: false, message: "Validation failed", errors: errorMessages]
        }
        return
      }

      // Save the person (cascade will save additionalSpouseDetails)
      person.save(flush: true, failOnError: true)

      flash.message = "Additional spouse details added successfully!"
      session.personId = person.id

      render(contentType: 'application/json') {
        [success: true, message: flash.message]
      }
    } catch (ValidationException e) {
      def errorMessages = e.errors.allErrors.collect { message(error: it) }.join(', ')
      response.status = 400
      render(contentType: 'application/json') {
        [success: false, message: "Failed to add additional spouse details", errors: errorMessages]
      }
    } catch (Exception e) {
      log.error("Error saving additional spouse details", e)
      response.status = 500
      render(contentType: 'application/json') {
        [success: false, message: "An unexpected error occurred: ${e.message}"]
      }
    }
  }
}
