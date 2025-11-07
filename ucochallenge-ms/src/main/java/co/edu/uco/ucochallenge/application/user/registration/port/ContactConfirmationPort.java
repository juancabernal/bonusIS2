package co.edu.uco.ucochallenge.application.user.registration.port;

public interface ContactConfirmationPort {

        void confirmEmail(String email);

        void confirmMobileNumber(String mobileNumber);
}
