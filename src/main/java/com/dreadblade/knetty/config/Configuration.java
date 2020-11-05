package com.dreadblade.knetty.config;

import com.dreadblade.knetty.util.ConfigurationException;
import com.dreadblade.knetty.util.ConfigurationLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Configuration {
    private static final Logger logger = LoggerFactory.getLogger(Configuration.class);
    private static Configuration instance;
    private static final int DEFAULT_PORT = 8080;
    private static final String DEFAULT_ROOT_PATH = "/";
    private int port;
    private String rootPath;

    private Configuration() {
        try {
            port = ConfigurationLoader.getPort();
            rootPath = ConfigurationLoader.getRootPath();
        } catch (ConfigurationException e) {
            logger.warn("Cannot load port and rootPath. Using default values: "
                    + DEFAULT_PORT + " and " + DEFAULT_ROOT_PATH);
            port = DEFAULT_PORT;
            rootPath = DEFAULT_ROOT_PATH;
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
}
