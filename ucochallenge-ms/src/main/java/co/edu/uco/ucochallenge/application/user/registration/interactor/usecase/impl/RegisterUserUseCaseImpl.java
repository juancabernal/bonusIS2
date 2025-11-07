package co.edu.uco.ucochallenge.application.user.registration.interactor.usecase.impl;

import java.util.UUID;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uco.ucochallenge.crosscutting.legacy.exception.BusinessException;
import co.edu.uco.ucochallenge.crosscutting.legacy.exception.DomainValidationException;
import co.edu.uco.ucochallenge.crosscutting.legacy.helper.TextHelper;
import co.edu.uco.ucochallenge.crosscutting.legacy.helper.UUIDHelper;
import co.edu.uco.ucochallenge.domain.user.registration.model.UserRegistrationDomainModel;
import co.edu.uco.ucochallenge.application.user.registration.interactor.usecase.RegisterUserUseCase;
import co.edu.uco.ucochallenge.application.user.registration.port.ContactConfirmationPort;
import co.edu.uco.ucochallenge.application.user.registration.port.IdTypeQueryPort;
import co.edu.uco.ucochallenge.application.user.registration.port.LocationQueryPort;
import co.edu.uco.ucochallenge.application.user.registration.port.NotificationPort;
import co.edu.uco.ucochallenge.application.user.registration.port.RegisterUserRepositoryPort;
import co.edu.uco.ucochallenge.application.user.registration.usecase.domain.validation.RegisterUserDomainValidator;
@Service
@Transactional
public class RegisterUserUseCaseImpl implements RegisterUserUseCase {

        private static final Logger LOGGER = LoggerFactory.getLogger(RegisterUserUseCaseImpl.class);
        private static final int MAX_ID_GENERATION_ATTEMPTS = 5;

        private final RegisterUserRepositoryPort repositoryPort;
        private final NotificationPort notificationPort;
        private final ContactConfirmationPort contactConfirmationPort;
        private final IdTypeQueryPort idTypeQueryPort;
        private final LocationQueryPort locationQueryPort;
        private final Supplier<UUID> idGenerator;
        private final RegisterUserDomainValidator validator;

        public RegisterUserUseCaseImpl(final RegisterUserRepositoryPort repositoryPort,
                        final NotificationPort notificationPort,
                        final ContactConfirmationPort contactConfirmationPort,
                        final IdTypeQueryPort idTypeQueryPort,
                        final LocationQueryPort locationQueryPort,
                        final Supplier<UUID> idGenerator) {
                this.repositoryPort = repositoryPort;
                this.notificationPort = notificationPort;
                this.contactConfirmationPort = contactConfirmationPort;
                this.idTypeQueryPort = idTypeQueryPort;
                this.locationQueryPort = locationQueryPort;
                this.idGenerator = idGenerator;
                this.validator = new RegisterUserDomainValidator(repositoryPort, notificationPort, idGenerator);
        }

        @Override
        public UserRegistrationDomainModel execute(final UserRegistrationDomainModel domain) {
                ensureContactInformation(domain);
                resolveIdentificationType(domain);
                validateLocation(domain);
                ensureUniqueUserId(domain);
                final var notification = validator.validate(domain, "register-api");
                if (notification.hasErrors()) {
                        throw new BusinessException("register.user.duplicated");
                }

                repositoryPort.save(domain);

                return domain;
        }

        private void ensureContactInformation(final UserRegistrationDomainModel domain) {
                if (!domain.hasEmail() && !domain.hasMobileNumber()) {
                        throw new DomainValidationException("register.user.validation.contact.required");
                }
        }

        private void resolveIdentificationType(final UserRegistrationDomainModel domain) {
                if (!UUIDHelper.getDefault().equals(domain.getIdType())) {
                        if (!idTypeQueryPort.existsById(domain.getIdType())) {
                                throw new DomainValidationException("register.user.validation.idtype.required");
                        }
                        return;
                }

                if (TextHelper.isEmpty(domain.getIdTypeName())) {
                        throw new DomainValidationException("register.user.validation.idtype.required");
                }

                final var idType = idTypeQueryPort.findIdByName(domain.getIdTypeName())
                                .orElseThrow(() -> new DomainValidationException("register.user.validation.idtype.required"));

                domain.updateIdType(idType);
        }

        private void validateLocation(final UserRegistrationDomainModel domain) {
                if (isMissing(domain.getCountryId())
                                || !locationQueryPort.countryExists(domain.getCountryId())) {
                        throw new DomainValidationException("register.user.validation.country.required");
                }

                if (isMissing(domain.getDepartmentId())
                                || !locationQueryPort.departmentExists(domain.getDepartmentId())) {
                        throw new DomainValidationException("register.user.validation.department.required");
                }

                if (isMissing(domain.getHomeCity())
                                || !locationQueryPort.cityExists(domain.getHomeCity())) {
                        throw new DomainValidationException("register.user.validation.city.required");
                }
        }

        private void ensureUniqueUserId(final UserRegistrationDomainModel domain) {
                UUID candidateId = domain.getId();
                int attempts = 0;

                while (repositoryPort.existsById(candidateId) && attempts < MAX_ID_GENERATION_ATTEMPTS) {
                        candidateId = idGenerator.get();
                        attempts++;
                }

                if (repositoryPort.existsById(candidateId)) {
                        LOGGER.warn("Unable to generate a unique user id after {} attempts", attempts);
                        throw new BusinessException("register.user.identifier.unavailable");
                }

                if (!candidateId.equals(domain.getId())) {
                        domain.updateId(candidateId);
                }
        }

        private void sendConfirmations(final UserRegistrationDomainModel domain) {
                if (domain.hasEmail()) {
                        contactConfirmationPort.confirmEmail(domain.getEmail());
                        LOGGER.info("Correo de confirmación enviado a {}", domain.getEmail());
                }

                if (domain.hasMobileNumber()) {
                        contactConfirmationPort.confirmMobileNumber(domain.getMobileNumber());
                        LOGGER.info("SMS de confirmación enviado a {}", domain.getMobileNumber());
                }
        }

        private boolean isMissing(final UUID value) {
                return value == null || UUIDHelper.getDefault().equals(value);
        }
}
