package com.murad.app.matrimony

import grails.testing.web.interceptor.InterceptorUnitTest
import spock.lang.Specification

class DbHealthInterceptorSpec extends Specification implements InterceptorUnitTest<DbHealthInterceptor> {

    void "test interceptor matching"() {
        when:
        withRequest(controller: "dbhealth")

        then:
        interceptor.doesMatch()
    }
}
