import org.grails.plugins.localization.*

class LocalizationsGrailsPlugin {
    def version = "1.4.2"
    def dependsOn = [:]

    // TODO Fill in these fields
    def author = "Paul Fernley"
    def authorEmail = "paul@pfernley.orangehome.co.uk"
    def title = "Localizations (messages) plugin"
    def description = '''\
The localizations plugin alters Grails to use the database as its means of
internationalization rather than message bundles (property files) in the i18n
directory of your application. All property files in the i18n directory of your
application (but not subdirectories of i18n) are automatically loaded in to the
database the first time a message is requested after the plugin is installed.
There is also an import facility (at a URL similar to
http://myServer/myApp/localizations/imports) to load subsequently created
property files - often the result of later installation of plugins. A 'message'
method is added to all domain classes and service classes as a convenience. An
'errorMessage' method is added to all domain classes that can set an error
message on either the domain as a whole or on a particular property of the
domain. A localizations controller and CRUD screens are included with the plugin.
The screens assume you are using a layout called main. Your database must be
configured to allow the use of Unicode data for this plugin to work.
'''

    // URL to the plugin's documentation
    def documentation = "https://github.com/halfbaked/grails-localization"
    def issueManagement = [ system: "GitHub", url: "https://github.com/halfbaked/grails-localization/issues" ]
    def developers = [ [ name: "Eamonn O'Connell", email: "eamonnoconnell@gmail.com" ]]
    def scm = [ url: "https://github.com/halfbaked/grails-stylus-resources" ]

    def doWithSpring = {
        // TODO Implement runtime spring config (optional)
        messageSource(LocalizationMessageSource)
    }

    def doWithApplicationContext = { applicationContext ->
        // TODO Implement post initialization spring config (optional)
    }

    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional)
    }

    def doWithDynamicMethods = { ctx ->
        application.domainClasses.each { domainClass ->
            domainClass.metaClass.message = {Map parameters -> Localization.getMessage(parameters)}
            domainClass.metaClass.errorMessage = {Map parameters -> Localization.setError(delegate, parameters)}
        }

        application.serviceClasses.each { serviceClass ->
            serviceClass.metaClass.message = {Map parameters -> Localization.getMessage(parameters)}
        }
    }

    def onChange = { event ->
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    def onConfigChange = { event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }
}
