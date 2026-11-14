package com.murad.app.matrimony

import org.grails.datastore.gorm.GormEntity

class TicketType implements GormEntity<TicketType> {

  String name
  String description

  static hasMany = [tickets: Ticket]
  static mapping = {
    table(name "ticket_type")
    version(versioned: false)

    tickets fetch: 'select', cascade: 'all'
  }

  static constraints = {
    tickets nullable: true
  }
}
