package co.edu.uco.ucochallenge.domain.user.registration.specification;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

import co.edu.uco.ucochallenge.crosscutting.legacy.notification.Notification;
import co.edu.uco.ucochallenge.domain.specification.Specification;
import co.edu.uco.ucochallenge.domain.user.registration.model.UserRegistrationDomainModel;
import co.edu.uco.ucochallenge.domain.user.registration.model.UserRegistrationExistingUserSnapshotDomainModel;

public class UserRegistrationUniqueEmailSpecification implements Specification<UserRegistrationDomainModel> {

        private static final String ERROR_CODE = "REGISTER_USER_EMAIL_DUPLICATED";
        private static final String ERROR_MESSAGE = "A user with the provided email already exists.";

        private final Notification notification;
        private final Function<String, Optional<UserRegistrationExistingUserSnapshotDomainModel>> finder;
        private final BiConsumer<String, String> emailOwnerNotifier;
        private final BiConsumer<String, String> executorNotifier;
        private final String executorIdentifier;

        public UserRegistrationUniqueEmailSpecification(final Notification notification,
                        final Function<String, Optional<UserRegistrationExistingUserSnapshotDomainModel>> finder,
                        final BiConsumer<String, String> emailOwnerNotifier,
                        final BiConsumer<String, String> executorNotifier,
                        final String executorIdentifier) {
                this.notification = notification;
                this.finder = finder;
                this.emailOwnerNotifier = emailOwnerNotifier;
                this.executorNotifier = executorNotifier;
                this.executorIdentifier = executorIdentifier;
        }

        @Override
        public boolean isSatisfiedBy(final UserRegistrationDomainModel candidate) {
                if (!candidate.hasEmail()) {
                        return true;
                }

                finder.apply(candidate.getEmail()).ifPresent(existing -> {
                        final var ownerMessage = String.format(
                                        "Hola %s %s, se intentó registrar una nueva cuenta utilizando tu correo %s.",
                                        existing.getFirstName(), existing.getFirstSurname(), candidate.getEmail());
                        final var executorMessage = String.format(
                                        "El correo %s ya pertenece al usuario %s %s con id %s.",
                                        candidate.getEmail(), existing.getFirstName(), existing.getFirstSurname(),
                                        existing.getId());

                        emailOwnerNotifier.accept(candidate.getEmail(), ownerMessage);
                        executorNotifier.accept(executorIdentifier, executorMessage);
                        notification.addError(ERROR_CODE, ERROR_MESSAGE);
                });

                return true;
        }
}
