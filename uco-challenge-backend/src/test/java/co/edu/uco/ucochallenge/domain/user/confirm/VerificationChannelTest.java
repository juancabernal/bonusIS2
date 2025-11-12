package co.edu.uco.ucochallenge.domain.user.confirm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import co.edu.uco.ucochallenge.crosscutting.exception.DomainValidationException;

class VerificationChannelTest {

    @Test
    void normalizeContactShouldLowercaseEmailsAndTrim() {
        String normalized = VerificationChannel.EMAIL.normalizeContact("  UsEr@Example.COM  ");

        assertEquals("user@example.com", normalized);
    }

    @Test
    void normalizeContactShouldTrimMobileNumbersWithoutChangingDigits() {
        String normalized = VerificationChannel.MOBILE.normalizeContact(" 3001234567 ");

        assertEquals("3001234567", normalized);
    }

    @Test
    void fromShouldBeCaseInsensitive() {
        VerificationChannel channel = VerificationChannel.from(" Email ");

        assertEquals(VerificationChannel.EMAIL, channel);
        assertTrue(channel.isEmail());
    }

    @Test
    void fromShouldThrowExceptionForUnknownValues() {
        assertThrows(DomainValidationException.class, () -> VerificationChannel.from("whatsapp"));
    }
}

