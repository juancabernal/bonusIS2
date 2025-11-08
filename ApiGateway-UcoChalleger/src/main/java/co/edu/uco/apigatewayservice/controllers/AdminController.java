package co.edu.uco.apigatewayservice.controllers;

import co.edu.uco.apigatewayservice.dto.ApiMessageResponse;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping(path = "/api/admin", produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminController {

    // 🔹 Dirección del backend real (ucochallenge-ms)
    private static final String USER_SERVICE_BASE_URL = "http://localhost:8083/uco-challenge/api/v1";

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/dashboard")
    public ResponseEntity<ApiMessageResponse> dashboard() {
        ApiMessageResponse response = new ApiMessageResponse(
                "Panel administrativo disponible.",
                "administrador",
                "dashboard"
        );
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
