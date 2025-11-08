package co.edu.uco.ucochallenge.application.user.find.usecase.impl;

import org.springframework.stereotype.Service;

import co.edu.uco.ucochallenge.crosscutting.exception.BusinessException;
import co.edu.uco.ucochallenge.domain.common.notification.Notification;
import co.edu.uco.ucochallenge.domain.user.port.FindUsersByFilterRepositoryPort;
import co.edu.uco.ucochallenge.application.user.find.usecase.FindUsersByFilterUseCase;
import co.edu.uco.ucochallenge.domain.user.find.model.FindUsersByFilterInputDomain;
import co.edu.uco.ucochallenge.domain.user.find.model.FindUsersByFilterResponseDomain;
import co.edu.uco.ucochallenge.domain.user.find.validation.FindUsersByFilterDomainValidator;

@Service
public class FindUsersByFilterUseCaseImpl implements FindUsersByFilterUseCase {

        private final FindUsersByFilterRepositoryPort repositoryPort;
        private final FindUsersByFilterDomainValidator validator;

        public FindUsersByFilterUseCaseImpl(final FindUsersByFilterRepositoryPort repositoryPort) {
                this.repositoryPort = repositoryPort;
                this.validator = new FindUsersByFilterDomainValidator();
        }

        @Override
        public FindUsersByFilterResponseDomain execute(final FindUsersByFilterInputDomain domain) {
                final Notification notification = validator.validate(domain);
                if (notification.hasErrors()) {
                        throw new BusinessException(notification.formattedMessages());
                }

                return repositoryPort.findAll(domain.getPage(), domain.getSize());
        }
}
