package co.edu.uco.ucochallenge.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import co.edu.uco.ucochallenge.secret.SecretProvider;
import com.notificationapi.NotificationApi;

@Configuration
public class NotificationApiConfig {

    private static final String SECRET_CLIENT_ID = "cliente-id";
    private static final String SECRET_CLIENT_SECRET = "cliente-secret-id";

    private final SecretProvider secrets;

    public NotificationApiConfig(SecretProvider secrets) {
        this.secrets = secrets;
    }

    @Bean
    public NotificationApi notificationApi() {
        String clientId = secrets.get(SECRET_CLIENT_ID);
        String clientSecret = secrets.get(SECRET_CLIENT_SECRET);
        return new NotificationApi(clientId, clientSecret);
    }
}
