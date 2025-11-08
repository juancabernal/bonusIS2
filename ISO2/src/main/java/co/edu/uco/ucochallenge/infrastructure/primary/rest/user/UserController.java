package co.edu.uco.ucochallenge.infrastructure.primary.rest.user;

import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import co.edu.uco.ucochallenge.application.user.confirm.service.SendVerificationCodeService;
import co.edu.uco.ucochallenge.application.user.confirm.service.UserContactConfirmationService;
import co.edu.uco.ucochallenge.domain.user.confirm.VerificationChannel;
import co.edu.uco.ucochallenge.application.user.confirm.dto.ConfirmVerificationCodeRequestDTO;
import co.edu.uco.ucochallenge.application.user.confirm.dto.ConfirmVerificationCodeResponseDTO;
import co.edu.uco.ucochallenge.application.user.find.interactor.FindUsersByFilterInteractor;
import co.edu.uco.ucochallenge.application.user.find.dto.FindUsersByFilterInputDTO;
import co.edu.uco.ucochallenge.application.user.find.dto.FindUsersByFilterOutputDTO;
import co.edu.uco.ucochallenge.application.user.register.interactor.RegisterUserInteractor;
import co.edu.uco.ucochallenge.application.user.register.dto.RegisterUserInputDTO;
import co.edu.uco.ucochallenge.application.user.register.dto.RegisterUserResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Validated
@RestController
@RequestMapping("/uco-challenge/api/v1")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final RegisterUserInteractor registerUserInteractor;
    private final FindUsersByFilterInteractor findUsersByFilterInteractor;
    private final UserContactConfirmationService userContactConfirmationService;
    private final SendVerificationCodeService sendVerificationCodeService;

    public UserController(final RegisterUserInteractor registerUserInteractor,
                          final FindUsersByFilterInteractor findUsersByFilterInteractor,
                          final UserContactConfirmationService userContactConfirmationService,
                          final SendVerificationCodeService sendVerificationCodeService) {
        this.registerUserInteractor = registerUserInteractor;
        this.findUsersByFilterInteractor = findUsersByFilterInteractor;
        this.userContactConfirmationService = userContactConfirmationService;
        this.sendVerificationCodeService = sendVerificationCodeService;
    }

    @PostMapping("/users")
    public Mono<ResponseEntity<RegisterUserResponseDTO>> create(@Valid @RequestBody final RegisterUserInputDTO request) {
        return Mono.fromCallable(() -> {
                    final RegisterUserResponseDTO response = registerUserInteractor.execute(request);
                    return ResponseEntity.status(HttpStatus.CREATED).body(response);
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    @GetMapping("/users")
    public Mono<ResponseEntity<FindUsersByFilterOutputDTO>> getUsers(
            @RequestParam(name = "page", required = false) final Integer page,
            @RequestParam(name = "size", required = false) final Integer size) {
        return Mono.fromCallable(() -> {
                    final var normalizedInput = FindUsersByFilterInputDTO.normalize(page, size);
                    final var response = findUsersByFilterInteractor.execute(normalizedInput);
                    return ResponseEntity.ok(response);
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    @PostMapping("/users/{id}/send-code")
    public Mono<ResponseEntity<Object>> sendCode(@PathVariable UUID id,
                                               @RequestParam("channel") String channel,
                                               HttpServletRequest req) {
        log.info("send-code hit: {} {} channel={}", req.getMethod(), req.getRequestURI(), channel);
        return Mono.fromCallable(() -> {
                    final VerificationChannel verificationChannel = VerificationChannel.from(channel);
                    sendVerificationCodeService.sendVerificationCode(id, verificationChannel);
                    return ResponseEntity.accepted().build();
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    @PostMapping("/users/{id}/confirm-code")
    public Mono<ResponseEntity<ConfirmVerificationCodeResponseDTO>> confirmCode(
            @PathVariable UUID id,
            @Valid @RequestBody final ConfirmVerificationCodeRequestDTO request) {
        return Mono.fromCallable(() -> {
                    final VerificationChannel channel = VerificationChannel.from(request.channel());
                    userContactConfirmationService.confirmVerificationCode(id, channel, request.code());
                    return ResponseEntity.ok(new ConfirmVerificationCodeResponseDTO(true));
                })
                .subscribeOn(Schedulers.boundedElastic());
    }
}
