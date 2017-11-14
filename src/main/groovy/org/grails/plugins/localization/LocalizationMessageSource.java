package org.grails.plugins.localization;

import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.core.io.ResourceLoader;

import java.text.MessageFormat;
import java.util.Locale;

public class LocalizationMessageSource extends AbstractMessageSource implements ResourceLoaderAware {

    private ResourceLoader resourceLoader = null;

    @Override
    protected MessageFormat resolveCode(String code, Locale locale) {
        String msg = Localization.decodeMessage(code, locale);
        return (msg != null) ? new MessageFormat(msg) : null;
    }

    @Override
    protected String resolveCodeWithoutArguments(String code, Locale locale) {
        return Localization.decodeMessage(code, locale);
    }

    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
