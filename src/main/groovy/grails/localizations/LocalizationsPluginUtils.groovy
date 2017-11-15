package grails.localizations

import grails.core.DefaultGrailsApplication
import grails.core.GrailsApplicationClass
import grails.util.BuildSettings
import grails.util.Environment
import grails.util.Holders
import groovy.util.logging.Slf4j
import org.grails.core.io.StaticResourceLoader
import org.grails.core.support.internal.tools.ClassRelativeResourcePatternResolver
import org.grails.plugins.BinaryGrailsPlugin
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.core.io.support.ResourcePatternResolver

@Slf4j
class LocalizationsPluginUtils {

    static final String GRAILS_APP_I18N_PATH_COMPONENT = "/grails-app/i18n/";
    static String messageBundleLocationPattern = "classpath*:*.properties";

    static List<Resource> getI18nResources() {
        Resource[] resources = []
        if (Environment.isDevelopmentEnvironmentAvailable()) {
            File[] propertiesFiles = new File(BuildSettings.BASE_DIR, GRAILS_APP_I18N_PATH_COMPONENT).listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(".properties");
                }
            });
            if (propertiesFiles != null && propertiesFiles.length > 0) {
                List<Resource> resourceList = new ArrayList<Resource>(propertiesFiles.length);
                for (File propertiesFile : propertiesFiles) {
                    log.debug "Project - "+ propertiesFile.name
                    resourceList.add(new FileSystemResource(propertiesFile));
                }
                resources = resourceList.toArray(new Resource[resourceList.size()]);
            } else {
                log.debug "No Messages Properties files found within project"
                resources = new Resource[0];
            }
        } else {
            DefaultGrailsApplication defaultGrailsApplication = (DefaultGrailsApplication) Holders.grailsApplication;
            GrailsApplicationClass applicationClass = defaultGrailsApplication.getApplicationClass();
            if (applicationClass != null) {
                ResourcePatternResolver resourcePatternResolver = new ClassRelativeResourcePatternResolver(applicationClass.getClass());
                resources = resourcePatternResolver.getResources(messageBundleLocationPattern);
            }
        }
        //Sort based on underscore tokens count as more underscores means more exact locale value
        return resources.sort { x, y ->
            x.filename.tokenize('_').size() <=> y.filename.tokenize('_').size()
        }
    }

    static List<Resource> getAllPluginI18nResources() {
        List<Resource> resources = []
        Holders.pluginManager.allPlugins.each {
            if (it instanceof BinaryGrailsPlugin) {
                List<Resource> pluginResources = getPluginI18nResources(it)
                if (pluginResources) {
                    log.debug "Plugin - "+ it.name
                    resources.addAll(pluginResources)
                }
            }
        }
        //Sort based on underscore tokens count as more underscores means more exact locale value
        return resources.sort { x, y ->
            x.filename.tokenize('_').size() <=> y.filename.tokenize('_').size()
        }

    }

    static List<Resource> getPluginI18nResources(BinaryGrailsPlugin plugin) {
        Resource url = plugin.baseResourcesResource;
        if (url != null) {
            StaticResourceLoader resourceLoader = new StaticResourceLoader();
            resourceLoader.setBaseResource(url);
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(resourceLoader);
            try {
                // first load all properties
                return resolver.getResources('*' + plugin.PROPERTIES_EXTENSION)
            } catch (IOException e) {
            }
        }
        return null;
    }

}
