package com.murad.app.matrimony

import grails.testing.web.interceptor.InterceptorUnitTest
import spock.lang.Specification

class ConnectivityInterceptorSpec extends Specification implements InterceptorUnitTest<ConnectivityInterceptor> {

    void "test interceptor matching"() {
        when:
        withRequest(controller: "connectivity")

        then:
        interceptor.doesMatch()
    }
}
