package com.grsm.poc.glovo.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI().info(new Info().title("Glovo API Mock").version("1.0")
				.license(new License().name("Apache 2.0").url("http://springdoc.org"))
				.contact(new Contact().name("Gabriel Saiz").email("grsm.social@gmail.com")));
	}

}