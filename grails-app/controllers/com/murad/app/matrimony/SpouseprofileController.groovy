package com.murad.app.matrimony

class SpouseprofileController {

  def readSpouseProfile() {
    Integer personId = params?.personId?.toLong()

    Person person = Person.findById(personId)
    render(view: "readSpouseProfile", model: [spouse: person]) // Pass data to the GSP

  }
}
