package com.c4_soft.springaddons.samples.webflux_oidcauthentication;
/*
 * Copyright 2019 Jérôme Wacongne.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.JwtMutator;

import com.c4_soft.springaddons.security.oauth2.test.webflux.WebTestClientSupport;
import com.c4_soft.springaddons.security.oauth2.test.webflux.jwt.AutoConfigureAddonsSecurityWebfluxJwt;

import reactor.core.publisher.Mono;

/**
 * @author Jérôme Wacongne &lt;ch4mp&#64;c4-soft.com&gt;
 */
@WebFluxTest(GreetingController.class)
@AutoConfigureAddonsSecurityWebfluxJwt
@Import({ SampleApi.WebSecurityConfig.class })
class GreetingControllerFluentApiTest {

	@MockBean
	private MessageService messageService;

	@Autowired
	WebTestClientSupport api;

	@BeforeEach
	public void setUp() {
		when(messageService.greet(any())).thenAnswer(invocation -> {
			final JwtAuthenticationToken auth = invocation.getArgument(0);
			return Mono.just(String.format("Hello %s! You are granted with %s.", auth.getName(), auth.getAuthorities()));
		});
		when(messageService.getSecret()).thenReturn(Mono.just("Secret message"));
	}

	@Test
	void greetWitoutAuthentication() throws Exception {
		api.get("https://localhost/greet").expectStatus().isUnauthorized();
	}

	@Test
	void greetWithDefaultAuthentication() throws Exception {
		api.mutateWith(mockJwt()).get("https://localhost/greet").expectBody(String.class).isEqualTo("Hello user! You are granted with [SCOPE_read].");
	}

	@Test
	void greetCh4mpy() throws Exception {
		api.mutateWith(ch4mpy()).get("https://localhost/greet").expectBody(String.class)
				.isEqualTo("Hello Ch4mpy! You are granted with [ROLE_AUTHORIZED_PERSONNEL].");
	}

	@Test
	void securedRouteWithoutAuthorizedPersonnelIsForbidden() throws Exception {
		api.mutateWith(mockJwt()).get("https://localhost/secured-route").expectStatus().isForbidden();
	}

	@Test
	void securedMethodWithoutAuthorizedPersonnelIsForbidden() throws Exception {
		api.mutateWith(mockJwt()).get("https://localhost/secured-method").expectStatus().isForbidden();
	}

	@Test
	void securedRouteWithAuthorizedPersonnelIsOk() throws Exception {
		api.mutateWith(ch4mpy()).get("https://localhost/secured-route").expectStatus().isOk();
	}

	@Test
	void securedMethodWithAuthorizedPersonnelIsOk() throws Exception {
		api.mutateWith(ch4mpy()).get("https://localhost/secured-method").expectStatus().isOk();
	}

	private JwtMutator ch4mpy() {
		return mockJwt().authorities(new SimpleGrantedAuthority("ROLE_AUTHORIZED_PERSONNEL")).jwt(token -> token.subject("Ch4mpy"));
	}
}
