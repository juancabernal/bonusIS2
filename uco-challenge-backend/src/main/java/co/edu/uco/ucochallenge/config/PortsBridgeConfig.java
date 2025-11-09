package co.edu.uco.ucochallenge.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import co.edu.uco.ucochallenge.domain.user.port.NotificationPort;
import co.edu.uco.ucochallenge.domain.user.port.ContactConfirmationPort;

@Configuration
public class PortsBridgeConfig {

    private static final Logger log = LoggerFactory.getLogger(PortsBridgeConfig.class);

    /**
     * Bridge: expone NotificationPort requerido por el dominio
     * delegando en ContactConfirmationPort (tu adaptador de confirmación).
     */
    @Bean
    public NotificationPort notificationPort(ContactConfirmationPort delegate) {
        return new NotificationPort() {
            @Override
            public void notifyAdministrator(String message) {
                // Por ahora no-op; deja log para trazar. Implementa según necesites.
                log.info("[NotificationPort] notifyAdministrator -> {}", message);
            }

            @Override
            public void notifyExecutor(String executorIdentifier, String message) {
                // Por ahora no-op; deja log para trazar. Implementa según necesites.
                log.info("[NotificationPort] notifyExecutor -> id={}, message={}",
                        executorIdentifier, message);
            }

            @Override
            public void notifyEmailOwner(String email, String message) {
                // Reutiliza el flujo de confirmación de email
                delegate.confirmEmail(email);
                log.debug("[NotificationPort] notifyEmailOwner delegated to confirmEmail: {}", email);
            }

            @Override
            public void notifyMobileOwner(String mobileNumber, String message) {
                // Reutiliza el flujo de confirmación de móvil
                delegate.confirmMobileNumber(mobileNumber);
                log.debug("[NotificationPort] notifyMobileOwner delegated to confirmMobileNumber: {}", mobileNumber);
            }
        };
    }
}
