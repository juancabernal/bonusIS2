package co.edu.uco.ucochallenge.user.registeruser.application.interactor.usecase.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.UUID;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import co.edu.uco.ucochallenge.crosscuting.exception.DomainValidationException;
import co.edu.uco.ucochallenge.crosscuting.helper.UUIDHelper;
import co.edu.uco.ucochallenge.user.registeruser.application.port.ContactConfirmationPort;
import co.edu.uco.ucochallenge.user.registeruser.application.port.IdTypeQueryPort;
import co.edu.uco.ucochallenge.user.registeruser.application.port.LocationQueryPort;
import co.edu.uco.ucochallenge.user.registeruser.application.port.NotificationPort;
import co.edu.uco.ucochallenge.user.registeruser.application.port.RegisterUserRepositoryPort;
import co.edu.uco.ucochallenge.user.registeruser.application.usecase.domain.RegisterUserDomain;

@ExtendWith(MockitoExtension.class)
class RegisterUserUseCaseImplTest {

        @Mock
        private RegisterUserRepositoryPort repositoryPort;
        @Mock
        private NotificationPort notificationPort;
        @Mock
        private ContactConfirmationPort contactConfirmationPort;
        @Mock
        private IdTypeQueryPort idTypeQueryPort;
        @Mock
        private LocationQueryPort locationQueryPort;
        @Mock
        private Supplier<UUID> idGenerator;

        @InjectMocks
        private RegisterUserUseCaseImpl useCase;

        @Test
        void shouldThrowDomainValidationExceptionWhenDomainValidationFails() {
                var domain = RegisterUserDomain.builder()
                                .id(UUID.randomUUID())
                                .idType(UUIDHelper.getDefault())
                                .idNumber("")
                                .firstName("")
                                .firstSurname("")
                                .homeCity(UUID.randomUUID())
                                .email("")
                                .mobileNumber("")
                                .build();

                assertThrows(DomainValidationException.class, () -> useCase.execute(domain));

                verify(repositoryPort, never()).save(domain);
                verify(contactConfirmationPort, never()).confirmEmail(ArgumentMatchers.anyString());
        }
}
