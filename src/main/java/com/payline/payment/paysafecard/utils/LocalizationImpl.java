package com.payline.payment.paysafecard.utils;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;


public class LocalizationImpl implements LocalizationService {

    private LocalizationImpl() {
    }

    private static class SingletonWrapper {
        private static final LocalizationService INSTANCE = new LocalizationImpl();
    }

    /**
     * Singleton threadsafe avec initialisation tardive.
     */
    public static LocalizationService getInstance() {
        return SingletonWrapper.INSTANCE;
    }

    /**
     * Le nom des fichiers d'internationalisation
     */
    private static final String APP_RESOURCES_FILE_NAME = "traduction";


    private String getLocalizedString(final String key, final Locale locale, final String... args) {

        String text = null;

        final ResourceBundle val = ResourceBundle.getBundle(APP_RESOURCES_FILE_NAME, locale);
        if (val != null && key != null && !key.isEmpty()) {
            text = val.getString(key);
            if (args != null) {
                return MessageFormat.format(text, (Object[]) args);
            }
        }
        return text;
    }


    @Override
    public String getSafeLocalizedString(final String key, final Locale locale, final String... args) {

        if (key != null && !key.isEmpty()) {
            try {
                return getLocalizedString(key, locale, args);
            } catch (final MissingResourceException e) {
                return "???" + locale + "." + key + "???";
            }
        }
        return null;
    }
}
