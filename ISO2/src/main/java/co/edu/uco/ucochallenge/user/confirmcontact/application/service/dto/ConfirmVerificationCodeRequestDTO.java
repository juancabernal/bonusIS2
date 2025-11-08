package co.edu.uco.ucochallenge.user.confirmcontact.application.service.dto;

import jakarta.validation.constraints.NotBlank;

public record ConfirmVerificationCodeRequestDTO(
                @NotBlank(message = "El canal es obligatorio") String channel,
                @NotBlank(message = "El código es obligatorio") String code) {
}
