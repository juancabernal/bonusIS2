package co.edu.uco.ucochallenge.user.findusers.application.interactor.mapper.impl;

import org.springframework.stereotype.Component;

import co.edu.uco.ucochallenge.application.interactor.mapper.DomainMapper;
import co.edu.uco.ucochallenge.user.findusers.application.interactor.dto.FindUsersByFilterInputDTO;
import co.edu.uco.ucochallenge.user.findusers.application.usecase.domain.FindUsersByFilterInputDomain;

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
