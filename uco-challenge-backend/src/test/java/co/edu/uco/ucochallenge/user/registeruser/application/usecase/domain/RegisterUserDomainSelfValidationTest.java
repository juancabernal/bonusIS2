package co.edu.uco.ucochallenge.user.registeruser.application.usecase.domain;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import co.edu.uco.ucochallenge.crosscutting.helper.UUIDHelper;
import co.edu.uco.ucochallenge.domain.common.notification.Notification;
import co.edu.uco.ucochallenge.domain.user.register.model.RegisterUserDomain;

class RegisterUserDomainSelfValidationTest {

        @Test
        void shouldReportErrorsWhenMandatoryFieldsAreMissing() {
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

                Notification notification = domain.validate();

                assertTrue(notification.hasErrors());
        }
}
