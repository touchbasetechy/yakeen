package com.murad.app.matrimony

import grails.gorm.transactions.Transactional
import grails.validation.ValidationException


class SignUpController {

  def index() {}

  @Transactional
  def save() {
    def personType = PersonType.findByPersonTypeName("User")

    if (!personType) {
      flash.error = "Attempt to create user failed!"
      redirect(controller: "person", action: "index")
    } else {
      def person = new Person(firstName: params?.firstName, lastName: params?.lastName, eMail: params?.eMail, password: params?.password, passwordResetToken: params?.passwordResetToken, personType: personType)
      person.personType = personType

      try {
        def personSaved = person.save(flush: true)

        if (personSaved) {
          session.person = personSaved
          flash.message = "User created successfully!"
          redirect(controller: "DBoard", action: "index")
        } else {
          flash.error = "Failed creating user!"
          redirect(controller: "SignUp", action: "index")
        }
      } catch (ValidationException e) {
        flash.error = "User not created ${e.errors}"
        redirect(controller: "SignUp", action: "index")
      }
    }
  }

}
