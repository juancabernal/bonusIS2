package co.edu.uco.ucochallenge.crosscutting.secrets;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

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
