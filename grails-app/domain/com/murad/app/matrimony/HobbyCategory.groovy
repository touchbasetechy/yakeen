package com.murad.app.matrimony

class HobbyCategory {

  String code            // stable key from JSON, e.g. "creative_arts"
  String name            // display label, e.g. "Creative & Arts"
  String description
  Integer displayOrder = 0
  Date dateCreated = new Date()
  Date lastUpdated

  static hasMany = [hobbies: Hobby]

  static constraints = {
    code blank: false, unique: true, maxSize: 60
    name blank: false, maxSize: 100
    description nullable: true, maxSize: 255
    displayOrder min: 0
  }

  static mapping = {
    table 'hobby_category'
    hobbies sort: 'name', order: 'asc'
  }

  String toString() { name }
}
