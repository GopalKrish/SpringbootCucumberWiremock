package com.dreamzlancer.cucumbersample.service;

import lombok.Getter;

@Getter
public class DownstreamServiceException extends RuntimeException {
    private final int statusCode;

    public DownstreamServiceException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

}