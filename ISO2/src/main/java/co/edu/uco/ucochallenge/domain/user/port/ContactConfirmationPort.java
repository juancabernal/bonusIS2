package co.edu.uco.ucochallenge.domain.user.port;

public interface ContactConfirmationPort {

        void confirmEmail(String email);

        void confirmMobileNumber(String mobileNumber);
}
