package com.murad.app.matrimony

import groovy.json.JsonSlurper
import groovy.transform.CompileStatic

@CompileStatic
class ReligionService {

  // volatile: the fully-built list is published safely to all request threads
  private volatile List<Map> religionsWithCasteCache

  /** Called once at startup by BootStrap (and by refreshReligionsWithCaste()). */
  void loadReligionsWithCaste() {
    def jsonUrl = getClass().classLoader.getResource('religionsWithCaste.json')
    if (!jsonUrl) {
      throw new RuntimeException("religionsWithCaste.json file not found in the classpath")
    }

    List<Map> parsed = (List<Map>) new JsonSlurper().parse(jsonUrl)

    // Assign only after parsing fully succeeds, so readers never see a half-built list
    religionsWithCasteCache = Collections.unmodifiableList(parsed)
  }

  /** What controllers call. Returns the cached list; no file I/O after startup. */
  List<Map> getReligionsWithCaste() {
    List<Map> cached = religionsWithCasteCache
    if (cached == null) {
      // Safety net only (e.g. tests that skip BootStrap). Normal path never gets here.
      synchronized (this) {
        if (religionsWithCasteCache == null) {
          loadReligionsWithCaste()
        }
        cached = religionsWithCasteCache
      }
    }
    cached
  }

  /** Call this if the JSON changes at runtime and you want to reload it. */
  void refreshReligionsWithCaste() {
    loadReligionsWithCaste()
  }
}
