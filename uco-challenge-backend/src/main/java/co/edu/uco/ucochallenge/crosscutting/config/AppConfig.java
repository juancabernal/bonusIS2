package co.edu.uco.ucochallenge.crosscutting.config;

import java.util.UUID;
import java.util.function.Supplier;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

        @Bean
        public Supplier<UUID> idGenerator() {
                return UUID::randomUUID;
        }
}
