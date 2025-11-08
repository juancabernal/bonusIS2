package co.edu.uco.ucochallenge.application.user.register.dto;

import java.util.UUID;

public record RegisterUserResponseDTO(
                UUID id,
                UUID idTypeId,
                String idNumber,
                String firstName,
                String middleName,
                String lastName,
                String secondLastName,
                String email,
                String mobile,
                UUID cityId) {
}
