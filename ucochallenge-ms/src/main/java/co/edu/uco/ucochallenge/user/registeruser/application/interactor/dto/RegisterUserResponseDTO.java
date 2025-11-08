package co.edu.uco.ucochallenge.user.registeruser.application.interactor.dto;

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
