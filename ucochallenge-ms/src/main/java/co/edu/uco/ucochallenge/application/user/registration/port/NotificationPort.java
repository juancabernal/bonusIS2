package co.edu.uco.ucochallenge.application.user.registration.port;

public interface NotificationPort {

        void notifyAdministrator(String message);

        void notifyExecutor(String executorIdentifier, String message);

        void notifyEmailOwner(String email, String message);

        void notifyMobileOwner(String mobileNumber, String message);
}
