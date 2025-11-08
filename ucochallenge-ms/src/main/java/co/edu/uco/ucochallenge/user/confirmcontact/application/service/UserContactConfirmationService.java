package co.edu.uco.ucochallenge.user.confirmcontact.application.service;

import java.util.UUID;

public interface UserContactConfirmationService {

        void confirmVerificationCode(UUID userId, VerificationChannel channel, String code);
}
