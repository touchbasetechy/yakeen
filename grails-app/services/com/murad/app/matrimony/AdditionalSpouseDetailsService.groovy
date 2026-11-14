package com.murad.app.matrimony

import groovy.json.JsonSlurper
import groovy.transform.CompileStatic

@CompileStatic
class AdditionalSpouseDetailsService {

  def getEducations() {
    def jsonFile = getClass().classLoader.getResource('educations.json')
    // Read from resources
    if (!jsonFile) {
      throw new RuntimeException("educations.json file not found in the classpath")
    }

    // Parse the JSON file
    def jsonSlurper = new JsonSlurper()
    def educations = jsonSlurper.parse(jsonFile)

    educations
  }
}
