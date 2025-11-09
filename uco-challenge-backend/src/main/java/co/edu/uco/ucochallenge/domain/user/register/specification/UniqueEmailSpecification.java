package co.edu.uco.ucochallenge.domain.user.register.specification;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

import co.edu.uco.ucochallenge.domain.common.notification.Notification;
import co.edu.uco.ucochallenge.domain.common.specification.Specification;
import co.edu.uco.ucochallenge.domain.user.register.model.ExistingUserSnapshotDomain;
import co.edu.uco.ucochallenge.domain.user.register.model.RegisterUserDomain;

public class UniqueEmailSpecification implements Specification<RegisterUserDomain> {

        private static final String ERROR_CODE = "REGISTER_USER_EMAIL_DUPLICATED";
        private static final String ERROR_MESSAGE = "A user with the provided email already exists.";

        private final Notification notification;
        private final Function<String, Optional<ExistingUserSnapshotDomain>> finder;
        private final BiConsumer<String, String> emailOwnerNotifier;
        private final BiConsumer<String, String> executorNotifier;
        private final String executorIdentifier;

        public UniqueEmailSpecification(final Notification notification,
                        final Function<String, Optional<ExistingUserSnapshotDomain>> finder,
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
        public boolean isSatisfiedBy(final RegisterUserDomain candidate) {
                if (!candidate.hasEmail()) {
                        return true;
                }

                finder.apply(candidate.getEmail())
                                .ifPresent(existing -> {
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
