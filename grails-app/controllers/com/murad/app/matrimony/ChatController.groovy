package com.murad.app.matrimony

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured
import groovy.util.logging.Slf4j
import org.springframework.http.HttpStatus

/**
 * HTTP endpoints consumed by the GSP chat UI.
 *
 *   GET  /chat                     → landing: list rooms
 *   GET  /chat/room/{roomId}       → chat room view
 *   GET  /chat/history/{roomId}    → JSON: last 50 messages
 *   DELETE /chat/message/{id}      → soft-delete own message
 */
@Secured(['IS_AUTHENTICATED_FULLY'])
@Slf4j
class ChatController {

  def chatService
  def springSecurityService

  static defaultAction = 'index'
  static allowedMethods = [
    history      : 'GET',
    deleteMessage: 'DELETE'
  ]

  // ── GET /chat ─────────────────────────────────────────────────────────────

  def index() {
    def rooms = chatService.listRooms()
    // Seed default rooms on first run
    if (!rooms) {
      ['general', 'introductions', 'matrimony-talk'].each { slug ->
        chatService.findOrCreateRoom(slug, slug.replace('-', ' ').capitalize())
      }
      rooms = chatService.listRooms()
    }
    [rooms: rooms, currentUser: currentUser()]
  }

  // ── GET /chat/room/{roomId} ────────────────────────────────────────────────

  def room(String roomId) {
    if (!roomId) {
      redirect action: 'index'
      return
    }
    def chatRoom = chatService.findOrCreateRoom(roomId)
    def rooms = chatService.listRooms()
    [
      chatRoom   : chatRoom,
      rooms      : rooms,
      currentUser: currentUser()
    ]
  }

  // ── GET /chat/history/{roomId} ─────────────────────────────────────────────

  def history(String roomId) {
    if (!roomId) {
      render status: HttpStatus.BAD_REQUEST.value()
      return
    }
    int max = params.int('max', 50)
    int offset = params.int('offset', 0)
    def msgs = chatService.getHistoryAsEvents(roomId, max)
    render msgs as JSON
  }

  // ── DELETE /chat/message/{id} ─────────────────────────────────────────────

  def deleteMessage(Long id) {
    chatService.softDeleteMessage(id, currentUser())
    render status: HttpStatus.NO_CONTENT.value()
  }

  // ── helpers ───────────────────────────────────────────────────────────────

  private String currentUser() {
    springSecurityService?.currentUser?.username ?: 'anonymous'
  }
}
