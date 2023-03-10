package com.app;

import com.app.data.Credentials;
import com.app.db.MongoHelper;
import com.app.file.CredentialsHelper;
import com.app.file.Extension;
import com.app.file.FileCredentialsType;
import com.app.file.FileEncrypterDecrypter;
import com.utils.CredentialsType;

public class Decrypter {
	
	public Decrypter() {
		
	}

	public static Credentials decryptFromDB(CredentialsType type) throws Exception {
		Credentials credentials = null;
		try {
			credentials = MongoHelper.getValues(type);
			String name = SecurityHelper.decrypt(credentials.name);
			String password = SecurityHelper.decrypt(credentials.password);
			credentials.name = name;
			credentials.password = password;
		} catch (Exception e) {
			throw e;
		}
		return credentials;
	}

	public static Credentials decryptFromFile(FileCredentialsType type) throws Exception {
		Credentials credentials = null;
		try {
			FileEncrypterDecrypter fed = new FileEncrypterDecrypter();
			String fileName = CredentialsHelper.get(type, Extension.ENC);
			String content = fed.decrypt(fileName, type);
			credentials = Credentials.get(content);
			String name = SecurityHelper.decrypt(credentials.name);
			String password = SecurityHelper.decrypt(credentials.password);
			credentials.name = name;
			credentials.password = password;
		} catch (Exception e) {
			throw e;
		}
		return credentials;
	}


}
