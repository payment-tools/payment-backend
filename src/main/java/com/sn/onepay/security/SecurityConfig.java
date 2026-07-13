package com.sn.onepay.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    static final String SUPER_ADMIN = "SUPER_ADMIN";
    static final String ENTERPRISE_ADMIN = "ENTERPRISE_ADMIN";
    static final String ENTERPRISE_FINANCE = "ENTERPRISE_FINANCE";
    static final String SALES_ADMIN = "SALES_ADMIN";
    static final String SALES_FINANCE = "SALES_FINANCE";
    static final String CLIENT = "CLIENT";
    static final String CASHIER = "CASHIER";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/v3/api-docs.yaml").permitAll()

                        // Plateforme : entreprises, commerces et partenariats gérés par le SUPER_ADMIN uniquement
                        .requestMatchers(HttpMethod.POST, "/v1/onepay/enterprise/**", "/v1/onepay/sales/**", "/v1/onepay/partnership/**").hasRole(SUPER_ADMIN)
                        .requestMatchers(HttpMethod.PUT, "/v1/onepay/enterprise/**", "/v1/onepay/sales/**", "/v1/onepay/partnership/**").hasRole(SUPER_ADMIN)
                        .requestMatchers(HttpMethod.DELETE, "/v1/onepay/enterprise/**", "/v1/onepay/sales/**", "/v1/onepay/partnership/**").hasRole(SUPER_ADMIN)

                        // Côté entreprise : clients, profils, configurations, groupes et subventions
                        .requestMatchers(HttpMethod.POST, "/v1/onepay/client/**", "/v1/onepay/enterprise-profile/**", "/v1/onepay/enterprise-configuration/**", "/v1/onepay/employee-group/**", "/v1/onepay/subvention/**").hasAnyRole(SUPER_ADMIN, ENTERPRISE_ADMIN)
                        .requestMatchers(HttpMethod.PUT, "/v1/onepay/client/**", "/v1/onepay/enterprise-profile/**", "/v1/onepay/enterprise-configuration/**", "/v1/onepay/employee-group/**", "/v1/onepay/subvention/**").hasAnyRole(SUPER_ADMIN, ENTERPRISE_ADMIN)
                        .requestMatchers(HttpMethod.DELETE, "/v1/onepay/client/**", "/v1/onepay/enterprise-profile/**", "/v1/onepay/enterprise-configuration/**", "/v1/onepay/employee-group/**", "/v1/onepay/subvention/**").hasAnyRole(SUPER_ADMIN, ENTERPRISE_ADMIN)

                        // Côté commerce : caissiers, profils et configurations du point de vente
                        .requestMatchers(HttpMethod.POST, "/v1/onepay/cashier/**", "/v1/onepay/sales-profile/**", "/v1/onepay/sales-configuration/**").hasAnyRole(SUPER_ADMIN, SALES_ADMIN)
                        .requestMatchers(HttpMethod.PUT, "/v1/onepay/cashier/**", "/v1/onepay/sales-profile/**", "/v1/onepay/sales-configuration/**").hasAnyRole(SUPER_ADMIN, SALES_ADMIN)
                        .requestMatchers(HttpMethod.DELETE, "/v1/onepay/cashier/**", "/v1/onepay/sales-profile/**", "/v1/onepay/sales-configuration/**").hasAnyRole(SUPER_ADMIN, SALES_ADMIN)

                        // Paiements : créés par un employé ou un caissier, annulés (suppression logique) par le caissier
                        .requestMatchers(HttpMethod.POST, "/v1/onepay/payment/**").hasAnyRole(CLIENT, CASHIER)
                        .requestMatchers(HttpMethod.PUT, "/v1/onepay/payment/**").hasRole(CASHIER)
                        .requestMatchers(HttpMethod.DELETE, "/v1/onepay/payment/**").hasRole(CASHIER)

                        // Factures : gérées par la plateforme et validées par les rôles finance
                        .requestMatchers(HttpMethod.POST, "/v1/onepay/bills/**").hasAnyRole(SUPER_ADMIN, ENTERPRISE_FINANCE, SALES_FINANCE)
                        .requestMatchers(HttpMethod.PUT, "/v1/onepay/bills/**").hasAnyRole(SUPER_ADMIN, ENTERPRISE_FINANCE, SALES_FINANCE)
                        .requestMatchers(HttpMethod.DELETE, "/v1/onepay/bills/**").hasAnyRole(SUPER_ADMIN, ENTERPRISE_FINANCE, SALES_FINANCE)

                        // Lectures (GET) et QR codes : tout utilisateur authentifié
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new KeycloakRealmRoleConverter());
        return converter;
    }
}
