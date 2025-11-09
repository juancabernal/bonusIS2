package co.edu.uco.ucochallenge.infrastructure.primary.rest.idtype;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.uco.ucochallenge.application.idtype.dto.IdTypeDTO;
import co.edu.uco.ucochallenge.application.idtype.service.IdTypeQueryService;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
@RequestMapping("/uco-challenge/api/v1/idtypes")
public class IdTypeController {

        private final IdTypeQueryService idTypeQueryService;

        public IdTypeController(final IdTypeQueryService idTypeQueryService) {
                this.idTypeQueryService = idTypeQueryService;
        }

        @GetMapping
        public Mono<ResponseEntity<List<IdTypeDTO>>> getAll() {
                return Mono.fromCallable(() -> ResponseEntity.ok(idTypeQueryService.findAll()))
                                .subscribeOn(Schedulers.boundedElastic());
        }
}
