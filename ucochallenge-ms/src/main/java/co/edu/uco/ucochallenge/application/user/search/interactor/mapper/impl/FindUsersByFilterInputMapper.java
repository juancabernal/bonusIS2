package co.edu.uco.ucochallenge.application.user.search.interactor.mapper.impl;

import org.springframework.stereotype.Component;

import co.edu.uco.ucochallenge.application.interactor.mapper.DomainMapper;
import co.edu.uco.ucochallenge.domain.user.search.model.UserSearchFilterDomainModel;
import co.edu.uco.ucochallenge.application.user.search.interactor.dto.FindUsersByFilterInputDTO;

@Component
public class FindUsersByFilterInputMapper
                implements DomainMapper<FindUsersByFilterInputDTO, UserSearchFilterDomainModel> {

        @Override
        public UserSearchFilterDomainModel toDomain(final FindUsersByFilterInputDTO dto) {
                final var normalized = FindUsersByFilterInputDTO.normalize(dto.page(), dto.size());
                return UserSearchFilterDomainModel.builder()
                                .page(normalized.page())
                                .size(normalized.size())
                                .build();
        }

        @Override
        public FindUsersByFilterInputDTO toDto(final UserSearchFilterDomainModel domain) {
                return new FindUsersByFilterInputDTO(domain.getPage(), domain.getSize());
        }
}
