package com.github.emw7.activemqdbauth.activemq.plugin.auth;

import java.util.Objects;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * An {@link Authenticator} implementation that validate supplied {@link User} credential with the
 * one available in the repository.
 * <p>
 * <b>Note</b>: this implementation is tied with {@link User} credentials and
 * {@link UserRepository} for retrieving it from the repository.
 */
public class AuthenticatorDbSimple implements Authenticator {

  @Autowired
  private UserRepository userRepository;

//  @Autowired(required = false)
//  private PasswordEncoder hasher = null;


  /**
   * Returns the {@link User} that correspond to the {@code user.account} if password matches.
   * <p>
   * Using the definitions from {@link Authenticator#authenticate(Object)}:
   * <ul>
   *   <li>{@code user} are the credentials to be validated.</li>
   *   <li>{@link User} is the authentication token.</li>
   * </ul>
   *
   * @param user the credentials to be validated; must be of {@link User} type
   *
   * @return the {@link User} that correspond to the {@code user.account} if password matches.
   *
   * @throws NullPointerException if user is {@code null}
   * @throws NullPointerException if user (after being cast to {@link User} is {@code null}
   * @throws ClassCastException if user is not an instance of {@link User}
   */
  @Override
  public @NonNull Object authenticate(@NonNull Object user) throws AuthenticationException {

    Objects.requireNonNull(user,"user cannot be null");

    // cast is safe here because of assert above
    final User userRequested = (User) user;

    Objects.requireNonNull(userRequested.getAccount());

    final Optional<User> userOptional = userRepository.findById(userRequested.getAccount());
    if ( userOptional.isEmpty() ) {
      // user not found.
      final String errorMsg = String.format("invalid username '%s' or password",
          userRequested.getAccount());
      throw new AuthenticationException(errorMsg);
    }
    // else...
    if ( !userOptional.get().getPassword().equals(userRequested.getPassword()) ) {
      // invalid password.
      final String errorMsg = String.format("invalid username '%s' or password",
          userRequested.getAccount());
      throw new AuthenticationException(errorMsg);
    }
    // else...
    return userOptional.get();
//    // print all retrieved users (DEBUG).
//    users.stream()
//        .forEach(u -> logger.notice(Level.DEBUG, $ -> $.what("retrieved user: {}", u.toString())));
//
//    if (users.size() == 0) {
//      throw new AuthenticationException(
//          String.format("invalid username '%s' or password", userRequested.getUsername()));
//    } else if (users.size() >= 2) {
//      throw new AuthenticationException(String.format("username '%s' match more than one (%s) user",
//          userRequested.getUsername(), users.size()));
//    } else {
//      User user = users.get(0);
//      if (!user.isActive() || // not active!
//          (user.isExpire()
//              && user.getExpiration().before(new Timestamp(System.currentTimeMillis())))) {
//        // %t: example 2020-02-02 14:05:06.663214
//        String message = String.format(
//            "username '%s' with id '%d' and uuid '%s' is either not active (%b) or expired (%b:%s)",
//            user.getUsername(), user.getId(), user.getUuid(), user.isActive(), user.isExpire(),
//            new Date(user.getExpiration().getTime()));
//        throw new AuthenticationException(message);
//      }
//      /*
//       * // allow to connect only if requested uuid (client-id) matches username uuid. else if (
//       * user.getUuid().equals(userRequested.getUuid()) ) { String message=
//       * String.format("username '%s' with id '%d' and uuid '%s' is not tied to requested uuid '%s'"
//       * , user.getUsername(),user.getId(),user.getUuid(),userRequested.getUuid());
//       * logger.notice(Level.WARN, $ -> $.what(message)); throw new
//       * AuthenticationException(message); }
//       */
//      else if (!getHasher().matches(userRequested.getPassword(), user.getPassword())) {
//        throw new AuthenticationException(
//            String.format("invalid username '%s' or password", userRequested.getUsername()));
//      } else {
//        // authentication ok, but will confirmed only if can update credentials.
//
//        // #576 auth plugin is in charge of deleting credentials - EM
//        if (user.getUuid() != null) {
//          // gw has not uuid == null, but cloud has!
//          user = updateCredentialsAterSuccessfulAuthentication(user);
//        }
//
//        logAuthenticated(user);
//
//        return user;
//      }
//
//    }
  }

  private boolean validateAccount (@Nullable final String account)
  {
    return account != null;
  }


  public UserRepository getUserRepository() {
    return userRepository;
  }

  public void setUserRepository(UserRepository userRepository) {
    this.userRepository = userRepository;
  }


//  protected PasswordEncoder defaultHasher() {
//    return new BCryptPasswordEncoder();
//  }
//
//  public PasswordEncoder getHasher() {
//    if (hasher == null) {
//      setHasher(defaultHasher());
//    }
//    return hasher;
//  }
//
//  public void setHasher(PasswordEncoder hasher) {
//    this.hasher = hasher;
//  }

}
