package co.edu.uco.ucochallenge.domain.user.registration.specification;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

import co.edu.uco.ucochallenge.crosscutting.legacy.notification.Notification;
import co.edu.uco.ucochallenge.domain.specification.Specification;
import co.edu.uco.ucochallenge.domain.user.registration.model.UserRegistrationDomainModel;
import co.edu.uco.ucochallenge.domain.user.registration.model.UserRegistrationExistingUserSnapshotDomainModel;

public class UserRegistrationUniqueMobileNumberSpecification implements Specification<UserRegistrationDomainModel> {

        private static final String ERROR_CODE = "REGISTER_USER_MOBILE_DUPLICATED";
        private static final String ERROR_MESSAGE = "A user with the provided mobile number already exists.";

        private final Notification notification;
        private final Function<String, Optional<UserRegistrationExistingUserSnapshotDomainModel>> finder;
        private final BiConsumer<String, String> mobileOwnerNotifier;
        private final BiConsumer<String, String> executorNotifier;
        private final String executorIdentifier;

        public UserRegistrationUniqueMobileNumberSpecification(final Notification notification,
                        final Function<String, Optional<UserRegistrationExistingUserSnapshotDomainModel>> finder,
                        final BiConsumer<String, String> mobileOwnerNotifier,
                        final BiConsumer<String, String> executorNotifier,
                        final String executorIdentifier) {
                this.notification = notification;
                this.finder = finder;
                this.mobileOwnerNotifier = mobileOwnerNotifier;
                this.executorNotifier = executorNotifier;
                this.executorIdentifier = executorIdentifier;
        }

        @Override
        public boolean isSatisfiedBy(final UserRegistrationDomainModel candidate) {
                if (!candidate.hasMobileNumber()) {
                        return true;
                }

                finder.apply(candidate.getMobileNumber()).ifPresent(existing -> {
                        final var ownerMessage = String.format(
                                        "Hola %s %s, se intentó registrar una nueva cuenta utilizando tu número %s.",
                                        existing.getFirstName(), existing.getFirstSurname(), candidate.getMobileNumber());
                        final var executorMessage = String.format(
                                        "El número móvil %s ya pertenece al usuario %s %s con id %s.",
                                        candidate.getMobileNumber(), existing.getFirstName(), existing.getFirstSurname(),
                                        existing.getId());

                        mobileOwnerNotifier.accept(candidate.getMobileNumber(), ownerMessage);
                        executorNotifier.accept(executorIdentifier, executorMessage);
                        notification.addError(ERROR_CODE, ERROR_MESSAGE);
                });

                return true;
        }
}
