package com.murad.app.matrimony


import grails.gorm.transactions.Transactional

@Transactional
class OAuth2Service {

  def springSecurityService

  Person findOrCreateUserFromFacebook(Map userInfo) {
    String facebookId = userInfo.id
    String email = userInfo.email
    String name = userInfo.name

    Person user = User.findByFacebookId(facebookId)

    if (!user && email) {
      user = User.findByEmail(email)
    }

    if (!user) {
      user = new Person(
        username: email ?: "facebook_${facebookId}",
        email: email,
        facebookId: facebookId
      )
      user.save(flush: true)
    } else if (!user.facebookId) {
      user.facebookId = facebookId
      user.save(flush: true)
    }

    return user
  }
}
