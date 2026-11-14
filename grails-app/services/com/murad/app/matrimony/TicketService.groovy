package com.murad.app.matrimony

import grails.gorm.services.Service
import grails.gorm.transactions.Transactional
import grails.validation.ValidationException
import groovy.util.logging.Slf4j
import org.hibernate.FetchMode

import java.time.LocalDateTime

@Slf4j
@Service(Ticket)
class TicketService {

  PersonService personService

  @Transactional
  def autoApproveAndAssignTicket(Ticket ticket, Person assigner, Person assignee) {
    if (!ticket) {
      throw new RuntimeException("Ticket with ID ${ticket.id} not found")
    }

    if (!assignee) {
      throw new RuntimeException("Assignee cannot be null")
    }

    // Set approval information
    ticket.approvedBy = assigner
    ticket.approvedAt = LocalDateTime.now()

    // Set assignment information
    ticket.assignedTo = assignee
    ticket.assignedAt = LocalDateTime.now()

    // Save the ticket
    try {
      if (ticket.validate() && ticket.save(flush: true)) {
        log.info("ticket assigned!!")
        println("ticket assigned!!")
      }
    } catch (ValidationException e) {
      log.error("failed assigning ticket to user", e)
      println("failed assigning ticket to user", e)
    }


    return ticket
  }

  def findTicketsForAutoApproval() {
    // Default values
    def params = [
      max         : 100,
      offset      : 0,
      sort        : 'createdAt',
      order       : 'asc',
      ticketType  : null,
      createdAfter: null
    ]

    def tickets = Ticket.withCriteria {
      maxResults(params.max.toInteger())
      firstResult(params.offset.toInteger())
      isNull('approvedBy')

      if (params.ticketType) {
        eq('ticketType', params.ticketType)
      }

      fetchMode 'createdBy', org.hibernate.FetchMode.SELECT // Avoid loading
      fetchMode 'putOnHoldBy', org.hibernate.FetchMode.SELECT // Avoid loading
      fetchMode 'closedBy', org.hibernate.FetchMode.SELECT // Avoid loading
      fetchMode 'markedAsDuplicateBy', org.hibernate.FetchMode.SELECT // Avoid loading
      fetchMode 'holdRevokedBy', org.hibernate.FetchMode.SELECT // Avoid loading
      fetchMode 'assignedTo', org.hibernate.FetchMode.SELECT // Avoid loading

      fetchMode 'ticketType.tickets', org.hibernate.FetchMode.SELECT // Avoid loading
      fetchMode 'person', org.hibernate.FetchMode.SELECT // Avoid loading

//      if (params.createdAfter) {
//        gt('createdAt', params.createdAfter as LocalDateTime)
//      }

      order(params.sort as String, params.order as String)
    }
    return tickets
  }

  int batchAutoApproveAndAssign() {
    List<Ticket> tickets = findTicketsForAutoApproval()
    List<Person> persons = findPersonsWithLeastAssignedTickets()
    Person assigner = personService.getSuperUser()

    if (!persons || !tickets || !assigner) return 0

    Map<Long, Integer> assignments = [:]
    persons.each { person -> assignments[person.id] = 0 }

    tickets.eachWithIndex { Ticket ticket, int index ->
      Person assignee = persons[index % persons.size()]
      try {
        autoApproveAndAssignTicket(ticket, assigner, assignee)
        assignments[assignee.id]++
      } catch (Exception e) {
        log.error("Assignment failed for ticket ${ticket.id}", e)
      }
    }

    log.info("Ticket assignment distribution: ${assignments}")
    return tickets.size()
  }

  @Transactional(readOnly = true)
  List<Person> findPersonsWithLeastAssignedTickets() {
    // 1. Create criteria query
    def results = Ticket.createCriteria().list {
      // 2. Filter for assigned tickets
      isNotNull('assignedTo')

      // 3. Projections block
      projections {
        groupProperty('assignedTo', 'person')  // Added alias
        count('id', 'ticketCount')            // Added alias
      }

      fetchMode 'createdBy', FetchMode.SELECT // Avoid loading
      fetchMode 'putOnHoldBy', FetchMode.SELECT // Avoid loading
      fetchMode 'approvedBy', FetchMode.SELECT // Avoid loading
      fetchMode 'closedBy', FetchMode.SELECT // Avoid loading
      fetchMode 'markedAsDuplicateBy', FetchMode.SELECT // Avoid loading
      fetchMode 'holdRevokedBy', FetchMode.SELECT // Avoid loading
      fetchMode 'assignedTo.personType', FetchMode.SELECT // Avoid loading

      fetchMode 'ticketType.tickets', FetchMode.SELECT // Avoid loading
      fetchMode 'person', FetchMode.SELECT // Avoid loading

      // 4. Order by the count (ascending = least assigned first)
      order('ticketCount', 'asc')
    } as List<Object[]>

    if (results) {
      // 5. Get the minimum count (first result after ordering)
      Long minCount = results[0][1] as Long

      // 6. Collect all persons with this minimum count
      return results.findAll { it[1] == minCount }
        .collect { it[0] as Person }
    }

    // 7. Fallback to admin users if no assignments exist
    def params = [
      max   : 100,
      offset: 0,
    ]
    return personService.getAllAdmins(params)
  }
}
