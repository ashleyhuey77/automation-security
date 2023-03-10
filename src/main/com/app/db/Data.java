package com.app.db;

import com.utils.CredentialsType;

public interface Data {

    void set(String name, String pwd, CredentialsType type) throws Exception;
}
