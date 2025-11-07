package co.edu.uco.ucochallenge.application.user.search.interactor.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uco.ucochallenge.application.interactor.mapper.DomainMapper;
import co.edu.uco.ucochallenge.domain.user.search.model.UserSearchFilterDomainModel;
import co.edu.uco.ucochallenge.domain.user.search.model.UserSearchResultDomainModel;
import co.edu.uco.ucochallenge.application.user.search.interactor.FindUsersByFilterInteractor;
import co.edu.uco.ucochallenge.application.user.search.interactor.dto.FindUsersByFilterInputDTO;
import co.edu.uco.ucochallenge.application.user.search.interactor.dto.FindUsersByFilterOutputDTO;
import co.edu.uco.ucochallenge.application.user.search.usecase.FindUsersByFilterUseCase;


@Service
@Transactional(readOnly = true)
public class FindUsersByFilterInteractorImpl implements FindUsersByFilterInteractor {

        private final FindUsersByFilterUseCase useCase;
        private final DomainMapper<FindUsersByFilterInputDTO, UserSearchFilterDomainModel> inputMapper;
        private final DomainMapper<FindUsersByFilterOutputDTO, UserSearchResultDomainModel> outputMapper;

        public FindUsersByFilterInteractorImpl(final FindUsersByFilterUseCase useCase,
                        final DomainMapper<FindUsersByFilterInputDTO, UserSearchFilterDomainModel> inputMapper,
                        final DomainMapper<FindUsersByFilterOutputDTO, UserSearchResultDomainModel> outputMapper) {
                this.useCase = useCase;
                this.inputMapper = inputMapper;
                this.outputMapper = outputMapper;
        }

        @Override
        public FindUsersByFilterOutputDTO execute(final FindUsersByFilterInputDTO dto) {
                final var domain = inputMapper.toDomain(dto);
                final var responseDomain = useCase.execute(domain);
                return outputMapper.toDto(responseDomain);
        }
}
