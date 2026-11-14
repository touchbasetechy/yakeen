package com.murad.app.matrimony

import grails.converters.JSON
import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.annotation.Secured
import grails.validation.ValidationException
import org.hibernate.FetchMode

@Secured(['permitAll'])
class AdminDBoardController {

  @Secured(['permitAll'])
  def index() {}

  def addPersonType() {
  }

  @Transactional
  def savePersonType() {
    PersonType personTypeFromDB = PersonType.findByPersonTypeName(params.personTypeName)
    if (personTypeFromDB) {
      flash.error = "User Type already exists!"
      redirect(action: "addPersonType")
    } else {
      def personType = new PersonType(params)

      try {
        if (personType.validate() && personType.save(flush: true)) {
          flash.message = "Saved user type successfully!"
          redirect(action: "index")
        } else {
          flash.error = "Failed creating user type!"
          redirect(action: "addPersonType")
        }
      } catch (ValidationException e) {
        flash.error = "Failed creating user type!"
        redirect(action: "addPersonType")
      }
    }
  }

  def addPerson() {}

  @Transactional
  def savePerson() {
    PersonType personType = PersonType.findById(params?.id?.toLong())

    if (!personType) {
      flash.error = "Attempt to create User failed!"
      redirect(action: "addPerson")
    } else {
      def person = new Person(params)
      person.personType = personType
      try {
        Person adminPersonSaved = person.save(flush: true)

        if (adminPersonSaved) {
          flash.message = "User of type ' ${personType.personTypeName} ' created successfully!"
          session.personId = adminPersonSaved?.id

          redirect(controller: 'DBoard', action: 'personDetails', params: [id: params?.id, ac: params?.ac])
        } else {
          flash.error = "Failed creating User!"
          redirect(controller: 'AdminDBoard', action: 'addPerson', params: [id: params?.id, ac: params?.ac])
        }
      } catch (ValidationException e) {
        flash.error = "User not created ${e.errors}"
        redirect(controller: 'AdminDBoard', action: 'addPerson', params: [id: params?.id, ac: params?.ac])
      }
    }
  }

  def listPersonType() {
    params.offset = params?.offset ? params.int('offset') : 0
    params.max = params?.max ? params.int('max') : 10

    // def personTypes = PersonType.list(params)

    def personTypes = PersonType.withCriteria {
      // Filter by personTypeName

      ne('personTypeName', 'Super')

      // Use fetchMode to avoid fetching the persons association
      fetchMode('persons', FetchMode.SELECT)

      firstResult(params.offset)
      maxResults(params.max)
    }

    return [personTypes: personTypes]
  }

  def listMorePersonTypes() {
    params.offset = params?.offset ? params.int('offset') : 0
    params.max = params?.max ? params.int('max') : 10

    def personTypes = PersonType.withCriteria {
      // Filter by personTypeName

      ne('personTypeName', 'Super')

      // Use fetchMode to avoid fetching the persons association
      fetchMode('persons', FetchMode.SELECT)

      firstResult(params.offset)
      maxResults(params.max)
    }

    def jsonData = personTypes as JSON
    response.contentType = 'application/json'
    render jsonData
  }

  def listPersonTypeForAdd() {
    return listPersonType()
  }

  def listPersonTypeForEdit() {
    params.offset = params?.offset ? params.int('offset') : 0
    params.max = params?.max ? params.int('max') : 10

    def personTypes = PersonType.withCriteria {
      // Filter by personTypeName
      ne('personTypeName', 'Super')
      // Use fetchMode to avoid fetching the persons association
      fetchMode('persons', FetchMode.SELECT)

      firstResult(params.offset)
      maxResults(params.max)
    }
    def person = Person.findById(params?.p)
    def pType = PersonType.get(person?.personTypeId)

    [personTypes: personTypes, person: person, pType: pType]
  }

  def listPerson() {
    params.offset = params?.offset ? params.int('offset') : 0
    params.max = params?.max ? params.int('max') : 10

    def persons = Person.withCriteria {
      // Filter by personTypeName
      personType {
        ne('personTypeName', 'Super')
        // Use fetchMode to avoid fetching the persons association
        fetchMode('persons', FetchMode.SELECT)
      }

      firstResult(params.offset)
      maxResults(params.max)
      // Optionally, you can join and fetch associated entities
      fetchMode('personDetails', FetchMode.JOIN)
      fetchMode('spouseDetails', FetchMode.JOIN)
    }

    [persons: persons]
  }

  def listMorePersons() {
    params.offset = params?.offset ? params.int('offset') : 0
    params.max = params?.max ? params.int('max') : 10

    def persons = Person.withCriteria {
      // Filter by personTypeName
      personType {
        ne('personTypeName', 'Super')
        // Use fetchMode to avoid fetching the persons association
        fetchMode('persons', FetchMode.SELECT)
      }

      firstResult(params.offset)
      maxResults(params.max)
      // Optionally, you can join and fetch associated entities
      fetchMode('personDetails', FetchMode.JOIN)
      fetchMode('spouseDetails', FetchMode.JOIN)
    }

    def jsonData = persons as JSON
    response.contentType = 'application/json'
    render jsonData
  }


  def editPersonType() {
    PersonType personType = PersonType.get(params?.id?.toLong())
    session.personTypeId = personType?.id
    redirect(action: "updatePersonType")
  }

