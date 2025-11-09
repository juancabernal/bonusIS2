package co.edu.uco.ucochallenge.infrastructure.primary.rest.location;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.uco.ucochallenge.application.location.dto.CityDTO;
import co.edu.uco.ucochallenge.application.location.dto.CountryDTO;
import co.edu.uco.ucochallenge.application.location.dto.DepartmentDTO;
import co.edu.uco.ucochallenge.application.location.service.LocationQueryService;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
@RequestMapping("/uco-challenge/api/v1/locations")
public class LocationController {

        private final LocationQueryService locationQueryService;

        public LocationController(final LocationQueryService locationQueryService) {
                this.locationQueryService = locationQueryService;
        }

        @GetMapping("/countries")
        public Mono<ResponseEntity<List<CountryDTO>>> getCountries() {
                return Mono.fromCallable(() -> ResponseEntity.ok(locationQueryService.getCountries()))
                                .subscribeOn(Schedulers.boundedElastic());
        }

        @GetMapping("/countries/{countryId}/departments")
        public Mono<ResponseEntity<List<DepartmentDTO>>> getDepartments(@PathVariable final UUID countryId) {
                return Mono.fromCallable(() -> ResponseEntity.ok(locationQueryService.getDepartmentsByCountry(countryId)))
                                .subscribeOn(Schedulers.boundedElastic());
        }

        @GetMapping("/departments/{departmentId}/cities")
        public Mono<ResponseEntity<List<CityDTO>>> getCities(@PathVariable final UUID departmentId) {
                return Mono.fromCallable(() -> ResponseEntity.ok(locationQueryService.getCitiesByDepartment(departmentId)))
                                .subscribeOn(Schedulers.boundedElastic());
        }
}
