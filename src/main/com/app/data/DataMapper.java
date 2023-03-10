package com.app.data;

public class DataMapper {
	
	private static ThreadLocal<Credentials> creds = new ThreadLocal<>();
	
	public static void setCredentials(Credentials value) {
		creds.set(value);
	}
	
	public static Credentials getCredentials() {
		return creds.get();
	}

}
