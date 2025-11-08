package co.edu.uco.ucochallenge.infrastructure.secondary.persistence.jpa.adapter;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import co.edu.uco.ucochallenge.infrastructure.secondary.persistence.jpa.repository.CityRepository;
import co.edu.uco.ucochallenge.infrastructure.secondary.persistence.jpa.repository.CountryRepository;
import co.edu.uco.ucochallenge.infrastructure.secondary.persistence.jpa.repository.StateRepository;
import co.edu.uco.ucochallenge.domain.user.port.LocationQueryPort;

@Repository
public class LocationRepositoryAdapter implements LocationQueryPort {

        private final CountryRepository countryRepository;
        private final StateRepository stateRepository;
        private final CityRepository cityRepository;

        public LocationRepositoryAdapter(final CountryRepository countryRepository,
                        final StateRepository stateRepository,
                        final CityRepository cityRepository) {
                this.countryRepository = countryRepository;
                this.stateRepository = stateRepository;
                this.cityRepository = cityRepository;
        }

        @Override
        public boolean countryExists(final UUID countryId) {
                return countryRepository.existsById(countryId);
        }

        @Override
        public boolean departmentExists(final UUID departmentId) {
                return stateRepository.existsById(departmentId);
        }

        @Override
        public boolean cityExists(final UUID cityId) {
                return cityRepository.existsById(cityId);
        }
}
