package com.grsm.poc.glovo.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Value("${basic.auth.user}") // grsm-poc
	String user;
	@Value("${basic.auth.pass}") // grsm-poc-password
	String pass;

	@Autowired
	private AuthenticationEntryPoint authenticationEntryPoint;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable().authorizeRequests()//
				.antMatchers("/b2b/*")//
				.authenticated().and().httpBasic()//
				.authenticationEntryPoint(authenticationEntryPoint);
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/v2/api-docs", //
				"/v3/api-docs", //
				"/configuration/ui", //
				"/swagger-resources", //
				"/configuration/security", //
				"/swagger-ui.html", //
				"/swagger-ui", //
				"/webjars/**");
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication() //
				.withUser(user) //
				.password("{noop}" + pass) //
				.roles("USER"); //
	}

}
