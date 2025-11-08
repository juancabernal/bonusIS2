package co.edu.uco.ucochallenge.primary.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.uco.ucochallenge.location.application.dto.CityDTO;
import co.edu.uco.ucochallenge.location.application.dto.CountryDTO;
import co.edu.uco.ucochallenge.location.application.dto.DepartmentDTO;
import co.edu.uco.ucochallenge.location.application.service.LocationQueryService;

@RestController
@RequestMapping("/uco-challenge/api/v1/locations")
public class LocationController {

        private final LocationQueryService locationQueryService;

        public LocationController(final LocationQueryService locationQueryService) {
                this.locationQueryService = locationQueryService;
        }

        @GetMapping("/countries")
        public ResponseEntity<List<CountryDTO>> getCountries() {
                final var countries = locationQueryService.getCountries();
                return ResponseEntity.ok(countries);
        }

        @GetMapping("/countries/{countryId}/departments")
        public ResponseEntity<List<DepartmentDTO>> getDepartments(@PathVariable final UUID countryId) {
                final var departments = locationQueryService.getDepartmentsByCountry(countryId);
                return ResponseEntity.ok(departments);
        }

        @GetMapping("/departments/{departmentId}/cities")
        public ResponseEntity<List<CityDTO>> getCities(@PathVariable final UUID departmentId) {
                final var cities = locationQueryService.getCitiesByDepartment(departmentId);
                return ResponseEntity.ok(cities);
        }
}
