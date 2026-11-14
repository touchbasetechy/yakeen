// src/main/groovy/com/murad/app/matrimony/identity/LoggedInIdentity.groovy
package com.murad.app.matrimony.identity

class LoggedInIdentity implements Serializable {
  int slot
  Long personId
  String email
  String displayName
  String controller
  String action
  Date loggedInAt = new Date()
}
