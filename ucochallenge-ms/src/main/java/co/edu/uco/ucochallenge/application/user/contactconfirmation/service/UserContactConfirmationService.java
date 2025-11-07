package co.edu.uco.ucochallenge.application.user.contactconfirmation.service;

import java.util.UUID;

public interface UserContactConfirmationService {

        void confirmVerificationCode(UUID userId, VerificationChannel channel, String code);
}
