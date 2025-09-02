package com.dreamzlancer.cucumbersample.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

import java.io.IOException;

public class DownstreamErrorHandler extends DefaultResponseErrorHandler {

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
            throw new DownstreamServiceException("Resource not found", 404);
        }
        // Handle other status codes as needed
        super.handleError(response);
    }
}