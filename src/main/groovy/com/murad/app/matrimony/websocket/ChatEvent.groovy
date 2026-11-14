package com.murad.app.matrimony.websocket

import groovy.transform.Canonical

/**
 * Outbound payload broadcast to /topic/chat.{roomId}
 */
@Canonical
class ChatEvent {
  Long id
  String sender
  String senderDisplayName
  String roomId
  String content
  String messageType
  String timestamp         // ISO-8601 string for JS Date
  String eventType         // MESSAGE | JOIN | LEAVE | TYPING | STOP_TYPING

  static ChatEvent fromPayload(ChatPayload p, Long id, String ts) {
    new ChatEvent(
      id: id,
      sender: p.sender,
      senderDisplayName: p.senderDisplayName ?: p.sender,
      roomId: p.roomId,
      content: p.content,
      messageType: p.messageType ?: 'TEXT',
      timestamp: ts,
      eventType: 'MESSAGE'
    )
  }

  static ChatEvent presence(String username, String displayName, String roomId, String type, String ts) {
    new ChatEvent(
      sender: username,
      senderDisplayName: displayName,
      roomId: roomId,
      content: "${displayName} has ${type == 'JOIN' ? 'joined' : 'left'} the room.",
      messageType: 'SYSTEM',
      timestamp: ts,
      eventType: type
    )
  }
}