  def updatePersonType() {}

  @Transactional
  def updateAndSavePersonType() {
    PersonType personType = PersonType.get(session?.personTypeId)

    if (personType?.personTypeName != params?.personTypeName) {
      personType.personTypeName = params?.personTypeName

      PersonType updatedPersonType = personType?.save(flush: true)

      if (updatedPersonType) {
        response.status = 200
        flash.message = "Successfully updated user type details!"
      } else {
        flash.error = "Failed updating user type details!"
      }
      redirect(action: "listPersonType")
    } else {
      flash.error = "User Type already exists!"
      redirect(action: "updatePersonType")
    }
  }

  def editPerson() {
    Person person = Person.get(params?.id)
    session.personId = person?.id
    redirect(action: "updatePerson")
  }

  def updatePerson() {}

  @Transactional
  def updateAndSavePerson() {
    Person person = request.person

    if ((person?.firstName != params?.firstName) || (person?.lastName != params?.lastName)) {
      person.firstName = params?.firstName
      person.lastName = params?.lastName

      try {
        if (person.validate() && person?.save(flush: true)) {
          response.status = 200
          flash.message = "Successfully updated user details!"
        } else {
          flash.error = "Failed updating user details!"
        }
        redirect(action: "listPerson")
      } catch (ValidationException e) {
        flash.error = "Failed updating user details!"
        redirect(action: "updatePerson")
      }

    } else {
      flash.error = "User already exists!"
      redirect(action: "updatePerson")
    }
  }

  def addPolicy() {}

  @Transactional
  def SavePolicies() {
    Person personToUpdate = request.person

    if (!personToUpdate?.policies) {
      personToUpdate.policies = new Policy()
    }

    personToUpdate.policies.isActive = params?.isActive
    personToUpdate.policies.isBlocked = params?.isBlocked
    personToUpdate.policies.isOnHold = params?.isOnHold

    try {
      if (personToUpdate.validate()) {
        Person updatedPerson = personToUpdate.save(flush: true)
        session.personId = updatedPerson?.id
        response.status = 200
        flash.message = "Successfully added user policies!"
        redirect(action: 'index')
      } else {
        flash.error = "Failed adding user policies!"
        redirect(action: 'addPolicy')
      }
    } catch (ValidationException e) {
      flash.error = "Failed adding user policies!"
      redirect(action: 'addPolicy')
    }
  }

  @Transactional
  def deletePersonType() {
    def personTypeToDelete = PersonType.get(params?.id)
    if (personTypeToDelete) {
      personTypeToDelete.delete(flush: true)
      flash.message = "User Type deleted successfully!"
    } else {
      flash.error = "User Type not found!"
    }
    redirect(action: "listPersonType")
  }

  @Transactional
  def deletePerson() {
    def personToDelete = Person.get(params?.id)

    if (personToDelete) {
      personToDelete.personType.persons.remove(personToDelete)
      personToDelete.delete(flush: true)
      flash.message = "User deleted successfully!"
    } else {
      flash.error = "User not found!"
    }
    redirect(action: "listPerson")
  }

  def addTicketType() {}

  @Transactional
  def saveTicketType() {
    TicketType ticketType = new TicketType(name: params?.TicketTypeName, description: params?.TicketTypeDescription)

    try {
      if (ticketType.validate() && ticketType.save(flush: true)) {
        flash.message = "Successfully created Ticket Type !"
        redirect(action: 'index')
      }
    } catch (ValidationException e) {
      flash.error = "Failed creating Ticket Type !"
      redirect(action: 'addTicketType')
    }
  }

  def listTicketType() {
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

  def editTicketType() {
    TicketType tType = TicketType.get(params?.tt?.toLong())

    session.ticketTypeId = tType?.id
    redirect(action: 'updateTicketType')
  }

  def updateTicketType() {}

  @Transactional
  def updateAndSaveTicketType() {
    def ticketType = TicketType.get(session?.ticketTypeId)

    if (ticketType.name != params?.ticketTypeName) {
      ticketType.name = params?.ticketTypeName
      ticketType.description = params?.ticketTypeDescription

      try {
        if (ticketType.validate() && ticketType.save(flush: true)) {
          flash.message = "Successfully updated Ticket Type !"
          redirect(action: 'index')
        } else {
          flash.error = "Failed updating Ticket Type !"
          redirect(action: 'updateTicketType', params: [tt: params?.tt])
        }
      } catch (ValidationException e) {
        flash.error = "Failed updating Ticket Type !"
        redirect(action: 'updateTicketType', params: [tt: params?.tt])
      }
    } else {
      flash.error = "Ticket Type already exists!"
      redirect(action: "updateTicketType")
    }
  }

  @Transactional
  def deleteTicketType() {
    def ticketTypeToDelete = TicketType.get(params?.id?.toLong())

    try {
      if (ticketTypeToDelete.validate() && ticketTypeToDelete.delete(flush: true)) {
        flash.message = "Ticket Type deleted successfully!"
      } else {
        flash.error = "Ticket Type not found!"
      }
    } catch (ValidationException e) {
      flash.error = "Ticket Type not found!"
    }
    redirect(action: "listTicketTypeForEdit")
  }

}
