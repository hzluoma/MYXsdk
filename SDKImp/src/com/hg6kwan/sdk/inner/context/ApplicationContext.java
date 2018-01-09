package com.hg6kwan.sdk.inner.context;

import java.security.PrivateKey;



public class ApplicationContext {
	private static ApplicationContext instance;
	public PrivateKey privateKey;
	public String UDID;
	
	private ApplicationContext() {
		
	}
	
	public static ApplicationContext shareContext() {
		if(instance == null) {
			instance = new ApplicationContext();
		}
		return instance;
	}
}
