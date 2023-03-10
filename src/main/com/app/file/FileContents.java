package com.app.file;

public class FileContents {
    private String key;

    public FileContents(String key) {
        this.key = key;
    }

    public final String getKey() {
        return this.key;
    }

    public final void setKey(String value) {
        this.key = value;
    }
}
