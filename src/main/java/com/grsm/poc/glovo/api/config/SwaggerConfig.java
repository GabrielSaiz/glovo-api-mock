package com.grsm.poc.glovo.api.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@Configuration
@OpenAPIDefinition(info = @Info(//
		title = "Glovo API Mock", version = "1.0", //
		contact = @Contact(name = "Gabriel Saiz", email = "grsm.social@gmail.com")))
@SecurityScheme( //
		name = "basicAuth", //
		type = SecuritySchemeType.HTTP, //
		scheme = "basic")
public class SwaggerConfig {

}
