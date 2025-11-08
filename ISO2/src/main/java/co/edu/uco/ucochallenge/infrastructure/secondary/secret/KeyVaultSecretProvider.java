package co.edu.uco.ucochallenge.infrastructure.secondary.secret;

import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import co.edu.uco.ucochallenge.crosscutting.secret.SecretProvider;

@Service
@Primary
public class KeyVaultSecretProvider implements SecretProvider {

  private final SecretClient client;

  public KeyVaultSecretProvider(@Value("${azure.keyvault.url}") String vaultUrl) {
    this.client = new SecretClientBuilder()
        .vaultUrl(vaultUrl)
        .credential(new DefaultAzureCredentialBuilder().build())
        .buildClient();
  }

  @Override
  public String get(String name) {
    return client.getSecret(name).getValue();
  }
}
