package co.edu.uco.ucochallenge.user.registeruser.application.interactor.mapper.impl;

import org.springframework.stereotype.Component;

import co.edu.uco.ucochallenge.application.interactor.mapper.DomainMapper;
import co.edu.uco.ucochallenge.crosscuting.helper.TextHelper;
import co.edu.uco.ucochallenge.user.registeruser.application.interactor.dto.RegisterUserResponseDTO;
import co.edu.uco.ucochallenge.user.registeruser.application.usecase.domain.RegisterUserDomain;

@Component
public class RegisterUserResponseMapper implements DomainMapper<RegisterUserResponseDTO, RegisterUserDomain> {

        @Override
        public RegisterUserDomain toDomain(final RegisterUserResponseDTO dto) {
                throw new UnsupportedOperationException("Conversion not supported");
        }

        @Override
        public RegisterUserResponseDTO toDto(final RegisterUserDomain domain) {
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
