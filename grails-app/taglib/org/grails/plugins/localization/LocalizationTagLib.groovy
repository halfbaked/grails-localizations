package org.grails.plugins.localization

import org.grails.plugins.localization.*

class LocalizationTagLib {

    def localizationService

    def localizationHelpBalloons = {attrs, body ->
        if (localizationService.hasPlugin("helpBalloons")) {
            out << g.helpBalloons(attrs)
        }
    }

    def localizationHelpBalloon = {attrs, body ->
        if (localizationService.hasPlugin("helpBalloons")) {
            out << g.helpBalloon(attrs)
        }
    }

    def localizationCriteria = {attrs, body ->
        if (localizationService.hasPlugin("criteria")) {
            out << """<div class="criteria">\n"""
            out << g.criteria(attrs)
            out << """</div>\n"""
        }
    }

    def localizationPaginate = {attrs, body ->
        attrs.total = localizationService.hasPlugin("criteria") || localizationService.hasPlugin("drilldown") ? Localization.selectCount(session, params) : Localization.count()
        out << g.paginate(attrs)
    }

    def localizationMenuButton = {attrs, body ->
        if (localizationService.hasPlugin("menus")) {
            out << '<span class="menuButton">'
            out << g.link(class: "menu", controller: "menu", action: "display") {
                g.message(code: "menu.display", default: "Menu")
            }
            out << '</span>'
        }
    }
}
