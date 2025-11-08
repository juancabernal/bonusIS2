package co.edu.uco.ucochallenge.location.application.service;

import java.util.List;
import java.util.UUID;

import co.edu.uco.ucochallenge.location.application.dto.CityDTO;
import co.edu.uco.ucochallenge.location.application.dto.CountryDTO;
import co.edu.uco.ucochallenge.location.application.dto.DepartmentDTO;

public interface LocationQueryService {

        List<CountryDTO> getCountries();

        List<DepartmentDTO> getDepartmentsByCountry(UUID countryId);

        List<CityDTO> getCitiesByDepartment(UUID departmentId);
}
