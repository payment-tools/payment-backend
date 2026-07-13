package com.sn.onepay.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class KeycloakRealmRoleConverterTest {

    final KeycloakRealmRoleConverter converter = new KeycloakRealmRoleConverter();

    private Jwt jwtWithClaim(String name, Object value) {
        return Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim(name, value)
                .build();
    }

    @Test
    void convert_mapsRealmRolesToPrefixedAuthorities() {
        var jwt = jwtWithClaim("realm_access", Map.of("roles", List.of("CLIENT", "CASHIER")));

        var authorities = converter.convert(jwt);

        assertThat(authorities)
                .extracting(GrantedAuthority::getAuthority)
                .containsExactlyInAnyOrder("ROLE_CLIENT", "ROLE_CASHIER");
    }

    @Test
    void convert_withoutRealmAccess_returnsNoAuthorities() {
        var jwt = jwtWithClaim("sub", "user");

        assertThat(converter.convert(jwt)).isEmpty();
    }

    @Test
    void convert_withRealmAccessWithoutRoles_returnsNoAuthorities() {
        var jwt = jwtWithClaim("realm_access", Map.of("other", "value"));

        assertThat(converter.convert(jwt)).isEmpty();
    }
}
