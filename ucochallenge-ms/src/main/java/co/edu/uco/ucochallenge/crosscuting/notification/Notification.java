package co.edu.uco.ucochallenge.crosscuting.notification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Simple notification object that collects validation errors without relying on
 * any framework infrastructure.
 */
public final class Notification {

        private final List<NotificationError> errors = new ArrayList<>();

        private Notification() {
        }

        public static Notification create() {
                return new Notification();
        }

        public void addError(final String code, final String message) {
                errors.add(new NotificationError(code, message));
        }

        public boolean hasErrors() {
                return !errors.isEmpty();
        }

        public List<NotificationError> getErrors() {
                return Collections.unmodifiableList(errors);
        }

        public void merge(final Notification other) {
                if (Objects.nonNull(other)) {
                        errors.addAll(other.errors);
                }
        }

        public String formattedMessages() {
                return errors.stream()
                                .map(NotificationError::message)
                                .collect(Collectors.joining(" | "));
        }

        public record NotificationError(String code, String message) {
        }
}
