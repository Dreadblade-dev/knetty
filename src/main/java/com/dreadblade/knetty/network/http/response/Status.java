package com.dreadblade.knetty.network.http.response;

public enum Status {
    OK("OK", 200, "index.html"),
    BAD_REQUEST("Bad request", 400, ""),
    NOT_FOUND("Not found", 404, "not_found_404.html"),
    INTERNAL_SERVER_ERROR("Internal server error", 500, "");


    private String message;
    private int code;
    private String defaultPageFilename;

    Status(String message, int code, String defaultPageFilename) {
        this.message = message;
        this.code = code;
        this.defaultPageFilename = defaultPageFilename;
    }

    public int getStatusCode() {
        return this.code;
    }

    public String getStatusMessage() {
        return this.message;
    }

    public String getDefaultPageFilename() {
        return defaultPageFilename;
    }
}
