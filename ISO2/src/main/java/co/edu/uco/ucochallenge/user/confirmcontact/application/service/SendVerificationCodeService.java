package co.edu.uco.ucochallenge.user.confirmcontact.application.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uco.ucochallenge.crosscuting.exception.BusinessException;
import co.edu.uco.ucochallenge.crosscuting.exception.NotificationDeliveryException;
import co.edu.uco.ucochallenge.crosscuting.exception.NotFoundException;
import co.edu.uco.ucochallenge.crosscutting.ParamKeys;
import co.edu.uco.ucochallenge.crosscutting.dto.ParameterDTO;
import co.edu.uco.ucochallenge.secondary.adapters.notification.NotificationContactConfirmationAdapter;
import co.edu.uco.ucochallenge.secondary.adapters.repository.entity.UserEntity;
import co.edu.uco.ucochallenge.secondary.adapters.repository.jpa.SpringDataUserRepository;
import co.edu.uco.ucochallenge.secondary.adapters.cache.catalog.ParametersCatalogCache;

@Service
public class SendVerificationCodeService {

        private static final Logger LOGGER = LoggerFactory.getLogger(SendVerificationCodeService.class);
        private static final String USER_NOT_FOUND_MESSAGE = "User not found";
        private static final String MISSING_EMAIL_MESSAGE = "El usuario no tiene correo electrónico configurado";
        private static final String MISSING_MOBILE_MESSAGE = "El usuario no tiene número móvil configurado";

        private final SpringDataUserRepository userRepo;
        private final VerificationCodeService codeService;
        private final NotificationContactConfirmationAdapter notifier;
        private final ParametersCatalogCache parametersCatalogCache;

        public SendVerificationCodeService(final SpringDataUserRepository userRepo,
                        final VerificationCodeService codeService,
                        final NotificationContactConfirmationAdapter notifier,
                        final ParametersCatalogCache parametersCatalogCache) {
                this.userRepo = userRepo;
                this.codeService = codeService;
                this.notifier = notifier;
                this.parametersCatalogCache = parametersCatalogCache;
        }

        @Transactional(noRollbackFor = NotificationDeliveryException.class)
        public void sendVerificationCode(final UUID userId, final VerificationChannel channel) {
                final UserEntity user = userRepo.findById(userId)
                                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND_MESSAGE));

                final String contact = resolveContact(user, channel);
                final String normalizedContact = channel.normalizeContact(contact);
                final String code = codeService.generateCode();
                final Integer ttlValue = parametersCatalogCache.getParameter(ParamKeys.TOKEN_DURATION_MINUTES)
                                .map(ParameterDTO::value)
                                .map(String::trim)
                                .map(Integer::parseInt)
                                .onErrorReturn(15)
                                .defaultIfEmpty(15)
                                .block();
                final int ttlMinutes = ttlValue == null || ttlValue <= 0 ? 15 : ttlValue;
                final LocalDateTime expiration = LocalDateTime.now().plusMinutes(ttlMinutes);

                LOGGER.info("[Verification] Saving code for contact={}, expires={}", normalizedContact, expiration);
                codeService.deleteByContactIgnoreCase(normalizedContact);
                codeService.save(normalizedContact, code, expiration);
                LOGGER.info("[Verification] Saved code for contact={}, expires={}", normalizedContact, expiration);

                try {
                        notifier.sendCodeForChannel(channel.isEmail() ? contact : null,
                                        channel.isMobile() ? contact : null,
                                        channel.getValue(),
                                        code);
                } catch (NotificationDeliveryException ex) {
                        throw ex;
                } catch (Exception ex) {
                        throw new NotificationDeliveryException("No se pudo enviar el código de verificación", ex);
                }
        }

        private String resolveContact(final UserEntity user, final VerificationChannel channel) {
                final String contact = channel.isEmail() ? user.getEmail() : user.getMobileNumber();
                if (contact == null || contact.trim().isEmpty()) {
                        throw new BusinessException(channel.isEmail() ? MISSING_EMAIL_MESSAGE : MISSING_MOBILE_MESSAGE);
                }
                return contact.trim();
        }
}
