package com.murad.app.matrimony

import groovy.json.JsonSlurper
import groovy.transform.CompileStatic

@CompileStatic
class CountriesService {

  def getCountries() {
    def jsonFile = getClass().classLoader.getResource('Countries.json')
    //  def countriesJson = grailsApplication.getParentContext().getResource("classpath:CountryCodesWithFlags.json").getText()
    // Read from resources
    if (!jsonFile) {
      throw new RuntimeException("Countries.json file not found in the classpath")
    }

    // Parse the JSON file
    def jsonSlurper = new JsonSlurper()
    def countries = jsonSlurper.parse(jsonFile)

    countries
  }
}
