package co.edu.uco.ucochallenge.infrastructure.secondary.adapters.cache.redis;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import co.edu.uco.ucochallenge.domain.user.search.model.UserSearchResultDomainModel;
import co.edu.uco.ucochallenge.application.user.search.port.FindUsersByFilterRepositoryPort;

/** Decorator que agrega caché a findAll(page,size) */
@Repository
public class UsersCacheRepositoryAdapter implements FindUsersByFilterRepositoryPort {

    private final FindUsersByFilterRepositoryPort delegate;

    public UsersCacheRepositoryAdapter(FindUsersByFilterRepositoryPort delegate) {
        this.delegate = delegate;
    }

    @Override
    @Cacheable(cacheNames = "usersByPage", keyGenerator = "usersPageKeyGenerator")
    public UserSearchResultDomainModel findAll(int page, int size) {
        return delegate.findAll(page, size);
    }
}
