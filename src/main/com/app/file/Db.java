package com.app.file;

public class Db implements CredentialsFile {
    public Db() {
    }

    public String getName(Extension ext) {
        String result = null;
        switch(ext) {
            case JSON:
                result = "Db_key.json";
                break;
            case ENC:
                result = "Db.enc";
        }
        return result;
    }
}
