package com.bh.ecs.integration.cloud;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.bh.ecs.integration.common.utility.Constants;

@Component
@Configuration
@Profile("cloud")
public class DatabaseConfigCloud extends DataSource implements EnvironmentAware {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseConfigCloud.class);

	@Override
	public void setEnvironment(Environment env) {
		String dbServiceName = env.getProperty("db_service_name");
		String envUserName = null;
		String envUserPasswrd = null;
		try {
			if (dbServiceName != null) {
				String envURL = env.getProperty(Constants.VCAP + dbServiceName + ".credentials.uri");
				setUrl(envURL);
				envUserName = env.getProperty(Constants.VCAP + dbServiceName + ".credentials.username");
				if (envUserName != null) {
					setUsername(envUserName);
				}
				envUserPasswrd = env.getProperty(Constants.VCAP + dbServiceName + ".credentials.password");
				if (envUserPasswrd != null) {
					setPassword(envUserPasswrd);
				}
			}	
	} catch(Exception e) {
		LOGGER.info("Exception occured "+ e);
	}
	}
}
