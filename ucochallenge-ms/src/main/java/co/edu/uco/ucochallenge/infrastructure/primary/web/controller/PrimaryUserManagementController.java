package co.edu.uco.ucochallenge.infrastructure.primary.web.controller;

import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import co.edu.uco.ucochallenge.application.user.contactconfirmation.service.SendVerificationCodeService;
import co.edu.uco.ucochallenge.application.user.contactconfirmation.service.UserContactConfirmationService;
import co.edu.uco.ucochallenge.application.user.contactconfirmation.service.VerificationChannel;
import co.edu.uco.ucochallenge.application.user.contactconfirmation.service.dto.ConfirmVerificationCodeRequestDTO;
import co.edu.uco.ucochallenge.application.user.contactconfirmation.service.dto.ConfirmVerificationCodeResponseDTO;
import co.edu.uco.ucochallenge.application.user.search.interactor.FindUsersByFilterInteractor;
import co.edu.uco.ucochallenge.application.user.search.interactor.dto.FindUsersByFilterInputDTO;
import co.edu.uco.ucochallenge.application.user.search.interactor.dto.FindUsersByFilterOutputDTO;
import co.edu.uco.ucochallenge.application.user.registration.interactor.RegisterUserInteractor;
import co.edu.uco.ucochallenge.application.user.registration.interactor.dto.RegisterUserInputDTO;
import co.edu.uco.ucochallenge.application.user.registration.interactor.dto.RegisterUserResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("/uco-challenge/api/v1")
public class PrimaryUserManagementController {

    private static final Logger log = LoggerFactory.getLogger(PrimaryUserManagementController.class);

    private final RegisterUserInteractor registerUserInteractor;
    private final FindUsersByFilterInteractor findUsersByFilterInteractor;
    private final UserContactConfirmationService userContactConfirmationService;
    private final SendVerificationCodeService sendVerificationCodeService;

    public PrimaryUserManagementController(final RegisterUserInteractor registerUserInteractor,
                                           final FindUsersByFilterInteractor findUsersByFilterInteractor,
                                           final UserContactConfirmationService userContactConfirmationService,
                                           final SendVerificationCodeService sendVerificationCodeService) {
        this.registerUserInteractor = registerUserInteractor;
        this.findUsersByFilterInteractor = findUsersByFilterInteractor;
        this.userContactConfirmationService = userContactConfirmationService;
        this.sendVerificationCodeService = sendVerificationCodeService;
    }

    @PostMapping("/users")
    public ResponseEntity<RegisterUserResponseDTO> create(@Valid @RequestBody final RegisterUserInputDTO request) {
        final RegisterUserResponseDTO response = registerUserInteractor.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/users")
    public ResponseEntity<FindUsersByFilterOutputDTO> getUsers(
            @RequestParam(name = "page", required = false) final Integer page,
            @RequestParam(name = "size", required = false) final Integer size) {
        final var normalizedInput = FindUsersByFilterInputDTO.normalize(page, size);
        final var response = findUsersByFilterInteractor.execute(normalizedInput);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/users/{id}/send-code")
    public ResponseEntity<Void> sendCode(@PathVariable UUID id,
                                         @RequestParam("channel") String channel,
                                         HttpServletRequest req) {
        log.info("send-code hit: {} {} channel={}", req.getMethod(), req.getRequestURI(), channel);
        final VerificationChannel verificationChannel = VerificationChannel.from(channel);
        sendVerificationCodeService.sendVerificationCode(id, verificationChannel);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/users/{id}/confirm-code")
    public ResponseEntity<ConfirmVerificationCodeResponseDTO> confirmCode(
            @PathVariable UUID id,
            @Valid @RequestBody final ConfirmVerificationCodeRequestDTO request) {
        final VerificationChannel channel = VerificationChannel.from(request.channel());
        userContactConfirmationService.confirmVerificationCode(id, channel, request.code());
        return ResponseEntity.ok(new ConfirmVerificationCodeResponseDTO(true));
    }
}
