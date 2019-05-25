/*
 * Copyright 2019 Jérôme Wacongne
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.security.oauth2.server.resource.authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.c4soft.oauth2.rfc7519.JwtClaimSet;

/**
 * @author Jérôme Wacongne &lt;ch4mp#64;c4-soft.com&gt;
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class JwtAuthenticationTest {
	@Mock
	JwtClaimSet principal;

	@Mock
	PrincipalGrantedAuthoritiesService authoritiesService;

	@Before
	public void setUp() {
		when(principal.getName()).thenReturn("ch4mpy");
		when(authoritiesService.getAuthorities(any())).thenReturn(Set.of(new SimpleGrantedAuthority("UNIT"), new SimpleGrantedAuthority("TEST")));
	}

	@Test
	public void nameIsPrincipalName() {
		final JwtAuthentication actual = new JwtAuthentication(principal, authoritiesService);
		assertThat(actual.getName()).isEqualTo("ch4mpy");
	}

	@Test
	public void authoritiesArethoseProvidedByAuthoritiesService() {
		final JwtAuthentication actual = new JwtAuthentication(principal, authoritiesService);
		assertThat(actual.getAuthorities()).containsExactlyInAnyOrder(new SimpleGrantedAuthority("UNIT"), new SimpleGrantedAuthority("TEST"));
	}

}
