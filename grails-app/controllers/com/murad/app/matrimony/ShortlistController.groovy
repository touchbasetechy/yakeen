package com.murad.app.matrimony

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured

@Secured(['ROLE_USER'])
class ShortlistController {

  def shortlistService
  def springSecurityService

  /**
   * Display user's shortlist
   */
  def index() {
    def currentUser = springSecurityService.currentUser
    def shortlist = shortlistService.getShortlist(currentUser, params)
    def shortlistCount = shortlistService.getShortlistCount(currentUser)

    [shortlist: shortlist, shortlistCount: shortlistCount]
  }

  /**
   * Add user to shortlist (AJAX)
   */
  def add() {
    def currentUser = springSecurityService.currentUser
    def userToShortlist = User.get(params.userId)

    if (!userToShortlist) {
      render([success: false, message: 'User not found'] as JSON)
      return
    }

    def result = shortlistService.addToShortlist(
      currentUser,
      userToShortlist,
      params.notes
    )

    render(result as JSON)
  }

  /**
   * Remove user from shortlist (AJAX)
   */
  def remove() {
    def currentUser = springSecurityService.currentUser
    def userToRemove = User.get(params.userId)

    if (!userToRemove) {
      render([success: false, message: 'User not found'] as JSON)
      return
    }

    def result = shortlistService.removeFromShortlist(currentUser, userToRemove)
    render(result as JSON)
  }

  /**
   * Check if user is shortlisted (AJAX)
   */
  def checkStatus() {
    def currentUser = springSecurityService.currentUser
    def userToCheck = User.get(params.userId)

    if (!userToCheck) {
      render([shortlisted: false] as JSON)
      return
    }

    def isShortlisted = shortlistService.isShortlisted(currentUser, userToCheck)
    render([shortlisted: isShortlisted] as JSON)
  }

  /**
   * Update shortlist notes
   */
  def updateNotes() {
    def currentUser = springSecurityService.currentUser
    def shortlistedUser = User.get(params.userId)

    if (!shortlistedUser) {
      render([success: false, message: 'User not found'] as JSON)
      return
    }

    def result = shortlistService.updateShortlistNotes(
      currentUser,
      shortlistedUser,
      params.notes
    )

    render(result as JSON)
  }

  /**
   * Show users who shortlisted current user
   */
  def shortlistedBy() {
    def currentUser = springSecurityService.currentUser
    def shortlistedBy = shortlistService.getUsersWhoShortlistedMe(currentUser, params)

    [shortlistedBy: shortlistedBy]
  }

  /**
   * Clear entire shortlist
   */
  def clear() {
    def currentUser = springSecurityService.currentUser
    def result = shortlistService.clearShortlist(currentUser)

    if (result.success) {
      flash.message = result.message
    } else {
      flash.error = result.message
    }

    redirect(action: 'index')
  }

  /**
   * Get shortlist count for current user (AJAX)
   */
  def count() {
    def currentUser = springSecurityService.currentUser
    def count = shortlistService.getShortlistCount(currentUser)
    render([count: count] as JSON)
  }
}
