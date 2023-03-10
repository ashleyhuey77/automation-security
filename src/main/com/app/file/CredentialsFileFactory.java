package com.app.file;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface CredentialsFileFactory {
    CredentialsFile create(FileCredentialsType type);

    static CredentialsFileFactory factory(Consumer<Builder> consumer) {
        Map<FileCredentialsType, Supplier<CredentialsFile>> map = new HashMap<>();
        consumer.accept(map::put);
        return type -> map.get(type).get();
    }

}
