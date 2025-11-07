package co.edu.uco.ucochallenge.application.user.registration.interactor.impl;

import org.springframework.stereotype.Service;

import co.edu.uco.ucochallenge.application.interactor.mapper.DomainMapper;
import co.edu.uco.ucochallenge.domain.user.registration.model.UserRegistrationDomainModel;
import co.edu.uco.ucochallenge.application.user.registration.interactor.RegisterUserInteractor;
import co.edu.uco.ucochallenge.application.user.registration.interactor.dto.RegisterUserInputDTO;
import co.edu.uco.ucochallenge.application.user.registration.interactor.dto.RegisterUserResponseDTO;
import co.edu.uco.ucochallenge.application.user.registration.interactor.usecase.RegisterUserUseCase;
import jakarta.transaction.Transactional;

@Transactional
@Service
public class RegisterUserInteractorImpl implements RegisterUserInteractor {

        private final RegisterUserUseCase useCase;
        private final DomainMapper<RegisterUserInputDTO, UserRegistrationDomainModel> inputMapper;
        private final DomainMapper<RegisterUserResponseDTO, UserRegistrationDomainModel> responseMapper;

        public RegisterUserInteractorImpl(final RegisterUserUseCase useCase,
                        final DomainMapper<RegisterUserInputDTO, UserRegistrationDomainModel> inputMapper,
                        final DomainMapper<RegisterUserResponseDTO, UserRegistrationDomainModel> responseMapper) {
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
