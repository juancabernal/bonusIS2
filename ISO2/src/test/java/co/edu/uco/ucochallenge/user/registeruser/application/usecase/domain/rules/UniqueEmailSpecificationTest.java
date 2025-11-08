package co.edu.uco.ucochallenge.user.registeruser.application.usecase.domain.rules;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;

import co.edu.uco.ucochallenge.crosscuting.notification.Notification;
import co.edu.uco.ucochallenge.user.registeruser.application.usecase.domain.ExistingUserSnapshotDomain;
import co.edu.uco.ucochallenge.user.registeruser.application.usecase.domain.RegisterUserDomain;

class UniqueEmailSpecificationTest {

        @Test
        void shouldAddNotificationErrorWhenEmailAlreadyExists() {
                Notification notification = Notification.create();
                AtomicBoolean ownerNotified = new AtomicBoolean(false);
                AtomicBoolean executorNotified = new AtomicBoolean(false);

                var existingUser = ExistingUserSnapshotDomain.builder()
                                .id(UUID.randomUUID())
                                .firstName("Carlos")
                                .firstSurname("Ruiz")
                                .email("existing@example.com")
                                .mobileNumber("1234567890")
                                .build();

                var specification = new UniqueEmailSpecification(notification,
                                email -> Optional.of(existingUser),
                                (email, message) -> ownerNotified.set(true),
                                (executor, message) -> executorNotified.set(true),
                                "executor-id");

                var domain = RegisterUserDomain.builder()
                                .id(UUID.randomUUID())
                                .idType(UUID.randomUUID())
                                .idNumber("100200300")
                                .firstName("Juan")
                                .secondName("Camilo")
                                .firstSurname("Perez")
                                .secondSurname("Lopez")
                                .homeCity(UUID.randomUUID())
                                .email("existing@example.com")
                                .mobileNumber("9876543210")
                                .build();

                specification.isSatisfiedBy(domain);

                assertTrue(notification.hasErrors());
                assertTrue(ownerNotified.get());
                assertTrue(executorNotified.get());
        }
}
