package org.jfw.util.auth;

public interface AuthUser {
	final String LOGIN_USER_FLAG_IN_SESSION ="JFW_SESSION_LOGIN_USER";	
	String getId();
	boolean hasAuthority(int auth);
}
