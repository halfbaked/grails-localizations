package org.grails.plugins.localization

import grails.util.GrailsWebUtil
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.codehaus.groovy.grails.web.context.ServletContextHolder
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.support.WebApplicationContextUtils
import org.springframework.web.servlet.support.RequestContextUtils

class Localization {

    private static loaded = false
    private static cache = new LinkedHashMap((int) 16, (float) 0.75, (boolean) true)
    private static long maxCacheSize = 128L * 1024L // Cache size in KB (default is 128kb)
    private static long currentCacheSize = 0L
    private static final missingValue = "\b" // an impossible value signifying that no such code exists in the database
    private static final keyDelimiter = missingValue
    private static long cacheHits = 0L
    private static long cacheMisses = 0L

    String code
    String locale
    Byte relevance = 0
    String text
    Date dateCreated
    Date lastUpdated

    static mapping = {
        columns {
            code index: "localizations_idx"
            locale column: "loc", index: "localizations_idx"
        }
    }

    static constraints = {
        code(blank: false, size: 1..250)
        locale(size: 1..4, unique: 'code', blank: false, matches: "\\*|([a-z][a-z]([A-Z][A-Z])?)")
        relevance(validator: {val, obj ->
            if (obj.locale) obj.relevance = obj.locale.length()
            return true
        })
        text(blank: false, size: 1..2000)
    }

    def localeAsObj() {
      switch(locale.size()){
        case 4:
          return new Locale(locale[0..1], locale[2..3])
        case 2:
          return new Locale(locale)
        default:
          return null
      }  
    }

    static String decodeMessage(String code, Locale locale) {

        if (!loaded) Localization.load()

        def key = code + keyDelimiter + locale.getLanguage() + locale.getCountry()
        def msg
        if (maxCacheSize > 0) {
            synchronized (cache) {
                msg = cache.get(key)
                if (msg) {
                    cacheHits++
                } else {
                    cacheMisses++
                }
            }
        }

        if (!msg) {
            def lst = Localization.findAll(
                    "from org.grails.plugins.localization.Localization as x where x.code = ? and x.locale in ('*', ?, ?) order by x.relevance desc",
                    [code, locale.getLanguage(), locale.getLanguage() + locale.getCountry()])
            msg = lst.size() > 0 ? lst[0].text : missingValue

            if (maxCacheSize > 0) {
                synchronized (cache) {

                    // Put it in the cache
                    def prev = cache.put(key, msg)

                    // Another user may have inserted it while we weren't looking
                    if (prev != null) currentCacheSize -= key.length() + prev.length()

                    // Increment the cache size with our data
                    currentCacheSize += key.length() + msg.length()

                    // Adjust the cache size if required
                    if (currentCacheSize > maxCacheSize) {
                        def entries = cache.entrySet().iterator()
                        def entry
                        while (entries.hasNext() && currentCacheSize > maxCacheSize) {
                            entry = entries.next()
                            currentCacheSize -= entry.getKey().length() + entry.getValue().length()
                            entries.remove()
                        }
                    }
                }
            }
        }

        return (msg == missingValue) ? null : msg
    }

    static getMessage(parameters) {
        def requestAttributes = RequestContextHolder.getRequestAttributes()
        def applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(ServletContextHolder.getServletContext())
        boolean unbindRequest = false

        // Outside of an executing request, establish a mock version
        if (!requestAttributes) {
            requestAttributes = GrailsWebUtil.bindMockWebRequest(applicationContext)
            unbindRequest = true
        }

        def messageSource = applicationContext.getBean("messageSource")
        def locale = RequestContextUtils.getLocale(requestAttributes.request)

        // What the heck is going on here with RequestContextUtils.getLocale() returning a String?
        // Beats the hell out of me, so just fix it!
        if (locale instanceof String) {

            // Now Javasoft have lost the plot and you can't easily get from a Locale.toString() back to a locale. Aaaargh!
            if (locale.length() >= 5) {
                locale = new Locale(locale[0..1], locale[3..4])
            } else {
                locale = new Locale(locale)
            }
        }

        def msg = messageSource.getMessage(parameters.code, parameters.args as Object[], parameters.default, locale)

        if (unbindRequest) RequestContextHolder.setRequestAttributes(null)
        if (parameters.encodeAs) {
            switch (parameters.encodeAs.toLowerCase()) {
                case 'html':
                    msg = msg.encodeAsHTML()
                    break

                case 'xml':
                    msg = msg.encodeAsXML()
                    break

                case 'url':
                    msg = msg.encodeAsURL()
                    break

                case 'javascript':
                    msg = msg.encodeAsJavaScript()
                    break

                case 'base64':
                    msg = msg.encodeAsBase64()
                    break
            }
        }

        return msg
    }

    static setError(domain, parameters) {
        def msg = Localization.getMessage(parameters)
        if (parameters.field) {
            domain.errors.rejectValue(parameters.field, null, msg)
        } else {
            domain.errors.reject(null, msg)
        }

        return msg
    }

