package com.murad.app.matrimony

import grails.compiler.GrailsCompileStatic
import groovy.transform.EqualsAndHashCode
import org.grails.datastore.gorm.GormEntity

@GrailsCompileStatic
@EqualsAndHashCode(includes = 'personTypeName')
class PersonType implements Serializable, GormEntity<PersonType> {
  String personTypeName

  static mapping = {
    table name: "person_type"
    version false
    personTypeName column: 'authority'
  }

  static constraints = {
    personTypeName blank: false, unique: true
  }

  String toString() {
    personTypeName
  }

  String getAuthority() {
    personTypeName
  }
}
