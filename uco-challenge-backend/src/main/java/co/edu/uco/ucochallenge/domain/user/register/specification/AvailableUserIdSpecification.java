package co.edu.uco.ucochallenge.domain.user.register.specification;

import java.util.UUID;
import java.util.function.Predicate;
import java.util.function.Supplier;

import co.edu.uco.ucochallenge.crosscutting.helper.UUIDHelper;
import co.edu.uco.ucochallenge.domain.common.specification.Specification;
import co.edu.uco.ucochallenge.domain.user.register.model.RegisterUserDomain;

public class AvailableUserIdSpecification implements Specification<RegisterUserDomain> {

        private final Predicate<UUID> idExists;
        private final Supplier<UUID> idGenerator;

        public AvailableUserIdSpecification(final Predicate<UUID> idExists, final Supplier<UUID> idGenerator) {
                this.idExists = idExists;
                this.idGenerator = idGenerator;
        }

        @Override
        public boolean isSatisfiedBy(final RegisterUserDomain candidate) {
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
