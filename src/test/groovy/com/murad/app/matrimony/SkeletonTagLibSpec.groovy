package com.murad.app.matrimony

import com.murad.app.matrimony.page.SkeletonTagLib
import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class SkeletonTagLibSpec extends Specification implements TagLibUnitTest<SkeletonTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
