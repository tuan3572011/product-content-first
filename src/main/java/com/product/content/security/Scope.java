package com.product.content.security;

public enum Scope {

    LOGIN ("login"), READ ("read"), SENSITIVE("sensitive");

    private String value;
    private Scope(String val) {
        this.value = val;
    }

    public String getValue() {
        return value;
    }
}
