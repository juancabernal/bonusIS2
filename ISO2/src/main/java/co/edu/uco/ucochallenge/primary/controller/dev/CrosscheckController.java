package co.edu.uco.ucochallenge.primary.controller.dev;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.uco.ucochallenge.crosscutting.dto.MessageDTO;
import co.edu.uco.ucochallenge.crosscutting.dto.ParameterDTO;
import co.edu.uco.ucochallenge.secondary.adapters.cache.catalog.MessagesCatalogCache;
import co.edu.uco.ucochallenge.secondary.adapters.cache.catalog.ParametersCatalogCache;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/test")
public class CrosscheckController {

    private final ParametersCatalogCache parametersCatalogCache;
    private final MessagesCatalogCache messagesCatalogCache;

    public CrosscheckController(ParametersCatalogCache parametersCatalogCache,
            MessagesCatalogCache messagesCatalogCache) {
        this.parametersCatalogCache = parametersCatalogCache;
        this.messagesCatalogCache = messagesCatalogCache;
    }

    @GetMapping("/parameters/{key}")
    public Mono<ParameterDTO> getParameter(@PathVariable String key) {
        return parametersCatalogCache.getParameter(key);
    }

    @GetMapping("/messages/{code}")
    public Mono<MessageDTO> getMessage(@PathVariable String code) {
        return messagesCatalogCache.getMessage(code);
    }
}
