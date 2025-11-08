package co.edu.uco.ucochallenge.security.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Component
public class GatewaySignatureFilter extends OncePerRequestFilter {

    private final String hmacSecret;

    public GatewaySignatureFilter(@Value("${security.gateway.hmac-secret:}") String hmacSecret) {
        this.hmacSecret = hmacSecret;
    }

    // ⬇️ Nuevo: no filtrar Actuator (/actuator/**)
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path != null && path.startsWith("/actuator/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (!StringUtils.hasText(hmacSecret)) {
            filterChain.doFilter(request, response);
            return;
        }

        String signature = request.getHeader("X-Internal-Signature");
        if (signature == null || !MessageDigest.isEqual(
                signature.getBytes(StandardCharsets.UTF_8),
                hmacSecret.getBytes(StandardCharsets.UTF_8))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Gateway signature required");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
