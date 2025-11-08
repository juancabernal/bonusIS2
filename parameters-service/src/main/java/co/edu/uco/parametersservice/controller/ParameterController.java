package co.edu.uco.parametersservice.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.uco.parametersservice.catalog.Parameter;
import co.edu.uco.parametersservice.catalog.ParameterCatalog;

@RestController
@RequestMapping("/parameters/api/v1/parameters")
public class ParameterController {
	
        @GetMapping
        public ResponseEntity<Map<String, Parameter>> getAllParameters() {
                return ResponseEntity.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(ParameterCatalog.getAllParameters());
        }

        @GetMapping("/{key}")
        public ResponseEntity<Parameter> getParameter(@PathVariable String key) {
                var value = ParameterCatalog.getParameterValue(key);
                if (value == null) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .build();
                }
                return ResponseEntity.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(value);
        }

        @PutMapping("/{key}/{value}")
        public ResponseEntity<Parameter> updateParameter(@PathVariable String key, @PathVariable String value) {
                var parameter = new Parameter(key, value);
                ParameterCatalog.synchronizeParameterValue(parameter);
                return ResponseEntity.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(parameter);
        }


}
