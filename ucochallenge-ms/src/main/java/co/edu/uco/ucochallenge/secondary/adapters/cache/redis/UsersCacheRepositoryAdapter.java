package co.edu.uco.ucochallenge.secondary.adapters.cache.redis;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import co.edu.uco.ucochallenge.user.findusers.application.port.FindUsersByFilterRepositoryPort;
import co.edu.uco.ucochallenge.user.findusers.application.usecase.domain.FindUsersByFilterResponseDomain;

/** Decorator que agrega caché a findAll(page,size) */
@Repository
public class UsersCacheRepositoryAdapter implements FindUsersByFilterRepositoryPort {

    private final FindUsersByFilterRepositoryPort delegate;

    public UsersCacheRepositoryAdapter(FindUsersByFilterRepositoryPort delegate) {
        this.delegate = delegate;
    }

    @Override
    @Cacheable(cacheNames = "usersByPage", keyGenerator = "usersPageKeyGenerator")
    public FindUsersByFilterResponseDomain findAll(int page, int size) {
        return delegate.findAll(page, size);
    }
}
