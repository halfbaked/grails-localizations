package grails.localizations

import grails.util.Holders
import org.grails.plugins.localization.Localization

class BootStrap {

    def init = { servletContext ->
        if (Holders.config?.grails?.plugin?.localizations?.enabled) {
            if (Holders.config?.grails?.plugin?.localizations?.reloadAll) {
                Localization.reload()
            } else {
                Localization.load()
            }
        }
    }
    def destroy = {
    }
}
