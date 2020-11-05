package com.dreadblade.knetty.config;

import com.dreadblade.knetty.exception.ConfigurationException;
import com.dreadblade.knetty.util.ConfigurationLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Configuration {
    private static final Logger logger = LoggerFactory.getLogger(Configuration.class);
    private static final int DEFAULT_PORT = 8080;
    private static final String DEFAULT_ROOT_PATH = "/";
    private static final String DEFAULT_STATIC_FILES_LOCATION = "./src/main/resources/static/";

    private static Configuration instance;
    private int port;
    private String rootPath;
    private String staticFilesLocation;

    private Configuration() {
        rootPath = ConfigurationLoader.getPropertyValue("rootPath");
        staticFilesLocation = ConfigurationLoader.getPropertyValue("staticFilesLocation");

        if (rootPath == null) {
            rootPath = DEFAULT_ROOT_PATH;
        }
        if (staticFilesLocation == null) {
            staticFilesLocation = DEFAULT_STATIC_FILES_LOCATION;
        }

        try {
            port = Integer.parseInt(ConfigurationLoader.getPropertyValue("port"));
        } catch (NumberFormatException e) {
            port = DEFAULT_PORT;
        }
    }

    public static Configuration getInstance() {
        if (instance == null) {
            instance = new Configuration();
        }
        return instance;
    }

    public int getPort() {
        return port;
    }

    public String getRootPath() {
        return rootPath;
    }

    public String getStaticFilesLocation() {
        return staticFilesLocation;
    }
}
