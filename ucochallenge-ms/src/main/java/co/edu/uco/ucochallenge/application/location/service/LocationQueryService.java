package co.edu.uco.ucochallenge.application.location.service;

import java.util.List;
import java.util.UUID;

import co.edu.uco.ucochallenge.application.location.service.dto.CityDTO;
import co.edu.uco.ucochallenge.application.location.service.dto.CountryDTO;
import co.edu.uco.ucochallenge.application.location.service.dto.DepartmentDTO;

public interface LocationQueryService {

        List<CountryDTO> getCountries();

        List<DepartmentDTO> getDepartmentsByCountry(UUID countryId);

        List<CityDTO> getCitiesByDepartment(UUID departmentId);
}
