package co.edu.uco.ucochallenge.domain.user.registration.specification;

import java.util.UUID;
import java.util.function.Predicate;
import java.util.function.Supplier;

import co.edu.uco.ucochallenge.crosscutting.legacy.helper.UUIDHelper;
import co.edu.uco.ucochallenge.domain.specification.Specification;
import co.edu.uco.ucochallenge.domain.user.registration.model.UserRegistrationDomainModel;

public class UserRegistrationAvailableIdSpecification implements Specification<UserRegistrationDomainModel> {

        private final Predicate<UUID> idExists;
        private final Supplier<UUID> idGenerator;

        public UserRegistrationAvailableIdSpecification(final Predicate<UUID> idExists,
                        final Supplier<UUID> idGenerator) {
                this.idExists = idExists;
                this.idGenerator = idGenerator;
        }

        @Override
        public boolean isSatisfiedBy(final UserRegistrationDomainModel candidate) {
                UUID currentId = candidate.getId();
                if (UUIDHelper.getDefault().equals(currentId) || idExists.test(currentId)) {
                        currentId = idGenerator.get();
                        candidate.updateId(currentId);
                }

                while (idExists.test(currentId)) {
                        currentId = idGenerator.get();
                        candidate.updateId(currentId);
                }

                return true;
        }
}
