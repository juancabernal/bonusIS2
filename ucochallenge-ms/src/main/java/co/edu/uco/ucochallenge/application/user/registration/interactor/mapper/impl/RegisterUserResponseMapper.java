package co.edu.uco.ucochallenge.application.user.registration.interactor.mapper.impl;

import org.springframework.stereotype.Component;

import co.edu.uco.ucochallenge.application.interactor.mapper.DomainMapper;
import co.edu.uco.ucochallenge.crosscutting.legacy.helper.TextHelper;
import co.edu.uco.ucochallenge.domain.user.registration.model.UserRegistrationDomainModel;
import co.edu.uco.ucochallenge.application.user.registration.interactor.dto.RegisterUserResponseDTO;

@Component
public class RegisterUserResponseMapper implements DomainMapper<RegisterUserResponseDTO, UserRegistrationDomainModel> {

        @Override
        public UserRegistrationDomainModel toDomain(final RegisterUserResponseDTO dto) {
                throw new UnsupportedOperationException("Conversion not supported");
        }

        @Override
        public RegisterUserResponseDTO toDto(final UserRegistrationDomainModel domain) {
                return new RegisterUserResponseDTO(
                                domain.getId(),
                                domain.getIdType(),
                                domain.getIdNumber(),
                                domain.getFirstName(),
                                TextHelper.isEmpty(domain.getSecondName()) ? null : domain.getSecondName(),
                                domain.getFirstSurname(),
                                TextHelper.isEmpty(domain.getSecondSurname()) ? null : domain.getSecondSurname(),
                                domain.hasEmail() ? domain.getEmail() : null,
                                domain.hasMobileNumber() ? domain.getMobileNumber() : null,
                                domain.getHomeCity());
        }
}
