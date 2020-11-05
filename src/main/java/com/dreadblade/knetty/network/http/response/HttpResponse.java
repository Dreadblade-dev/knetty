package com.dreadblade.knetty.network.http.response;

import com.dreadblade.knetty.network.http.Header;

import java.util.List;

public interface HttpResponse {
    String getVersion();
    void setVersion(String version);;
    Status getStatus();
    void setStatus(Status status);
    List<Header> getHeaders();
    void setHeaders(List<Header> headers);

    byte[] getBytes();
}
