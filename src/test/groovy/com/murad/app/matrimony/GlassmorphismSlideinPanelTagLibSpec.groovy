package com.murad.app.matrimony

import com.murad.app.matrimony.slideinsheet.GlassSlideinPanelTagLib
import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class GlassmorphismSlideinPanelTagLibSpec extends Specification implements TagLibUnitTest<GlassSlideinPanelTagLib> {

  void "test simple tag as method"() {
    expect:
    tagLib.simple()
  }
}
