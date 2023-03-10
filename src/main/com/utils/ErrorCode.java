package com.utils;

public enum ErrorCode {

    JSON_FILE(0, "There was an issue with the .json file."),
    ENC_FILE(1, "There was an issue with the .enc file");

    private final int code;
    private final String description;

    private ErrorCode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String toString() {
        return code + ": " + description;
    }
}
