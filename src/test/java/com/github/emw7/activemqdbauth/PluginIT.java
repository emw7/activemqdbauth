package com.github.emw7.activemqdbauth;

import com.github.emw7.activemqdbauth.activemq.plugin.auth.config.Config;
import com.github.emw7.activemqdbauth.activemq.plugin.auth.AuthenticationException;
import com.github.emw7.activemqdbauth.activemq.plugin.auth.Authenticator;
import com.github.emw7.activemqdbauth.activemq.plugin.auth.AuthenticatorDbSimple;
import com.github.emw7.activemqdbauth.activemq.plugin.auth.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig
class PluginIT {

  @Configuration
  @Import(Config.class)
  public static class SpringConfig {
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertiesResolver() {
      return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public static AuthenticatorDbSimple authenticatorDbSimple () {
      return new AuthenticatorDbSimple();
    }
  }

  @Autowired
  Authenticator authenticator;

  @Test
  public void givenInvalidInputType_whenAuthenticate_thenThrowClassCastException () {
    Assertions.assertThatThrownBy(() -> authenticator.authenticate(new Object()))
        .as("authenticate throws ClassCastException if input is not of type User")
        .isInstanceOf(ClassCastException.class);
  }

  @Test
  public void givenNullAccount_whenAuthenticate_thenThrowNullPointerException () {
    Assertions.assertThatThrownBy(() -> authenticator.authenticate(new User(null, null, true, null, null)))
        .as("authenticate throws NullPointerException if account is null")
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  public void givenNotExistentAccount_whenAuthenticate_thenThrowAuthenticationException () {
    Assertions.assertThatThrownBy(() -> authenticator.authenticate(new User("does-not-exist", null, true, null, null)))
        .as("authenticate throws AuthenticationException if user does not exist")
        .isInstanceOf(AuthenticationException.class)
        .hasMessage("invalid username 'does-not-exist' or password");
  }

  @Test
  public void givenNullPassword_whenAuthenticate_thenThrowAuthenticationException () {
    Assertions.assertThatThrownBy(() -> authenticator.authenticate(new User("here-I-am", null, true, null, null)))
        .as("authenticate throws AuthenticationException if password is null")
        .isInstanceOf(AuthenticationException.class)
        .hasMessage("invalid username 'here-I-am' or password");
  }

  @Test
  public void givenInvalidPassword_whenAuthenticate_thenThrowAuthenticationException () {
    Assertions.assertThatThrownBy(() -> authenticator.authenticate(new User("here-I-am", "x", true, null, null)))
        .as("authenticate throws AuthenticationException if password is not valid")
        .isInstanceOf(AuthenticationException.class)
        .hasMessage("invalid username 'here-I-am' or password");
  }

  @Test
  public void givenValidCredentials_whenAuthenticate_thenAuthenticateGoFine () throws AuthenticationException{
    Assertions.assertThatNoException().as("authenticate goes fine if username and password are valid")
        .isThrownBy(() -> authenticator.authenticate(new User("here-I-am", "with-This-p@ssw0rd", true, null, null)));
  }

}
