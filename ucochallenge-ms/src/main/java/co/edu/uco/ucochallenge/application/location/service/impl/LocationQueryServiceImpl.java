package co.edu.uco.ucochallenge.application.location.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import co.edu.uco.ucochallenge.crosscutting.legacy.helper.TextHelper;
import co.edu.uco.ucochallenge.application.location.service.dto.CityDTO;
import co.edu.uco.ucochallenge.application.location.service.dto.CountryDTO;
import co.edu.uco.ucochallenge.application.location.service.dto.DepartmentDTO;
import co.edu.uco.ucochallenge.application.location.service.LocationQueryService;
import co.edu.uco.ucochallenge.infrastructure.secondary.adapters.repository.entity.CityEntity;
import co.edu.uco.ucochallenge.infrastructure.secondary.adapters.repository.entity.CountryEntity;
import co.edu.uco.ucochallenge.infrastructure.secondary.adapters.repository.entity.StateEntity;
import co.edu.uco.ucochallenge.infrastructure.secondary.adapters.repository.jpa.SpringDataCityRepository;
import co.edu.uco.ucochallenge.infrastructure.secondary.adapters.repository.jpa.SpringDataCountryRepository;
import co.edu.uco.ucochallenge.infrastructure.secondary.adapters.repository.jpa.SpringDataStateRepository;
import jakarta.transaction.Transactional;

@Service
@Transactional(Transactional.TxType.SUPPORTS)
public class LocationQueryServiceImpl implements LocationQueryService {

        private final SpringDataCountryRepository countryRepository;
        private final SpringDataStateRepository stateRepository;
        private final SpringDataCityRepository cityRepository;

        public LocationQueryServiceImpl(final SpringDataCountryRepository countryRepository,
                        final SpringDataStateRepository stateRepository,
                        final SpringDataCityRepository cityRepository) {
                this.countryRepository = countryRepository;
                this.stateRepository = stateRepository;
                this.cityRepository = cityRepository;
        }

        @Override
        public List<CountryDTO> getCountries() {
                return countryRepository.findAll()
                                .stream()
                                .map(this::mapCountry)
                                .toList();
        }

        @Override
        public List<DepartmentDTO> getDepartmentsByCountry(final UUID countryId) {
                return stateRepository.findByCountryId(countryId)
                                .stream()
                                .map(this::mapDepartment)
                                .toList();
        }

        @Override
        public List<CityDTO> getCitiesByDepartment(final UUID departmentId) {
                return cityRepository.findByStateId(departmentId)
                                .stream()
                                .map(this::mapCity)
                                .toList();
        }

        private CountryDTO mapCountry(final CountryEntity entity) {
                final CountryDTO dto = new CountryDTO();
                dto.setId(entity.getId());
                dto.setName(TextHelper.getDefaultWithTrim(entity.getName()));
                return dto;
        }

        private DepartmentDTO mapDepartment(final StateEntity entity) {
                final DepartmentDTO dto = new DepartmentDTO();
                dto.setId(entity.getId());
                dto.setName(TextHelper.getDefaultWithTrim(entity.getName()));
                return dto;
        }

        private CityDTO mapCity(final CityEntity entity) {
                final CityDTO dto = new CityDTO();
                dto.setId(entity.getId());
                dto.setName(TextHelper.getDefaultWithTrim(entity.getName()));
                return dto;
        }
}
