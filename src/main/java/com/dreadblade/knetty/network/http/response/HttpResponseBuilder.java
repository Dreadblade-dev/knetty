package com.dreadblade.knetty.network.http.response;

import com.dreadblade.knetty.network.http.Header;

import java.util.ArrayList;
import java.util.List;

public class HttpResponseBuilder {

    private Response response;

    public HttpResponseBuilder() {
        response = new Response();
        response.addHeader("Server: knetty-1.0-SHAPSHOT");
        response.addHeader("Connection: close");
    }

    public HttpResponseBuilder setVersion(String version) {
        response.setVersion(version);
        return this;
    }

    public HttpResponseBuilder setStatus(Status status) {
        response.setStatus(status);
        return this;
    }

    public HttpResponseBuilder addHeader(Header header) {
        response.headers.add(header);
        return this;
    }

    public HttpResponseBuilder addHeader(String header) {
        response.headers.add(new Header(header));
        return this;
    }

    public HttpResponseBuilder addHeader(String name, String value) {
        response.headers.add(new Header(name, value));
        return this;
    }

    public HttpResponseBuilder addHeaders(List<Header> headers) {
        response.headers.addAll(headers);
        return this;
    }

    public HttpResponseBuilder addBody(String body) {
        response.body += body;
        return this;
    }

    public HttpResponse create() {
        return response;
    }

    private static class Response implements HttpResponse {

        private String version;
        private Status status;
        private List<Header> headers;
        private String body;

        public Response() {
            this.headers = new ArrayList<Header>();
            this.body = "";
        }

        @Override
        public String getVersion() {
            return version;
        }

        @Override
        public void setVersion(String version) {
            this.version = version;
        }

        @Override
        public Status getStatus() {
            return status;
        }

        @Override
        public void setStatus(Status status) {
            this.status = status;
        }

        @Override
        public List<Header> getHeaders() {
            return headers;
        }

        @Override
        public void setHeaders(List<Header> headers) {
            this.headers = headers;
        }

        @Override
        public byte[] getBytes() {
            return this.toString().getBytes();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (Header header : headers) {
                sb.append(header);
                sb.append('\n');
            }
            return version + " " + status.getStatusCode() + " " + status.getStatusMessage() +
                    '\n' + sb.toString() + "\n\n" + body;
        }

        public void addHeader(Header header) {
            this.headers.add(header);
        }

        public void addHeader(String header) {
            this.headers.add(new Header(header));
        }

        public void addHeader(String name, String value) {
            this.headers.add(new Header(name, value));
        }

        public void addHeaders(List<Header> headers) {
            this.headers.addAll(headers);
        }

        public void addBody(String body) {
            this.body += body;
        }
    }
}
