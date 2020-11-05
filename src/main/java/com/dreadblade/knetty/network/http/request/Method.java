package com.dreadblade.knetty.network.http.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum Method {
    GET,
    PUT,
    POST,
    DELETE;

    private static final Logger logger = LoggerFactory.getLogger(Method.class);

    public static Method getMethod(String method) {
        if (method == null) {
            return null;
        }

        Method result = valueOf(method);
        if (result != null) {
            return valueOf(method);
        } else {
            logger.info("Unsupported request method");
            throw new IllegalArgumentException("Unsupported request method");
        }
    }
}
