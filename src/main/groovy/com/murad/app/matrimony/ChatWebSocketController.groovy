package com.murad.app.matrimony

import com.murad.app.matrimony.websocket.ChatEvent
import com.murad.app.matrimony.websocket.ChatPayload
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller

/**
 * STOMP WebSocket controller.
 *
 * Clients publish to:  /app/chat.send          → persists & broadcasts message
 *                      /app/chat.join           → broadcasts join event
 *                      /app/chat.leave          → broadcasts leave event
 *                      /app/chat.typing         → broadcasts typing indicator
 *                      /app/chat.stopTyping     → clears typing indicator
 *
 * Clients subscribe to: /topic/chat.{roomId}
 */
@Controller
@Slf4j
class ChatWebSocketController {

  @Autowired
  ChatService chatService
  @Autowired
  SimpMessagingTemplate brokerTemplate

  @MessageMapping('/chat.send')
  void sendMessage(@Payload ChatPayload payload) {
    log.debug "chat.send from=${payload.sender} room=${payload.roomId}"
    def saved = chatService.saveMessage(payload)
    def event = chatService.buildEvent(payload, saved)
    brokerTemplate.convertAndSend("/topic/chat.${payload.roomId}", event)
  }

  @MessageMapping('/chat.join')
  void joinRoom(@Payload ChatPayload payload) {
    log.debug "chat.join from=${payload.sender} room=${payload.roomId}"
    def event = ChatEvent.presence(
      payload.sender,
      payload.senderDisplayName ?: payload.sender,
      payload.roomId, 'JOIN', nowIso()
    )
    brokerTemplate.convertAndSend("/topic/chat.${payload.roomId}", event)
  }

  @MessageMapping('/chat.leave')
  void leaveRoom(@Payload ChatPayload payload) {
    log.debug "chat.leave from=${payload.sender} room=${payload.roomId}"
    def event = ChatEvent.presence(
      payload.sender,
      payload.senderDisplayName ?: payload.sender,
      payload.roomId, 'LEAVE', nowIso()
    )
    brokerTemplate.convertAndSend("/topic/chat.${payload.roomId}", event)
  }

  @MessageMapping('/chat.typing')
  void typing(@Payload ChatPayload payload) {
    def event = new ChatEvent(
      sender: payload.sender,
      senderDisplayName: payload.senderDisplayName ?: payload.sender,
      roomId: payload.roomId,
      eventType: 'TYPING',
      messageType: 'SYSTEM',
      content: '',
      timestamp: nowIso()
    )
    brokerTemplate.convertAndSend("/topic/chat.${payload.roomId}", event)
  }

  @MessageMapping('/chat.stopTyping')
  void stopTyping(@Payload ChatPayload payload) {
    def event = new ChatEvent(
      sender: payload.sender,
      senderDisplayName: payload.senderDisplayName ?: payload.sender,
      roomId: payload.roomId,
      eventType: 'STOP_TYPING',
      messageType: 'SYSTEM',
      content: '',
      timestamp: nowIso()
    )
    brokerTemplate.convertAndSend("/topic/chat.${payload.roomId}", event)
  }

  private static String nowIso() {
    new Date().format("yyyy-MM-dd'T'HH:mm:ss'Z'")
  }
}
