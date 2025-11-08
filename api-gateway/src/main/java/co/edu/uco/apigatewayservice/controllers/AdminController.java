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

    // ✅ Nuevo endpoint que redirige la petición al backend
    @GetMapping("/users")
    public ResponseEntity<String> getUsersFromBackend(
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size
    ) {
        try {
            // Construimos la URL al backend real
            String url = USER_SERVICE_BASE_URL + "/users?page=" + page + "&size=" + size;

            // Llamada al microservicio ucochallenge-ms
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            // Devuelve la misma respuesta al frontend
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());

        } catch (Exception e) {
            // Manejo básico de errores
            String error = String.format("{\"error\": \"Error al comunicarse con el servicio de usuarios: %s\"}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
