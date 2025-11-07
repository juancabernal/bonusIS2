package co.edu.uco.ucochallenge.infrastructure.primary.web.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.uco.ucochallenge.application.idtype.service.dto.IdTypeDTO;
import co.edu.uco.ucochallenge.application.idtype.service.interactor.usecase.IdTypeQueryService;

@RestController
@RequestMapping("/uco-challenge/api/v1/idtypes")
public class PrimaryIdTypeQueryController {

        private final IdTypeQueryService idTypeQueryService;

        public PrimaryIdTypeQueryController(final IdTypeQueryService idTypeQueryService) {
                this.idTypeQueryService = idTypeQueryService;
        }

        @GetMapping
        public ResponseEntity<List<IdTypeDTO>> getAll() {
                final var idTypes = idTypeQueryService.findAll();
                return ResponseEntity.ok(idTypes);
        }
}
