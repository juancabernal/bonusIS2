package co.edu.uco.ucochallenge.primary.controller.advice;

import java.util.Optional;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import co.edu.uco.ucochallenge.application.ApiErrorResponse;
import co.edu.uco.ucochallenge.crosscuting.exception.BusinessException;   // (tu paquete con 1 't')
import co.edu.uco.ucochallenge.crosscuting.exception.DomainValidationException;   // (tu paquete con 1 't')
import co.edu.uco.ucochallenge.crosscuting.exception.NotFoundException;  // (tu paquete con 1 't')
import co.edu.uco.ucochallenge.crosscuting.exception.NotificationDeliveryException;   // (tu paquete con 1 't')
import co.edu.uco.ucochallenge.crosscutting.dto.MessageDTO;              // DTO (paquete con 2 't')
import co.edu.uco.ucochallenge.secondary.adapters.cache.catalog.MessagesCatalogCache;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private static final String GENERIC_ERROR_MESSAGE =
            "Ocurrió un error inesperado. Por favor, intente nuevamente más tarde.";

    private final MessagesCatalogCache messagesCatalogCache;

    public GlobalExceptionHandler(final MessagesCatalogCache messagesCatalogCache) {
        this.messagesCatalogCache = messagesCatalogCache;
    }

    /** Resuelve un mensaje humano desde el Message-Service con fallback seguro. */
    private String resolveCatalogMessage(final String code) {
        if (code == null || code.isBlank()) {
            return GENERIC_ERROR_MESSAGE;
        }
        try {
            // Tipado explícito para evitar “Cannot infer type arguments for map…”
            final Function<MessageDTO, String> toUserMessage = new Function<MessageDTO, String>() {
                @Override public String apply(MessageDTO dto) {
                    return dto.getUserMessageResolved();
                }
            };

            Optional<String> resolved = messagesCatalogCache
                    .getMessage(code)              // Mono<MessageDTO>
                    .map(toUserMessage)            // Mono<String>
                    .onErrorResume(e -> {          // por si 404/timeout
                        log.warn("Fallo consultando catálogo para '{}': {}", code, e.toString());
                        return reactor.core.publisher.Mono.empty();
                    })
                    .blockOptional();

            return resolved.filter(msg -> msg != null && !msg.isBlank()).orElse(code);

        } catch (Exception ex) {
            log.warn("No se pudo obtener el mensaje para el código '{}': {}", code, ex.toString());
            return code;
        }
    }

    // ================== EXCEPCIONES DE NEGOCIO ==================
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorResponse> handleBusiness(final BusinessException ex) {
        final String message = resolveCatalogMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiErrorResponse.businessError(message));
    }

    @ExceptionHandler(DomainValidationException.class)
    public ResponseEntity<ApiErrorResponse> handleDomainValidation(final DomainValidationException ex) {
        final String message = resolveCatalogMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiErrorResponse.validationError(message));
    }

    // ================== RECURSOS NO ENCONTRADOS ==================
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(final NotFoundException ex) {
        final String message = resolveCatalogMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiErrorResponse.businessError(message));
    }

    // ================== VALIDACIONES (Jakarta / Spring) ==================
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleJakartaConstraint(final ConstraintViolationException ex) {
        final String message = ex.getConstraintViolations().stream()
                .map(v -> Optional.ofNullable(v.getMessage()).orElse(GENERIC_ERROR_MESSAGE))
                .map(this::resolveCatalogMessage)
                .findFirst()
                .orElse(GENERIC_ERROR_MESSAGE);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiErrorResponse.validationError(message));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex) {
        final String message = Optional.ofNullable(ex.getBindingResult().getFieldError())
                .map(FieldError::getDefaultMessage)
                .map(this::resolveCatalogMessage)
                .orElse(GENERIC_ERROR_MESSAGE);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiErrorResponse.validationError(message));
    }

    @ExceptionHandler(NotificationDeliveryException.class)
    public ResponseEntity<ApiErrorResponse> handleNotificationDelivery(final NotificationDeliveryException ex) {
        final String message = resolveCatalogMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(ApiErrorResponse.unexpectedError(message));
    }

    // ================== ERRORES DE BASE DE DATOS ==================
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrity(final DataIntegrityViolationException ex) {
        final String message = resolveCatalogMessage("exception.general.technical");
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiErrorResponse.businessError(message));
    }

    // ================== ENTIDAD RELACIONADA NO ENCONTRADA ==================
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleEntityNotFound(final EntityNotFoundException ex) {
        final String message = resolveCatalogMessage("exception.general.validation");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiErrorResponse.businessError(message));
    }

    // ================== ERROR DESCONOCIDO ==================
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneric(final Exception ex) {
        log.error("Error inesperado: ", ex);
        final String message = resolveCatalogMessage("exception.general.unexpected");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiErrorResponse.unexpectedError(message));
    }
}
