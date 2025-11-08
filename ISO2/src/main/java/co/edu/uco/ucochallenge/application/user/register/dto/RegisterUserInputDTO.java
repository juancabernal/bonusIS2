package co.edu.uco.ucochallenge.application.user.register.dto;

import java.util.UUID;

import co.edu.uco.ucochallenge.crosscutting.helper.TextHelper;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterUserInputDTO(
                UUID idTypeId,
                String idTypeName,
                @NotBlank(message = "register.user.validation.idnumber.required") String idNumber,
                @NotBlank(message = "register.user.validation.firstname.required") String firstName,
                String middleName,
                @NotBlank(message = "register.user.validation.lastname.required") String lastName,
                String secondLastName,
                @Email(message = "register.user.validation.email.invalid") String email,
                String mobile,
                @NotNull(message = "register.user.validation.country.required") UUID countryId,
                @NotNull(message = "register.user.validation.department.required") UUID departmentId,
                @NotNull(message = "register.user.validation.city.required") UUID cityId) {

        public RegisterUserInputDTO {
                idTypeName = TextHelper.isEmpty(idTypeName) ? null : TextHelper.getDefaultWithTrim(idTypeName);
                idNumber = TextHelper.getDefaultWithTrim(idNumber);
                firstName = TextHelper.getDefaultWithTrim(firstName);
                middleName = TextHelper.isEmpty(middleName) ? null : TextHelper.getDefaultWithTrim(middleName);
                lastName = TextHelper.getDefaultWithTrim(lastName);
                secondLastName = TextHelper.isEmpty(secondLastName) ? null : TextHelper.getDefaultWithTrim(secondLastName);
                email = TextHelper.isEmpty(email) ? null : TextHelper.getDefaultWithTrim(email);
                mobile = TextHelper.isEmpty(mobile) ? null : TextHelper.getDefaultWithTrim(mobile);
        }
}
