package co.edu.uco.apigatewayservice.config;

import java.util.ArrayList;
import java.util.Collection;
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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
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

    // ✅ Claim correcto según tu token:
    private static final String ROLES_CLAIM = "https://uco-challenge-api/roles";
    private static final String ADMIN_ROLE = "administrador"; // ✅ Coincide con tu JWT
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

    // ✅ Cadena 0: permite Actuator sin autenticación
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

    // ✅ Cadena 1: seguridad principal
    @Bean
    @Order(1)
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .cors(corsSpec -> corsSpec.configurationSource(corsConfigurationSource()))
            .authorizeExchange(exchange -> exchange
                .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .pathMatchers("/api/public/**").permitAll()
                .pathMatchers("/debug/whoami").authenticated()
                .pathMatchers("/api/admin/**").hasAuthority(ADMIN_ROLE)    // ✅ admin con rol "administrador"
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

    // ✅ CORS heredado desde application.properties
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("http://localhost:5174");
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.setAllowCredentials(true);
        config.addExposedHeader("Authorization");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    // ✅ Valida issuer + audience
    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        NimbusJwtDecoder nimbus = (NimbusJwtDecoder) JwtDecoders.fromOidcIssuerLocation(issuer);
        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuer);
        OAuth2TokenValidator<Jwt> withAudience =
            new DelegatingOAuth2TokenValidator<>(withIssuer, new AudienceValidator(audience));
        nimbus.setJwtValidator(withAudience);
        return token -> Mono.fromCallable(() -> nimbus.decode(token));
    }

    // ✅ Extrae roles desde el claim personalizado de Auth0
    @Bean
    public Converter<Jwt, Mono<AbstractAuthenticationToken>> jwtAuthenticationConverter() {
        return new ReactiveJwtAuthenticationConverterAdapter(jwt -> {
            JwtGrantedAuthoritiesConverter converter = new JwtGrantedAuthoritiesConverter();
            converter.setAuthoritiesClaimName(ROLES_CLAIM);
            converter.setAuthorityPrefix(""); // sin ROLE_
            
            // Si el claim no existe o no es una lista, evita ClassCastException
            Collection<GrantedAuthority> authorities = new ArrayList<>();
            try {
                authorities = converter.convert(jwt);
            } catch (Exception e) {
                System.err.println("⚠️ Error al leer roles desde el token: " + e.getMessage());
            }

            return new JwtAuthenticationToken(jwt, authorities);
        });
    }

}
