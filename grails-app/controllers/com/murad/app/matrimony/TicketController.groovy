package com.murad.app.matrimony

import grails.converters.JSON
import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.annotation.Secured
import grails.validation.ValidationException

import java.time.LocalDateTime


class TicketController {

  def listTicketTypeForAdd() {
    params.offset = params?.offset ? params.int('offset') : 0
    params.max = params?.max ? params.int('max') : 10

    def ticketTypes = TicketType.list(params)

    [ticketTypes: ticketTypes]
  }

  def listTicketTypeForAddToManageTickets() {
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
      def date = new Date()
      def currentTimeMillis = date.getTime().longValue()
      def ticketId = "Tkt" + currentTimeMillis

      def ticket = new Ticket(ticketId: ticketId, subject: params?.subject, message: params?.message, ticketType: ticketType)

      ticket.createdBy = session.person
      ticket.createdAt = LocalDateTime.now()

      try {
        Ticket ticketSaved = ticket.save(flush: true)

        if (ticketSaved) {
          flash.message = "Ticket of type ' ${ticketType?.name} ' created successfully!"
//          session.ticket = adminTicketSaved
          if (params?.ac == 'l') {
            redirect(controller: 'ticket', action: 'listTicketToManage', params: [id: params?.id, ac: params?.ac])
          } else {
            redirect(controller: 'AdminDBoard', action: 'index', params: [id: params?.id, ac: params?.ac])
          }
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

  def listTicket() {
    params.offset = params?.offset ? params.int('offset') : 0
    params.max = params?.max ? params.int('max') : 10

    def tickets = Ticket.list(params)

    [tickets: tickets]
  }

  def listTicketTypeForEdit() {
    params.offset = params?.offset ? params.int('offset') : 0
    params.max = params?.max ? params.int('max') : 10

    def ticketTypes = TicketType.list(params)
    def ticket = Ticket.findById(params?.t)
    def tType = TicketType.get(ticket?.ticketTypeId)

    [ticketTypes: ticketTypes, ticket: ticket, tType: tType]
  }

  def edit() {
    Ticket ticket = Ticket.get(params?.t?.toLong())
    session.ticketToEdit = ticket

    redirect(action: "updateTicket", params: [t: params?.t, ott: params?.ott, ptt: params?.ptt])
  }

  def updateTicket() {
    def oldTt = TicketType.findById(params?.ott)
    def presentTt = TicketType.findById(params?.ptt)

    [oldTt: oldTt, presentTt: presentTt]
  }

  @Transactional
  def updateAndSaveTicket() {
    Ticket ticket = Ticket.get(session?.ticketToEdit?.id)

    ticket.subject = params?.subject
    ticket.message = params?.message

    if (ticket?.ticketTypeId != params?.ptt) {
      TicketType ticketType = TicketType.get(params?.ptt)

      if (ticketType) {
        ticket.ticketType = ticketType
      }
    }

    Ticket updatedTicket = ticket?.save(flush: true)

    if (updatedTicket) {
      response.status = 200
      flash.message = "Successfully updated ticket details!"
      redirect(action: "listTicket")
    } else {
      flash.error = "Failed updating ticket details!"
      redirect(action: "updateTicket")
    }
  }

  @Transactional
  def deleteTicket() {
    def ticketToDelete = Ticket.get(params?.id)

    if (ticketToDelete) {
      ticketToDelete.delete(flush: true)
      flash.message = "Ticket deleted successfully!"
    } else {
      flash.error = "Ticket not found!"
    }
    redirect(action: "listTicket")
  }

  @Secured(['ROLE_SUPER'])
  def listTicketToManage() {
    def approvalPendingTickets = getApprovalPendingTickets(params)
    def approvedTickets = getApprovedTickets(params)
    def onHoldTickets = getHeldTickets(params)
    def revokedTickets = getRevokedTickets(params)
    def duplicatedTickets = getDuplicatedTickets(params)
    def closedTickets = getClosedTickets(params)

    [approvalPendingTickets: approvalPendingTickets, approvedTickets: approvedTickets, onHoldTickets: onHoldTickets, revokedTickets: revokedTickets, duplicatedTickets: duplicatedTickets, closedTickets: closedTickets]
  }

  @Transactional
  def putOnHoldTicket() {
    def ticketToPutOnHold = Ticket.get(params?.t)

//    ticketToPutOnHold.holdRevokedBy = null
//    ticketToPutOnHold.holdRevokedAt = null

    ticketToPutOnHold.putOnHoldBy = session.person
    ticketToPutOnHold.putOnHoldAt = LocalDateTime.now()
    ticketToPutOnHold.reasonForHold = params?.Reason

    def heldTicket = ticketToPutOnHold.save(flush: true)

    if (heldTicket) {
      flash.message = "Ticket with id " + heldTicket.ticketId + " has been put on hold successfully !!"
    } else {
      flash.error = "Attempt to put the ticket with id " + ticketToPutOnHold.ticketId + "on hold failed !!"
    }

    forward(action: "listTicketToManage")
  }

  @Transactional
  def approveTicket() {
    _approveTicket(params)

    forward(action: "listTicketToManage")
  }


  @Transactional
  def closeTicket() {
    def ticketToClose = Ticket.get(params?.t)

    ticketToClose.closedBy = session?.person
    ticketToClose.closedAt = LocalDateTime.now()
    ticketToClose.reasonForClose = params?.Reason

    def closedTicket = ticketToClose.save(flush: true)

    if (closedTicket) {
      flash.message = "Ticket with id " + closedTicket.ticketId + " has been closed successfully !!"
    } else {
      flash.error = "Attempt to close the ticket with id " + ticketToClose.ticketId + " failed !!"
    }

    forward(action: "listTicketToManage")
  }

  @Transactional
  def markDuplicateTicket() {
    def ticketToDuplicate = Ticket.get(params?.t)

    ticketToDuplicate.markedAsDuplicateBy = session.person
    ticketToDuplicate.markedAsDuplicateAt = LocalDateTime.now()
    ticketToDuplicate.reasonForDuplicate = params?.Reason

    def duplicatedTicket = ticketToDuplicate.save(flush: true)

    if (duplicatedTicket) {
      flash.message = "Ticket with id " + duplicatedTicket.ticketId + " has been marked as duplicate successfully !!"
    } else {
      flash.error = "Attempt to mark ticket with id " + ticketToDuplicate.ticketId + " as duplicate failed !!"
    }

    forward(action: "listTicketToManage")
  }

  @Transactional
  def revokeHeldTicket() {
    def ticketToRevoke = Ticket.get(params?.t)

    ticketToRevoke.holdRevokedBy = session.person
    ticketToRevoke.holdRevokedAt = LocalDateTime.now()
    ticketToRevoke.reasonForRevoke = params?.Reason

    def revokedTicket = ticketToRevoke.save(flush: true)

    if (revokedTicket) {
      flash.message = "Ticket with id " + revokedTicket.ticketId + " has been reactivated successfully !!"
    } else {
      flash.error = "Attempt to reactivate ticket with id " + ticketToRevoke.ticketId + " failed !!"
    }

    forward(action: "listTicketToManage")
  }

  @Transactional
  def approveTicketAndReturnToHold() {
    _approveTicket()

    forward(action: "viewAndHoldTicket", params: [t: params?.t])
  }


  @Transactional
  def approveTicketAndReturnToRevoke() {
    _approveTicket(params)

    forward(action: "viewAndRevokeTicket", params: [t: params?.t])
  }

  @Transactional
  def approveTicketAndReturnToDuplicate() {
    _approveTicket(params)

    forward(action: "viewAndDuplicateTicket", params: [t: params?.t])
  }

  @Transactional
  def approveTicketAndReturnToClose() {
    _approveTicket(params)

    forward(action: "viewAndCloseTicket", params: [t: params?.t])
  }

  def _approveTicket(params) {
    def ticketToApprove = Ticket.get(params?.t)

    ticketToApprove.approvedBy = session.person
    ticketToApprove.approvedAt = LocalDateTime.now()

    def approvedTicket = ticketToApprove.save(flush: true)

    if (approvedTicket) {
      flash.message = "Ticket with id " + approvedTicket.ticketId + " approved successfully !!"
    } else {
      flash.error = "Attempt to approve ticket with id " + ticketToApprove.ticketId + " failed !!"
    }
  }

  def viewTicket() {
    def ticketToView = Ticket.get(params?.t)

    [ticket: ticketToView]
  }

  def viewAndHoldTicket() {
    def ticketToHold = Ticket.get(params?.t)

    [ticket: ticketToHold]
  }

  def viewAndRevokeTicket() {
    def ticketToRevoke = Ticket.get(params?.t)

    [ticket: ticketToRevoke]
  }

  def viewAndDuplicateTicket() {
    def ticketToDuplicate = Ticket.get(params?.t)

    [ticket: ticketToDuplicate]
  }

  def loadTicketsToCompare() {
    params.offset = params.offset ? params?.offset?.toInteger() : 0
    params.max = params.max ? params?.max?.toInteger() : 10

// Fetch tickets for comparison, excluding the ticket with the specified ID
    def ticketsForComparison = Ticket.withCriteria {
      if (params?.t) {
        ne('id', params?.long('t'))
      }

      isNull('closedBy')
      isNull('closedAt')

      createAlias('ticketType', 'tt')
      // Select the necessary fields
      projections {
        property('ticketId')
        property('subject')
        property('message')
        property('tt.name') // Fetch the ticketType name
      }
      firstResult(params?.offset)
      maxResults(params?.max)
    }

    // Process the result set
    def result = ticketsForComparison.collect {
      [
        ticketId      : it[0],
        subject       : it[1],
        message       : it[2],
        ticketTypeName: it[3] // The name of the ticket type
      ]
    }
    render result as JSON
  }

  def viewAndCloseTicket() {
    def ticketToClose = Ticket.get(params?.t)

    [ticket: ticketToClose]
  }

  def viewAndDeleteTicket() {
    def ticketToDelete = Ticket.get(params?.t)

    [ticket: ticketToDelete]
  }

  @Transactional
  def manageAndDeleteTicket() {
    def ticketToDelete = Ticket.get(params?.t)

    if (ticketToDelete) {
      ticketToDelete.delete(flush: true)
      flash.message = "Ticket deleted successfully!"
    } else {
      flash.error = "Ticket not found!"
    }
    redirect(action: "listTicketToManage")
  }

  def listMoreApprovalPendingTickets() {
    def approvalPendingTickets = getApprovalPendingTickets(params)

    render approvalPendingTickets as JSON
  }

  def listMoreApprovedTickets() {
    def approvedTickets = getApprovedTickets(params)

    render approvedTickets as JSON
  }

  def listMoreOnHoldTickets() {
    def onHoldTickets = getHeldTickets(params)

    render onHoldTickets as JSON
  }

  def listMoreRevokedTickets() {
    def revokedTickets = getRevokedTickets(params)

    render revokedTickets as JSON
  }

  def listMoreDuplicatedTickets() {
    def duplicatedTickets = getDuplicatedTickets(params)

    render duplicatedTickets as JSON
  }

  def listMoreClosedTickets() {
    def closedTickets = getClosedTickets(params)

    render closedTickets as JSON
  }

  def getApprovalPendingTickets(params) {
    def approvalPendingTickets = Ticket.withCriteria {
      and {
        isNull('approvedBy')
        isNull('approvedAt')
        isNull('putOnHoldBy')
        isNull('putOnHoldAt')
        isNull('holdRevokedBy')
        isNull('holdRevokedAt')
        isNull('markedAsDuplicateBy')
        isNull('markedAsDuplicateAt')
        isNull('closedBy')
        isNull('closedAt')
      }
      firstResult(params?.offset ? params?.offset?.toInteger() : 0) // Set the offset
      maxResults(params?.max ? params?.max?.toInteger() : 10) // Set the maximum number of records to return
    }

    approvalPendingTickets
  }

  def getApprovedTickets(params) {
    def approvedTickets = Ticket.withCriteria {
      and {
        isNotNull('approvedBy')
        isNotNull('approvedAt')
        isNull('putOnHoldBy')
        isNull('putOnHoldAt')
        isNull('holdRevokedBy')
        isNull('holdRevokedAt')
        isNull('markedAsDuplicateBy')
        isNull('markedAsDuplicateAt')
        isNull('closedBy')
        isNull('closedAt')
      }
      firstResult(params?.offset ? params?.offset?.toInteger() : 0) // Set the offset
      maxResults(params?.max ? params?.max?.toInteger() : 10) // Set the maximum number of records to return
    }

    approvedTickets
  }

  def getHeldTickets(params) {
    def onHoldTickets = Ticket.withCriteria {
      and {
        isNotNull('approvedBy')
        isNotNull('approvedAt')
        isNotNull('putOnHoldBy')
        isNotNull('putOnHoldAt')
        isNull('holdRevokedBy')
        isNull('holdRevokedAt')
        isNull('markedAsDuplicateBy')
        isNull('markedAsDuplicateAt')
        isNull('closedBy')
        isNull('closedAt')
      }
      firstResult(params?.offset ? params?.offset?.toInteger() : 0) // Set the offset
      maxResults(params?.max ? params?.max?.toInteger() : 10) // Set the maximum number of records to return
    }

    onHoldTickets
  }

  def getRevokedTickets(params) {
    def revokedTickets = Ticket.withCriteria {
      and {
        isNotNull('approvedBy')
        isNotNull('approvedAt')
        isNotNull('putOnHoldBy')
        isNotNull('putOnHoldAt')
        isNotNull('holdRevokedBy')
        isNotNull('holdRevokedAt')
        isNull('markedAsDuplicateBy')
        isNull('markedAsDuplicateAt')
        isNull('closedBy')
        isNull('closedAt')
      }
      firstResult(params?.offset ? params?.offset?.toInteger() : 0) // Set the offset
      maxResults(params?.max ? params?.max?.toInteger() : 10) // Set the maximum number of records to return
    }

    revokedTickets
  }


  def getDuplicatedTickets(params) {
    def duplicatedTickets = Ticket.withCriteria {
      and {
        isNotNull('approvedBy')
        isNotNull('approvedAt')
        isNotNull('markedAsDuplicateBy')
        isNotNull('markedAsDuplicateAt')
        isNull('closedBy')
        isNull('closedAt')
      }
      firstResult(params?.offset ? params?.offset?.toInteger() : 0) // Set the offset
      maxResults(params?.max ? params?.max?.toInteger() : 10) // Set the maximum number of records to return
    }

    duplicatedTickets
  }

  def getClosedTickets(params) {
    def closedTickets = Ticket.withCriteria {
      and {
        isNotNull('approvedBy')
        isNotNull('approvedAt')
        isNotNull('closedBy')
        isNotNull('closedAt')
      }
      firstResult(params?.offset ? params?.offset?.toInteger() : 0) // Set the offset
      maxResults(params?.max ? params?.max?.toInteger() : 10) // Set the maximum number of records to return
    }

    closedTickets
  }

  def refreshClosedTicketsPage() {
    def closedTickets = getClosedTickets(params)

    closedTickets as JSON
  }
}
