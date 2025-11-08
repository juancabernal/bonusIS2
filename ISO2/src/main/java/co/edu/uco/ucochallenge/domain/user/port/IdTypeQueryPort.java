package co.edu.uco.ucochallenge.domain.user.port;

import java.util.Optional;
import java.util.UUID;

public interface IdTypeQueryPort {

        boolean existsById(UUID id);

        Optional<UUID> findIdByName(String name);
}
