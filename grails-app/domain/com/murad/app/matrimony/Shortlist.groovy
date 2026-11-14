package com.murad.app.matrimony

import org.grails.datastore.gorm.GormEntity

class Shortlist implements GormEntity<Shortlist> {

  Person user              // User who is shortlisting
  Person shortlistedUser   // User being shortlisted
  String notes           // Optional notes about why shortlisted
  Date dateCreated
  Date lastUpdated

  static constraints = {
    user nullable: false
    shortlistedUser nullable: false
    notes nullable: true, blank: true, maxSize: 500
    // Ensure a user can't shortlist the same profile twice
    user unique: ['shortlistedUser']
    // Prevent self-shortlisting
    shortlistedUser validator: { val, obj ->
      return val?.id != obj.user?.id
    }
  }

  static mapping = {
    table 'shortlists'
    version false
    user index: 'idx_shortlist_user'
    shortlistedUser index: 'idx_shortlist_shortlisted_user'
    dateCreated index: 'idx_shortlist_date_created'
  }

  String toString() {
    return "${user?.firstName} ${user?.lastName} shortlisted ${shortlistedUser?.firstName} ${shortlistedUser?.lastName}"
  }
}
