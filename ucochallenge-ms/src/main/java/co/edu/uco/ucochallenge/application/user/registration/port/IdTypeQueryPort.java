package co.edu.uco.ucochallenge.application.user.registration.port;

import java.util.Optional;
import java.util.UUID;

public interface IdTypeQueryPort {

        boolean existsById(UUID id);

        Optional<UUID> findIdByName(String name);
}
