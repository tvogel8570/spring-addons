package com.c4_soft.springaddons.security.oauth2.test.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithSecurityContext;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@WithSecurityContext(factory = WithOAuth2Login.OAuth2AuthenticationTokenFactory.class)
public @interface WithOAuth2Login {

    @AliasFor("authorities")
    String[] value() default {};

    @AliasFor("value")
    String[] authorities() default {};

    OpenIdClaims claims() default @OpenIdClaims();

    String tokenString() default "machin.truc.chose";

    String authorizedClientRegistrationId() default "bidule";

    /**
     * @return the key used to access the user's &quot;name&quot; from claims. This
     *         takes precedence over OpenIdClaims::usernameClaim if both are defined
     */
    String nameAttributeKey() default JwtClaimNames.SUB;

    @AliasFor(annotation = WithSecurityContext.class)
    TestExecutionEvent setupBefore() default TestExecutionEvent.TEST_METHOD;

    public static final class OAuth2AuthenticationTokenFactory
            extends AbstractAnnotatedAuthenticationBuilder<WithOAuth2Login, OAuth2AuthenticationToken> {
        @Override
        public OAuth2AuthenticationToken authentication(WithOAuth2Login annotation) {
            final var token = super.claims(annotation.claims()).usernameClaim(annotation.nameAttributeKey()).build();
            final var authorities = super.authorities(annotation.authorities());
            final var principal = new DefaultOAuth2User(authorities, token, annotation.nameAttributeKey());

            return new OAuth2AuthenticationToken(principal, authorities, annotation.authorizedClientRegistrationId());
        }
    }
}