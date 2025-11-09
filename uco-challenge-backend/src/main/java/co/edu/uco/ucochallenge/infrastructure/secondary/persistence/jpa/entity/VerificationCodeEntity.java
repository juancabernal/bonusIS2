package co.edu.uco.ucochallenge.infrastructure.secondary.persistence.jpa.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "verification_tokens")
public class VerificationCodeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String contact; // email o teléfono

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private LocalDateTime expiration;

    @Column(nullable = false)
    private int attempts = 0;

    protected VerificationCodeEntity() {
        // JPA requirement
    }

    public VerificationCodeEntity(String contact, String code, LocalDateTime expiration) {
        this.contact = contact != null ? contact.trim() : null;
        this.code = code != null ? code.trim() : null;
        this.expiration = expiration;
    }

    public String getContact() {
        return contact;
    }

    public String getCode() {
        return code;
    }

    public LocalDateTime getExpiration() {
        return expiration;
    }

    public int getAttempts() {
        return attempts;
    }

    public void incrementAttempts() {
        this.attempts++;
    }
}
