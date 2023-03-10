package com;

import java.io.Console;
import com.app.Encrypter;
import com.utils.CredentialsType;

public class App {

	public static void main(String[] args) throws Exception {
		Console console = System.console();
		int credInt = 8;
		while(credInt > 7) {
			console.printf("Of the following options enter the numeric value for the credentials type to be created \n[0 - BASE, \n1 - MIRA \n2 - ARCHIVIST \n3 - DEFAULT \n4 - FEATURE \n5 - MEDIA_MANAGER \n6 - TRANSFERS \n7 - EA]: ");
			String credType = console.readLine();
			credInt = Integer.parseInt(credType);
			if (credInt > 7) {
				console.printf("You did not enter a correct number.");
			}
		}
		
		console.printf("Enter the user name to be encrypted: ");
		String name = console.readLine();
		
		console.printf("Enter the password to be encrypted: ");
		char[] passwordChars = console.readPassword();
		
		encryptCredsInDB(name, new String(passwordChars), CredentialsType.values()[credInt]);
	}
	
	private static void encryptCredsInDB(String name, String password, CredentialsType type) throws Exception {
		try {
			Encrypter encrypter = new Encrypter(name, password, type);
			encrypter.encryptToDB();
		} catch (Exception e) {
			throw e;
		}
	}

}
