package co.edu.uco.ucochallenge.user.registeruser.application.interactor.impl;

import org.springframework.stereotype.Service;

import co.edu.uco.ucochallenge.application.interactor.mapper.DomainMapper;
import co.edu.uco.ucochallenge.user.registeruser.application.interactor.RegisterUserInteractor;
import co.edu.uco.ucochallenge.user.registeruser.application.interactor.dto.RegisterUserInputDTO;
import co.edu.uco.ucochallenge.user.registeruser.application.interactor.dto.RegisterUserResponseDTO;
import co.edu.uco.ucochallenge.user.registeruser.application.interactor.usecase.RegisterUserUseCase;
import co.edu.uco.ucochallenge.user.registeruser.application.usecase.domain.RegisterUserDomain;
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
