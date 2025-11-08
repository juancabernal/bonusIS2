package co.edu.uco.ucochallenge.secondary.adapters.restclient;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import co.edu.uco.ucochallenge.crosscutting.dto.MessageDTO;
import co.edu.uco.ucochallenge.secondary.ports.restclient.MessageServicePort;
import reactor.core.publisher.Mono;

@Component
public class MessageServiceClient implements MessageServicePort {

    private final WebClient webClient;

    public MessageServiceClient(
            @Value("${external.messages.base-url}") String baseUrl,
            WebClient.Builder builder) {
        this.webClient = builder.baseUrl(baseUrl).build();
    }

    @Override
    public Mono<MessageDTO> getMessage(String code) {
        return webClient.get()
                .uri("/{code}", code)
                .retrieve()
                .bodyToMono(MessageDTO.class);
    }

    @Override
    public Mono<Map<String, MessageDTO>> getAllMessages() {
        return webClient.get()
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, MessageDTO>>() {
                });
    }

    @Override
    public Mono<MessageDTO> upsertMessage(String code, MessageDTO body) {
        return webClient.put()
                .uri("/{code}", code)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(MessageDTO.class);
    }

    @Override
    public Mono<MessageDTO> deleteMessage(String code) {
        return webClient.delete()
                .uri("/{code}", code)
                .retrieve()
                .bodyToMono(MessageDTO.class);
    }
}
