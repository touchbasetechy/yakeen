package com.murad.app.matrimony


import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.annotation.Secured
import grails.validation.ValidationException

import java.time.LocalDateTime

class HobbyController {

  @Secured('ROLE_ADMIN')
  def addHobby() {}

  @Transactional
  def saveHobby() {
    def createdAt = LocalDateTime.now()
    def hobbyToSave = new Hobby(name: params?.name, description: params?.description, createdBy: session?.person, createdAt: createdAt)
    try {
      def hobbySaved = hobbyToSave.save(flush: true)
      if (hobbySaved) {
        flash.message = "Hobby with name '${hobbySaved?.name}' created successfully!"
        if (params?.ac == 'an') {
          forward(controller: 'AdminDBoard', action: 'index', params: [ac: params?.ac])
        } else if (params?.ac == 'l') {
          forward(controller: 'Hobby', action: 'listHobbiesForEdit', params: [ac: params?.ac])
        } else {
          forward(controller: 'Person', action: 'index', params: [ac: params?.ac])
        }
      } else {
        flash.error = "Failed creating Hobby!"
        forward(controller: 'Hobby', action: 'addHobby', params: [ac: params?.ac])
      }
    } catch (ValidationException e) {
      flash.error = "Hobby not created: ${e.errors}"
      redirect(controller: 'Hobby', action: 'addHobby', params: [ac: params?.ac])
    }
  }

  def listHobbiesForEdit() {
    params.offset = params?.offset ? params.int('offset') : 0
    params.max = params?.max ? params.int('max') : 10

    def hobbies = Hobby.list(params)

    [hobbies: hobbies]
  }

  def listMoreHobbies() {
    params.offset = params?.offset ? params.int('offset') : 0
    params.max = params?.max ? params.int('max') : 10

    Person person = request.getAttribute('currentPerson')

    def associatedHobbyIds = person?.hobbies?.collect { it.id } ?: []
    def hobbies, totalCount

    if (associatedHobbyIds) {
      hobbies = Hobby.withCriteria {
        not { 'in'('id', associatedHobbyIds) }
        maxResults(params.max)
        firstResult(params.offset)
      }
      totalCount = Hobby.withCriteria {
        not { 'in'('id', associatedHobbyIds) }
        projections { count('id') }
      }?.get(0) ?: 0
    } else {
      hobbies = Hobby.withCriteria { maxResults(params.max); firstResult(params.offset) }
      totalCount = Hobby.count()
    }

    response.status = 200
    respond([success: true, hobbies: hobbies, totalCount: totalCount], formats: ['json'])
  }

  def editHobby() {
    def hobbyToEdit = Hobby.findById(params?.id)

    [hobby: hobbyToEdit]
  }

  @Transactional
  def updateAndSaveHobby() {
    def hobbyToUpdate = Hobby.get(params?.id)

    hobbyToUpdate.name = params?.name
    if (params?.description) {
      hobbyToUpdate.description = params?.description
    }

    def updatedHobby = hobbyToUpdate.save(flush: true)

    if (updatedHobby) {
      flash.message = "Successfully updated hobby details!"
      forward(action: "listHobbiesForEdit")
    } else {
      flash.error = "Failed updating hobby details!"
      forward(action: "editHobby")
    }
  }

  @Transactional
  def deleteHobby() {
    def hobbyToDelete = Hobby?.get(params?.id)

    if (hobbyToDelete) {
      hobbyToDelete.delete(flush: true)
      flash.message = "Hobby deleted successfully!"
    } else {
      flash.error = "Hobby not found!!"
    }

    forward(action: "listHobbiesForEdit")
  }

  @Transactional
  def addHobbyToUser() {
    def hobbyToAdd = Hobby.get(params?.id)
    request.person.hobbies.add(hobbyToAdd)

    def personSaved = request.person.save(flush: true)
    if (personSaved) {
      flash.message = "Hobby with name '" + hobbyToAdd?.name + "' added to user '" + personSaved?.firstName + " " + personSaved?.lastName + "'"
      forward(controller: 'DBoard', action: 'listHobbiesToAdd')
    } else {
      flash.error = "Failed adding hobby with name '" + hobbyToAdd?.name + "' to user '" + personSaved?.firstName + " " + personSaved?.lastName + "'"
      forward(controller: 'DBoard', action: 'listHobbiesToAdd')
    }
  }
}
