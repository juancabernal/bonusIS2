package co.edu.uco.ucochallenge.application.user.registration.port;

import java.util.Optional;
import java.util.UUID;

import co.edu.uco.ucochallenge.domain.user.registration.model.UserRegistrationDomainModel;
import co.edu.uco.ucochallenge.domain.user.registration.model.UserRegistrationExistingUserSnapshotDomainModel;

public interface RegisterUserRepositoryPort {

        boolean existsById(UUID id);

        Optional<UserRegistrationExistingUserSnapshotDomainModel> findByIdentification(UUID idType, String idNumber);

        Optional<UserRegistrationExistingUserSnapshotDomainModel> findByEmail(String email);

        Optional<UserRegistrationExistingUserSnapshotDomainModel> findByMobileNumber(String mobileNumber);

        void save(UserRegistrationDomainModel domain);
}
