package com.murad.app.matrimony.websocket

import groovy.transform.Canonical

/**
 * Inbound payload arriving from the browser over STOMP/WebSocket.
 * Maps to the JSON the client publishes to /app/chat.send
 */
@Canonical
class ChatPayload {
  String sender
  String senderDisplayName
  String roomId
  String content
  String messageType = 'TEXT'
}
