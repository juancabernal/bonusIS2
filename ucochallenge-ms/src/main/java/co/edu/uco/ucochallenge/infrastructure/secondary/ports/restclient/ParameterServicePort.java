package co.edu.uco.ucochallenge.infrastructure.secondary.ports.restclient;

import java.util.Map;

import co.edu.uco.ucochallenge.crosscutting.dto.ParameterDTO;
import reactor.core.publisher.Mono;

public interface ParameterServicePort {

    Mono<ParameterDTO> getParameter(String key);

    Mono<Map<String, ParameterDTO>> getAllParameters();

    Mono<ParameterDTO> updateParameter(String key, String value);
}
