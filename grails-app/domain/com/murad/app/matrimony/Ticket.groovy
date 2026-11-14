package com.murad.app.matrimony

import org.grails.datastore.gorm.GormEntity

import java.time.LocalDateTime

class Ticket implements GormEntity<Ticket> {
  String ticketId
  String subject
  String message
  Person createdBy
  Person putOnHoldBy
  Person approvedBy
  Person closedBy
  Person markedAsDuplicateBy
  Person holdRevokedBy
  Person assignedTo
  LocalDateTime createdAt
  LocalDateTime putOnHoldAt
  LocalDateTime approvedAt
  LocalDateTime closedAt
  LocalDateTime markedAsDuplicateAt
  LocalDateTime holdRevokedAt
  LocalDateTime assignedAt
  String reasonForHold
  String reasonForDuplicate
  String reasonForRevoke
  String reasonForClose

  static belongsTo = [ticketType: TicketType, person: Person]
  static mapping = {
    table name: "ticket"
    version false

    createdBy lazy: true, fetch: 'select', cascade: 'save-update'
    putOnHoldBy lazy: true, fetch: 'select', cascade: 'save-update'
    approvedBy lazy: true, fetch: 'select', cascade: 'save-update'
    closedBy lazy: true, fetch: 'select', cascade: 'save-update'
    markedAsDuplicateBy lazy: true, fetch: 'select', cascade: 'save-update'
    holdRevokedBy lazy: true, fetch: 'select', cascade: 'save-update'
    assignedTo lazy: true, fetch: 'select', cascade: 'save-update'
  }

  static constraints = {
    createdBy nullable: true
    putOnHoldBy nullable: true
    approvedBy nullable: true
    closedBy nullable: true
    markedAsDuplicateBy nullable: true
    holdRevokedBy nullable: true
    assignedTo nullable: true
    createdAt nullable: true
    putOnHoldAt nullable: true
    approvedAt nullable: true
    closedAt nullable: true
    assignedAt nullable: true
    markedAsDuplicateAt nullable: true
    holdRevokedAt nullable: true
    reasonForHold nullable: true
    reasonForDuplicate nullable: true
    reasonForRevoke nullable: true
    reasonForClose nullable: true
    ticketType nullable: true
    person nullable: true
  }
}
