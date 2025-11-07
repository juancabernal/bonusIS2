package co.edu.uco.ucochallenge.application.user.registration.usecase.domain.validation;

import java.util.UUID;
import java.util.function.Supplier;

import co.edu.uco.ucochallenge.crosscutting.legacy.notification.Notification;
import co.edu.uco.ucochallenge.domain.specification.Specification;
import co.edu.uco.ucochallenge.domain.user.registration.model.UserRegistrationDomainModel;
import co.edu.uco.ucochallenge.domain.user.registration.specification.UserRegistrationAvailableIdSpecification;
import co.edu.uco.ucochallenge.domain.user.registration.specification.UserRegistrationUniqueEmailSpecification;
import co.edu.uco.ucochallenge.domain.user.registration.specification.UserRegistrationUniqueIdentificationSpecification;
import co.edu.uco.ucochallenge.domain.user.registration.specification.UserRegistrationUniqueMobileNumberSpecification;
import co.edu.uco.ucochallenge.application.user.registration.port.NotificationPort;
import co.edu.uco.ucochallenge.application.user.registration.port.RegisterUserRepositoryPort;

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

        public Notification validate(final UserRegistrationDomainModel domain, final String executorIdentifier) {
                final var notification = Notification.create();
                notification.merge(domain.validate());

                final Specification<UserRegistrationDomainModel> availableIdSpec = new UserRegistrationAvailableIdSpecification(
                                repositoryPort::existsById, idGenerator);

                final Specification<UserRegistrationDomainModel> uniqueIdentificationSpec = new UserRegistrationUniqueIdentificationSpecification(
                                notification,
                                repositoryPort::findByIdentification,
                                notificationPort::notifyAdministrator,
                                notificationPort::notifyExecutor,
                                executorIdentifier);

                final Specification<UserRegistrationDomainModel> uniqueEmailSpec = new UserRegistrationUniqueEmailSpecification(
                                notification,
                                repositoryPort::findByEmail,
                                notificationPort::notifyEmailOwner,
                                notificationPort::notifyExecutor,
                                executorIdentifier);

                final Specification<UserRegistrationDomainModel> uniqueMobileSpec = new UserRegistrationUniqueMobileNumberSpecification(
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
