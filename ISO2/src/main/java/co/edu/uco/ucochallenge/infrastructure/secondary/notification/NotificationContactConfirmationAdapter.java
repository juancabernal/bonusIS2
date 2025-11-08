package co.edu.uco.ucochallenge.infrastructure.secondary.notification;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.notificationapi.NotificationApi;
import com.notificationapi.model.NotificationRequest;
import com.notificationapi.model.SmsOptions;
import com.notificationapi.model.User;

import co.edu.uco.ucochallenge.crosscutting.exception.NotificationDeliveryException;
import co.edu.uco.ucochallenge.domain.user.port.ContactConfirmationPort;
import co.edu.uco.ucochallenge.infrastructure.secondary.persistence.jpa.entity.VerificationCodeEntity;
import co.edu.uco.ucochallenge.infrastructure.secondary.persistence.jpa.repository.VerificationCodeRepository;

@Component
public class NotificationContactConfirmationAdapter implements ContactConfirmationPort {

    private static final int CODE_UPPER_BOUND = 1_000_000;
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationContactConfirmationAdapter.class);

    private final NotificationApi api;
    private final VerificationCodeRepository codeRepository;
    private final SecureRandom random = new SecureRandom();

    public NotificationContactConfirmationAdapter(NotificationApi api, VerificationCodeRepository codeRepository) {
        this.api = api;
        this.codeRepository = codeRepository;
    }

    @Override
    public void confirmEmail(String email) {
        sendConfirmation(email, null, "Confirma tu correo electrónico - UCO Challenge");
    }

    @Override
    public void confirmMobileNumber(String mobileNumber) {
        sendConfirmation(null, mobileNumber, "Confirma tu número móvil - UCO Challenge");
    }

    @Transactional
    private void sendConfirmation(String email, String number, String subject) {
        try {
            String contact = email != null ? email : number;
            String code = String.format("%06d", random.nextInt(CODE_UPPER_BOUND));

            codeRepository.findByContact(contact).ifPresent(codeRepository::delete);
            codeRepository.save(new VerificationCodeEntity(contact, code, LocalDateTime.now().plusMinutes(15)));

            User user = new User(contact)
                    .setEmail(email)
                    .setNumber(number);

            // ✅ Si es email, usa la plantilla "predeterminado" y pasa el token
            if (email != null) {
                Map<String, Object> params = new HashMap<>();
                params.put("verificationCode", code); // Referencia {{verificationCode}} en la plantilla

                NotificationRequest request = new NotificationRequest("confirmar_datos", user)
                        .setTemplateId("predeterminado")
                        .setParameters(params);

                String response = api.send(request);
                LOGGER.info("[NotificationAPI] Email verification code {} sent to {} | Response: {}", code, contact, response);
                return;
            }

            // 🚫 No se toca el resto del código (SMS y demás)
            Map<String, Object> mergeTags = new HashMap<>();
            mergeTags.put("name", contact);
            mergeTags.put("confirmationCode", code);
            mergeTags.put("currentYear", "2025");
            mergeTags.put("comment", subject);

            NotificationRequest request = new NotificationRequest("confirmar_datos", user)
                    .setTemplateId("predeterminado")
                    .setMergeTags(mergeTags);

            String response = api.send(request);
            LOGGER.info("[NotificationAPI] Sent code {} to {} | Response: {}", code, contact, response);
        } catch (Exception e) {
            LOGGER.error("[NotificationAPI] Error sending code", e);
            throw new NotificationDeliveryException("No se pudo enviar el código de verificación", e);
        }
    }

    public void sendCodeForChannel(String email, String number, String channel, String code) {
        try {
            Map<String, Object> merge = new HashMap<>();
            String name = email != null ? email : number;
            merge.put("name", name);
            merge.put("confirmationCode", code);
            merge.put("currentYear", "2025");
            merge.put("comment", "Confirma tu contacto - UCO Challenge");

            if ("mobile".equalsIgnoreCase(channel)) {
                String e164 = normalizeColombianNumber(number);

                User user = new User(e164).setNumber(e164);

                NotificationRequest req = new NotificationRequest("confirmar_datos", user)
                        .setTemplateId("template_one")
                        .setSms(new SmsOptions().setMessage("Tu código de verificación UCO Challenge es: " + code))
                        .setMergeTags(merge);

                String resp = api.send(req);
                LOGGER.info("[NotificationAPI] SMS code sent: {}", resp);
                return;
            }

            if ("email".equalsIgnoreCase(channel)) {
                User user = new User(email).setEmail(email);

                // ✅ Aquí también usamos la plantilla predeterminada con el token
                Map<String, Object> params = new HashMap<>();
                params.put("verificationCode", code);

                NotificationRequest req = new NotificationRequest("confirmar_datos", user)
                        .setTemplateId("predeterminado")
                        .setParameters(params);

                String resp = api.send(req);
                LOGGER.info("[NotificationAPI] Email code sent: {}", resp);
                return;
            }

            LOGGER.warn("Unknown channel '{}'", channel);
        } catch (Exception e) {
            LOGGER.error("[NotificationAPI] Error sending code", e);
            throw new NotificationDeliveryException("No se pudo enviar el código de verificación", e);
        }
    }

    private String normalizeColombianNumber(String number) {
        if (number == null) {
            return null;
        }

        String digits = number.replaceAll("\\D", "");
        if (digits.length() == 10 && digits.startsWith("3")) {
            digits = "57" + digits;
        }

        if (digits.startsWith("57")) {
            return "+" + digits;
        }

        return number;
    }
}
