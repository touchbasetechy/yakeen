package com.murad.app.matrimony

import com.app.matrimony.InterestStatus
import grails.plugin.springsecurity.annotation.Secured

@Secured(['ROLE_USER'])
class InterestController {

  InterestService interestService

  def index() {
    redirect(action: 'received')
  }

  /**
   * Show received interests
   */
  def received() {
    def interests = interestService.getReceivedInterests(params)
    def stats = interestService.getInterestStats()

    [interests: interests, stats: stats.received]
  }

  /**
   * Show sent interests
   */
  def sent() {
    def interests = interestService.getSentInterests(params)
    def stats = interestService.getInterestStats()

    [interests: interests, stats: stats.sent]
  }

  /**
   * Send interest to a user
   */
  def send() {
    if (request.method == 'POST') {
      def result = interestService.sendInterest(params.long('receiverId'), params.message)

      if (result.success) {
        flash.message = result.message
        redirect(action: 'sent')
      } else {
        flash.error = result.message
        redirect(controller: 'user', action: 'show', id: params.receiverId)
      }
    } else {
      def receiver = User.get(params.long('receiverId'))
      if (!receiver) {
        flash.error = 'User not found'
        redirect(controller: 'user', action: 'search')
        return
      }
      [receiver: receiver]
    }
  }

  /**
   * Respond to an interest
   */
  def respond() {
    if (request.method == 'POST') {
      InterestStatus response = params.response as InterestStatus
      def result = interestService.respondToInterest(params.long('id'), response)

      flash.message = result.message
      if (!result.success) {
        flash.error = result.message
      }

      redirect(action: 'received')
    } else {
      def interest = Interest.get(params.long('id'))
      if (!interest) {
        flash.error = 'Interest not found'
        redirect(action: 'received')
        return
      }
      [interest: interest]
    }
  }

  /**
   * Withdraw sent interest
   */
  def withdraw() {
    def result = interestService.withdrawInterest(params.long('id'))

    flash.message = result.message
    if (!result.success) {
      flash.error = result.message
    }

    redirect(action: 'sent')
  }

  /**
   * AJAX endpoint to send interest
   */
  def sendAjax() {
    def result = interestService.sendInterest(params.long('receiverId'), params.message)
    render(contentType: 'application/json') {
      success = result.success
      message = result.message
    }
  }

  /**
   * AJAX endpoint to respond to interest
   */
  def respondAjax() {
    InterestStatus response = params.response as InterestStatus
    def result = interestService.respondToInterest(params.long('id'), response)

    render(contentType: 'application/json') {
      success = result.success
      message = result.message
    }
  }
}
