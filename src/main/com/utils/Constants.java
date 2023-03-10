package com.utils;

import com.app.file.CredentialsHelper;
import com.app.file.Extension;
import com.app.file.FileCredentialsType;

import java.io.File;
import java.nio.file.Path;

public class Constants {

	public static final String DETERMINISTIC = "AEAD_AES_256_CBC_HMAC_SHA_512-Deterministic";
	public static final int SIZE_MASTER_KEY = 96;
	public static final String MASTER_KEY_FILENAME = "master-key.txt";
	public static final String ENCRYPTED_BASE_CRED_FILE_EXPORT_PATH = CredentialsHelper.get(FileCredentialsType.DATABASE, Extension.ENC);
	public static final String ENCRYPTED_BASE_CRED_FILE_IMPORT_PATH = CredentialsHelper.get(FileCredentialsType.DATABASE, Extension.ENC);
	public static final String KEY_JSON_BASE_FILE_EXPORT_PATH = CredentialsHelper.get(FileCredentialsType.DATABASE, Extension.ENC);
	public static final String KEY_JSON_BASE_FILE_IMPORT_PATH = CredentialsHelper.get(FileCredentialsType.DATABASE, Extension.ENC);

}
