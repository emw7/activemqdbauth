package com.github.emw7.activemqdbauth.activemq.plugin.auth.config;

import java.io.ObjectInputFilter;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Properties;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.jdbc.core.dialect.JdbcPostgresDialect;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.data.relational.core.dialect.Dialect;
import org.springframework.data.relational.core.dialect.PostgresDialect;
import org.springframework.data.relational.core.mapping.NamingStrategy;
import org.springframework.data.relational.core.mapping.RelationalMappingContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.transaction.TransactionManager;

@Configuration
@PropertySource(
		value={"classpath:application.properties"},
		ignoreResourceNotFound = false)
@EnableJdbcRepositories(basePackages = "com.github.emw7.activemqdbauth.activemq.plugin.auth")
public class Config extends AbstractJdbcConfiguration {

	private static final Logger logger= LoggerFactory.getLogger(ObjectInputFilter.Config.class);

	// Tag: datasource.
	@Value("${app.datasource.driver.class.name}")
	private String driverClassName;
	@Value("${app.datasource.url}")
	private String url;
	@Value("${app.datasource.username}")
	private String username;
	@Value("${app.datasource.password}")
	private String password;
	// End-Of-Tag: datasource.

	@Override
	protected @NonNull List<?> userConverters() {
		final List<? extends Converter<? extends Serializable, ? extends Comparable<? extends Comparable<?>>>> converters = List.of(
				new ZonedDateTimeConverterRead(), new ZonedDateTimeConverterWrite());
		return converters;
	}

	@WritingConverter
	public static class ZonedDateTimeConverterWrite implements Converter<ZonedDateTime, Timestamp>
	{
		@Nullable
		@Override
		public Timestamp convert(final ZonedDateTime source) {
			return Timestamp.from(source.toInstant());
		}
	}

	@ReadingConverter
	public static class ZonedDateTimeConverterRead implements Converter<Timestamp, ZonedDateTime>
	{
		@Nullable
		@Override
		public ZonedDateTime convert(final Timestamp source) {
			final Instant instant = source.toInstant();
			return instant.atZone(ZoneOffset.UTC);
		}
	}

	@Bean
	public DataSource dataSource(){

		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(driverClassName);
		dataSource.setUrl(url);
		dataSource.setUsername(username);
		dataSource.setPassword(password);
		logger.info("[EMW7] data source bean created; driver: " + driverClassName + "; url: " + url + "; username: " + username);
		return dataSource;
	}

	@Bean
	public NamedParameterJdbcOperations namedParameterJdbcOperations(DataSource dataSource) {
		return new NamedParameterJdbcTemplate(dataSource);
	}

	@Bean
	public TransactionManager transactionManager(DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}

}
