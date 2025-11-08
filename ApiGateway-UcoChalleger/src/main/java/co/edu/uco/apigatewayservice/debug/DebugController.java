package co.edu.uco.apigatewayservice.debug;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.*;

@RestController
@RequestMapping("/debug")
public class DebugController {

  private static final String ROLES_CLAIM = "https://uco-challenge/roles";
  private static final Set<String> SAFE_LIST_CLAIMS = Set.of(ROLES_CLAIM);

  @GetMapping("/whoami")
  public Mono<Map<String, Object>> whoami(@AuthenticationPrincipal Jwt jwt, Authentication auth) {
    Map<String, Object> out = new LinkedHashMap<>();
    out.put("authenticated", auth != null && auth.isAuthenticated());
    out.put("name", auth != null ? auth.getName() : null);
    out.put("authorities", auth != null ? auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList() : List.of());
    out.put("roles_claim", safeStringListClaim(jwt, ROLES_CLAIM));
    out.put("aud", jwt != null ? jwt.getAudience() : List.of());
    out.put("iss", jwt != null ? String.valueOf(jwt.getIssuer()) : null);
    return Mono.just(out);
  }

  private List<String> safeStringListClaim(Jwt jwt, String claimName) {
    if (jwt == null || claimName == null || !SAFE_LIST_CLAIMS.contains(claimName)) {
      return List.of();
    }
    List<String> claimValues = jwt.getClaimAsStringList(claimName);
    if (claimValues == null) {
      return List.of();
    }
    return claimValues.stream()
        .filter(Objects::nonNull)
        .map(String::valueOf)
        .toList();
  }
}

