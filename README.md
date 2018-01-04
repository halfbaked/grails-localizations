grails-localization
===================

The localizations plugin alters Grails to use the database as its means of
internationalization rather than message bundles (property files) in the i18n
directory of your application. All property files in the i18n directory of your
application (but not subdirectories of i18n) are automatically loaded in to the
database the first time a message is requested after the plugin is installed.

##Installation

Add dependency to your build.gradle for > Grails 3.2.x:

```
repositories {
  ...
  maven { url "http://dl.bintray.com/sachinverma/plugins" }
}

dependencies {
    compile 'org.grails.plugins:grails-localizations:0.1.3'
}
```


Add dependency to your BuildConfig.groovy for Grails 2.x:

```
plugins {
        compile ":localizations:2.4"
}
```
Source Code for Grails 2.x:
https://github.com/vsachinv/grails-localizations/tree/Plugin_2.X

####Enhancement for > Grails 3.2.x

You can enable/disable the localization value from DB using following configurations in `application.groovy` or `application.yml`

```
grails.plugin.localizations.enabled = true
```

Importing
----------

There is also an import facility (at a URL similar to
http://myServer/myApp/localization/imports) to load subsequently created
property files - often the result of later installation of plugins. A 'message'
method is added to all domain classes and service classes as a convenience. An
'errorMessage' method is added to all domain classes that can set an error
message on either the domain as a whole or on a particular property of the
domain. 

CRUD
----

A localizations controller and CRUD screens are included with the plugin.
The screens assume you are using a layout called main. Your database must be
configured to allow the use of Unicode data for this plugin to work.

Caching
-------

Localizations are cached for speed. You can reset the cache by going to 
http://myServer/myApp/localization/cache

