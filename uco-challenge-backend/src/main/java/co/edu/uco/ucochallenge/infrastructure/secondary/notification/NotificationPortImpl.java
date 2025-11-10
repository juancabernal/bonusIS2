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

    // Fallback si no está configurado en parámetros
    private static final String DEFAULT_ADMIN_EMAIL = "juanjosenarvaezmarin13092005@gmail.com";

    // NotificationAPI
    private static final String DUP_NOTIFICATION_ID = "alerta_campo_duplicado"; // <-- SOLO email
    private static final String DUP_TEMPLATE_ID     = "template_one";           // <-- tu plantilla de email

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

    // ---------- Builders (Email-only con PARAMETERS) ----------

    private static Map<String, Object> duplicateParams(String subject, String message) {
        Map<String, Object> p = new HashMap<>();
        p.put("subject", subject);          // Úsalo en el Subject de la plantilla si quieres: {{subject}}
        p.put("message", message);          // Cuerpo del correo: {{message}}
        p.put("currentYear", currentYear()); // Footer: {{currentYear}}
        return p;
    }

    private NotificationRequest buildDuplicateEmail(final String toEmail,
                                                    final String subject,
                                                    final String message) {
        final User user = new User(toEmail).setEmail(toEmail); // NO setNumber
        return new NotificationRequest(DUP_NOTIFICATION_ID, user)
                .setTemplateId(DUP_TEMPLATE_ID)
                .setParameters(duplicateParams(subject, message));
    }

    private NotificationRequest buildAdminDuplicateEmail(final String adminEmail,
                                                         final String message) {
        final User user = new User(adminEmail).setEmail(adminEmail);
        return new NotificationRequest(DUP_NOTIFICATION_ID, user)
                .setTemplateId(DUP_TEMPLATE_ID)
                .setParameters(duplicateParams("UCO Challenge - Alerta de duplicado", message));
    }

    private void trySend(final NotificationRequest request, final String context) {
        try {
            notificationApi.send(request);
        } catch (Exception ex) {
            log.warn("Notification send failed ({}). Continuing without blocking. Cause={}", context, ex.toString());
        }
    }

    // ========== Implementación del puerto (solo email) ==========

    @Override
    public void notifyAdministrator(final String message) {
        final String admin = resolveAdminEmail();
        trySend(buildAdminDuplicateEmail(admin, message), "notifyAdministrator");
    }

    @Override
    public void notifyExecutor(final String executorIdentifier, final String message) {
        // "executor" puede ser email del usuario que intenta registrar. Si no parece email -> solo admin.
        if (executorIdentifier == null || !executorIdentifier.contains("@")) {
            final String admin = resolveAdminEmail();
            trySend(buildAdminDuplicateEmail(admin, "Executor no-email: " + executorIdentifier + ". " + message),
                    "notifyExecutor(admin)");
            return;
        }
        final String subject = "UCO Challenge - Registro duplicado";
        trySend(buildDuplicateEmail(executorIdentifier, subject, message), "notifyExecutor.email");
    }

    @Override
    public void notifyEmailOwner(final String email, final String message) {
        final String subject = "UCO Challenge - Intento con correo ya registrado";
        trySend(buildDuplicateEmail(email, subject, message), "notifyEmailOwner");
    }

    @Override
    public void notifyMobileOwner(final String mobileNumber, final String message) {
        // No SMS: avisamos al admin por email incluyendo el número
        final String admin = resolveAdminEmail();
        final String body = "Se intentó registrar el móvil ya existente: " + String.valueOf(mobileNumber)
                + ". " + message;
        trySend(buildAdminDuplicateEmail(admin, body), "notifyMobileOwner(adminOnly)");
    }
}
