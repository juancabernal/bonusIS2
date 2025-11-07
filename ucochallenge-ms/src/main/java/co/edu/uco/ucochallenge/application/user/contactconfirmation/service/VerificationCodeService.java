package co.edu.uco.ucochallenge.application.user.contactconfirmation.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import co.edu.uco.ucochallenge.infrastructure.secondary.adapters.repository.entity.VerificationCodeEntity;
import co.edu.uco.ucochallenge.infrastructure.secondary.adapters.repository.jpa.VerificationCodeRepository;

@Service
public class VerificationCodeService {

        private static final int CODE_UPPER_BOUND = 1_000_000;

        private final SecureRandom secureRandom = new SecureRandom();
        private final VerificationCodeRepository verificationCodeRepository;

        public VerificationCodeService(final VerificationCodeRepository verificationCodeRepository) {
                this.verificationCodeRepository = verificationCodeRepository;
        }

        public String generateCode() {
                return String.format("%06d", secureRandom.nextInt(CODE_UPPER_BOUND));
        }

        public Optional<VerificationCodeEntity> findByContact(final String contact) {
                return verificationCodeRepository.findByContact(contact);
        }

        public Optional<VerificationCodeEntity> findByContactIgnoreCase(final String contact) {
                return verificationCodeRepository.findByContactIgnoreCase(contact);
        }

        public void deleteByContact(final String contact) {
                verificationCodeRepository.deleteByContact(contact);
        }

        public void deleteByContactIgnoreCase(final String contact) {
                verificationCodeRepository.deleteByContactIgnoreCase(contact);
        }

        public VerificationCodeEntity save(final String contact, final String code, final LocalDateTime expiration) {
                return verificationCodeRepository.save(new VerificationCodeEntity(contact, code, expiration));
        }
}
