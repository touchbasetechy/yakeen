package com.murad.app.matrimony

import grails.gorm.services.Service

@Service(Person)
class PersonService {

  Person getSuperUser() {
    Person.withCriteria {
      personTypes {
        eq('personTypeName', 'ROLE_SUPER')
      }
      fetchMode 'personTypes', org.hibernate.FetchMode.SELECT // Avoid loading
      maxResults(1) // Ensure we only get one result
    }?.find() // Get the first result or null
  }

  def getAllAdmins(Map params) {
    Person.withCriteria {
      personTypes {
        eq('personTypeName', 'ROLE_ADMIN')
      }

      order('lastName', 'asc')
      order('firstName', 'asc')

      // Optional: tuning performance if necessary
      fetchMode 'personTypes', org.hibernate.FetchMode.SELECT

      // Pagination
      maxResults(params.max as Integer ?: 10)
      firstResult(params.offset as Integer ?: 0)
    }
  }
}
