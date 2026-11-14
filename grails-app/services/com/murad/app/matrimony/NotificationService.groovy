package com.murad.app.matrimony

import grails.gorm.transactions.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.simp.SimpMessagingTemplate

@Transactional
class NotificationService {

  @Autowired
  SimpMessagingTemplate messagingTemplate

  def springSecurityService

  /**
   * Send real-time notification when interest is received
   */
  void sendInterestReceivedNotification(Interest interest) {
    try {
      def notification = [
        id          : interest.id,
        type        : 'INTEREST_RECEIVED',
        senderId    : interest.sender.id,
        senderName  : "${interest.sender.firstName} ${interest.sender.lastName}",
        senderAvatar: "/user/avatar/${interest.sender.id}",
        message     : interest.message ?: "sent you an interest",
        timestamp   : interest.dateCreated.time,
        dateCreated : interest.dateCreated.format("MMM dd, yyyy 'at' hh:mm a")
      ]

      // Send to specific user
      messagingTemplate.convertAndSendToUser(
        interest.receiver.id.toString(),
        "/queue/interests",
        notification
      )

      // Update interest count
      def interestCount = getUnreadInterestCount(interest.receiver.id)
      messagingTemplate.convertAndSendToUser(
        interest.receiver.id.toString(),
        "/queue/interest-count",
        [count: interestCount]
      )

      log.info("Real-time interest notification sent to user ${interest.receiver.id}")

    } catch (Exception e) {
      log.error("Failed to send real-time interest notification: ${e.message}", e)
    }
  }

  /**
   * Send real-time notification when interest is responded to
   */
  void sendInterestResponseNotification(Interest interest) {
    try {
      def notification = [
        id            : interest.id,
        type          : 'INTEREST_RESPONDED',
        receiverId    : interest.receiver.id,
        receiverName  : "${interest.receiver.firstName} ${interest.receiver.lastName}",
        receiverAvatar: "/user/avatar/${interest.receiver.id}",
        status        : interest.status.value,
        timestamp     : interest.respondedDate.time,
        dateResponded : interest.respondedDate.format("MMM dd, yyyy 'at' hh:mm a")
      ]

      // Send to sender
      messagingTemplate.convertAndSendToUser(
        interest.sender.id.toString(),
        "/queue/interest-responses",
        notification
      )

      log.info("Real-time interest response notification sent to user ${interest.sender.id}")

    } catch (Exception e) {
      log.error("Failed to send real-time interest response notification: ${e.message}", e)
    }
  }

  /**
   * Send typing indicator for chat
   */
  void sendTypingIndicator(Long senderId, Long receiverId, boolean isTyping) {
    try {
      def notification = [
        senderId : senderId,
        isTyping : isTyping,
        timestamp: System.currentTimeMillis()
      ]

      messagingTemplate.convertAndSendToUser(
        receiverId.toString(),
        "/queue/typing",
        notification
      )
    } catch (Exception e) {
      log.error("Failed to send typing indicator: ${e.message}", e)
    }
  }

  /**
   * Get unread interest count for user
   */
  Integer getUnreadInterestCount(Long userId) {
    return Interest.countByReceiverIdAndStatusAndLastUpdatedGreaterThan(
      userId,
      InterestStatus.PENDING,
      new Date() - 30 // Last 30 days
    )
  }

  /**
   * Send online status update
   */
  void sendOnlineStatusUpdate(Long userId, boolean isOnline) {
    try {
      def statusUpdate = [
        userId   : userId,
        isOnline : isOnline,
        timestamp: System.currentTimeMillis()
      ]

      // Broadcast to all connected users (or specific friends/connections)
      messagingTemplate.convertAndSend("/topic/user-status", statusUpdate)

    } catch (Exception e) {
      log.error("Failed to send online status update: ${e.message}", e)
    }
  }
}