    // repopulates the localization table from the i18n property files
    static reload() {
      Localization.executeUpdate("delete Localization")
      loaded = false
      load()
      resetAll()
    }

    static load() {
        def count = Localization.count()
        if (count == 0) {
            def path = RequestContextHolder.currentRequestAttributes().getServletContext().getRealPath("/")
            if (path) {
                def dir = new File(new File(path).getParent(), "grails-app${File.separator}i18n")
                if (!(dir.exists() && dir.canRead())) {   // if we're running in deploy war mode
                    dir = new File(new File(path), "WEB-INF${File.separator}grails-app${File.separator}i18n")
                }

                if (dir.exists() && dir.canRead()) {
                    def names = []
                    dir.listFiles().each {
                        if (it.isFile() && it.canRead() && it.getName().endsWith(".properties")) {
                            names << it.getName()
                        }
                    }

                    names.sort()

                    def locale
                    names.each {
                        if (it ==~ /.+_[a-z][a-z]_[A-Z][A-Z]\.properties$/) {
                            locale = new Locale(it.substring(it.length() - 16, it.length() - 14), it.substring(it.length() - 13, it.length() - 11))
                        } else if (it ==~ /.+_[a-z][a-z]\.properties$/) {
                            locale = new Locale(it.substring(it.length() - 13, it.length() - 11))
                        } else {
                            locale = null
                        }

                        Localization.loadPropertyFile(new File(dir, it), locale)
                    }
                }
            }
        }

        def size = ConfigurationHolder.config.localizations.cache.size.kb
        if (size != null && size instanceof Integer && size >= 0 && size <= 1024 * 1024) {
            maxCacheSize = size * 1024L
        }

        loaded = true
    }

    static loadPropertyFile(file, locale) {
        def loc = locale ? locale.getLanguage() + locale.getCountry() : "*"
        def props = new Properties()
        def reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"))
        try {
            props.load(reader)
        } finally {
            if (reader) reader.close()
        }

        def rec, txt
        def counts = [imported: 0, skipped: 0]
        props.stringPropertyNames().each {key ->
            txt = props.getProperty(key)
            if (key && key.length() <= 250 && txt && txt.length() <= 2000) {
                rec = Localization.findByCodeAndLocale(key, loc)
                if (!rec) {
                    Localization.withTransaction {status ->
                        rec = new Localization()
                        rec.code = key
                        rec.locale = loc
                        rec.text = txt
                        if (rec.save(flush: true)) {
                            counts.imported = counts.imported + 1
                        } else {
                            counts.skipped = counts.skipped + 1
                            status.setRollbackOnly()
                        }
                    }
                } else {
                    counts.skipped = counts.skipped + 1
                }
            } else {
                counts.skipped = counts.skipped + 1
            }
        }

        // Clear the whole cache if we actually imported any new keys
        if (counts.imported > 0) Localization.resetAll()

        return counts
    }

    static loadPluginData(baseName) {

        def path = RequestContextHolder.currentRequestAttributes().getServletContext().getRealPath("/")
        if (path) {
            def dir = new File(new File(path).getParent(), "grails-app${File.separator}i18n")
            if (dir.exists() && dir.canRead()) {
                def names = []
                def name, ending
                dir.listFiles().each {
                    name = it.getName()
                    if (name.startsWith(baseName) && name.endsWith(".properties") && it.isFile() && it.canRead()) {
                        ending = name.substring(baseName.length(), name.length() - 11)
                        if (ending == "" || ending ==~ /_[a-z][a-z]/ || ending ==~ /_[a-z][a-z]_[A-Z][A-Z]/) {
                            names << name
                        }
                    }
                }

                names.sort()

                def locale
                names.each {
                    if (it ==~ /.+_[a-z][a-z]_[A-Z][A-Z]\.properties$/) {
                        locale = new Locale(it.substring(it.length() - 16, it.length() - 14), it.substring(it.length() - 13, it.length() - 11))
                    } else if (it ==~ /.+_[a-z][a-z]\.properties$/) {
                        locale = new Locale(it.substring(it.length() - 13, it.length() - 11))
                    } else {
                        locale = null
                    }

                    Localization.loadPropertyFile(new File(dir, it), locale)
                }
            }
        }
    }

    static resetAll() {
        synchronized (cache) {
            cache.clear()
            currentCacheSize = 0L
            cacheHits = 0L
            cacheMisses = 0L
        }
    }

    static resetThis(String key) {
        key += keyDelimiter
        synchronized (cache) {
            def entries = cache.entrySet().iterator()
            def entry
            while (entries.hasNext()) {
                entry = entries.next()
                if (entry.getKey().startsWith(key)) {
                    currentCacheSize -= entry.getKey().length() + entry.getValue().length()
                    entries.remove()
                }
            }
        }
    }

    static statistics() {
        def stats = [:]
        synchronized (cache) {
            stats.max = maxCacheSize
            stats.size = currentCacheSize
            stats.count = cache.size()
            stats.hits = cacheHits
            stats.misses = cacheMisses
        }

        return stats
    }
}
