package co.edu.uco.ucochallenge.user.registeruser.application.port;

public interface ContactConfirmationPort {

        void confirmEmail(String email);

        void confirmMobileNumber(String mobileNumber);
}
