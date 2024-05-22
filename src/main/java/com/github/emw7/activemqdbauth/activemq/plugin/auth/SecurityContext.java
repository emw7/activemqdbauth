package com.github.emw7.activemqdbauth.activemq.plugin.auth;

import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.Set;
import java.util.UUID;

/**
 * The security context returns by
 * {@link AuthenticatorAuthenticationBroker#authenticate(String, String, X509Certificate[])}.
 */
public class SecurityContext extends org.apache.activemq.security.SecurityContext {

	private Set<Principal> principals= null;
	private UUID token= null;
	
	public SecurityContext (String username, Set<Principal> principals)
	{
		super(username);
		this.principals= principals;
		token= UUID.randomUUID();
	}

	@Override
	public Set<Principal> getPrincipals() {
		return principals;
	}

	public String getToken() {
		return token.toString();
	}

}
