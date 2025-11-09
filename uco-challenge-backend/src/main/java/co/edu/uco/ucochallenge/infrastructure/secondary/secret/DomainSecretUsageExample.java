package co.edu.uco.ucochallenge.infrastructure.secondary.secret;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import co.edu.uco.ucochallenge.crosscutting.secret.SecretProvider;

@Component
@Profile("dev")
public class DomainSecretUsageExample {

  private final SecretProvider secretProvider;

  public DomainSecretUsageExample(SecretProvider secretProvider) {
    this.secretProvider = secretProvider;
  }

  public String demonstrateUsage() {
    String password = secretProvider.get("db-password");
    return password;
  }
}
