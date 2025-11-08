package co.edu.uco.ucochallenge.user.registeruser.application.usecase.domain.validation;

import java.util.UUID;
import java.util.function.Supplier;

import co.edu.uco.ucochallenge.crosscuting.notification.Notification;
import co.edu.uco.ucochallenge.domain.specification.Specification;
import co.edu.uco.ucochallenge.user.registeruser.application.port.RegisterUserRepositoryPort;
import co.edu.uco.ucochallenge.user.registeruser.application.port.NotificationPort;
import co.edu.uco.ucochallenge.user.registeruser.application.usecase.domain.RegisterUserDomain;
import co.edu.uco.ucochallenge.user.registeruser.application.usecase.domain.rules.AvailableUserIdSpecification;
import co.edu.uco.ucochallenge.user.registeruser.application.usecase.domain.rules.UniqueEmailSpecification;
import co.edu.uco.ucochallenge.user.registeruser.application.usecase.domain.rules.UniqueIdentificationSpecification;
import co.edu.uco.ucochallenge.user.registeruser.application.usecase.domain.rules.UniqueMobileNumberSpecification;

public class RegisterUserDomainValidator {

        private final RegisterUserRepositoryPort repositoryPort;
        private final NotificationPort notificationPort;
        private final Supplier<UUID> idGenerator;

        public RegisterUserDomainValidator(final RegisterUserRepositoryPort repositoryPort,
                        final NotificationPort notificationPort,
                        final Supplier<UUID> idGenerator) {
                this.repositoryPort = repositoryPort;
                this.notificationPort = notificationPort;
                this.idGenerator = idGenerator;
        }

        public Notification validate(final RegisterUserDomain domain, final String executorIdentifier) {
                final var notification = Notification.create();
                notification.merge(domain.validate());

                final Specification<RegisterUserDomain> availableIdSpec = new AvailableUserIdSpecification(
                                repositoryPort::existsById, idGenerator);

                final Specification<RegisterUserDomain> uniqueIdentificationSpec = new UniqueIdentificationSpecification(
                                notification,
                                repositoryPort::findByIdentification,
                                notificationPort::notifyAdministrator,
                                notificationPort::notifyExecutor,
                                executorIdentifier);

                final Specification<RegisterUserDomain> uniqueEmailSpec = new UniqueEmailSpecification(
                                notification,
                                repositoryPort::findByEmail,
                                notificationPort::notifyEmailOwner,
                                notificationPort::notifyExecutor,
                                executorIdentifier);

                final Specification<RegisterUserDomain> uniqueMobileSpec = new UniqueMobileNumberSpecification(
                                notification,
                                repositoryPort::findByMobileNumber,
                                notificationPort::notifyMobileOwner,
                                notificationPort::notifyExecutor,
                                executorIdentifier);

                availableIdSpec.and(uniqueIdentificationSpec)
                                .and(uniqueEmailSpec)
                                .and(uniqueMobileSpec)
                                .isSatisfiedBy(domain);

                return notification;
        }
}
