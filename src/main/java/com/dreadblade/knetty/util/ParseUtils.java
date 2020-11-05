package com.dreadblade.knetty.util;

import com.dreadblade.knetty.network.http.Header;
import com.dreadblade.knetty.network.http.request.HttpRequest;
import com.dreadblade.knetty.network.http.request.Method;

import java.util.ArrayList;
import java.util.List;

public class ParseUtils {
    public static HttpRequest parseRequest(String data) {
        String[] requestData = data.split("\r\\n");
        String[] requestFirstLine = requestData[0].split("\\s");

        Method method = Method.getMethod(requestFirstLine[0]);
        String path = requestFirstLine[1];
        String version = requestFirstLine[2];

        List<Header> headers = new ArrayList<>();

        for (int i = 1; i < requestData.length; i++) {
            headers.add(new Header(requestData[i]));
        }

        return new HttpRequest(method, path, version, headers);
    }
}
