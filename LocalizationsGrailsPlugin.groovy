import org.grails.plugins.localization.*

class LocalizationsGrailsPlugin {
    def version = "1.4.4.2"
    def grailsVersion = "2.0 > *"
    def dependsOn = [:]
    def author = "Paul Fernley"
    def authorEmail = "paul@pfernley.orangehome.co.uk"
    def title = "Localizations (messages) plugin"
    def description = '''\
This plugin will pull i18n definitions from the database rather than from the standard properties files in the i18n folder.

It will do the following:
* Create a domain class and corresponding table called Localization
* Prepopulate the table with all the message properties it finds in the i18n folder
* Ensure Grails writes i18n messages based on what it finds in the database rather than the 118n folder

In addtion the plugin also has these added features to help you:
* A CRUD UI to add, delete, and update i18n messages
* A cache for increased speed 
* A JSONP action which can be useful in client-side templating.

Asumptions:
* Your database supports unicode
* Your application has a layout called main
'''

    // URL to the plugin's documentation
    def documentation = "https://github.com/halfbaked/grails-localizations"
    def issueManagement = [ system: "GitHub", url: "https://github.com/halfbaked/grails-localizations/issues" ]
    def developers = [ [ name: "Eamonn O'Connell", email: "eamonnoconnell@gmail.com" ]]
    def scm = [ url: "https://github.com/halfbaked/grails-localizations" ]

    def doWithSpring = {
        // Implement runtime spring config (optional)
        messageSource(LocalizationMessageSource)
    }

    def doWithDynamicMethods = { ctx ->
        application.domainClasses.each { domainClass ->
            domainClass.metaClass.message = {Map parameters -> Localization.getMessage(parameters)}
            domainClass.metaClass.errorMessage = {Map parameters -> Localization.setError(delegate, parameters)}
        }

        application.serviceClasses.each { serviceClass ->
            serviceClass.metaClass.message = { Map parameters -> Localization.getMessage(parameters) }
        }
    }

}
