package com.murad.app.matrimony

import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.annotation.Secured

@Secured('permitAll')
class LocationController {

    GeoLocationService geoLocationService

    def index() {
        // renders the GSP with the JS geolocation prompt
    }

    // Called via AJAX when browser geolocation succeeds
    @Transactional
    def saveBrowserLocation() {
        def lat = params.double('lat')
        def lng = params.double('lng')

        String ipAddress = getClientIp()

        def location = new UserLocation(
                ipAddress: ipAddress,
                latitude: lat,
                longitude: lng,
                source: "browser"
        )

        if (location.save(flush: true)) {
            respond([success: true, location: location], formats: ['json'])
        } else {
            def errors = location.errors.allErrors.collect { it.toString() }
            log.warn("UserLocation validation failed: ${errors}")
            response.status = 422
            respond([success: false, errors: errors], formats: ['json'])
        }
    }

    // Called via AJAX when browser geolocation is denied/unavailable
    @Transactional
    def saveIpLocation() {
        String ipAddress = getClientIp()
        def geo = geoLocationService.getLocationFromIp(ipAddress)

        if (geo) {
            def location = new UserLocation(
                    ipAddress: ipAddress,
                    latitude: geo.latitude,
                    longitude: geo.longitude,
                    city: geo.city,
                    country: geo.country,
                    source: geo.source
            )

            if (location.save(flush: true)) {
                respond([success: true, location: location], formats: ['json'])
            } else {
                def errors = location.errors.allErrors.collect { it.toString() }
                log.warn("UserLocation validation failed: ${errors}")
                response.status = 422
                respond([success: false, errors: errors], formats: ['json'])
            }
        } else {
            render(status: 500, text: "Could not determine location")
        }
    }

    private String getClientIp() {
        String forwarded = request.getHeader('X-Forwarded-For')
        if (forwarded) {
            return forwarded.split(',')[0].trim()
        }
        return request.remoteAddr
    }
}
