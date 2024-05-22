package com.github.emw7.activemqdbauth.activemq.plugin.auth;

/**
 * The exception thrown by an {@link Authenticator} in case of authentication failure.
 */
public class AuthenticationException extends Exception {

	public AuthenticationException (String message, Throwable cause)
	{
		super(message, cause);
	}

	public AuthenticationException (String message)
	{
		super(message);
	}

	public AuthenticationException (Throwable cause)
	{
		super(cause);
	}
	
}
