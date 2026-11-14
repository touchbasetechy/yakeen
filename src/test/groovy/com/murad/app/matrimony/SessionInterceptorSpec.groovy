package com.murad.app.matrimony

import grails.testing.web.interceptor.InterceptorUnitTest
import spock.lang.Specification

class SessionInterceptorSpec extends Specification implements InterceptorUnitTest<SessionInterceptor> {

    void "test interceptor matching"() {
        when:
        withRequest(controller: "session")

        then:
        interceptor.doesMatch()
    }
}
