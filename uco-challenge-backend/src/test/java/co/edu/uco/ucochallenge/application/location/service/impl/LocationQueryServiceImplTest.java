package co.edu.uco.ucochallenge.application.location.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import co.edu.uco.ucochallenge.application.location.dto.CityDTO;
import co.edu.uco.ucochallenge.application.location.dto.CountryDTO;
import co.edu.uco.ucochallenge.application.location.dto.DepartmentDTO;
import co.edu.uco.ucochallenge.infrastructure.secondary.persistence.jpa.entity.CityEntity;
import co.edu.uco.ucochallenge.infrastructure.secondary.persistence.jpa.entity.CountryEntity;
import co.edu.uco.ucochallenge.infrastructure.secondary.persistence.jpa.entity.StateEntity;
import co.edu.uco.ucochallenge.infrastructure.secondary.persistence.jpa.repository.SpringDataCityRepository;
import co.edu.uco.ucochallenge.infrastructure.secondary.persistence.jpa.repository.SpringDataCountryRepository;
import co.edu.uco.ucochallenge.infrastructure.secondary.persistence.jpa.repository.SpringDataStateRepository;

@ExtendWith(MockitoExtension.class)
class LocationQueryServiceImplTest {

    @Mock
    private SpringDataCountryRepository countryRepository;

    @Mock
    private SpringDataStateRepository stateRepository;

    @Mock
    private SpringDataCityRepository cityRepository;

    @InjectMocks
    private LocationQueryServiceImpl service;

    @Test
    void getCountriesShouldReturnMappedDtos() {
        UUID countryId = UUID.randomUUID();
        CountryEntity colombia = new CountryEntity.Builder()
                .id(countryId)
                .name(" Colombia ")
                .build();
        when(countryRepository.findAll()).thenReturn(List.of(colombia));

        List<CountryDTO> result = service.getCountries();

        assertEquals(1, result.size());
        CountryDTO dto = result.get(0);
        assertEquals(countryId, dto.getId());
        assertEquals("Colombia", dto.getName());
        verify(countryRepository).findAll();
    }

    @Test
    void getDepartmentsByCountryShouldUseRepositoryResult() {
        UUID countryId = UUID.randomUUID();
        UUID departmentId = UUID.randomUUID();
        StateEntity antioquia = new StateEntity.Builder()
                .id(departmentId)
                .name(" Antioquia ")
                .build();
        when(stateRepository.findByCountryId(countryId)).thenReturn(List.of(antioquia));

        List<DepartmentDTO> result = service.getDepartmentsByCountry(countryId);

        assertEquals(1, result.size());
        DepartmentDTO dto = result.get(0);
        assertEquals(departmentId, dto.getId());
        assertEquals("Antioquia", dto.getName());
        verify(stateRepository).findByCountryId(countryId);
    }

    @Test
    void getCitiesByDepartmentShouldReturnEmptyWhenRepositoryReturnsEmptyList() {
        UUID departmentId = UUID.randomUUID();
        when(cityRepository.findByStateId(departmentId)).thenReturn(List.of());

        List<CityDTO> result = service.getCitiesByDepartment(departmentId);

        assertTrue(result.isEmpty());
        verify(cityRepository).findByStateId(departmentId);
    }

    @Test
    void getCitiesByDepartmentShouldMapEntitiesToDtos() {
        UUID departmentId = UUID.randomUUID();
        UUID cityId = UUID.randomUUID();
        CityEntity medellin = new CityEntity.Builder()
                .id(cityId)
                .name(" Medellín ")
                .build();
        when(cityRepository.findByStateId(departmentId)).thenReturn(List.of(medellin));

        List<CityDTO> result = service.getCitiesByDepartment(departmentId);

        assertEquals(1, result.size());
        CityDTO dto = result.get(0);
        assertEquals(cityId, dto.getId());
        assertEquals("Medellín", dto.getName());
    }
}

