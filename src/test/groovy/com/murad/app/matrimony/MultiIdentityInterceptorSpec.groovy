package com.murad.app.matrimony

import grails.testing.web.interceptor.InterceptorUnitTest
import spock.lang.Specification

class MultiIdentityInterceptorSpec extends Specification implements InterceptorUnitTest<MultiIdentityInterceptor> {

    void "test interceptor matching"() {
        when:
        withRequest(controller: "multiidentity")

        then:
        interceptor.doesMatch()
    }
}
