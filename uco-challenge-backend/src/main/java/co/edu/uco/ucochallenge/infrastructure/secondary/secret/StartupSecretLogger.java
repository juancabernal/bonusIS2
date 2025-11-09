package co.edu.uco.ucochallenge.infrastructure.secondary.secret;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import co.edu.uco.ucochallenge.crosscutting.secret.SecretProvider;

@Component
@Profile("dev")
public class StartupSecretLogger {

  private static final Logger LOGGER = LoggerFactory.getLogger(StartupSecretLogger.class);
  private final SecretProvider secretProvider;

  public StartupSecretLogger(SecretProvider secretProvider) {
    this.secretProvider = secretProvider;
  }

  @EventListener(ApplicationReadyEvent.class)
  public void logSecretAtStartup() {
    try {
      String secret = secretProvider.get("db-password");
      LOGGER.info("Azure Key Vault secret 'db-password' retrieved successfully: {}", mask(secret));
    } catch (RuntimeException ex) {
      LOGGER.warn("Unable to read secret 'db-password' from Azure Key Vault. Cause: {}", ex.getMessage());
    }
  }

  private String mask(String value) {
    if (value == null || value.isBlank()) {
      return "(empty)";
    }
    int unmaskedChars = Math.min(2, value.length());
    String tail = value.substring(value.length() - unmaskedChars);
    return "****" + tail;
  }
}
