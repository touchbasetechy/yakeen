package com.murad.app.matrimony

import com.murad.app.matrimony.SearchService
import grails.testing.services.ServiceUnitTest
import spock.lang.Specification

class SearchServiceSpec extends Specification implements ServiceUnitTest<SearchService> {

  void "test something"() {
    expect:
    service.doSomething()
  }
}
