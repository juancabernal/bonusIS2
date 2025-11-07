package co.edu.uco.ucochallenge.infrastructure.secondary.adapters.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import co.edu.uco.ucochallenge.crosscutting.legacy.helper.TextHelper;
import co.edu.uco.ucochallenge.infrastructure.secondary.adapters.repository.entity.IdTypeEntity;
import co.edu.uco.ucochallenge.infrastructure.secondary.ports.repository.IdTypeRepository;
import co.edu.uco.ucochallenge.application.user.registration.port.IdTypeQueryPort;

@Repository
public class IdTypeRepositoryAdapter implements IdTypeQueryPort {

        private final IdTypeRepository repository;

        public IdTypeRepositoryAdapter(final IdTypeRepository repository) {
                this.repository = repository;
        }

        @Override
        public boolean existsById(final UUID id) {
                return repository.existsById(id);
        }

        @Override
        public Optional<UUID> findIdByName(final String name) {
                if (TextHelper.isEmpty(name)) {
                        return Optional.empty();
                }

                return repository.findByName(name)
                                .map(IdTypeEntity::getId);
        }
}
