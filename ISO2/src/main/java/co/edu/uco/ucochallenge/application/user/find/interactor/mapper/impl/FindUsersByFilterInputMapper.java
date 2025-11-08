package co.edu.uco.ucochallenge.application.user.find.interactor.mapper.impl;

import org.springframework.stereotype.Component;

import co.edu.uco.ucochallenge.application.common.mapper.DomainMapper;
import co.edu.uco.ucochallenge.application.user.find.dto.FindUsersByFilterInputDTO;
import co.edu.uco.ucochallenge.domain.user.find.model.FindUsersByFilterInputDomain;

@Component
public class FindUsersByFilterInputMapper
                implements DomainMapper<FindUsersByFilterInputDTO, FindUsersByFilterInputDomain> {

        @Override
        public FindUsersByFilterInputDomain toDomain(final FindUsersByFilterInputDTO dto) {
                final var normalized = FindUsersByFilterInputDTO.normalize(dto.page(), dto.size());
                return FindUsersByFilterInputDomain.builder()
                                .page(normalized.page())
                                .size(normalized.size())
                                .build();
        }

        @Override
        public FindUsersByFilterInputDTO toDto(final FindUsersByFilterInputDomain domain) {
                return new FindUsersByFilterInputDTO(domain.getPage(), domain.getSize());
        }
}
