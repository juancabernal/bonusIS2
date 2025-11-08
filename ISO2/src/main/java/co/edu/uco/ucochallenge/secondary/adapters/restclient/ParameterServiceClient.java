package co.edu.uco.ucochallenge.secondary.adapters.restclient;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import co.edu.uco.ucochallenge.crosscutting.dto.ParameterDTO;
import co.edu.uco.ucochallenge.secondary.ports.restclient.ParameterServicePort;
import reactor.core.publisher.Mono;

@Component
public class ParameterServiceClient implements ParameterServicePort {

    private final WebClient webClient;

    public ParameterServiceClient(
            @Value("${external.parameters.base-url}") String baseUrl,
            WebClient.Builder builder) {
        this.webClient = builder.baseUrl(baseUrl).build();
    }

    @Override
    public Mono<ParameterDTO> getParameter(String key) {
        return webClient.get()
                .uri("/{key}", key)
                .retrieve()
                .bodyToMono(ParameterDTO.class);
    }

    @Override
    public Mono<Map<String, ParameterDTO>> getAllParameters() {
        return webClient.get()
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, ParameterDTO>>() {
                });
    }

    @Override
    public Mono<ParameterDTO> updateParameter(String key, String value) {
        return webClient.put()
                .uri("/{key}/{value}", key, value)
                .retrieve()
                .bodyToMono(ParameterDTO.class);
    }
}
