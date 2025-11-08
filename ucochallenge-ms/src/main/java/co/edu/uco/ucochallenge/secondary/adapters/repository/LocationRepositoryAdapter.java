package co.edu.uco.ucochallenge.secondary.adapters.repository;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import co.edu.uco.ucochallenge.secondary.ports.repository.CityRepository;
import co.edu.uco.ucochallenge.secondary.ports.repository.CountryRepository;
import co.edu.uco.ucochallenge.secondary.ports.repository.StateRepository;
import co.edu.uco.ucochallenge.user.registeruser.application.port.LocationQueryPort;

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
