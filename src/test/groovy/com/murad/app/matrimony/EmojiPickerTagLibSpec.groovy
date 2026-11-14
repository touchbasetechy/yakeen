package com.murad.app.matrimony

import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class EmojiPickerTagLibSpec extends Specification implements TagLibUnitTest<EmojiPickerTagLib> {

     void "test simple tag as method"() {
       expect:
       tagLib.simple()
     }
}
