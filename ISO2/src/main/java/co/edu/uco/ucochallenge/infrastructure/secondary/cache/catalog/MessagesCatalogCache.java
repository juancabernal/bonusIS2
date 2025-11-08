package co.edu.uco.ucochallenge.infrastructure.secondary.cache.catalog;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import co.edu.uco.ucochallenge.crosscutting.dto.MessageDTO;
import co.edu.uco.ucochallenge.infrastructure.secondary.http.port.MessageServicePort;
import reactor.core.publisher.Mono;

@Component
public class MessagesCatalogCache {

    private final MessageServicePort client;

    public MessagesCatalogCache(MessageServicePort client) {
        this.client = client;
    }

    /** Cachea el DTO (no el Mono) */
    @Cacheable(cacheNames = "messagesCatalog", key = "#code")
    public MessageDTO getMessageSync(String code) {
        return client.getMessage(code).block();
    }

    /** Facilidad para capas reactivas que esperan Mono */
    public Mono<MessageDTO> getMessage(String code) {
        return Mono.fromCallable(() -> getMessageSync(code));
    }

    /** Invalida un ítem tras upsert */
    @CacheEvict(cacheNames = "messagesCatalog", key = "#code")
    public MessageDTO upsertMessageSync(String code, MessageDTO body) {
        return client.upsertMessage(code, body).block();
    }

    /** Invalida un ítem tras delete */
    @CacheEvict(cacheNames = "messagesCatalog", key = "#code")
    public MessageDTO deleteMessageSync(String code) {
        return client.deleteMessage(code).block();
    }

    /** (Opcional) Invalidación masiva si se requiere:
     *  @CacheEvict(cacheNames = "messagesCatalog", allEntries = true)
     *  public void evictAllMessages() {}
     */
}
