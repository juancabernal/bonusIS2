package co.edu.uco.ucochallenge.user.confirmcontact.application.service;

import java.util.Arrays;

import co.edu.uco.ucochallenge.crosscuting.exception.DomainValidationException;

public enum VerificationChannel {

        EMAIL("email"),
        MOBILE("mobile");

        private final String value;

        VerificationChannel(final String value) {
                this.value = value;
        }

        public String getValue() {
                return value;
        }

        public boolean isEmail() {
                return this == EMAIL;
        }

        public boolean isMobile() {
                return this == MOBILE;
        }

        public String normalizeContact(final String contact) {
                if (contact == null) {
                        return null;
                }
                final String trimmed = contact.trim();
                return isEmail() ? trimmed.toLowerCase() : trimmed;
        }

        public static VerificationChannel from(final String rawChannel) {
                final String normalized = rawChannel == null ? null : rawChannel.trim();
                return Arrays.stream(values())
                                .filter(channel -> channel.value.equalsIgnoreCase(normalized))
                                .findFirst()
                                .orElseThrow(() -> new DomainValidationException("Canal de verificación inválido"));
        }
}
