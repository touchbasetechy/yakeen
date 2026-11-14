package com.murad.app.matrimony

import com.murad.app.matrimony.websocket.ChatEvent
import com.murad.app.matrimony.websocket.ChatPayload
import grails.gorm.transactions.Transactional
import groovy.util.logging.Slf4j

@Slf4j
@Transactional
class ChatService {

  // ── persistence ──────────────────────────────────────────────────────────

  ChatMessage saveMessage(ChatPayload payload) {
    def msg = new ChatMessage(
      sender: payload.sender,
      senderDisplayName: payload.senderDisplayName ?: payload.sender,
      roomId: payload.roomId,
      content: payload.content,
      messageType: payload.messageType ?: 'TEXT'
    )
    if (!msg.save(flush: true)) {
      log.warn "ChatMessage save failed: ${msg.errors}"
    }
    msg
  }

  @Transactional(readOnly = true)
  List<ChatMessage> getHistory(String roomId, int max = 50, int offset = 0) {
    ChatMessage.findAllByRoomIdAndDeleted(roomId, false,
      [max: max, offset: offset, sort: 'dateCreated', order: 'asc'])
  }

  @Transactional(readOnly = true)
  List<Map> getHistoryAsEvents(String roomId, int max = 50) {
    getHistory(roomId, max).collect { ChatMessage m ->
      [
        id               : m.id,
        sender           : m.sender,
        senderDisplayName: m.senderDisplayName,
        roomId           : m.roomId,
        content          : m.content,
        messageType      : m.messageType,
        timestamp        : m.dateCreated?.format("yyyy-MM-dd'T'HH:mm:ss'Z'"),
        eventType        : 'MESSAGE'
      ]
    }
  }

  void softDeleteMessage(Long id, String requestingUser) {
    def msg = ChatMessage.get(id)
    if (msg && msg.sender == requestingUser) {
      msg.deleted = true
      msg.save(flush: true)
    }
  }

  // ── rooms ─────────────────────────────────────────────────────────────────

  @Transactional(readOnly = true)
  List<ChatRoom> listRooms() {
    ChatRoom.findAllByPrivateRoom(false, [sort: 'name', order: 'asc'])
  }

  ChatRoom findOrCreateRoom(String roomId, String name = null) {
    ChatRoom.findByRoomId(roomId) ?: new ChatRoom(
      roomId: roomId,
      name: name ?: roomId.capitalize(),
      privateRoom: false
    ).save(flush: true)
  }

  // ── event factory ─────────────────────────────────────────────────────────

  ChatEvent buildEvent(ChatPayload payload, ChatMessage saved) {
    ChatEvent.fromPayload(payload, saved?.id,
      saved?.dateCreated?.format("yyyy-MM-dd'T'HH:mm:ss'Z'") ?: nowIso())
  }

  private static String nowIso() {
    new Date().format("yyyy-MM-dd'T'HH:mm:ss'Z'")
  }
}
