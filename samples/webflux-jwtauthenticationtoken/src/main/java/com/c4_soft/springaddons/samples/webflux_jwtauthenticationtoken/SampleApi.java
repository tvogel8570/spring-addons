package com.c4_soft.springaddons.samples.webflux_jwtauthenticationtoken;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import com.c4_soft.springaddons.security.oauth2.ReactiveJwt2AuthenticationConverter;
import com.c4_soft.springaddons.security.oauth2.ReactiveJwt2GrantedAuthoritiesConverter;
import com.c4_soft.springaddons.security.oauth2.config.reactive.AuthorizeExchangeSpecPostProcessor;

@SpringBootApplication
public class SampleApi {
	public static void main(String[] args) {
		new SpringApplicationBuilder(SampleApi.class).web(WebApplicationType.REACTIVE).run(args);
	}

	@EnableReactiveMethodSecurity()
	public static class WebSecurityConfig {

		@Bean
		public ReactiveJwt2AuthenticationConverter<JwtAuthenticationToken> authenticationConverter(
				ReactiveJwt2GrantedAuthoritiesConverter authoritiesConverter) {
			return jwt -> authoritiesConverter.convert(jwt).collectList().map(authorities -> new JwtAuthenticationToken(jwt, authorities));
		}

		@Bean
		public AuthorizeExchangeSpecPostProcessor authorizeExchangeSpecPostProcessor() {
			return (ServerHttpSecurity.AuthorizeExchangeSpec spec) -> spec
					.pathMatchers("/secured-route")
					.hasRole("AUTHORIZED_PERSONNEL")
					.anyExchange()
					.authenticated();
		}

	}
}