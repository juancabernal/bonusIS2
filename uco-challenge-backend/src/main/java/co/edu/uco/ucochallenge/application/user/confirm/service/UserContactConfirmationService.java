package co.edu.uco.ucochallenge.application.user.confirm.service;

import java.util.UUID;

import co.edu.uco.ucochallenge.domain.user.confirm.VerificationChannel;

public interface UserContactConfirmationService {

        void confirmVerificationCode(UUID userId, VerificationChannel channel, String code);
}
