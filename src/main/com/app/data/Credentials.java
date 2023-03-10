package com.app.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.utils.ErrorCode;
import com.utils.JsonPathException;

import java.io.IOException;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Credentials {
	
	@JsonProperty("name")
	public String name;
	
	@JsonProperty("password")
	public String password;
	
	public static Credentials get(String value) throws IOException {
    		ObjectMapper mapper = new ObjectMapper();
			Credentials data = mapper.readValue(value, Credentials.class);
    		DataMapper.setCredentials(data);
    		return data;
	}
}
