package com.app.file;

public class Error implements CredentialsFile {
    public Error() {
    }

    public String getName(Extension ext) {
        String result = null;
        switch(ext) {
            case JSON:
                result = "Error_key.json";
                break;
            case ENC:
                result = "Error.enc";
        }

        return result;
    }
}

