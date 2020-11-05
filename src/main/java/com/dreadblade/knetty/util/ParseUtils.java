package com.dreadblade.knetty.util;

import com.dreadblade.knetty.network.http.Header;
import com.dreadblade.knetty.network.http.request.HttpRequest;
import com.dreadblade.knetty.exception.InvalidHttpRequestException;
import com.dreadblade.knetty.network.http.request.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ParseUtils {
    private static final Logger logger = LoggerFactory.getLogger(ParseUtils.class);

    public static HttpRequest parseRequest(String data) throws InvalidHttpRequestException {
        String[] requestData = data.split("\r\\n");
        String[] requestFirstLine = requestData[0].split("\\s");

        if (requestData.length <= 1) {
            logger.info("Invalid HTTP request");
            throw new InvalidHttpRequestException();
        }

        Method method = Method.getMethod(requestFirstLine[0]);
        String path = requestFirstLine[1];
        String version = requestFirstLine[2];

        List<Header> headers = new ArrayList<>();

        for (int i = 1; i < requestData.length; i++) {
            headers.add(new Header(requestData[i]));
        }

        if (headers.size() == 0) {
            logger.info("Invalid HTTP request headers");
            throw new InvalidHttpRequestException();
        }

        return new HttpRequest(method, path, version, headers);
    }
}
