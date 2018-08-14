package com.payline.payment.paysafecard.utils;

import java.util.Locale;

public interface LocalizationService {
    /**
     * Renvoie la valeur correspondant à la clé dans les fichiers d'internationalisation, la clé si elle est introuvable
     *
     * @param key    la clé de la ligne du fichier d'internationalisation
     * @param locale la locale pour déterminer la langue
     * @return
     */
    String getSafeLocalizedString(String key, Locale locale, String... args);
}
