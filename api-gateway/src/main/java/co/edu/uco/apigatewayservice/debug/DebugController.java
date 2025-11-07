package co.edu.uco.apigatewayservice.debug;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
public class DebugController {

    /**
     * Devuelve los claims del JWT recibido y las authorities resueltas por Spring Security.
     * Llamar con Authorization: Bearer <TOKEN>
     */
    @GetMapping(path = "/debug/whoami", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map<String, Object>> whoami() {
        return ReactiveSecurityContextHolder.getContext()
            .map(ctx -> ctx.getAuthentication())
            .flatMap(this::buildResponseFromAuth)
            .switchIfEmpty(Mono.just(Map.of("error", "no authentication in security context")));
    }

    private Mono<Map<String, Object>> buildResponseFromAuth(Authentication auth) {
        if (auth == null) {
            return Mono.just(Map.of("error", "authentication is null"));
        }

        // Intentar obtener Jwt desde el principal; si no, intentar desde credentials
        Object principal = auth.getPrincipal();
        Jwt jwt = null;
        if (principal instanceof Jwt) {
            jwt = (Jwt) principal;
        } else {
            Object credentials = auth.getCredentials();
            if (credentials instanceof Jwt) {
                jwt = (Jwt) credentials;
            }
        }

        List<String> authorities = auth.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        if (jwt != null) {
            return Mono.just(Map.of(
                    "principalName", auth.getName(),
                    "claims", jwt.getClaims(),
                    "authorities", authorities
            ));
        } else {
            // Si no se pudo parsear Jwt, devolvemos lo que hay en Authentication
            return Mono.just(Map.of(
                    "principalType", principal == null ? "null" : principal.getClass().getName(),
                    "principalToString", String.valueOf(principal),
                    "authorities", authorities
            ));
        }
    }
}
