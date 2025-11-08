package co.edu.uco.ucochallenge.user.findusers.application.usecase.domain.validation;

import co.edu.uco.ucochallenge.crosscuting.notification.Notification;
import co.edu.uco.ucochallenge.user.findusers.application.usecase.domain.FindUsersByFilterInputDomain;

public class FindUsersByFilterDomainValidator {

        public Notification validate(final FindUsersByFilterInputDomain domain) {
                final var notification = Notification.create();
                notification.merge(domain.validate());
                return notification;
        }
}
