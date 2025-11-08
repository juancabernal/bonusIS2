package co.edu.uco.ucochallenge.user.findusers.application.usecase.impl;

import org.springframework.stereotype.Service;

import co.edu.uco.ucochallenge.crosscuting.exception.BusinessException;
import co.edu.uco.ucochallenge.crosscuting.notification.Notification;
import co.edu.uco.ucochallenge.user.findusers.application.port.FindUsersByFilterRepositoryPort;
import co.edu.uco.ucochallenge.user.findusers.application.usecase.FindUsersByFilterUseCase;
import co.edu.uco.ucochallenge.user.findusers.application.usecase.domain.FindUsersByFilterInputDomain;
import co.edu.uco.ucochallenge.user.findusers.application.usecase.domain.FindUsersByFilterResponseDomain;
import co.edu.uco.ucochallenge.user.findusers.application.usecase.domain.validation.FindUsersByFilterDomainValidator;

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
