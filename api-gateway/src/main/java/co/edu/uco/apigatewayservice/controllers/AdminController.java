package co.edu.uco.apigatewayservice.controllers;

import java.util.List;
import java.util.Map;
//.
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.uco.apigatewayservice.dto.ApiMessageResponse;

@RestController
@RequestMapping(path = "/api/admin", produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminController {

    @GetMapping("/dashboard")
    public ResponseEntity<ApiMessageResponse> dashboard() {
        ApiMessageResponse response = new ApiMessageResponse(
                "Panel administrativo disponible.",
                "administrador",
                "dashboard"
        );
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    
    @RestController
    @RequestMapping("/api/admin")
    public class AdminUserController {

        @GetMapping("/users")
        public ResponseEntity<Object> listUsers() {
            // simula respuesta
            return ResponseEntity.ok(Map.of(
                    "message", "Lista de usuarios (solo administrador).",
                    "users", List.of("Alice", "Bob", "Charlie")
            ));
        }
    }
}
