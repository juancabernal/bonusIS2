package co.edu.uco.ucochallenge.infrastructure.secondary.persistence.jpa.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.uco.ucochallenge.infrastructure.secondary.persistence.jpa.entity.VerificationCodeEntity;

@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCodeEntity, UUID> {
    Optional<VerificationCodeEntity> findByContact(String contact);

    Optional<VerificationCodeEntity> findByContactIgnoreCase(String contact);

    void deleteByContact(String contact);

    void deleteByContactIgnoreCase(String contact);
}
