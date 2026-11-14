package com.murad.app.matrimony

import com.murad.app.matrimony.background.GlassBackgroundCosmicTagLib
import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class GlassBackgroundCosmicTagLibSpec extends Specification implements TagLibUnitTest<GlassBackgroundCosmicTagLib> {

  void "test simple tag as method"() {
    expect:
    tagLib.simple()
  }
}
