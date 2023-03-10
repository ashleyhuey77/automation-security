package com.app;

import com.app.db.MongoHelper;
import com.utils.CredentialsType;

public class Encrypter {
	
	private static String name;
	private static String password;
	private static CredentialsType type;
	
	public Encrypter(String name, String password, CredentialsType type) {
		Encrypter.name = name;
		Encrypter.password = password;
		Encrypter.type = type;
	}
	
	public void encryptToDB() throws Exception {
		try {
			String uName = SecurityHelper.encrypt(name);
			String uPwd = SecurityHelper.encrypt(password);
			MongoHelper.setValue(uName, uPwd, type);
		} catch (Exception e) {
			throw e;
		}
	}

}
