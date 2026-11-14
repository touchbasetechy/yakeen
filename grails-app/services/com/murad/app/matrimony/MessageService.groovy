package com.murad.app.matrimony

import grails.gorm.transactions.Transactional
import groovy.transform.CompileDynamic

@CompileDynamic
// GORM dynamic finders (findAllBy*, countBy*, findAll HQL) are
@Transactional
// resolved at runtime — @CompileStatic cannot see them at compile time.
class MessageService {

  Message saveMessage(Map<String, Object> messageData) {
    Message message = new Message()
    message.content = messageData.content as String
    message.messageType = messageData.messageType ?: 'TEXT'
    message.sender = Person.get(messageData.senderId as Long)

    if (messageData.chatRoomId) {
      message.chatRoom = ChatRoom.get(messageData.chatRoomId as Long)
    }

    if (messageData.recipientId) {
      message.recipient = Person.get(messageData.recipientId as Long)
    }

    message.timestamp = new Date()
    message.attachmentUrl = messageData.attachmentUrl as String

    message.save(flush: true, failOnError: true)
    return message
  }

  Message saveMessage(Message message) {
    message.timestamp = new Date()
    message.save(flush: true, failOnError: true)
    return message
  }

  List<Map<String, Object>> getRoomMessages(Long roomId, int max = 50) {
    List<Message> messages = Message.findAllByChatRoom(
      ChatRoom.get(roomId),
      [max: max, sort: 'timestamp', order: 'desc']
    )
    return messages.collect { Message message ->
      [
        id       : message.id,
        content  : message.content,
        sender   : message.sender.firstName + " " + message.sender.lastName,
        timestamp: message.timestamp,
        read     : message.read
      ] as Map<String, Object>
    }
  }

  List<Map<String, Object>> getPrivateMessages(Long userId1, Long userId2, int max = 50) {
    List<Message> messages = Message.findAllBySenderAndRecipientOrSenderAndRecipient(
      Person.get(userId1), Person.get(userId2),
      Person.get(userId2), Person.get(userId1),
      [max: max, sort: 'timestamp', order: 'desc']
    )
    return messages.collect { Message message ->
      [
        id        : message.id,
        content   : message.content,
        senderId  : message.sender.id,
        senderName: message.sender.firstName + " " + message.sender.lastName,
        timestamp : message.timestamp,
        read      : message.read
      ] as Map<String, Object>
    }
  }

  Map<String, Object> getUnreadCount(Long userId) {
    Long count = Message.countByRecipientAndRead(Person.get(userId), false)
    return [unreadCount: count] as Map<String, Object>
  }

  List<Map<String, Object>> getRecentChats(Long userId) {
    Person user = Person.get(userId)

    List<Message> recentMessages = Message.findAll(
      "FROM Message m WHERE m.sender = :user OR m.recipient = :user ORDER BY m.timestamp DESC",
      [user: user, max: 100]
    )

    // Group by conversation partner
    Map<Long, Map<String, Object>> conversations = [:]

    recentMessages.each { Message msg ->
      Long partnerId = msg.sender.id == userId ? msg.recipient?.id : msg.sender.id
      if (partnerId) {
        if (!conversations.containsKey(partnerId)) {
          conversations[partnerId] = [
            partner    : Person.get(partnerId),
            lastMessage: msg,
            unreadCount: 0
          ] as Map<String, Object>
        }
        if (!msg.read && msg.recipient?.id == userId) {
          conversations[partnerId].unreadCount = (conversations[partnerId].unreadCount as int) + 1
        }
      }
    }

    return conversations.values().sort { Map<String, Object> it ->
      -((it.lastMessage as Message).timestamp.time)
    }
  }
}
