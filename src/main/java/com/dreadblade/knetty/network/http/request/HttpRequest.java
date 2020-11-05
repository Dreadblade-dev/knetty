package com.dreadblade.knetty.network.http.request;

import com.dreadblade.knetty.network.http.Header;

import java.util.List;

public class HttpRequest {
    private final Method method;
    private final String path;
    private final String version;
    private final List<Header> headers;

    public HttpRequest(Method method, String path, String version, List<Header> headers) {
        this.method = method;
        this.path = path;
        this.version = version;
        this.headers = headers;
    }

    public Method getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getVersion() {
        return version;
    }

    public List<Header> getHeaders() {
        return headers;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Header header : headers) {
            sb.append(header);
            sb.append('\n');
        }
        return method.name() + " " + path + " " + version + sb.toString();
    }
}
