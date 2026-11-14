package com.murad.app.matrimony

import org.grails.datastore.gorm.GormEntity
import org.springframework.security.core.GrantedAuthority

class Role implements GrantedAuthority, GormEntity<Role> {

  String authority

  static mapping = {
    cache true
  }

  static constraints = {
    authority blank: false, unique: true
  }

  @Override
  String getAuthority() {
    authority
  }
}
