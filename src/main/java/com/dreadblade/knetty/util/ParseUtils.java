package com.dreadblade.knetty.util;

import com.dreadblade.knetty.exception.InvalidHttpRequestException;
import com.dreadblade.knetty.network.http.Header;
import com.dreadblade.knetty.network.http.request.HttpRequest;
import com.dreadblade.knetty.network.http.request.Method;

import java.util.ArrayList;
import java.util.List;

public class ParseUtils {

    public static HttpRequest parseRequest(String data) throws InvalidHttpRequestException {
        String[] requestData = data.split("\r\\n");
        String[] requestFirstLine = requestData[0].split("\\s");

        if (requestData.length <= 1) {
            throw new InvalidHttpRequestException("Invalid HTTP request");
        }

        Method method = Method.getMethod(requestFirstLine[0]);
        String path = requestFirstLine[1];
        String version = requestFirstLine[2];

        List<Header> headers = new ArrayList<>();

        for (int i = 1; i < requestData.length; i++) {
            headers.add(new Header(requestData[i]));
        }

        if (headers.size() == 0) {
            throw new InvalidHttpRequestException("Invalid HTTP request headers");
        }

        return new HttpRequest(method, path, version, headers);
    }
}
