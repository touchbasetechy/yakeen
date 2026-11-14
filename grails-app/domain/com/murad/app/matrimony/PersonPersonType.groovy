package com.murad.app.matrimony

import org.grails.datastore.gorm.GormEntity

class PersonPersonType implements Serializable, GormEntity<PersonPersonType> {
  Person person
  PersonType personType

  static mapping = {
    table 'person_person_type'
    version false
    id composite: ['person', 'personType']
    person column: 'person_id'
    personType column: 'person_type_id'
  }

  static constraints = {
    person nullable: false
    personType nullable: false
  }
}
