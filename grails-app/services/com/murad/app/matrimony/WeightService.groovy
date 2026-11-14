package com.murad.app.matrimony

import groovy.json.JsonSlurper
import groovy.transform.CompileStatic

@CompileStatic
class WeightService {

  def getHeights() {
    def jsonFile = getClass().classLoader.getResource('weights.json')
    // Read from resources
    if (!jsonFile) {
      throw new RuntimeException("weights.json file not found in the classpath")
    }

    // Parse the JSON file
    def jsonSlurper = new JsonSlurper()
    def weights = jsonSlurper.parse(jsonFile)

    weights
  }
}
