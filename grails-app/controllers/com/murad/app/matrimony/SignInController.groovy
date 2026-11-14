package com.murad.app.matrimony

import grails.gorm.transactions.Transactional

import java.time.LocalDateTime

class SignInController {

  def index() {
    // Clear any existing session if coming here directly
//    if (!params?.loginAttempt) {
//      session.invalidate()
//    }
//    render(view: "/signIn/index")
  }

  @Transactional
  def login() {
    Person person = Person.findByEMail(params?.eMail)

    if (!person) {
      flash.error = "User does not exist."
      redirect(action: "index", params: [loginAttempt: true])
      return
    }

    if (person?.password != params?.password) {
      flash.error = "Invalid credentials."
      redirect(action: "index", params: [loginAttempt: true])
      return
    }

    def signInLog = SignInLog.findByUser(person) ?: new SignInLog(user: person)
    signInLog.loggedInAt = LocalDateTime.now()
    signInLog.status = "Online"

    if (!signInLog.save(flush: true)) {
      flash.error = "Login failed. Please try again."
      redirect(action: "index")
      return
    }

    session.personId = person.id

    def redirectMap = [
      User : [controller: "DBoard", action: "index"],
      Admin: [controller: "person", action: "index"],
      Super: [controller: "AdminDBoard", action: "index"]
    ]

    def redirectAction = redirectMap[person?.personType?.personTypeName]
    redirect(redirectAction ?: [controller: "signIn", action: "index"])
  }

  @Transactional
  def logOut() {
    def personId = session?.personId

    try {
      if (personId) {
        Person person = Person.read(personId)
        if (person) {
          def signInLog = SignInLog.findByUser(person)
          if (signInLog) {
            signInLog.loggedOutAt = LocalDateTime.now()
            signInLog.status = "Offline"
            signInLog.save(flush: true)
          }
        }
      }
    } catch (Exception e) {
      log.error("Error during logout", e)
    } finally {
      session.invalidate()
      redirect(action: "index")
    }
  }
}
