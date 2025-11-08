package co.edu.uco.ucochallenge.application.user.register.interactor.impl;

import org.springframework.stereotype.Service;

import co.edu.uco.ucochallenge.application.common.mapper.DomainMapper;
import co.edu.uco.ucochallenge.application.user.register.interactor.RegisterUserInteractor;
import co.edu.uco.ucochallenge.application.user.register.dto.RegisterUserInputDTO;
import co.edu.uco.ucochallenge.application.user.register.dto.RegisterUserResponseDTO;
import co.edu.uco.ucochallenge.application.user.register.usecase.RegisterUserUseCase;
import co.edu.uco.ucochallenge.domain.user.register.model.RegisterUserDomain;
import jakarta.transaction.Transactional;

@Transactional
@Service
public class RegisterUserInteractorImpl implements RegisterUserInteractor {

        private final RegisterUserUseCase useCase;
        private final DomainMapper<RegisterUserInputDTO, RegisterUserDomain> inputMapper;
        private final DomainMapper<RegisterUserResponseDTO, RegisterUserDomain> responseMapper;

        public RegisterUserInteractorImpl(final RegisterUserUseCase useCase,
                        final DomainMapper<RegisterUserInputDTO, RegisterUserDomain> inputMapper,
                        final DomainMapper<RegisterUserResponseDTO, RegisterUserDomain> responseMapper) {
                this.useCase = useCase;
                this.inputMapper = inputMapper;
                this.responseMapper = responseMapper;
        }

        @Override
        public RegisterUserResponseDTO execute(final RegisterUserInputDTO dto) {
                final var domain = inputMapper.toDomain(dto);
                final var result = useCase.execute(domain);
                return responseMapper.toDto(result);
        }
}
