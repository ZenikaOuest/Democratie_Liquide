package com.zenika.si.core.zenika.authentication.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.social.security.SpringSocialConfigurer;

import com.zenika.si.core.zenika.authentication.AppConfig;
import com.zenika.si.core.zenika.authentication.spring.social.google.GoogleConfigurerAdapter;

@Configuration
@EnableWebSecurity
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
@Profile({ "docker", "test-prod" })
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	private static final String LOGIN_PAGE = "/static/index.html";

	@Autowired
	RememberMeServices rememberMeServices;
	@Autowired
	AppConfig appConfig;

	@Autowired
	GoogleConfigurerAdapter googleConfigurerAdapter;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		SpringSocialConfigurer springSocialConfigurer = new SpringSocialConfigurer();
		http.exceptionHandling().and().authorizeRequests()
				.antMatchers("/", "/test", "/signin/google", "/styles/**", "/scripts/**").permitAll()
				.antMatchers("/test/list", "/api/**").hasRole("ZENIKA").anyRequest().fullyAuthenticated().and()
				.formLogin().disable().anonymous().and().logout().deleteCookies("JSESSIONID")
				.logoutSuccessUrl(LOGIN_PAGE + "?param.action=loggedOut").and().rememberMe()
				.rememberMeServices(rememberMeServices).key(appConfig.getRememberMeKey()).and().csrf().disable()
				.apply(springSocialConfigurer);
	}
}