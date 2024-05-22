package com.github.emw7.activemqdbauth.activemq.plugin.auth;

/**
 * Implementations of this interface are responsible for authenticating the client
 * and returning the authentication token.
 * <p>
 * <b>Note</b>: the responsibility here is authentication only and not authorization.
 */
public interface Authenticator {

	/**
	 * Returns the authentication token as response of valid credentials.
	 * <p>
	 * Credentials and token are kept generic and both are of {@link Object} type, and it is
	 * responsibility of the implementors requesting the right credentials type and returning the
	 * type that caller can manage.
	 * <pre>
	 * Example 1
	 *   class FooAuthenticator implements Authenticator {
	 *     public Object authenticate (Object object) throws AuthenticationException {
	 *       if ( ! (object instanceof FooCredentials) ) {
	 *         throws new AuthenticationException(...)
	 *       }
	 *       else {
	 *         return new FooAuthToken(...);
	 *       }
	 *     }
	 *   }
	 *
	 *   class Foo {
	 *     private FooAuthenticator authenticator;
	 *
	 *     public void doSomethingFor () {
	 *       // ...
	 *       FooAuthToken authToken= authenticator.authenticate(new FooCredentials(...);
	 *       // do something with authToken...
	 *       // ...
	 *     }
	 *   }
	 * </pre>
	 *
	 * @param object the credentials to be validated
	 * @return the authentication token
	 * @throws AuthenticationException if either credentials are not valid or any other error that
	 * do not allow to complete the authentication
	 */
	Object authenticate (Object object) throws AuthenticationException;
}
