package co.edu.uco.ucochallenge.infrastructure.secondary.notification;

import java.time.Year;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.notificationapi.NotificationApi;
import com.notificationapi.model.NotificationRequest;
import com.notificationapi.model.User;

import co.edu.uco.ucochallenge.crosscutting.ParamKeys;
import co.edu.uco.ucochallenge.crosscutting.dto.ParameterDTO;
import co.edu.uco.ucochallenge.infrastructure.secondary.cache.catalog.ParametersCatalogCache;
import co.edu.uco.ucochallenge.domain.user.port.NotificationPort;

@Component
public class NotificationPortImpl implements NotificationPort {

    private static final Logger log = LoggerFactory.getLogger(NotificationPortImpl.class);

    private static final String DEFAULT_ADMIN_EMAIL = "juancamilobernal2222@gmail.com";

    private static final String DUP_NOTIFICATION_ID = "uco_duplicate";
    private static final String DUP_TEMPLATE_ID     = "template_one";

    private final NotificationApi notificationApi;
    private final ParametersCatalogCache parametersCatalogCache;

    public NotificationPortImpl(final NotificationApi notificationApi,
                                final ParametersCatalogCache parametersCatalogCache) {
        this.notificationApi = notificationApi;
        this.parametersCatalogCache = parametersCatalogCache;
    }

    private String resolveAdminEmail() {
        final String adminEmail = parametersCatalogCache.getParameter(ParamKeys.ADMIN_EMAIL)
                .map(ParameterDTO::value)
                .map(String::trim)
                .filter(v -> !v.isEmpty())
                .defaultIfEmpty(DEFAULT_ADMIN_EMAIL)
                .onErrorReturn(DEFAULT_ADMIN_EMAIL)
                .block();
        return (adminEmail == null || adminEmail.isBlank()) ? DEFAULT_ADMIN_EMAIL : adminEmail;
    }

    private static String currentYear() {
        return String.valueOf(Year.now().getValue());
    }

    private static Map<String, Object> duplicateTags(String to, String subject, String message) {
        Map<String, Object> tags = new HashMap<>();
        tags.put("subject", subject);
        tags.put("message", message);
        tags.put("to", to);
        tags.put("channel", "email");
        tags.put("currentYear", currentYear());
        return tags;
    }

    // ----- Builders (solo email, siguiendo el ejemplo oficial) -----

    private NotificationRequest buildDuplicateEmail(final String toEmail,
                                                    final String subject,
                                                    final String message) {
        final User user = new User(toEmail).setEmail(toEmail);
        return new NotificationRequest(DUP_NOTIFICATION_ID, user)
                .setTemplateId(DUP_TEMPLATE_ID)
                .setMergeTags(duplicateTags(toEmail, subject, message));
    }

    private NotificationRequest buildAdminDuplicateEmail(final String adminEmail,
                                                         final String message) {
        final User user = new User(adminEmail).setEmail(adminEmail);
        return new NotificationRequest(DUP_NOTIFICATION_ID, user)
                .setTemplateId(DUP_TEMPLATE_ID)
                .setMergeTags(duplicateTags(adminEmail, "UCO Challenge - Correo duplicado", message));
    }

    private void trySend(final NotificationRequest request, final String context) {
        try {
            notificationApi.send(request);
        } catch (Exception ex) {
            log.warn("Notification send failed ({}). Continuing without blocking. Cause={}", context, ex.toString());
        }
    }

    // ===== Implementación del puerto (solo email) =====

    @Override
    public void notifyAdministrator(final String message) {
        final String admin = resolveAdminEmail();
        trySend(buildAdminDuplicateEmail(admin, message), "notifyAdministrator");
    }

    @Override
    public void notifyExecutor(final String executorIdentifier, final String message) {
        final String subject = "UCO Challenge - Registro duplicado";
        if (executorIdentifier == null || !executorIdentifier.contains("@")) {
            final String admin = resolveAdminEmail();
            trySend(buildAdminDuplicateEmail(admin, message), "notifyExecutor(admin)");
            return;
        }
        trySend(buildDuplicateEmail(executorIdentifier, subject, message), "notifyExecutor.email");
    }

    @Override
    public void notifyEmailOwner(final String email, final String message) {
        final String subject = "UCO Challenge - Correo duplicado";
        trySend(buildDuplicateEmail(email, subject, message), "notifyEmailOwner");
    }

    @Override
    public void notifyMobileOwner(final String mobileNumber, final String message) {
        // Sin SMS: notificar por email al admin incluyendo el número en el cuerpo
        final String admin = resolveAdminEmail();
        final String subject = "UCO Challenge - Telefono duplicado";
        final String body = "Numero reportado: " + String.valueOf(mobileNumber) + ". " + message;
        trySend(buildAdminDuplicateEmail(admin, body), "notifyMobileOwner(adminOnly)");
    }
}
