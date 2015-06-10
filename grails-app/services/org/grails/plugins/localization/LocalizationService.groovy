package org.grails.plugins.localization

import org.grails.plugins.localization.*

class LocalizationService {

    boolean transactional = true

    def hasPlugin(name) {
        return grails.util.Holders.getPluginManager()?.hasGrailsPlugin(name)
    }
}
