package com.murad.app.matrimony

import groovy.json.JsonSlurper
import groovy.transform.CompileStatic

@CompileStatic
class CountryCodeService {

  static final int DEFAULT_MAX = 10

  private static final String COUNTRY_CODES_FILE = 'CountryCodesWithFlags.json'
  private static final String CURRENCIES_FILE = 'currencies.json'

  private volatile List<Map> countryCodesCache
  private volatile List<Map> worldCurrenciesCache

  // ── Startup loaders (called by BootStrap) ─────────────────────────

  void loadCountryCodes() {
    countryCodesCache = readJsonList(COUNTRY_CODES_FILE)
  }

  void loadWorldCurrencies() {
    worldCurrenciesCache = readJsonList(CURRENCIES_FILE)
  }

  // ── Country codes: full list ──────────────────────────────────────

  List<Map> getAllCountryCodes() {
    List<Map> cached = countryCodesCache
    if (cached == null) {
      synchronized (this) {
        if (countryCodesCache == null) {
          loadCountryCodes()
        }
        cached = countryCodesCache
      }
    }
    cached
  }

  // ── Country codes: paged ──────────────────────────────────────────

  /** No args: the first page (offset 0, DEFAULT_MAX entries). */
  List<Map> getCountryCodes() {
    getCountryCodes(0, DEFAULT_MAX)
  }

  /** Entries starting at offset, at most max of them. */
  List<Map> getCountryCodes(int offset, int max) {
    paginate(getAllCountryCodes(), offset, max)
  }

  int getCountryCodesCount() {
    getAllCountryCodes().size()
  }

  // ── Currencies (unchanged: still returns everything) ──────────────

  List<Map> getWorldCurrencies() {
    List<Map> cached = worldCurrenciesCache
    if (cached == null) {
      synchronized (this) {
        if (worldCurrenciesCache == null) {
          loadWorldCurrencies()
        }
        cached = worldCurrenciesCache
      }
    }
    cached
  }

  void refreshCountryCodes() {
    loadCountryCodes()
  }

  void refreshWorldCurrencies() {
    loadWorldCurrencies()
  }

  // ── Helpers ───────────────────────────────────────────────────────

  private List<Map> readJsonList(String resourceName) {
    def jsonUrl = getClass().classLoader.getResource(resourceName)
    if (!jsonUrl) {
      throw new RuntimeException("${resourceName} file not found in the classpath")
    }
    List<Map> parsed = (List<Map>) new JsonSlurper().parse(jsonUrl)
    Collections.unmodifiableList(parsed)
  }

  private static List<Map> paginate(List<Map> all, int offset, int max) {
    int from = Math.max(offset, 0)
    int limit = max > 0 ? max : DEFAULT_MAX
    if (from >= all.size()) {
      return Collections.<Map> emptyList()
    }
    int to = (int) Math.min((long) from + limit, (long) all.size())
    Collections.unmodifiableList(new ArrayList<Map>(all.subList(from, to)))
  }
}
