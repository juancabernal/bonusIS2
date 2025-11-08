package co.edu.uco.ucochallenge.user.registeruser.application.port;

public interface NotificationPort {

        void notifyAdministrator(String message);

        void notifyExecutor(String executorIdentifier, String message);

        void notifyEmailOwner(String email, String message);

        void notifyMobileOwner(String mobileNumber, String message);
}
