package co.edu.uco.ucochallenge.application.user.search.usecase.impl;

import org.springframework.stereotype.Service;

import co.edu.uco.ucochallenge.crosscutting.legacy.exception.BusinessException;
import co.edu.uco.ucochallenge.crosscutting.legacy.notification.Notification;
import co.edu.uco.ucochallenge.domain.user.search.model.UserSearchFilterDomainModel;
import co.edu.uco.ucochallenge.domain.user.search.model.UserSearchResultDomainModel;
import co.edu.uco.ucochallenge.application.user.search.port.FindUsersByFilterRepositoryPort;
import co.edu.uco.ucochallenge.application.user.search.usecase.FindUsersByFilterUseCase;
import co.edu.uco.ucochallenge.application.user.search.usecase.domain.validation.FindUsersByFilterDomainValidator;

@Service
public class FindUsersByFilterUseCaseImpl implements FindUsersByFilterUseCase {

        private final FindUsersByFilterRepositoryPort repositoryPort;
        private final FindUsersByFilterDomainValidator validator;

        public FindUsersByFilterUseCaseImpl(final FindUsersByFilterRepositoryPort repositoryPort) {
                this.repositoryPort = repositoryPort;
                this.validator = new FindUsersByFilterDomainValidator();
        }

        @Override
        public UserSearchResultDomainModel execute(final UserSearchFilterDomainModel domain) {
                final Notification notification = validator.validate(domain);
                if (notification.hasErrors()) {
                        throw new BusinessException(notification.formattedMessages());
                }

                return repositoryPort.findAll(domain.getPage(), domain.getSize());
        }
}
