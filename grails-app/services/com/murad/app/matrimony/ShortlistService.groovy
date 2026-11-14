package com.murad.app.matrimony

import grails.gorm.PagedResultList
import grails.gorm.transactions.Transactional

@Transactional
class ShortlistService {

  /**
   * Add a user to current user's shortlist
   */
  def addToShortlist(Person currentUser, Person userToShortlist, String notes = null) {
    try {
      // Check if already shortlisted
      if (isShortlisted(currentUser, userToShortlist)) {
        return [success: false, message: 'Profile is already in your shortlist']
      }

      // Check if trying to shortlist self
      if (currentUser.id == userToShortlist.id) {
        return [success: false, message: 'You cannot shortlist your own profile']
      }

      Shortlist shortlist = new Shortlist(
        user: currentUser,
        shortlistedUser: userToShortlist,
        notes: notes
      )

      if (shortlist.save(flush: true)) {
        return [success: true, message: 'Profile added to shortlist successfully', shortlist: shortlist]
      } else {
        return [success: false, message: 'Failed to add profile to shortlist', errors: shortlist.errors]
      }
    } catch (Exception e) {
      log.error("Error adding to shortlist: ${e.message}", e)
      return [success: false, message: 'An error occurred while adding to shortlist']
    }
  }

  /**
   * Remove a user from current user's shortlist
   */
  def removeFromShortlist(Person currentUser, Person userToRemove) {
    try {
      Shortlist shortlist = Shortlist.findByUserAndShortlistedUser(currentUser, userToRemove)
      if (shortlist) {
        shortlist.delete(flush: true)
        return [success: true, message: 'Profile removed from shortlist successfully']
      } else {
        return [success: false, message: 'Profile not found in shortlist']
      }
    } catch (Exception e) {
      log.error("Error removing from shortlist: ${e.message}", e)
      return [success: false, message: 'An error occurred while removing from shortlist']
    }
  }

  /**
   * Check if a user is shortlisted by current user
   */
  boolean isShortlisted(Person currentUser, Person userToCheck) {
    return Shortlist.countByUserAndShortlistedUser(currentUser, userToCheck) > 0
  }

  /**
   * Get user's shortlist with pagination
   */
  PagedResultList getShortlist(Person currentUser, Map params = [:]) {
    // Validate required parameter
    if (!currentUser) {
      throw new IllegalArgumentException("Current user cannot be null")
    }

    // Set default pagination parameters
    def max = params.max as Integer ?: 10
    def offset = params.offset as Integer ?: 0
    def sort = params.sort ?: 'dateCreated'
    def order = params.order ?: 'desc'

    // Validate sort parameter to prevent SQL injection
    def validSortFields = ['dateCreated', 'lastUpdated', 'notes']
    if (!(sort in validSortFields)) {
      sort = 'dateCreated'
    }

    // Validate order parameter
    def validOrders = ['asc', 'desc']
    if (!(order.toLowerCase() in validOrders)) {
      order = 'desc'
    }

    // Build the where query
    def query = Shortlist.where {
      user == currentUser
    }

    // Apply sorting and pagination
    def results = query.list(
      max: max,
      offset: offset,
      sort: sort,
      order: order,
      fetch: [shortlistedUser: 'join']  // Eager fetch to avoid N+1 queries
    )

    return results
  }

  /**
   * Get count of user's shortlisted profiles
   */
  int getShortlistCount(Person currentUser) {
    return Shortlist.countByUser(currentUser)
  }

  /**
   * Get users who have shortlisted the current user
   */
  PagedResultList getUsersWhoShortlistedMe(Person currentUser, Map params = [:]) {
    def query = Shortlist.where {
      shortlistedUser == currentUser
    }

    def sort = params.sort ?: 'dateCreated'
    def order = params.order ?: 'desc'

    return query.list(
      max: params.max ?: 10,
      offset: params.offset ?: 0,
      sort: sort,
      order: order
    )
  }

  /**
   * Update notes for a shortlisted profile
   */
  def updateShortlistNotes(Person currentUser, Person shortlistedUser, String newNotes) {
    try {
      Shortlist shortlist = Shortlist.findByUserAndShortlistedUser(currentUser, shortlistedUser)
      if (shortlist) {
        shortlist.notes = newNotes
        shortlist.save(flush: true)
        return [success: true, message: 'Notes updated successfully']
      } else {
        return [success: false, message: 'Profile not found in shortlist']
      }
    } catch (Exception e) {
      log.error("Error updating shortlist notes: ${e.message}", e)
      return [success: false, message: 'An error occurred while updating notes']
    }
  }

  /**
   * Clear all shortlist entries for a user
   */
  def clearShortlist(Person currentUser) {
    try {
      def deletedCount = Shortlist.where {
        user == currentUser
      }.deleteAll()

      return [success: true, message: "Cleared ${deletedCount} profiles from shortlist"]
    } catch (Exception e) {
      log.error("Error clearing shortlist: ${e.message}", e)
      return [success: false, message: 'An error occurred while clearing shortlist']
    }
  }
}
