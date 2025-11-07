package co.edu.uco.ucochallenge.infrastructure.primary.configuration;

import co.edu.uco.ucochallenge.infrastructure.primary.security.web.GatewaySignatureFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final GatewaySignatureFilter gatewaySignatureFilter;
    private final String expectedAudience;
    private final String issuerUri;

    public SecurityConfig(GatewaySignatureFilter gatewaySignatureFilter,
                          @Value("${jwt.expected-audience}") String expectedAudience,
                          @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}") String issuerUri) {
        this.gatewaySignatureFilter = gatewaySignatureFilter;
        this.expectedAudience = expectedAudience;
        this.issuerUri = issuerUri;
    }

    // ------------------------------------------------------------
    // 1) Cadena SOLO para Actuator (sin HMAC ni JWT)
    // ------------------------------------------------------------
    @Bean
    @Order(1)
    public SecurityFilterChain actuatorFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/actuator/**")
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/actuator/health",
                    "/actuator/info",
                    "/actuator/prometheus"
                ).permitAll()
                .anyRequest().permitAll()
            );
        return http.build();
    }

    // ------------------------------------------------------------
    // 2) Cadena principal para la aplicación
    // ------------------------------------------------------------
    @Bean
    @Order(2)
    public SecurityFilterChain appFilterChain(HttpSecurity http) throws Exception {
        http
            // ✅ Activa CORS
            .cors().and()
            // ✅ Desactiva CSRF para APIs REST
            .csrf(csrf -> csrf.disable())
            // ✅ Configura autorización
            .authorizeHttpRequests(authorize -> authorize
                // 👉 Permitir preflight CORS (esto es CLAVE)
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // 👉 Permitir registro de usuarios sin token
                .requestMatchers(HttpMethod.POST, "/uco-challenge/api/v1/users").permitAll()
                .requestMatchers("/uco-challenge/api/v1/users/verify-code").permitAll()
                // 👉 El resto requiere autenticación
                .anyRequest().authenticated()
            )
            // ✅ IMPORTANTE: esto permite que el filtro CORS corra antes del JWT
            .oauth2ResourceServer(oauth2 -> oauth2.jwt())
            .addFilterBefore(gatewaySignatureFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ------------------------------------------------------------
    // 3) Configuración del JWT Decoder y validación de audiencia
    // ------------------------------------------------------------
    @Bean
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withIssuerLocation(issuerUri).build();
        OAuth2TokenValidator<Jwt> issuerValidator = JwtValidators.createDefaultWithIssuer(issuerUri);
        OAuth2TokenValidator<Jwt> audienceValidator = new AudienceValidator(expectedAudience);
        jwtDecoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(issuerValidator, audienceValidator));
        return jwtDecoder;
    }

    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter());
        return converter;
    }

    private Converter<Jwt, Collection<GrantedAuthority>> grantedAuthoritiesConverter() {
        return jwt -> {
            Set<GrantedAuthority> authorities = new LinkedHashSet<>();
            List<String> permissions = jwt.getClaimAsStringList("permissions");
            if (!CollectionUtils.isEmpty(permissions)) {
                permissions.stream()
                    .filter(StringUtils::hasText)
                    .forEach(p -> authorities.add(new SimpleGrantedAuthority("SCOPE_" + p)));
            } else {
                String scope = jwt.getClaimAsString("scope");
                if (StringUtils.hasText(scope)) {
                    for (String value : scope.split("\\s+")) {
                        if (StringUtils.hasText(value)) {
                            authorities.add(new SimpleGrantedAuthority("SCOPE_" + value));
                        }
                    }
                }
            }
            return new ArrayList<>(authorities);
        };
    }

    // ------------------------------------------------------------
    // 4) Validador personalizado de audiencia JWT
    // ------------------------------------------------------------
    private static class AudienceValidator implements OAuth2TokenValidator<Jwt> {
        private final String expectedAudience;

        private AudienceValidator(String expectedAudience) {
            this.expectedAudience = expectedAudience;
        }

        @Override
        public OAuth2TokenValidatorResult validate(Jwt token) {
            List<String> audiences = token.getAudience();
            if (!StringUtils.hasText(expectedAudience)
                || CollectionUtils.isEmpty(audiences)
                || !audiences.contains(expectedAudience)) {
                throw new BadCredentialsException("Invalid audience");
            }
            return OAuth2TokenValidatorResult.success();
        }
    }
}
