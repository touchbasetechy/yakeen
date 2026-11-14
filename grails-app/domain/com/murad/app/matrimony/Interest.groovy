package com.murad.app.matrimony

import com.app.matrimony.InterestStatus
import org.grails.datastore.gorm.GormEntity

class Interest implements GormEntity<Interest> {
  Person sender
  Person receiver
  InterestStatus status = InterestStatus.PENDING
  String message
  Date dateCreated
  Date lastUpdated
  Date respondedDate

  static constraints = {
    sender nullable: false
    receiver nullable: false
    message nullable: true, maxSize: 500
    respondedDate nullable: true
    status nullable: false

    // Prevent sending interest to self and duplicate interests
    sender validator: { val, obj ->
      if (val?.id == obj.receiver?.id) {
        return 'interest.sender.self'
      }
    }
  }

  static mapping = {
    message type: 'text'
  }
}
