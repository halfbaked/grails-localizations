grails-localization
===================

The localizations plugin alters Grails to use the database as its means of
internationalization rather than message bundles (property files) in the i18n
directory of your application. All property files in the i18n directory of your
application (but not subdirectories of i18n) are automatically loaded in to the
database the first time a message is requested after the plugin is installed.

Importing
----------

There is also an import facility (at a URL similar to
http://myServer/myApp/localizations/imports) to load subsequently created
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
http://myServer/myApp/localizations/cache

