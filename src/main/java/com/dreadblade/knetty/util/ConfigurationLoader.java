package com.dreadblade.knetty.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;

public class ConfigurationLoader {
    private static final Logger logger = LoggerFactory.getLogger(ConfigurationLoader.class);
    private static final int DEFAULT_PORT = 8080;
    private static final String DEFAULT_ROOT_PATH = "/";
    private static Properties properties;


    private static void loadProperties() throws ConfigurationException {
        if (properties == null) {
            try {
                InputStream inputStream = new FileInputStream("./src/main/resources/config.properties");
                properties = new Properties();
                properties.load(inputStream);
            } catch (IOException e) {
                logger.error(e.getMessage());
                throw new ConfigurationException(e.getMessage());
            }
        }
    }

    public static int getPort() throws ConfigurationException {
        if (properties == null) {
            loadProperties();
        }
        String strPort = properties.getProperty("knetty.server.port");
        return strPort == null ? DEFAULT_PORT : Integer.parseInt(strPort);
    }

    public static String getRootPath() throws ConfigurationException {
        if (properties == null) {
            loadProperties();
        }
        String rootPath = properties.getProperty("knetty.server.rootPath");
        return rootPath == null ? DEFAULT_ROOT_PATH : rootPath;
    }
}
