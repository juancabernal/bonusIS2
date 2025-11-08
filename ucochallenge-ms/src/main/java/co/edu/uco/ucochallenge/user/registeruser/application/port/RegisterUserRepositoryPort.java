package co.edu.uco.ucochallenge.user.registeruser.application.port;

import java.util.Optional;
import java.util.UUID;

import co.edu.uco.ucochallenge.user.registeruser.application.usecase.domain.ExistingUserSnapshotDomain;
import co.edu.uco.ucochallenge.user.registeruser.application.usecase.domain.RegisterUserDomain;

public interface RegisterUserRepositoryPort {

        boolean existsById(UUID id);

        Optional<ExistingUserSnapshotDomain> findByIdentification(UUID idType, String idNumber);

        Optional<ExistingUserSnapshotDomain> findByEmail(String email);

        Optional<ExistingUserSnapshotDomain> findByMobileNumber(String mobileNumber);

        void save(RegisterUserDomain domain);
}
