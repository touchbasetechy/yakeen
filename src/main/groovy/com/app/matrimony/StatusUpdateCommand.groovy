package com.app.matrimony

import grails.validation.Validateable

// Create a proper command object class for status updates
class StatusUpdateCommand implements Validateable {
  String status
  Long userId

  static constraints = {
    status nullable: false, blank: false, inList: ['ONLINE', 'OFFLINE', 'AWAY']
    userId nullable: false
  }
}
