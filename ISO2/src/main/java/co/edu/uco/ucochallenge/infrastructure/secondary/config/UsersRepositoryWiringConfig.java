package co.edu.uco.ucochallenge.infrastructure.secondary.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import co.edu.uco.ucochallenge.domain.user.port.FindUsersByFilterRepositoryPort;
import co.edu.uco.ucochallenge.infrastructure.secondary.cache.redis.UsersCacheRepositoryAdapter;
import co.edu.uco.ucochallenge.infrastructure.secondary.persistence.jpa.adapter.UserRepositoryAdapter;

/**
 * Expone el puerto como decorator de caché envolviendo al adaptador JPA real.
 * No se cambia el interactor ni el use case.
 */
@Configuration
public class UsersRepositoryWiringConfig {

    @Bean
    @Primary
    public FindUsersByFilterRepositoryPort usersRepositoryPortCached(UserRepositoryAdapter jpaAdapter) {
        // Inyectamos la clase concreta JPA para evitar qualifiers/nombres
        return new UsersCacheRepositoryAdapter(jpaAdapter);
    }
}
