package co.edu.uco.ucochallenge.user.registeruser.application.port;

import java.util.Optional;
import java.util.UUID;

public interface IdTypeQueryPort {

        boolean existsById(UUID id);

        Optional<UUID> findIdByName(String name);
}
