package com.app.file;

import com.app.data.Credentials;

public class FileMapper {
    private static ThreadLocal<FileCredentials> creds = new ThreadLocal();

    public FileMapper() {
    }

    public static void setCredentials(FileCredentials value) {
        creds.set(value);
    }

    public static FileCredentials getCredentials() {
        return creds.get();
    }
}
