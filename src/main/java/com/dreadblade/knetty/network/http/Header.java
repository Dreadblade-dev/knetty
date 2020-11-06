package com.dreadblade.knetty.network.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Header {
    private static final Logger logger = LoggerFactory.getLogger(Header.class);
    private static final String HEADER_DELIMITER = ": ";

    private String name;
    private String value;

    public Header() {
    }

    public Header(String header) {
        String[] headerParts = header.split(HEADER_DELIMITER);
        if (headerParts.length == 2) {
            this.name = headerParts[0].trim();
            this.value = headerParts[1].trim();
        } else {
            logger.error("Illegal header argument!");
            throw new IllegalArgumentException();
        }
    }

    public Header(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return name + ": " + value;
    }
}
