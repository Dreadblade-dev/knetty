package com.dreadblade.knetty.network.http.response;

public enum Status {
    OK("OK", 200),
    FORBIDDEN("Forbidden", 403),
    NOT_FOUND("Not found", 404),
    INTERNAL_SERVER_ERROR("Internal server error", 500);


    private String description;
    private int code;

    Status(String description, int code) {
        this.description = description;
        this.code = code;
    }

    public int getStatusCode() {
        return this.code;
    }

    public String getStatusMessage() {
        return this.description;
    }
}
