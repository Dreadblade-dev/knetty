package com.dreadblade.knetty.network.http.response;

public enum Status {
    OK(200);

    private int code;

    Status(int code) {
        this.code = code;
    }

    public int getStatusCode() {
        return this.code;
    }

    public String getStatusMessage() {
        return this.name();
    }
}
