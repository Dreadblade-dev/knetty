package com.dreadblade.knetty.network.http.response;

import com.dreadblade.knetty.network.http.Header;

import java.util.List;

/**
 * HttpResponse class represents a HTTP response
 * https://www.w3.org/Protocols/rfc2616/rfc2616-sec6.html
 */
public interface HttpResponse {
    String getVersion();
    void setVersion(String version);
    Status getStatus();
    void setStatus(Status status);
    List<Header> getHeaders();
    void setHeaders(List<Header> headers);

    byte[] getBytes();
}
