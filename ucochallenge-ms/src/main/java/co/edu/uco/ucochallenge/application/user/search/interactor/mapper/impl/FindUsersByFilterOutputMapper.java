package co.edu.uco.ucochallenge.application.user.search.interactor.mapper.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import co.edu.uco.ucochallenge.application.interactor.mapper.DomainMapper;
import co.edu.uco.ucochallenge.domain.user.search.model.UserSearchResultDomainModel;
import co.edu.uco.ucochallenge.domain.user.search.model.UserSearchSummaryDomainModel;
import co.edu.uco.ucochallenge.application.user.search.interactor.dto.FindUsersByFilterOutputDTO;
import co.edu.uco.ucochallenge.application.user.search.interactor.dto.UserSummaryDTO;

@Component
public class FindUsersByFilterOutputMapper
                implements DomainMapper<FindUsersByFilterOutputDTO, UserSearchResultDomainModel> {

        @Override
        public UserSearchResultDomainModel toDomain(final FindUsersByFilterOutputDTO dto) {
                final List<UserSummaryDTO> dtoUsers = dto.getUsers();
                final List<UserSearchSummaryDomainModel> users = dtoUsers == null
                                ? List.of()
                                : dtoUsers.stream()
                                                .map(this::mapToDomain)
                                                .collect(Collectors.toList());

                return UserSearchResultDomainModel.builder()
                                .users(users)
                                .page(dto.getPage())
                                .size(dto.getSize())
                                .totalElements(dto.getTotalElements())
                                .build();
        }

        @Override
        public FindUsersByFilterOutputDTO toDto(final UserSearchResultDomainModel domain) {
                final List<UserSummaryDTO> users = domain.getUsers()
                                .stream()
                                .map(this::mapToDto)
                                .collect(Collectors.toList());

                final FindUsersByFilterOutputDTO dto = new FindUsersByFilterOutputDTO();
                dto.setUsers(users);
                dto.setPage(domain.getPage());
                dto.setSize(domain.getSize());
                dto.setTotalElements(domain.getTotalElements());
                return dto;
        }

        private UserSearchSummaryDomainModel mapToDomain(final UserSummaryDTO dto) {
                return UserSearchSummaryDomainModel.builder()
                                .id(dto.getId())
                                .idType(dto.getIdTypeId())
                                .idNumber(dto.getIdNumber())
                                .firstName(dto.getFirstName())
                                .secondName(dto.getSecondName())
                                .firstSurname(dto.getLastName())
                                .secondSurname(dto.getSecondSurname())
                                .homeCity(dto.getHomeCityId())
                                .email(dto.getEmail())
                                .mobileNumber(dto.getMobileNumber())
                                .emailConfirmed(Boolean.TRUE.equals(dto.getEmailConfirmed()))
                                .mobileNumberConfirmed(Boolean.TRUE.equals(dto.getMobileNumberConfirmed()))
                                .build();
        }

        private UserSummaryDTO mapToDto(final UserSearchSummaryDomainModel domain) {
                final UserSummaryDTO dto = new UserSummaryDTO();
                dto.setId(domain.getId());
                dto.setIdTypeId(domain.getIdType());
                dto.setIdNumber(domain.getIdNumber());
                dto.setFirstName(domain.getFirstName());
                dto.setSecondName(domain.getSecondName());
                dto.setLastName(domain.getFirstSurname());
                dto.setSecondSurname(domain.getSecondSurname());
                dto.setHomeCityId(domain.getHomeCity());
                dto.setEmail(domain.getEmail());
                dto.setMobileNumber(domain.getMobileNumber());
                dto.setEmailConfirmed(domain.isEmailConfirmed());
                dto.setMobileNumberConfirmed(domain.isMobileNumberConfirmed());
                return dto;
        }
}
