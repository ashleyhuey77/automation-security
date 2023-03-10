package com.app.file;

import com.app.data.DataMapper;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FileCredentials {
    @JsonProperty("name")
    public String name;
    @JsonProperty("password")
    public String password;
    @JsonProperty("uri")
    public String uri;

    public FileCredentials() {
    }

    public static FileCredentials get(String value) throws Exception {
        try {
            ObjectMapper mapper = new ObjectMapper();
            FileCredentials data = mapper.readValue(value, FileCredentials.class);
            FileMapper.setCredentials(data);
            return data;
        } catch (Exception var3) {
            throw var3;
        }
    }
}
