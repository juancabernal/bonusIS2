package co.edu.uco.apigatewayservice.config;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.config.GlobalCorsProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

    private static final String ROLES_CLAIM = "https://uco-challenge-api/roles";
    private static final String ADMIN_ROLE = "admin";
    private static final String USER_ROLE = "usuario";

    private final String audience;
    private final String issuer;
    private final GlobalCorsProperties globalCorsProperties;

    public SecurityConfig(@Value("${auth0.audience}") String audience,
                          @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}") String issuer,
                          GlobalCorsProperties globalCorsProperties) {
        this.audience = audience;
        this.issuer = issuer;
        this.globalCorsProperties = globalCorsProperties;
    }

    /**
     * Cadena 0: libera /actuator/** (incluye /actuator/prometheus) sin autenticacion.
     */
    @Bean
    @Order(0)
    public SecurityWebFilterChain actuatorChain(ServerHttpSecurity http) {
        return http
            .securityMatcher(ServerWebExchangeMatchers.pathMatchers("/actuator/**"))
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .cors(c -> c.configurationSource(corsConfigurationSource()))
            .authorizeExchange(ex -> ex.anyExchange().permitAll())
            .build();
    }

    /**
     * Cadena 1: resto de rutas con tu configuracion original.
     */
    @Bean
    @Order(1)
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .cors(corsSpec -> corsSpec.configurationSource(corsConfigurationSource()))
            .authorizeExchange(exchange -> exchange
                .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .pathMatchers("/actuator/health").permitAll() // opcional; ya cubierto por la cadena 0
                .pathMatchers("/api/public/**").permitAll()
                .pathMatchers("/debug/whoami").authenticated()
                .pathMatchers("/api/admin/**").hasAuthority(ADMIN_ROLE)
                .pathMatchers("/api/user/**").hasAnyAuthority(ADMIN_ROLE, USER_ROLE)
                .anyExchange().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwtSpec -> jwtSpec
                    .jwtDecoder(jwtDecoder())
                    .jwtAuthenticationConverter(jwtAuthenticationConverter())
                )
            )
            .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("http://localhost:5175");
        config.addAllowedOrigin("http://localhost:5174");
        config.addAllowedOrigin("http://localhost:5173");
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.setAllowCredentials(true);
        config.addExposedHeader("Authorization");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }


    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        NimbusJwtDecoder nimbus = (NimbusJwtDecoder) JwtDecoders.fromOidcIssuerLocation(issuer);
        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuer);
        OAuth2TokenValidator<Jwt> withAudience =
            new DelegatingOAuth2TokenValidator<>(withIssuer, new AudienceValidator(audience));
        nimbus.setJwtValidator(withAudience);
        return token -> Mono.fromCallable(() -> nimbus.decode(token));
    }

    @Bean
    public Converter<Jwt, Mono<AbstractAuthenticationToken>> jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthoritiesClaimName(ROLES_CLAIM);
        authoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);

        return new ReactiveJwtAuthenticationConverterAdapter(jwtConverter);
    }
}
