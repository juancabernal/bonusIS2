package co.edu.uco.ucochallenge.domain.user.registration.model;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import co.edu.uco.ucochallenge.crosscutting.legacy.helper.UUIDHelper;

class UserRegistrationDomainModelSelfValidationTest {

        @Test
        void shouldReportErrorsWhenMandatoryFieldsAreMissing() {
                var domain = UserRegistrationDomainModel.builder()
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
