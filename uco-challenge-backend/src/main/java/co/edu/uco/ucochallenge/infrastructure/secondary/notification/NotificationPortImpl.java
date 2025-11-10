package co.edu.uco.ucochallenge.infrastructure.secondary.notification;

import java.time.Year;
import com.notificationapi.model.SmsOptions;

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

    // Notificación para avisos al usuario (correo)
    private static final String DUP_NOTIFICATION_ID = "duplicate_alert";
    private static final String DUP_TEMPLATE_ID     = "template_one";

    // Notificación para avisos al usuario (SMS)
    private static final String DUP_SMS_NOTIFICATION_ID = "duplicado_sms";

    // ✅ NUEVO: Notificación exclusiva para el administrador
    private static final String DUP_ADMIN_NOTIFICATION_ID = "duplicado_aviso_admin";
    private static final String ADMIN_TEMPLATE_ID          = "template_one"; // usa tu plantilla

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

    // ---------- Builders (Email-only con PARAMETERS para el usuario) ----------
    private static Map<String, Object> duplicateParams(String subject, String message) {
        Map<String, Object> p = new HashMap<>();
        p.put("subject", subject);
        p.put("message", message);
        p.put("currentYear", currentYear());
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

    private NotificationRequest buildDuplicateSms(final String toMobile, final String message) {
        final String e164 = normalizeToE164(toMobile);
        final User user = new User(e164).setNumber(e164);
        return new NotificationRequest(DUP_SMS_NOTIFICATION_ID, user)
                .setTemplateId(DUP_TEMPLATE_ID)
                .setSms(new SmsOptions().setMessage(message));
    }

    // ✅ NUEVO: Builder para el correo del ADMIN con solo duplicatedValue y channel
    private NotificationRequest buildAdminDuplicateAlertEmail(final String adminEmail,
                                                              final String duplicatedValue,
                                                              final String channel) {
        final User user = new User(adminEmail).setEmail(adminEmail);
        Map<String, Object> params = new HashMap<>();
        params.put("duplicatedValue", duplicatedValue); // {{duplicatedValue}}
        params.put("channel", channel);                 // {{channel}} -> "email" | "sms"
        return new NotificationRequest(DUP_ADMIN_NOTIFICATION_ID, user)
                .setTemplateId(ADMIN_TEMPLATE_ID)
                .setParameters(params);
    }

    private void trySend(final NotificationRequest request, final String context) {
        try {
            notificationApi.send(request);
        } catch (Exception ex) {
            log.warn("Notification send failed ({}). Continuing without blocking. Cause={}", context, ex.toString());
        }
    }

    // ========== Implementación del puerto ==========
    @Override
    public void notifyAdministrator(final String message) {
        // Compatibilidad: usa la nueva notificación del admin.
        final String admin = resolveAdminEmail();
        trySend(buildAdminDuplicateAlertEmail(admin, message, "desconocido"), "notifyAdministrator");
    }

    @Override
    public void notifyExecutor(final String executorIdentifier, final String message) {
        // Mantenemos el comportamiento existente (no requerido por tu cambio actual)
        if (executorIdentifier == null || !executorIdentifier.contains("@")) {
            final String admin = resolveAdminEmail();
            // Usamos la nueva notificación también aquí para consistencia.
            trySend(buildAdminDuplicateAlertEmail(admin, String.valueOf(executorIdentifier), "desconocido"),
                    "notifyExecutor(admin)");
            return;
        }
        final String subject = "UCO Challenge - Registro duplicado";
        trySend(buildDuplicateEmail(executorIdentifier, subject, message), "notifyExecutor.email");
    }

    @Override
    public void notifyEmailOwner(final String email, final String message) {
        // Correo al dueño (sin cambios)
        final String subject = "UCO Challenge - Intento con correo ya registrado";
        trySend(buildDuplicateEmail(email, subject, message), "notifyEmailOwner");

        // ✅ Aviso al ADMIN con valor duplicado y canal = email
        final String admin = resolveAdminEmail();
        trySend(buildAdminDuplicateAlertEmail(admin, email, "email"), "notifyEmailOwner.admin");
    }

    @Override
    public void notifyMobileOwner(final String mobileNumber, final String message) {
        // SMS al dueño (sin cambios)
        trySend(buildDuplicateSms(mobileNumber, message), "notifyMobileOwner.sms");

        // ✅ Aviso al ADMIN con valor duplicado y canal = sms
        final String admin = resolveAdminEmail();
        final String e164 = normalizeToE164(mobileNumber);
        trySend(buildAdminDuplicateAlertEmail(admin, e164, "sms"), "notifyMobileOwner.admin");
    }

    // --- helper E.164 ---
    private String normalizeToE164(String raw) {
        if (raw == null) return null;
        String digits = raw.replaceAll("\\D", ""); // deja solo números

        // +57XXXXXXXXXX (12 dígitos) -> OK
        if (digits.startsWith("57") && digits.length() == 12) {
            return "+" + digits;
        }
        // 3XXXXXXXXX (10 dígitos móviles en CO) -> anteponer +57
        if (digits.length() == 10 && digits.startsWith("3")) {
            return "+57" + digits;
        }
        // si ya viene con + al inicio, regrésalo tal cual
        if (raw.startsWith("+")) return raw;

        // fallback: agrega + al inicio
        return "+" + digits;
    }
}
