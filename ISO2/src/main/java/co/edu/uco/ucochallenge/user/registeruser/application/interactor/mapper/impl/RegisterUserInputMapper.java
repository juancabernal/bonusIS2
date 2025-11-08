package co.edu.uco.ucochallenge.user.registeruser.application.interactor.mapper.impl;

import java.util.UUID;

import org.springframework.stereotype.Component;

import co.edu.uco.ucochallenge.application.interactor.mapper.DomainMapper;
import co.edu.uco.ucochallenge.user.registeruser.application.interactor.dto.RegisterUserInputDTO;
import co.edu.uco.ucochallenge.user.registeruser.application.usecase.domain.RegisterUserDomain;

@Component
public class RegisterUserInputMapper implements DomainMapper<RegisterUserInputDTO, RegisterUserDomain> {

        @Override
        public RegisterUserDomain toDomain(final RegisterUserInputDTO dto) {
                return RegisterUserDomain.builder()
                                .id(UUID.randomUUID())
                                .idType(dto.idTypeId())
                                .idTypeName(dto.idTypeName())
                                .idNumber(dto.idNumber())
                                .firstName(dto.firstName())
                                .secondName(dto.middleName())
                                .firstSurname(dto.lastName())
                                .secondSurname(dto.secondLastName())
                                .countryId(dto.countryId())
                                .departmentId(dto.departmentId())
                                .homeCity(dto.cityId())
                                .email(dto.email())
                                .mobileNumber(dto.mobile())
                                .build();
        }

        @Override
        public RegisterUserInputDTO toDto(final RegisterUserDomain domain) {
                return new RegisterUserInputDTO(
                                domain.getIdType(),
                                domain.getIdTypeName(),
                                domain.getIdNumber(),
                                domain.getFirstName(),
                                domain.getSecondName(),
                                domain.getFirstSurname(),
                                domain.getSecondSurname(),
                                domain.getEmail(),
                                domain.getMobileNumber(),
                                domain.getCountryId(),
                                domain.getDepartmentId(),
                                domain.getHomeCity());
        }
}
