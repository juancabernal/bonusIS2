package co.edu.uco.ucochallenge.infrastructure.secondary.ports.restclient;

import java.util.Map;

import co.edu.uco.ucochallenge.crosscutting.dto.MessageDTO;
import reactor.core.publisher.Mono;

public interface MessageServicePort {

    Mono<MessageDTO> getMessage(String code);

    Mono<Map<String, MessageDTO>> getAllMessages();

    Mono<MessageDTO> upsertMessage(String code, MessageDTO body);

    Mono<MessageDTO> deleteMessage(String code);
}
