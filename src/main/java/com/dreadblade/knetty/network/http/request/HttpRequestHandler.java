package com.dreadblade.knetty.network.http.request;

import com.dreadblade.knetty.network.http.response.HttpResponse;


public interface HttpRequestHandler {
    HttpResponse handle(HttpRequest request);
}
