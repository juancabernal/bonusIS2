package co.edu.uco.ucochallenge.infrastructure.primary.web.controller.dev;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import co.edu.uco.ucochallenge.crosscutting.secrets.SecretProvider;

@RestController
@RequestMapping("/dev/kv")
@Profile("dev")
public class PrimaryKeyVaultSecretsController {

  private final SecretProvider secrets;

  public PrimaryKeyVaultSecretsController(SecretProvider secrets) {
    this.secrets = secrets;
  }

  @GetMapping("/{name}")
  public String read(@PathVariable String name) {
    return secrets.get(name);
  }
}
