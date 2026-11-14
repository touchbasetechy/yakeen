package com.murad.app.matrimony

import grails.testing.web.interceptor.InterceptorUnitTest
import spock.lang.Specification

class SessionInterceptorInterceptorSpec extends Specification implements InterceptorUnitTest<SessionInterceptorInterceptor> {

    void "test interceptor matching"() {
        when:
        withRequest(controller: "sessioninterceptor")

        then:
        interceptor.doesMatch()
    }
}
