package com.utils;

import org.json.JSONObject;

public class JsonFormatter {
    public JsonFormatter() {
    }

    public static String getJSONObject(String name, String password, String uri) throws Exception {
        String result = null;

        try {
            JSONObject json = new JSONObject();
            json.put("name", name);
            json.put("password", password);
            json.put("uri", uri);
            result = json.toString();
            return result;
        } catch (Exception var4) {
            throw var4;
        }
    }
}
