package com.github.emw7.activemqdbauth.activemq.plugin.auth;

import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.command.ConnectionInfo;
import org.apache.activemq.security.AbstractAuthenticationBroker;
import org.apache.activemq.security.SecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The activemq plugin the delegates the authentication to the provided authenticator.
 * <p>
 * Implements {@link AbstractAuthenticationBroker} from activemq that provides default
 * implementation for all methods needed to be implemented by a broker plugin ({@link Broker}.<br/>
 * So only two methods are needed to be implemented:
 * <ul>
 *   <li>{@link #addConnection(ConnectionContext, ConnectionInfo)}</li>
 *   <li>{@link #authenticate(String, String, X509Certificate[])}</li>
 * </ul>
 * The first has the responsibility of allowing the connection of a client by verifying the
 * supplied credentials too.<br/>
 * The second comes from {@link org.apache.activemq.security.AuthenticationBroker} that is
 * implemented by {@link AbstractAuthenticationBroker} that does not give an implementation. That
 * means that {@link AbstractAuthenticationBroker} does not call it and so the only mean to
 * implement the authentication is to call {@link #authenticate(String, String, X509Certificate[])}
 * from {@link #addConnection(ConnectionContext, ConnectionInfo)}.
 * <p>
 * <b>Note</b>: why this name? Because the authentication is delegated to the provided
 * Authenticator.
 */
public class AuthenticatorAuthenticationBroker extends AbstractAuthenticationBroker {

  private final Logger logger= LoggerFactory.getLogger(AuthenticatorAuthenticationBroker.class);

  /**
   * Must be an authenticator that accept {@link User} and:
   * <ul>
   *   <li>in case of successful authentication returns a fulfilled {@link User};</li>
   *   <li>in case of successful authentication throws an {@link AuthenticationException};</li>
   * </ul>
   */
  private final Authenticator authenticator;

  public AuthenticatorAuthenticationBroker(Broker next, Authenticator authenticator,
      boolean logPassword) {
    super(next);
    this.authenticator= authenticator;
  }

  public AuthenticatorAuthenticationBroker(Broker next, Authenticator authenticator) {
    this(next, authenticator, false);
  }

  //region API
  /**
   * Adds the connection.
   *
   * @param context the connection context passed by activemq
   * @param info the connection information passed by activemq
   * @throws Exception
   * @throws NullPointerException if {@code context} is {@code null}
   * throws NullPointerException if {@code info} is {@code null}
   */
  @Override
  public void addConnection(ConnectionContext context, ConnectionInfo info) throws Exception {

    Objects.requireNonNull(context, "context cannot be null!");
    Objects.requireNonNull(info, "info cannot be null!");

    loggerAddConnection(context, info);

    SecurityContext securityContext = context.getSecurityContext();

    if (securityContext == null) {
      securityContext = authenticate(info.getUserName(), info.getPassword(), null);
      context.setSecurityContext(securityContext);
      securityContexts.add(securityContext);
    }

    try {
      super.addConnection(context, info);
    } catch (Exception e) {
      securityContexts.remove(securityContext);
      context.setSecurityContext(null);
      throw e;
    }
  }

  @Override
  public SecurityContext authenticate(String username, String password,
      X509Certificate[] certificates) throws SecurityException {

    logger.info("[EMW7] authenticate :: username {}, password {}, certificates {}",username, password, certificates);

    final SecurityContext securityContext;

    try {
      final User user = new User(username, password, false, null, null);
      final Object authToken  = getAuthenticator().authenticate(user);
      final Set<Principal> principals = Collections.emptySet();
      securityContext = createSecurityContext(username, principals);
      return securityContext;
    } catch (AuthenticationException e) {
      throw new SecurityException(( e.getCause() != null ) ? e.getCause().getMessage() : e.getMessage(),e);
    }
  }
  //endregion API

  //region Protected methods
  protected final SecurityContext createSecurityContext(String username, Set<Principal> principals) {
    return new com.github.emw7.activemqdbauth.activemq.plugin.auth.SecurityContext(username, principals);
  }
  //endregion Protected methods

  //region Private methods
  private final void loggerAddConnection(ConnectionContext context, ConnectionInfo info) {
    logger.info("[EMW7] addConnection: username {}, client-id {}, client-ip {}, connection-id {}",
        info.getUserName(),  info.getClientId(), info.getClientIp(), info.getConnectionId());
  }
  //endregion Private methods

  // Tag: getter & setter.
  private final  Authenticator getAuthenticator() {
    return authenticator;
  }

  // End-Of-Tag: getter & setter.

}
