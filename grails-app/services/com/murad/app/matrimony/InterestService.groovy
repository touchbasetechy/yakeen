package com.murad.app.matrimony

import com.app.matrimony.InterestStatus
import grails.gorm.transactions.Transactional

@Transactional
class InterestService {

  def springSecurityService
  NotificationService notificationService

  /**
   * Send interest with real-time notification
   */
  def sendInterest(Long receiverId, String message = null) {
    Person sender = springSecurityService.currentUser
    Person receiver = User.get(receiverId)

    if (!receiver) {
      return [success: false, message: 'User not found']
    }

    if (sender.id == receiver.id) {
      return [success: false, message: 'Cannot send interest to yourself']
    }

    // Check if interest already exists
    Interest existingInterest = Interest.findBySenderAndReceiver(sender, receiver)
    if (existingInterest) {
      return [success: false, message: 'Interest already sent to this user']
    }

    try {
      Interest interest = new Interest(
        sender: sender,
        receiver: receiver,
        message: message,
        status: InterestStatus.PENDING
      )

      if (interest.save(flush: true)) {
        // Send real-time notification
        notificationService.sendInterestReceivedNotification(interest)

        return [success: true, message: 'Interest sent successfully', interest: interest]
      } else {
        return [success: false, message: 'Failed to send interest', errors: interest.errors]
      }
    } catch (Exception e) {
      log.error("Error sending interest: ${e.message}", e)
      return [success: false, message: 'An error occurred while sending interest']
    }
  }

  /**
   * Respond to interest with real-time notification
   */
  def respondToInterest(Long interestId, InterestStatus response) {
    Person currentUser = springSecurityService.currentUser
    Interest interest = Interest.get(interestId)

    if (!interest) {
      return [success: false, message: 'Interest not found']
    }

    if (interest.receiver.id != currentUser.id) {
      return [success: false, message: 'Unauthorized to respond to this interest']
    }

    if (interest.status != InterestStatus.PENDING) {
      return [success: false, message: 'Interest has already been responded to']
    }

    try {
      interest.status = response
      interest.respondedDate = new Date()

      if (interest.save(flush: true)) {
        // Send real-time notification to sender
        notificationService.sendInterestResponseNotification(interest)

        return [success: true, message: "Interest ${response.value} successfully", interest: interest]
      } else {
        return [success: false, message: 'Failed to respond to interest', errors: interest.errors]
      }
    } catch (Exception e) {
      log.error("Error responding to interest: ${e.message}", e)
      return [success: false, message: 'An error occurred while responding to interest']
    }
  }

  /**
   * Withdraw sent interest
   */
  def withdrawInterest(Long interestId) {
    Person currentUser = springSecurityService.currentUser
    Interest interest = Interest.get(interestId)

    if (!interest) {
      return [success: false, message: 'Interest not found']
    }

    if (interest.sender.id != currentUser.id) {
      return [success: false, message: 'Unauthorized to withdraw this interest']
    }

    if (interest.status != InterestStatus.PENDING) {
      return [success: false, message: 'Can only withdraw pending interests']
    }

    try {
      interest.status = InterestStatus.WITHDRAWN

      if (interest.save(flush: true)) {
        return [success: true, message: 'Interest withdrawn successfully']
      } else {
        return [success: false, message: 'Failed to withdraw interest', errors: interest.errors]
      }
    } catch (Exception e) {
      log.error("Error withdrawing interest: ${e.message}", e)
      return [success: false, message: 'An error occurred while withdrawing interest']
    }
  }

  /**
   * Get received interests for current user
   */
  def getReceivedInterests(Map params = [:]) {
    Person currentUser = springSecurityService.currentUser

    def criteria = Interest.createCriteria()
    def interests = criteria.list(params) {
      eq('receiver', currentUser)
      if (params.status) {
        eq('status', params.status)
      }
      order('dateCreated', 'desc')
    }

    return interests
  }

  /**
   * Get sent interests for current user
   */
  def getSentInterests(Map params = [:]) {
    Person currentUser = springSecurityService.currentUser

    def criteria = Interest.createCriteria()
    def interests = criteria.list(params) {
      eq('sender', currentUser)
      if (params.status) {
        eq('status', params.status)
      }
      order('dateCreated', 'desc')
    }

    return interests
  }

  /**
   * Get interest statistics for current user
   */
  def getInterestStats() {
    Person currentUser = springSecurityService.currentUser

    def sentStats = Interest.createCriteria().list {
      eq('sender', currentUser)
      projections {
        groupProperty('status')
        count('status')
      }
    }

    def receivedStats = Interest.createCriteria().list {
      eq('receiver', currentUser)
      projections {
        groupProperty('status')
        count('status')
      }
    }

    return [
      sent    : sentStats.collectEntries { [it[0], it[1]] },
      received: receivedStats.collectEntries { [it[0], it[1]] }
    ]
  }

  private void sendInterestNotification(Interest interest) {
    // Implement notification logic (email, SMS, push notification)
    log.info("Interest notification sent to ${interest.receiver.email}")
  }

  private void sendResponseNotification(Interest interest) {
    // Implement notification logic for interest response
    log.info("Interest response notification sent to ${interest.sender.email}")
  }

}
