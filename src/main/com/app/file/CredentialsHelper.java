package com.app.file;

public class CredentialsHelper {
    public CredentialsHelper() {
    }

    public static String get(FileCredentialsType type, Extension ext) {
        CredentialsFileFactory factory = CredentialsFileFactory.factory((builder) -> {
            builder.add(FileCredentialsType.DATABASE, Db::new);
            builder.add(FileCredentialsType.ERROR, Error::new);
        });
        CredentialsFile file = factory.create(type);
        return file.getName(ext);
    }
}
