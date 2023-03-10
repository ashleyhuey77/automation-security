package com.app.file;

import java.util.function.Supplier;

public interface Builder {
    void add(FileCredentialsType credentials, Supplier<CredentialsFile> var2);
}
