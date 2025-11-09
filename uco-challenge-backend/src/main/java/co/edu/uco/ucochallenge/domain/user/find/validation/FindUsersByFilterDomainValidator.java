package co.edu.uco.ucochallenge.domain.user.find.validation;

import co.edu.uco.ucochallenge.domain.common.notification.Notification;
import co.edu.uco.ucochallenge.domain.user.find.model.FindUsersByFilterInputDomain;

public class FindUsersByFilterDomainValidator {

        public Notification validate(final FindUsersByFilterInputDomain domain) {
                final var notification = Notification.create();
                notification.merge(domain.validate());
                return notification;
        }
}
