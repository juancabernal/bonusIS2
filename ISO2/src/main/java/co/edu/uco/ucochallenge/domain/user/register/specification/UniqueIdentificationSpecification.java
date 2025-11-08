package co.edu.uco.ucochallenge.domain.user.register.specification;

import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import co.edu.uco.ucochallenge.crosscutting.helper.TextHelper;
import co.edu.uco.ucochallenge.crosscutting.helper.UUIDHelper;
import co.edu.uco.ucochallenge.domain.common.notification.Notification;
import co.edu.uco.ucochallenge.domain.common.specification.Specification;
import co.edu.uco.ucochallenge.domain.user.register.model.ExistingUserSnapshotDomain;
import co.edu.uco.ucochallenge.domain.user.register.model.RegisterUserDomain;

public class UniqueIdentificationSpecification implements Specification<RegisterUserDomain> {

        private static final String ERROR_CODE = "REGISTER_USER_IDENTIFICATION_DUPLICATED";
        private static final String ERROR_MESSAGE = "A user with the provided identification already exists.";

        private final Notification notification;
        private final BiFunction<UUID, String, Optional<ExistingUserSnapshotDomain>> finder;
        private final Consumer<String> administratorNotifier;
        private final BiConsumer<String, String> executorNotifier;
        private final String executorIdentifier;

        public UniqueIdentificationSpecification(final Notification notification,
                        final BiFunction<UUID, String, Optional<ExistingUserSnapshotDomain>> finder,
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
        public boolean isSatisfiedBy(final RegisterUserDomain candidate) {
                if (UUIDHelper.getDefault().equals(candidate.getIdType())
                                || TextHelper.isEmpty(candidate.getIdNumber())) {
                        return true;
                }

                finder.apply(candidate.getIdType(), candidate.getIdNumber())
                                .ifPresent(existing -> {
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
