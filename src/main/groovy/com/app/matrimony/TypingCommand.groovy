package com.app.matrimony

import grails.validation.Validateable

// Create a proper command object class for typing indicators
class TypingCommand implements Validateable {
  String message
  Long userId
  Long chatId
  boolean isTyping

  static constraints = {
    message nullable: true
    userId nullable: false
    chatId nullable: false
    isTyping nullable: false
  }
}
