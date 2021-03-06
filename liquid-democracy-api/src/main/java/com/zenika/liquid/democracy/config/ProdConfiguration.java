package com.zenika.liquid.democracy.config;

import java.io.IOException;

import com.zenika.liquid.democracy.authentication.persistence.CollaboratorRepository;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.web.ProviderSignInController;

import com.zenika.liquid.democracy.authentication.AppConfig;
import com.zenika.liquid.democracy.authentication.security.config.cond.ConditionnalOnGoogleKey;
import com.zenika.liquid.democracy.authentication.service.CollaboratorService;
import com.zenika.liquid.democracy.authentication.spring.social.SimpleSignInAdapter;

@Configuration
@Profile({ "prod", "dev" })
public class ProdConfiguration {

	private static final String PATTERN = "[%d{yyyy-MM-dd HH:mm:ss.SSS}] boot%X{context} - ${PID} %5p [%t] --- %c{3}: %m%n";
	private static final String DATE_PATTERN = "yyyy-MM-dd";

	@Autowired
	AppConfig appConfig;

	@Bean
	public CollaboratorService collaboratorService(CollaboratorRepository collaboratorRepository) {
		return new CollaboratorService(collaboratorRepository);
	}

	@Bean
	public Logger apiLogger() throws IOException {
		Logger apiLogger = Logger.getLogger("com.zenika.liquid.democracy.api");
		apiLogger.setLevel(Level.INFO);
		apiLogger.addAppender(apiFileAppender());
		apiLogger.addAppender(consoleAppender());

		return apiLogger;
	}

	@Bean
	public Logger springLogger() throws IOException {
		Logger apiLogger = Logger.getLogger("org.springframework");
		apiLogger.setLevel(Level.INFO);
		apiLogger.addAppender(applicationFileAppender());
		apiLogger.addAppender(consoleAppender());

		return apiLogger;
	}

	@Bean
	@ConditionnalOnGoogleKey
	public ProviderSignInController providerSignInController(ConnectionFactoryLocator connectionFactoryLocator,
	        UsersConnectionRepository usersConnectionRepository, SimpleSignInAdapter simpleSignInAdapter) {
		ProviderSignInController controller = new ProviderSignInController(connectionFactoryLocator,
		        usersConnectionRepository, simpleSignInAdapter);
		controller.setApplicationUrl(appConfig.getApplicationUrl());
		return controller;
	}


	private ConsoleAppender consoleAppender() {
		PatternLayout patternLayout = new PatternLayout(PATTERN);
		ConsoleAppender consoleAppender = new ConsoleAppender(patternLayout);
		consoleAppender.setThreshold(Level.ALL);
		return consoleAppender;
	}

	private DailyRollingFileAppender apiFileAppender() throws IOException {
		PatternLayout patternLayout = new PatternLayout(PATTERN);

		DailyRollingFileAppender apiFileAppender = new DailyRollingFileAppender(patternLayout,
				"./logs/liquid_democracy_api.log", DATE_PATTERN);
		apiFileAppender.setThreshold(Level.ALL);

		return apiFileAppender;
	}

	private DailyRollingFileAppender applicationFileAppender() throws IOException {
		PatternLayout patternLayout = new PatternLayout(PATTERN);

		DailyRollingFileAppender apiFileAppender = new DailyRollingFileAppender(patternLayout,
				"./logs/liquid_democracy_application.log", DATE_PATTERN);
		apiFileAppender.setThreshold(Level.ERROR);

		return apiFileAppender;
	}

}
