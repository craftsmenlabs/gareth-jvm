package com.acme.gareth.definitions;

import org.springframework.stereotype.Service;

@Service
public class MockDB {
    private String value;
    private String baseLineValue;

    public String getBaseLineValue() {
        return baseLineValue;
    }

    public MockDB setBaseLineValue(final String baseLineValue) {
        this.baseLineValue = baseLineValue;
        return this;
    }

    public String getValue() {
        return value;
    }

    public MockDB setValue(final String value) {
        this.value = value;
        return this;
    }
}
