package com.murad.app.matrimony

import grails.converters.JSON
import grails.gorm.transactions.Transactional
import grails.validation.ValidationException


class PersonTicketController {

  def listTicketTypeForAdd() {
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

  def addTicket() {}

  @Transactional
  def saveTicket() {
    TicketType ticketType = TicketType.findById(params?.id?.toLong())

    if (!ticketType) {
      flash.error = "Attempt to create ticket failed!"
      redirect(action: "addTicket")
    } else {
      def ticketId = "xxxx"
      def ticket = new Ticket(ticketId: ticketId, subject: params?.subject, message: params?.message, ticketType: ticketType)

      try {
        Ticket adminTicketSaved = ticket.save(flush: true)

        if (adminTicketSaved) {
          flash.message = "Ticket of type ' ${ticketType.name} ' created successfully!"
//          session.ticket = adminTicketSaved

          redirect(controller: 'AdminDBoard', action: 'index', params: [id: params?.id, ac: params?.ac])
        } else {
          flash.error = "Failed creating ticket!"
          redirect(controller: 'Ticket', action: 'addTicket', params: [id: params?.id, ac: params?.ac])
        }
      } catch (ValidationException e) {
        flash.error = "Ticket not created ${e.errors}"
        redirect(controller: 'Ticket', action: 'addTicket', params: [id: params?.id, ac: params?.ac])
      }
    }
  }
}
