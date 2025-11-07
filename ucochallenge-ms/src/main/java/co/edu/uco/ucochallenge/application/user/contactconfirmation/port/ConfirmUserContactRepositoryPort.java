package co.edu.uco.ucochallenge.application.user.contactconfirmation.port;

import java.util.UUID;

public interface ConfirmUserContactRepositoryPort {

        boolean confirmEmail(UUID id);

        boolean confirmMobileNumber(UUID id);

        void confirmEmailOrMobile(String contact);
}
