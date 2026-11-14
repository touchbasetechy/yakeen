package com.murad.app.matrimony

import com.murad.app.matrimony.toggle.GlassDotMeshTagLib
import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class GlassDotMeshTagLibSpec extends Specification implements TagLibUnitTest<GlassDotMeshTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
