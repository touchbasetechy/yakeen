package com.murad.app.matrimony

class BootStrap {

  def geoLocationService
  def religionService
  def countryCodeService

  def init = { servletContext ->
    // Fail fast: if a data file is missing/invalid, the app refuses to start
    // instead of failing on the first user request.
    religionService.loadReligionsWithCaste()
    countryCodeService.loadCountryCodes()
    countryCodeService.loadWorldCurrencies()
  }


  def destroy = {
    geoLocationService.shutdown()
  }
}

