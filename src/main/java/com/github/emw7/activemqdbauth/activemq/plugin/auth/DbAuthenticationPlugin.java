package com.github.emw7.activemqdbauth.activemq.plugin.auth;

import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.BrokerPlugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Installs the {@link AuthenticatorAuthenticationBroker} activemq broker plugin.
 * <p>
 * The plugin is created in (and returned by) {@link #installPlugin(Broker)}) method.<br/>
 * The method {@link #installPlugin(Broker)}) gets a {@link Broker} instance as argument (passed by
 * activemq it self) that is passed to {@link AuthenticatorAuthenticationBroker} in order to chain the
 * plugins.
 * <p>
 * Note that it is here that the {@link Authenticator} instance is injected because it is this
 * class that is instantiated by activemq (see activemq.xml file). In turn {@link Authenticator}
 * instance is passed as constructor argument to {@link AuthenticatorAuthenticationBroker} that uses
 * {@link Authenticator} actually.
 * <p>
 * <b>Note</b>: I think that the name {@code BrokerPlugin} is misleading as this is not
 * the plugin, this is where the plugin is created (by {@link #installPlugin(Broker)}).
 *
 * @see AuthenticatorAuthenticationBroker
 */
public class DbAuthenticationPlugin implements BrokerPlugin {

	private static final Logger log= LoggerFactory.getLogger(DbAuthenticationPlugin.class);

	private Authenticator authenticator;

	// refer to class documentation.
	public Broker installPlugin(Broker broker) throws Exception {
		log.info(String.format("[EMW7] creating %s plugin",
				DbAuthenticationPlugin.class.getName()));
		return new AuthenticatorAuthenticationBroker(broker,authenticator);
	}

	// used for setter dependency injection!
	public void setAuthenticator(Authenticator authenticator) {
		this.authenticator = authenticator;
	}

}
