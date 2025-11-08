package co.edu.uco.ucochallenge.secondary.adapters.cache.catalog;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import co.edu.uco.ucochallenge.crosscutting.dto.ParameterDTO;
import co.edu.uco.ucochallenge.secondary.ports.restclient.ParameterServicePort;
import reactor.core.publisher.Mono;

@Component
public class ParametersCatalogCache {

    private final ParameterServicePort client;

    public ParametersCatalogCache(ParameterServicePort client) {
        this.client = client;
    }

    @Cacheable(cacheNames = "parametersCatalog", key = "#key")
    public ParameterDTO getParameterSync(String key) {
        return client.getParameter(key).block();
    }

    public Mono<ParameterDTO> getParameter(String key) {
        return Mono.fromCallable(() -> getParameterSync(key));
    }

    @CacheEvict(cacheNames = "parametersCatalog", key = "#key")
    public ParameterDTO updateParameterSync(String key, String value) {
        return client.updateParameter(key, value).block();
    }

    /** (Opcional)
     * @CacheEvict(cacheNames = "parametersCatalog", allEntries = true)
     * public void evictAllParameters() {}
     */
}
