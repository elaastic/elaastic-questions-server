package org.elaastic.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class OAuthSecurityConfig { // TODO JT: Check if this is required ; and if it can be migrated into kotlin easily
    interface AuthoritiesConverter extends Converter<Map<String, Object>, Collection<GrantedAuthority>> {
    }

    @Bean
    AuthoritiesConverter realmRolesAuthoritiesConverter() {
        return claims -> {
            final var realmAccess = Optional.ofNullable((Map<String, Object>) claims.get("realm_access"));
            final var roles =
                    realmAccess.flatMap(map -> Optional.ofNullable((List<String>) map.get("roles")));
            return roles.map(List::stream).orElse(Stream.empty()).map(SimpleGrantedAuthority::new)
                    .map(GrantedAuthority.class::cast).toList();
        };
    }

    @Bean
    GrantedAuthoritiesMapper authenticationConverter(
            Converter<Map<String, Object>, Collection<GrantedAuthority>> realmRolesAuthoritiesConverter) {
        return (authorities) -> authorities.stream()
                .filter(authority -> authority instanceof OidcUserAuthority)
                .map(OidcUserAuthority.class::cast).map(OidcUserAuthority::getIdToken)
                .map(OidcIdToken::getClaims).map(realmRolesAuthoritiesConverter::convert)
                .flatMap(roles -> roles.stream()).collect(Collectors.toSet());
    }

}
