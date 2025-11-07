package co.edu.uco.ucochallenge.domain.user.registration.specification;

import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import co.edu.uco.ucochallenge.crosscutting.legacy.helper.TextHelper;
import co.edu.uco.ucochallenge.crosscutting.legacy.helper.UUIDHelper;
import co.edu.uco.ucochallenge.crosscutting.legacy.notification.Notification;
import co.edu.uco.ucochallenge.domain.specification.Specification;
import co.edu.uco.ucochallenge.domain.user.registration.model.UserRegistrationDomainModel;
import co.edu.uco.ucochallenge.domain.user.registration.model.UserRegistrationExistingUserSnapshotDomainModel;

public class UserRegistrationUniqueIdentificationSpecification implements Specification<UserRegistrationDomainModel> {

        private static final String ERROR_CODE = "REGISTER_USER_IDENTIFICATION_DUPLICATED";
        private static final String ERROR_MESSAGE = "A user with the provided identification already exists.";

        private final Notification notification;
        private final BiFunction<UUID, String, Optional<UserRegistrationExistingUserSnapshotDomainModel>> finder;
        private final Consumer<String> administratorNotifier;
        private final BiConsumer<String, String> executorNotifier;
        private final String executorIdentifier;

        public UserRegistrationUniqueIdentificationSpecification(final Notification notification,
                        final BiFunction<UUID, String, Optional<UserRegistrationExistingUserSnapshotDomainModel>> finder,
                        final Consumer<String> administratorNotifier,
                        final BiConsumer<String, String> executorNotifier,
                        final String executorIdentifier) {
                this.notification = notification;
                this.finder = finder;
                this.administratorNotifier = administratorNotifier;
                this.executorNotifier = executorNotifier;
                this.executorIdentifier = executorIdentifier;
        }

        @Override
        public boolean isSatisfiedBy(final UserRegistrationDomainModel candidate) {
                if (UUIDHelper.getDefault().equals(candidate.getIdType()) || TextHelper.isEmpty(candidate.getIdNumber())) {
                        return true;
                }

                finder.apply(candidate.getIdType(), candidate.getIdNumber()).ifPresent(existing -> {
                        final var message = String.format(
                                        "A user with identification type %s and number %s already exists with id %s.",
                                        candidate.getIdType(), candidate.getIdNumber(), existing.getId());

                        administratorNotifier.accept(message);
                        executorNotifier.accept(executorIdentifier, message);
                        notification.addError(ERROR_CODE, ERROR_MESSAGE);
                });

                return true;
        }
}
