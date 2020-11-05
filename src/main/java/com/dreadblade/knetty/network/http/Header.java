package com.dreadblade.knetty.network.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Header {
    private static final Logger logger = LoggerFactory.getLogger(Header.class);

    private String name;
    private String value;

    public Header() {
    }

    public Header(String header) {
        String[] splitHeader = header.split(": ");
        if (splitHeader.length == 2) {
            this.name = splitHeader[0];
            this.value = splitHeader[1];
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
