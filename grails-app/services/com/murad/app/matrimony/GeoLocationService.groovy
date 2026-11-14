package com.murad.app.matrimony

import com.maxmind.geoip2.DatabaseReader
import com.maxmind.geoip2.model.CityResponse
import groovy.util.logging.Slf4j

import java.util.concurrent.ConcurrentHashMap

@Slf4j
class GeoLocationService {

    def grailsApplication

    private DatabaseReader dbReader
    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>()
    private static final long CACHE_TTL_MS = 24 * 60 * 60 * 1000 // 24 hours

    private static class CacheEntry {
        Map data
        long timestamp
    }

    // Lazily initialise the reader once, reused across requests
    private DatabaseReader getReader() {
        if (!dbReader) {
            synchronized (this) {
                if (!dbReader) {
                    String path = grailsApplication.config.getProperty('geoip.database.path',
                            String, 'src/main/resources/geoip/GeoLite2-City.mmdb')
                    File dbFile = new File(path)
                    if (!dbFile.exists()) {
                        log.error("GeoLite2 database not found at ${path}")
                        return null
                    }
                    dbReader = new DatabaseReader.Builder(dbFile).withCache(new com.maxmind.db.CHMCache()).build()
                    log.info("Loaded GeoLite2 database from ${path}")
                }
            }
        }
        dbReader
    }

    Map getLocationFromIp(String ip) {
        if (!ip || ip == "0:0:0:0:0:0:0:1" || ip == "127.0.0.1") {
            return null // can't look up localhost in a real geo database
        }

        def cached = cache.get(ip)
        if (cached && (System.currentTimeMillis() - cached.timestamp) < CACHE_TTL_MS) {
            return cached.data
        }

        try {
            DatabaseReader reader = getReader()
            if (!reader) return fallbackToRemoteApi(ip)

            InetAddress inetAddress = InetAddress.getByName(ip)
            CityResponse response = reader.city(inetAddress)

            def result = [
                    latitude : response.location.latitude,
                    longitude: response.location.longitude,
                    city     : response.city?.name,
                    country  : response.country?.name,
                    source   : "maxmind"
            ]

            cache.put(ip, new CacheEntry(data: result, timestamp: System.currentTimeMillis()))
            return result

        } catch (com.maxmind.geoip2.exception.AddressNotFoundException e) {
            log.warn("IP not found in GeoLite2 database: ${ip}")
            return null
        } catch (Exception e) {
            log.error("MaxMind lookup failed for ${ip}: ${e.message}")
            return fallbackToRemoteApi(ip)
        }
    }

    // Optional safety net if the local DB fails or is missing
    private Map fallbackToRemoteApi(String ip) {
        try {
            def url = "http://ip-api.com/json/${ip}".toURL()
            def json = new groovy.json.JsonSlurper().parse(url)
            if (json.status == "success") {
                def result = [
                        latitude : json.lat,
                        longitude: json.lon,
                        city     : json.city,
                        country  : json.country,
                        source   : "ip-api-fallback"
                ]
                cache.put(ip, new CacheEntry(data: result, timestamp: System.currentTimeMillis()))
                return result
            }
        } catch (Exception e) {
            log.error("Fallback IP lookup also failed: ${e.message}")
        }
        return null
    }

    // Called by GeoIpUpdateJob after a fresh database file has been downloaded
    void reload() {
        synchronized (this) {
            dbReader?.close()
            dbReader = null // will be lazily reopened on next lookup via getReader()
        }
        log.info("GeoLocationService reloaded with updated database")
    }

    void shutdown() {
        dbReader?.close()
    }
}
