package com.app.file;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@JsonIgnoreProperties(
        ignoreUnknown = true
)
public class Key {
    @JsonProperty("key")
    public String key;

    public Key() {
    }

    public static Key get(String value) throws Exception {
        try {
            try (InputStream fileIn = Key.class
                    .getClassLoader()
                    .getResourceAsStream(value);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(fileIn))) {
                String lines = reader.lines()
                        .collect(Collectors.joining(System.lineSeparator()));
                ObjectMapper mapper = new ObjectMapper();
                Key data = (Key) mapper.readValue(lines, Key.class);
                return data;
            }
        } catch (Exception var4) {
            throw var4;
        }
    }
}
