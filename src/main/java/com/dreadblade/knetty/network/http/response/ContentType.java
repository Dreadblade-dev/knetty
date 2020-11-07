package com.dreadblade.knetty.network.http.response;

import java.util.Arrays;
import java.util.List;

public enum ContentType {
    APPLICATION_JSON("application/json", new String[]{".json"}),
    APPLICATION_JAVASCRIPT("application/javascript", new String[]{".js"}),
    APPLICATION_XML("application/xml", new String[]{".xml"}),
    TEXT_CSS("text/css", new String[]{".css"}),
    TEXT_CSV("text/csv", new String[]{".csv"}),
    TEXT_HTML("text/html", new String[]{".html"}),
    TEXT_PLAIN("text/plain", new String[]{".txt"}),
    TEXT_MARKDOWN("text/markdown", new String[]{".md"});

    String contentType;
    List<String> fileExtensions;

    ContentType(String contentType, String[] fileExtensions) {
        this.contentType = contentType;
        this.fileExtensions = Arrays.asList(fileExtensions);
    }

    public static String getContentType(String filename) {
        for (ContentType contentType : ContentType.values()) {
            for (String extension : contentType.fileExtensions) {
                if (filename.endsWith(extension)) {
                    return contentType.contentType;
                }
            }
        }
        return TEXT_PLAIN.contentType;
    }
}
