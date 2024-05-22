package com.github.emw7.activemqdbauth.activemq.plugin.auth;

import java.time.ZonedDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * The representation of account-password (user) credential at persistence level.
 * <p>
 * <b>Note</b>: a better name for this class would be {@code AccountPasswordCredential}.
 */
@Table(schema = "activemqdbauth", name = "users")
public class User {

	@Id
	private final String account;
	private final String password;
	private final boolean isActive;
	private final ZonedDateTime createTime;
	private final ZonedDateTime disableTime;

	public User(final String account, final String password, final boolean isActive,
			final ZonedDateTime createTime,
			final ZonedDateTime disableTime) {
		this.account = account;
		this.password = password;
		this.isActive = isActive;
		this.createTime = createTime;
		this.disableTime = disableTime;
	}

	public String getAccount() {
		return account;
	}

	public String getPassword() {
		return password;
	}

	public boolean isActive() {
		return isActive;
	}

	public ZonedDateTime getCreateTime() {
		return createTime;
	}

	public ZonedDateTime getDisableTime() {
		return disableTime;
	}

}
