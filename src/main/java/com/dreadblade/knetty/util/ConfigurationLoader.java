package com.dreadblade.knetty.util;

import com.dreadblade.knetty.exception.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;

public class ConfigurationLoader {
    private static final Logger logger = LoggerFactory.getLogger(ConfigurationLoader.class);
    private static final String CONFIG_FILEPATH = "./src/main/resources/config.properties";
    private static final String PROPERTIES_PREFIX = "knetty.server.";

    private static Properties properties;

    private static void loadProperties() throws ConfigurationException {
        try {
            InputStream inputStream = new FileInputStream(CONFIG_FILEPATH);
            properties = new Properties();
            properties.load(inputStream);
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new ConfigurationException(e.getMessage());
        }
    }

    public static String getPropertyValue(String key) {
        if (properties == null) {
            try {
                loadProperties();
            } catch (ConfigurationException e) {
                logger.error(e.getMessage());
            }
        }
        return properties.getProperty(PROPERTIES_PREFIX + key);
    }
}
