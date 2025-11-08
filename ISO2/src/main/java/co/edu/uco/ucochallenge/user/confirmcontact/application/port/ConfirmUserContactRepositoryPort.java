package co.edu.uco.ucochallenge.user.confirmcontact.application.port;

import java.util.UUID;

public interface ConfirmUserContactRepositoryPort {

        boolean confirmEmail(UUID id);

        boolean confirmMobileNumber(UUID id);

        void confirmEmailOrMobile(String contact);
}
