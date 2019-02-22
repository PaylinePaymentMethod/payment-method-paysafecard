package com.payline.payment.paysafecard.utils.properties.service;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public interface PropertiesService {


    Logger LOGGER = LogManager.getLogger(PropertiesService.class);

    /**
     * Get a config property by its name.
     *
     * @param properties : the used Properties object
     * @param key        The name of the property to recover
     * @return The property value. Can be null if the property has not been found.
     */
    default String getProperty(final Properties properties, final String key) {

        return properties.getProperty(key);
    }

    /**
     * Get the properties file's name
     *
     * @return the properties file's name
     */
    String getFilename();

    String get(final String key);

    /**
     * Reads the properties file and stores the result.
     */
    default void readProperties(Properties properties) {

        String fileName = getFilename();

        if (fileName == null || fileName.isEmpty()) {

            throw new RuntimeException("No file's name found");
        }

        try {

            InputStream inputStream = PropertiesService.class.getClassLoader().getResourceAsStream(fileName);
            properties.load(inputStream);

        } catch (IOException e) {
            LOGGER.error("Unable to load the file {}", fileName, e);
            throw new RuntimeException(e);
        }

    }


}