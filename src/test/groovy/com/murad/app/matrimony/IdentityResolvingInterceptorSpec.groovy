package com.murad.app.matrimony

import grails.testing.web.interceptor.InterceptorUnitTest
import spock.lang.Specification

class IdentityResolvingInterceptorSpec extends Specification implements InterceptorUnitTest<IdentityResolvingInterceptor> {

    void "test interceptor matching"() {
        when:
        withRequest(controller: "identityresolving")

        then:
        interceptor.doesMatch()
    }
}
