package com.murad.app.matrimony

import groovy.json.JsonSlurper
import groovy.transform.CompileStatic

@CompileStatic
class HeightService {

  def getHeights() {
    def jsonFile = getClass().classLoader.getResource('heights.json')
    // Read from resources
    if (!jsonFile) {
      throw new RuntimeException("heights.json file not found in the classpath")
    }

    // Parse the JSON file
    def jsonSlurper = new JsonSlurper()
    def heights = jsonSlurper.parse(jsonFile)

    heights
  }

}
