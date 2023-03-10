package com.utils;

public class JsonPathException extends RuntimeException {
    private static final long serialVersionUID = -8460356990632230194L;

    private final ErrorCode code;

    public JsonPathException(ErrorCode code) {
        super(code.getDescription());
        this.code = code;
    }

    public JsonPathException(String message, ErrorCode code) {
        super(code.getDescription() + "\n" + message);
        this.code = code;
    }

    public ErrorCode getCode() {
        return this.code;
    }
}
